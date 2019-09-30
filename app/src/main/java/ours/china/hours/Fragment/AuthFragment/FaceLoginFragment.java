package ours.china.hours.Fragment.AuthFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ours.china.hours.Activity.Auth.FaceRegisterActivity;
import ours.china.hours.Activity.Auth.PerfectInforActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.FaceDetect.faceserver.CompareResult;
import ours.china.hours.FaceDetect.faceserver.FaceServer;
import ours.china.hours.FaceDetect.model.DrawInfo;
import ours.china.hours.FaceDetect.model.FacePreviewInfo;
import ours.china.hours.FaceDetect.util.ConfigUtil;
import ours.china.hours.FaceDetect.util.DrawHelper;
import ours.china.hours.FaceDetect.util.camera.CameraHelper;
import ours.china.hours.FaceDetect.util.camera.CameraListener;
import ours.china.hours.FaceDetect.util.face.FaceHelper;
import ours.china.hours.FaceDetect.util.face.FaceListener;
import ours.china.hours.FaceDetect.util.face.RequestFeatureStatus;
import ours.china.hours.FaceDetect.widget.FaceRectView;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.FaceInfo;
import ours.china.hours.Model.User;
import ours.china.hours.R;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import ours.china.hours.Activity.Auth.FaceLoginActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Url;
import ours.china.hours.R;

public class FaceLoginFragment extends Fragment {

    private static String featureFileUrl;
    private static String featureImageUrl;
    private String featureFileDownloadDir;
    private String featureImageFileDownloadDir;
    private String isImage = "no";
    private static final int REGISTER_STATUS_READY = 0;
    private static final int REGISTER_STATUS_PROCESSING = 1;
    private static final int REGISTER_STATUS_DONE = 2;
    private int registerStatus = REGISTER_STATUS_DONE;

    private static String TAG = "FaceLoginFragment";
    String tempMobileNumber = "";

    ProgressBar progressBar;
    private FaceRectView faceRectView;

    private static int requestNumber = 1;

    User currentUser = new User();
    SharedPreferencesManager sessionManager;

    ImageView imgDetected;
    RelativeLayout relButton;
    Button btnTryAgain, btnLogin;


    private int afCode = -1;
    private View previewView;
    private static final int MAX_DETECT_NUM = 10;
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer rgbCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private FaceHelper faceHelper;
    private List<CompareResult> compareResultList;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final float SIMILAR_THRESHOLD = 0.8F;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static FaceLoginFragment newInstance(){
        return  new FaceLoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30000)
                .setConnectTimeout(30000)
                .build();
        PRDownloader.initialize(getContext().getApplicationContext(), config);

        // for face
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getActivity().getWindow().setAttributes(attributes);
        }

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_login, container, false);

        init(view);
        event();
        return view;
    }

    public void init(View rootView) {
        sessionManager = new SharedPreferencesManager(getContext());
        progressBar = rootView.findViewById(R.id.progressbar);
        faceRectView = rootView.findViewById(R.id.face_rect_view);
        previewView = rootView.findViewById(R.id.texture_preview);
        imgDetected = rootView.findViewById(R.id.imgDetected);
        imgDetected.setVisibility(View.GONE);

        relButton = rootView.findViewById(R.id.relButton);
        btnTryAgain = rootView.findViewById(R.id.btnTryAgain);
        btnLogin = rootView.findViewById(R.id.btnLogin);

        featureFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "features" + File.separator;
        featureImageFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "imgs" + File.separator;

    }

    public void event() {
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relButton.setVisibility(View.GONE);
                imgDetected.setVisibility(View.GONE);
                faceRectView.setVisibility(View.VISIBLE);

                startScanFace();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder byteString = new StringBuilder();
                if (Global.faceIDFeature == null) {
                    return;
                }
                for(byte b : Global.faceFeature.getFeatureData()){
                    byteString.append(Byte.toString(b));
                }
                Global.faceHash = byteString.toString();
                Global.faceHash = Global.faceHash.substring(0,100);
                Log.i(TAG, "hash => " + Global.faceHash);

                loginApiWork();
            }
        });
    }

    public void startScanFace(){

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            requestPermissions(NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            initEngine();
            initCamera();
        }

    }

    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(getContext(), FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(getContext()),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);

        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(getContext(), getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
    }

    private void unInitEngine() {

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }


//    public void getDataFromServer() {
//        progressBar.setVisibility(View.VISIBLE);
//        Ion.with(getActivity())
//                .load(Url.faceLogin)
//                .setTimeout(10000)
//                .setBodyParameter("mobile", tempMobileNumber)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        Log.i(TAG, "result => " + result);
//
//                        if (e == null) {
//                            JSONObject resObj = null;
//                            try {
//                                resObj = new JSONObject(result.toString());
//
//                                if (resObj.getString("res").equals("success")) {
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }, 1000);
//
//                                } else {
//                                    Toast.makeText(getActivity(), "Extra error", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException ex) {
//                                ex.printStackTrace();
//                            }
//
//                        } else {
//                            Toast.makeText(getActivity(), "Api error", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    ArrayList<FaceInfo> faceInfoArrayList;

    public void getFaceDataFromServer() {
        Global.showLoading(getContext(),"generate_report");
        Ion.with(getActivity())
            .load(Url.query_userface)
            .setTimeout(10000)
            .setBodyParameter("access_token", "")
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {

                    Log.i(TAG, "result => " + result);

                    if (e == null) {
                        JSONObject resObj = null;
                        try {
                            resObj = new JSONObject(result.toString());

                            if (resObj.getString("res").equals("success")) {

                                JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                Log.i(TAG, "count => " + dataArray.length());

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<FaceInfo>>() {}.getType();
                                faceInfoArrayList = gson.fromJson(dataArray.toString(), type);
                                downloadAllFeatureFiles();
                            } else {
                                Toast.makeText(getActivity(), "发生错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Global.hideLoading();
                        Toast.makeText(getActivity(), "发生错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    int downloadCount = 0;
    int totalCount = 0;
    public void downloadAllFeatureFiles() {
        totalCount = faceInfoArrayList.size();

        for (FaceInfo one : faceInfoArrayList) {
            String fileName = one.faceInfoUrl.substring(one.faceInfoUrl.lastIndexOf('/') + 1);
            PRDownloader.download(Url.domainUrl + one.faceInfoUrl, featureFileDownloadDir, fileName)
                    .build()
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.i(TAG, "downloading file => " + downloadCount);
                            downloadCount++;
                            if (downloadCount == totalCount) {
                                startScanFace();
                                Global.hideLoading();
//                                Toast.makeText(getContext(), "所有数据已加载。", Toast.LENGTH_LONG).show();

                                FaceServer.getInstance().init(getActivity());
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            downloadCount++;
                            if (downloadCount == totalCount) {
                                startScanFace();
                                Global.hideLoading();
//                                Toast.makeText(getContext(), "你没有收到所有数据。", Toast.LENGTH_LONG).show();

                                FaceServer.getInstance().init(getActivity());
                            }

                        }
                    });
        }
    }

    public String electedPhoneNumber = "";
    public void downloadImageFile(String faceFeatureLocalName) {
        String imgDownloadUrl = "";
        for (FaceInfo one : faceInfoArrayList) {
            if (one.faceInfoUrl.contains(faceFeatureLocalName)) {
                imgDownloadUrl = one.faceImageUrl;
                electedPhoneNumber = one.mobile;
                break;
            }
        }

        Log.i(TAG, "imgDownloadUrl => " + imgDownloadUrl + "phoneNumber => " + electedPhoneNumber);
        String imgFileLocalName = imgDownloadUrl.substring(imgDownloadUrl.lastIndexOf('/') + 1);

        PRDownloader.download(Url.domainUrl + imgDownloadUrl, featureImageFileDownloadDir, imgFileLocalName)
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        imgDetected.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        faceRectView.setVisibility(View.INVISIBLE);

                        relButton.setVisibility(View.VISIBLE);

                        Glide.with(getContext())
                                .load(featureImageFileDownloadDir + imgFileLocalName)
                                .into(imgDetected);
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    public void loginApiWork() {
        Global.showLoading(getContext(),"generate_report");
        Ion.with(getActivity())
                .load(Url.faceLogin)
                .setTimeout(10000)
                .setBodyParameter("grant_type", "face")
                .setBodyParameter("client_id", Url.CLIENT_ID)
                .setBodyParameter("client_secret", Url.CLIENT_SECRET)
                .setBodyParameter("scope", Url.SCOPE)
                .setBodyParameter("username", electedPhoneNumber)
                .setBodyParameter("face_hash", Global.faceHash)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        Log.i(TAG, "result => " + result);
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {

                                    // save token and refresh token.
                                    Global.access_token = resObj.getString("access_token");
                                    Global.refresh_token = resObj.getString("refresh_token");

                                    // to update token
                                    Intent intent = new Intent();
                                    intent.setAction(Url.filter_refresh_token);
                                    intent.putExtra("refresh_token", Global.refresh_token);
                                    getActivity().sendBroadcast(intent);

                                    // save session data.
                                    currentUser.mobile = electedPhoneNumber;
                                    currentUser.faceHash = Global.faceHash;
                                    UsersManagement.saveCurrentUser(currentUser, sessionManager);

                                    getUserInfo();

                                } else {
                                    Global.hideLoading();
                                    Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                Global.hideLoading();
                            }

                        } else {
                            Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                            Global.hideLoading();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper != null) {
            synchronized (faceHelper) {
                unInitEngine();
            }
            ConfigUtil.setTrackId(getContext(), faceHelper.getCurrentTrackId());
            faceHelper.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }


    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                //FR成功
                if (faceFeature != null) {
                    searchFace(faceFeature, requestId);
                }
                else {
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };


        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);
                Log.i(TAG, "onCameraOpened: " + drawHelper.toString());
                faceHelper = new FaceHelper.Builder()
                        .faceEngine(faceEngine)
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .currentTrackId(ConfigUtil.getTrackId(getContext().getApplicationContext()))
                        .build();
            }


            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21);
                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    drawPreviewInfo(facePreviewInfoList);
                }
                registerFace(nv21, facePreviewInfoList);
                clearLeftFace(facePreviewInfoList);

                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {

                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                            faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraID != null ? rgbCameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }



    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable.create(new ObservableOnSubscribe<CompareResult>() {
            @Override
            public void subscribe(ObservableEmitter<CompareResult> emitter) {
                CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
                Log.i(TAG, compareResult.toString());

                if (compareResult == null) {
                    emitter.onError(null);
                } else {
                    emitter.onNext(compareResult);
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                            return;
                        }

//                        Log.i(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() + " trackId = " + requestId + "  similar = " + compareResult.getSimilar());
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                faceHelper.addName(requestId, "VISITOR " + requestId);

                                Log.i(TAG, "compare result username => " + compareResult.getUserName());
                                downloadImageFile(compareResult.getUserName());
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);

                                Log.i(TAG, "compare result username => " + compareResult.getUserName());
                                downloadImageFile(compareResult.getUserName());
                                Log.i("What error", "I don't know");
//                                String tempByteStr = new String(frFace.getFeatureData());
//                                alerDialogWork(tempByteStr);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUserName());

                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "VISITOR " + requestId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            return;
        }

        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(integer);
                livenessMap.remove(integer);
            }
        }

    }

    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
            Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(facePreviewInfoList.get(i).getFaceInfo().getRect()), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness == null ? LivenessInfo.UNKNOWN : liveness,
                    name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
        }
        drawHelper.draw(faceRectView, drawInfoList);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                progressBar.setVisibility(View.VISIBLE);
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void registerFace(final byte[] nv21, final List<FacePreviewInfo> facePreviewInfoList) {
        if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            registerStatus = REGISTER_STATUS_PROCESSING;
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    boolean success = FaceServer.getInstance().registerNv21(getContext(), nv21.clone(), previewSize.width, previewSize.height,
                            facePreviewInfoList.get(0).getFaceInfo(), "registered " + faceHelper.getCurrentTrackId());
                    emitter.onNext(success);
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean success) {
                            String result = success ? "register success!" : "register failed!";
//                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "register failed!", Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    void getUserInfo() {
        Ion.with(getActivity())
                .load(Url.get_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                User user = new UsersManagement().mapFetchProfileResponse(resObj);
                                user.mobile = electedPhoneNumber;
                                user.faceHash = Global.faceHash;
                                UsersManagement.saveCurrentUser(user, sessionManager);

                                if (user.faceInfoUrl.isEmpty()){
                                    Global.hideLoading();
                                    Intent intent = new Intent(getActivity(), PerfectInforActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteAlreadyExistFiles() {

        try {
            FileUtils.deleteDirectory(new File(featureFileDownloadDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "featureDir deleted");
        try {
            FileUtils.deleteDirectory(new File(featureImageFileDownloadDir));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Log.i(TAG, "featureImageDir deleted");

    }

//    class DownloadFeatureFile extends AsyncTask<String, String, String> {
//
//        private Context context;
//        private String fileName;
//        private String folder;
//
//        public DownloadFeatureFile(Context context) {
//            this.context = context;
//        }
//
//        /* Access modifiers changed, original: protected */
//        public void onPreExecute() {
//            super.onPreExecute();
//        }
//
//
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            try {
//                URL url = new URL(Url.domainUrl + f_url[0]);
//                URLConnection connection = url.openConnection();
//                connection.connect();
//                // getting file length
//                int lengthOfFile = connection.getContentLength();
//
//                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
//
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                String dir = "";
//
//                if (isImage.equals("no")) {
//                    dir = featureFileDownloadDir;
//                } else if (isImage.equals("yes")) {
//                    dir = featureImageFileDownloadDir;
//                }
//
//                File directory = new File(dir);
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                OutputStream output = new FileOutputStream(featureFileDownloadDir + fileName);
//                Log.i(TAG, "featureDownloadFileUrl => " + featureFileDownloadDir + fileName);
//
//                byte data[] = new byte[1024];
//                long total = 0;
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
//                    output.write(data, 0, count);
//                }
//                output.flush();
//                output.close();
//                input.close();
//
//                return "success";
//
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//
//            return "fail";
//        }
//
//        @Override
//        protected void onPostExecute(String message) {
//            super.onPostExecute(message);
//            if (message.equals("success") && isImage.equals("no")) {
//                Log.i(TAG, "feautre file download end");
//
//                isImage = "yes";
//                new DownloadFeatureFile(getContext()).execute(featureImageUrl);
//
//            } else if (message.equals("success") && isImage.equals("yes")) {
//
//                Global.hideLoading();
//                Log.i(TAG, "feature image file download end");
//
//                isImage = "no";
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                startActivity(intent);
//
//                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
//            } else {
//
//                Intent intent = new Intent(getActivity(), PerfectInforActivity.class);
//                startActivity(intent);
//                Global.hideLoading();
//                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
