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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_login, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getActivity().getWindow().setAttributes(attributes);
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        FaceServer.getInstance().init(getActivity());

        init(view);
        event(view);
        return view;
    }

    public void init(View rootView) {
        sessionManager = new SharedPreferencesManager(getContext());
        progressBar = rootView.findViewById(R.id.progressbar);
        faceRectView = rootView.findViewById(R.id.face_rect_view);
        previewView = rootView.findViewById(R.id.texture_preview);

        featureFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "features" + File.separator;
        featureImageFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "imgs" + File.separator;


    }

    public void startScanFace(){

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                register();
            }
        }, 3000);
    }

    public void event(View view) {

    }
    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(getActivity(), FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(getActivity()),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);

        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(getActivity(), getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
    }
    private void unInitEngine() {

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    public void displayImage() {
    }

    public void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(getActivity())
                .load(Url.faceLogin)
                .setTimeout(10000)
                .setBodyParameter("mobile", tempMobileNumber)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "result => " + result);

                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 1000);

                                } else {
                                    Toast.makeText(getActivity(), "Extra error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), "Api error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper != null) {
            synchronized (faceHelper) {
                unInitEngine();
            }
            ConfigUtil.setTrackId(getActivity(), faceHelper.getCurrentTrackId());
            faceHelper.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        FaceServer.getInstance().unInit();
        super.onDestroyView();
    }
    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getActivity(), neededPermission) == PackageManager.PERMISSION_GRANTED;
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
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }

                }
                //FR 失败
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
                        .currentTrackId(ConfigUtil.getTrackId(getActivity().getApplicationContext()))
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
//        Toast.makeText(FaceRegisterActivity.this, "Hello", Toast.LENGTH_SHORT).show();
        if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            registerStatus = REGISTER_STATUS_PROCESSING;
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    boolean success = FaceServer.getInstance().registerNv21(getActivity(), nv21.clone(), previewSize.width, previewSize.height,
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
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;

                            if (result.equals("register success!")) {
                                loginWithFaceInfo();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "register failed!", Toast.LENGTH_SHORT).show();
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void register() {
        if (registerStatus == REGISTER_STATUS_DONE) {
            registerStatus = REGISTER_STATUS_READY;
        }
        Global.canGetFaceFeature = "yes";
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == requestNumber && !Global.faceImageLocalUrl.equals("")) {
            tempMobileNumber = Global.faceImageLocalUrl.substring(Global.faceImageLocalUrl.lastIndexOf('/') + 1);
            tempMobileNumber = tempMobileNumber.split(",")[0];

            Log.i(TAG, "mobile number => " + tempMobileNumber);
            getDataFromServer();
            displayImage();
        }
    }

    void loginWithFaceInfo(){
        Ion.with(getActivity())
                .load(Url.faceLogin)
                .setTimeout(10000)
                .setBodyParameter("grant_type", "face")
                .setBodyParameter("client_id", Url.CLIENT_ID)
                .setBodyParameter("client_secret", Url.CLIENT_SECRET)
                .setBodyParameter("scope", Url.SCOPE)
                .setBodyParameter("face_hash", Global.faceHash)
                .setBodyParameter("username", "")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        Log.i(TAG, "result => " + result);
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                // save token and refresh token.
                                Global.access_token = resObj.getString("access_token");
                                Global.refresh_token = resObj.getString("refresh_token");

                                UsersManagement.saveCurrentUser(currentUser, sessionManager);

                                if (Global.access_token != null && !Global.access_token.equals("")) {
                                    getUserInfo();
                                } else {
                                    Toast.makeText(getContext(), "Received data error", Toast.LENGTH_SHORT).show();
                                    Global.hideLoading();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                Global.hideLoading();
                            }

                        } else {
                            Global.hideLoading();
                            Toast.makeText(getContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void getUserInfo(){
        Ion.with(getActivity())
                .load(Url.get_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                User user = new UsersManagement().mapFetchProfileResponse(resObj);
                                user.password = currentUser.password;
                                featureImageUrl = user.faceImageUrl;
                                featureFileUrl = user.faceInfoUrl;
                                UsersManagement.saveCurrentUser(user, sessionManager);

                                if (user.faceInfoUrl.isEmpty()){
                                    Intent intent = new Intent(getActivity(), PerfectInforActivity.class);
                                    startActivity(intent);
                                }else{
                                    deleteAlreadyExistFiles();
                                    new PasswordLoginFragment.DownloadFeatureFile(getContext()).execute(user.faceInfoUrl);
                                }
                            } catch (JSONException ex) {
                                Global.hideLoading();
                                ex.printStackTrace();
                                Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Global.hideLoading();
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

}
