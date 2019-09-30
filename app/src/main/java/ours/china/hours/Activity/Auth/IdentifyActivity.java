package ours.china.hours.Activity.Auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Evaluator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import cz.msebera.android.httpclient.impl.io.IdentityInputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ours.china.hours.Activity.Camera.CameraCrop;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.BuildConfig;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.ActivityCodes;
import ours.china.hours.Common.Utils.FileCompressor;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.UploadIdentify;
import ours.china.hours.Model.User;
import ours.china.hours.R;
import ours.china.hours.Utility.MultipartUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ours.china.hours.Activity.Global.REQUEST_GALLERY_PHOTO_BACK;
import static ours.china.hours.Activity.Global.REQUEST_GALLERY_PHOTO_FACE;
import static ours.china.hours.Activity.Global.REQUEST_TAKE_PHOTO_BACK;
import static ours.china.hours.Activity.Global.REQUEST_TAKE_PHOTO_FACE;


public class IdentifyActivity extends AppCompatActivity {
    private final String TAG = "IdentifyActivity";

    private ImageButton imgBtnIdentifyFace, imgBtnIdentifyBack;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_photo, linIdentifyBack;
    private Button btnIdentifySubmit;
    private TextView tvIdentyPhoto,tvIdentyAlbum, tvIdentyCancel;
    private ImageView imgIdentyFace, imgIdentyBack, imgIdentiBack;
    Uri filePath;
    File mPhotoFile, mPhotoBackFile;
    FileCompressor mCompressor;

    APIInterface apiInterface;
    SharedPreferencesManager sessionManager;
    User currentUser;

    private Bitmap mainBimap;
    private int processImageValue;
    private FaceEngine faceEngine;
    private int faceEngineCode = -1;

    private FaceFeature mainFeature = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mCompressor = new FileCompressor(this);

        initUI();
        initEngine();

        gotoBack();
        showBottomSetting();
        cancelImage();
        uploadIdentify();
    }

    private void initEngine() {

        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);

        Log.i(TAG, "initEngine: init " + faceEngineCode);

        if (faceEngineCode != ErrorInfo.MOK) {
//            Toast.makeText(this, getString(R.string.init_failed, faceEngineCode), Toast.LENGTH_SHORT).show();
        }
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + faceEngineCode);
        }
    }

    private void initUI(){

        imgBtnIdentifyFace = (ImageButton)findViewById(R.id.imgBtnIdentifyFace);
        imgBtnIdentifyBack = (ImageButton)findViewById(R.id.imgBtnIdentifyBack);
        btnIdentifySubmit = (Button)findViewById(R.id.btnIdentifySubmit);
        bottom_photo = (LinearLayout)findViewById(R.id.bottom_photo);
        sheetBehavior = BottomSheetBehavior.from(bottom_photo);
        tvIdentyPhoto = (TextView)findViewById(R.id.tvIdentyPhoto);
        tvIdentyAlbum = (TextView)findViewById(R.id.tvIdentyAlbum);
        tvIdentyCancel = (TextView)findViewById(R.id.tvIdentyCancel);
        imgIdentyFace = (ImageView)findViewById(R.id.imgIdentyFace);
        imgIdentyBack = (ImageView)findViewById(R.id.imgIdentyBack);
        imgIdentiBack = (ImageView)findViewById(R.id.imgIdentiBack);
        linIdentifyBack = (LinearLayout)findViewById(R.id.linIdentifyBack);
        sessionManager = new SharedPreferencesManager(this);

        currentUser = UsersManagement.getCurrentUser(sessionManager);
        if (!currentUser.idCardFront.isEmpty())
        {
            Ion.with(IdentifyActivity.this)
                    .load(Url.domainUrl + currentUser.idCardFront)
                    .withBitmap()
                    .placeholder(R.drawable.id_prompt1_icon)
                    .intoImageView(imgIdentyFace);

        }
        if (!currentUser.idCardBack.isEmpty())
        {
            Ion.with(IdentifyActivity.this)
                    .load(Url.domainUrl + currentUser.idCardBack)
                    .withBitmap()
                    .placeholder(R.drawable.id_prompt2_icon)
                    .intoImageView(imgIdentyBack);

        }
    }


    private void gotoBack(){
        imgIdentiBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    IdentifyActivity.this.finish();
                } else {
                    IdentifyActivity.super.onBackPressed();
                }
            }
        });
    }


    private void showBottomSetting(){

        imgBtnIdentifyFace.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottom_photo.setVisibility(View.VISIBLE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnIdentifySubmit.setVisibility(View.INVISIBLE);
//                    linIdentifyBack.setBackgroundColor(getResources().getColor(R.color.default_shadow_color));

                    chooseImageFromCamera(0);
                    chooseImageFromAlbum(0);

                } else {
                    bottom_photo.setVisibility(View.INVISIBLE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnIdentifySubmit.setVisibility(View.VISIBLE);
                }
            }
        });

        imgBtnIdentifyBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottom_photo.setVisibility(View.VISIBLE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnIdentifySubmit.setVisibility(View.INVISIBLE);
//                    linIdentifyBack.setBackgroundColor(getResources().getColor(R.color.default_shadow_color));

                    chooseImageFromCamera(1);
                    chooseImageFromAlbum(1);

                } else {
                    bottom_photo.setVisibility(View.INVISIBLE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnIdentifySubmit.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void chooseImageFromAlbum(int i){

        if (i == 0){
            tvIdentyAlbum.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    dispatchGalleryIntent(0);
                }
            });
        }else if (i == 1){
            tvIdentyAlbum.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    dispatchGalleryIntent(1);
                }
            });
        }


    }


    private void chooseImageFromCamera(int i){
        if (i == 0){
            tvIdentyPhoto.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    dispatchTakePictureIntent(0);
                }
            });
        }else if (i == 1){
            tvIdentyPhoto.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    dispatchTakePictureIntent(1);
                }
            });
        }

    }


    private void cancelImage(){

        tvIdentyCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottom_photo.setVisibility(View.GONE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnIdentifySubmit.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    private void uploadIdentify(){
        btnIdentifySubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                if (mPhotoFile == null){
                    Global.alert(IdentifyActivity.this, getResources().getString(R.string.app_name), "请设置图片", getResources().getString(R.string.confirm));
                    return;
                }
                if (mPhotoBackFile == null){
                    Global.alert(IdentifyActivity.this, getResources().getString(R.string.app_name), "请设置图片", getResources().getString(R.string.confirm));
                    return;
                }

                if (mainFeature == null) {
                    Global.alert(IdentifyActivity.this, getResources().getString(R.string.app_name), "请设置图片", getResources().getString(R.string.confirm));
                    return;
                }

                uploadIdCardImages();

            }
        });

    }


    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent(int i) {
        bottom_photo.setVisibility(View.INVISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        btnIdentifySubmit.setVisibility(View.VISIBLE);
        Intent intent = new Intent(IdentifyActivity.this, CameraCrop.class);
        if (i == 0){
            intent.putExtra("type", 0);
            startActivityForResult(intent, ActivityCodes.CROP_IMAGE_FACE);
        }else if (i == 1){
            intent.putExtra("type", 1);
            startActivityForResult(intent, ActivityCodes.CROP_IMAGE_BACK);
        }


    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent(int i) {

        bottom_photo.setVisibility(View.INVISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        btnIdentifySubmit.setVisibility(View.VISIBLE);
        if (i == 0){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO_FACE);
        }else if (i == 1){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO_BACK);
        }

    }


    /**
     * Create file with current timestamp name    */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        Log.i("IdentiActivity", mFile.toString());
        Log.i("IdentiActivity", "mFile = " + mFile.getPath().toString());
        Log.i("IdentiActivity", "mFile = " + storageDir.toString());
        return mFile;
    }


    /**
     * Get real file path from URI    */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        unInitEngine();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GALLERY_PHOTO_FACE) {
                    Uri selectedImage = data.getData();
                    try {
                        mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));

                        mainBimap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        processImage(mainBimap);

                        Log.i("IdentifyActivity", "mPhotoFile = " + mPhotoFile.getAbsolutePath() + "mainBitmap = " + mainBimap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyFace.setVisibility(View.INVISIBLE);
                } else if (requestCode == REQUEST_GALLERY_PHOTO_BACK){
                    Uri selectedImage = data.getData();
                    try {
                        mPhotoBackFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyBack.setVisibility(View.INVISIBLE);
                    Glide.with(IdentifyActivity.this).load(mPhotoBackFile).apply(new RequestOptions().placeholder(R.drawable.id_back_icon)).into(imgIdentyBack);
                } else if (requestCode == ActivityCodes.CROP_IMAGE_FACE){
                    if (data.getStringExtra("cropImagePath") != null) {//

                        String imagePath = data.getStringExtra("cropImagePath");
                        Log.i(TAG, "CropImagePath => " + imagePath);
                        File imgFile = new File(imagePath);
                        if (Global.faceFeature == null){
                            mPhotoFile = imgFile;
//                            imgIdentyFace.setImageURI(Uri.fromFile(imgFile));

                            try {
                                mainBimap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imgFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            processImage(mainBimap);

                            Log.i("IdentifyActivity", "mPhotoFile = " + mPhotoFile.getAbsolutePath() + "mainBitmap = " + mainBimap);
                        }
//                        else{
//                            mPhotoBackFile = imgFile;
//                            imgIdentyBack.setImageURI(Uri.fromFile(imgFile));
//                        }
                    }
                } else if (requestCode == ActivityCodes.CROP_IMAGE_BACK) {
                    if (data.getStringExtra("cropImagePath") != null) {
                        String imagePath = data.getStringExtra("cropImagePath");
                        Log.i(TAG, "CropImagePath => " + imagePath);
                        File imgFile = new File(imagePath);

                        mPhotoBackFile = imgFile;
                        imgIdentyBack.setImageURI(Uri.fromFile(imgFile));
                    }
                }
        }
    }

    private void uploadIdCardImages(){
        Global.showLoading(IdentifyActivity.this,"generate_report");
        String header = "Bearer " + Global.access_token;
        Ion.with(IdentifyActivity.this)
                .load(Url.uploadIdCard)
                .setTimeout(10000)
                .setHeader("Authorization", header)
                .setMultipartFile("front", "application/form-data", mPhotoFile)
                .setMultipartFile("back", "application/form-data", mPhotoBackFile)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {

                                    Global.identify = getResources().getString(R.string.identify_success);
                                    Toast.makeText(IdentifyActivity.this, "认证成功", Toast.LENGTH_LONG).show();

                                    currentUser.idCardFront = resObj.getString("idCardFront");
                                    currentUser.idCardBack = resObj.getString("idCardBack");
                                    UsersManagement.saveCurrentUser(currentUser, sessionManager);

                                    Global.faceIDFeature = mainFeature;
                                    IdentifyActivity.super.onBackPressed();

                                } else {
                                    Toast.makeText(IdentifyActivity.this, "与服务器通信时发生错误。", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(IdentifyActivity.this, "面部注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void processImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        if (faceEngine == null) {
            return;
        }

        //接口需要的bgr24宽度必须为4的倍数
        bitmap = ImageUtils.alignBitmapForBgr24(bitmap);

        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //bitmap转bgr24
        final byte[] bgr24 = ImageUtils.bitmapToBgr24(bitmap);

        if (bgr24 != null) {

            List<FaceInfo> faceInfoList = new ArrayList<>();
            //人脸检测
            int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
            if (detectCode != 0 || faceInfoList.size() == 0) {
                Toast.makeText(IdentifyActivity.this, "面部不在", Toast.LENGTH_SHORT).show();
                return;
            }
            //绘制bitmap
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);
            paint.setColor(Color.YELLOW);

            if (faceInfoList.size() > 0) {

                for (int i = 0; i < faceInfoList.size(); i++) {
                    //绘制人脸框
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(faceInfoList.get(i).getRect(), paint);
                    //绘制人脸序号
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setTextSize(faceInfoList.get(i).getRect().width() / 2);
                    canvas.drawText("" + i, faceInfoList.get(i).getRect().left, faceInfoList.get(i).getRect().top, paint);

                }
            }

            int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
            Log.i(TAG, "processImage: " + faceProcessCode);
            if (faceProcessCode != ErrorInfo.MOK) {
                Toast.makeText(IdentifyActivity.this, "面部检测失败", Toast.LENGTH_SHORT).show();
                return;
            }
            //年龄信息结果
            List<AgeInfo> ageInfoList = new ArrayList<>();
            //性别信息结果
            List<GenderInfo> genderInfoList = new ArrayList<>();
            //三维角度结果
            List<Face3DAngle> face3DAngleList = new ArrayList<>();
            //获取年龄、性别、三维角度
            int ageCode = faceEngine.getAge(ageInfoList);
            int genderCode = faceEngine.getGender(genderInfoList);
            int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);

            if ((ageCode | genderCode | face3DAngleCode) != ErrorInfo.MOK) {
                Toast.makeText(IdentifyActivity.this, "面部检测失败", Toast.LENGTH_SHORT).show();
                return;
            }

            //人脸比对数据显示
            if (faceInfoList.size() > 0) {
                if (Global.faceFeature == null) {
                    mainFeature = new FaceFeature();
                    int res = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), mainFeature);
                    if (res != ErrorInfo.MOK) {
                        mainFeature = null;
                        mPhotoFile = null;
                    }

                    Glide.with(IdentifyActivity.this).load(mPhotoFile).apply(new RequestOptions().placeholder(R.drawable.id_obverse_icon)).into(imgIdentyFace);

                } else {
                    FaceFeature faceFeature = new FaceFeature();
                    int res = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
                    if (res == 0) {
                        FaceSimilar faceSimilar = new FaceSimilar();
                        int compareResult = faceEngine.compareFaceFeature(Global.faceFeature, faceFeature, faceSimilar);
                        if (compareResult == ErrorInfo.MOK) {

                            if (faceSimilar.getScore() > 0.8f) {
                                // in case of success, mPhotoFile is exist.
                                mainFeature = faceFeature;
                                Toast.makeText(IdentifyActivity.this, "面部检测成功", Toast.LENGTH_SHORT).show();
                                Glide.with(IdentifyActivity.this).load(mPhotoFile).apply(new RequestOptions().placeholder(R.drawable.id_obverse_icon)).into(imgIdentyFace);

                            } else {
                                mPhotoFile = null;
                            }
                        } else {
                            Toast.makeText(IdentifyActivity.this, "面部检测失败", Toast.LENGTH_SHORT).show();
                            mPhotoFile = null;
                        }
                    } else {
                        mPhotoFile = null;
                    }
                }
            } else {
                if (Global.faceFeature == null) {
                    mainBimap = null;
                    mPhotoFile = null;
                }
            }

        } else {
            Toast.makeText(IdentifyActivity.this, "面部检测失败", Toast.LENGTH_SHORT).show();
            mPhotoFile = null;
        }
    }

}
