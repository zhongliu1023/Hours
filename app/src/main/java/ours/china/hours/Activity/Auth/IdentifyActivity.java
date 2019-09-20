package ours.china.hours.Activity.Auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    int type = 0;
    SharedPreferencesManager sessionManager;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mCompressor = new FileCompressor(this);

        initUI();

        gotoBack();
        showBottomSetting();
        cancelImage();
        uploadIdentify();
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
                    Global.alert(IdentifyActivity.this, getResources().getString(R.string.app_name), "please set a image", getResources().getString(R.string.confirm));
                    return;
                }
                if (mPhotoBackFile == null){
                    Global.alert(IdentifyActivity.this, getResources().getString(R.string.app_name), "please set a image", getResources().getString(R.string.confirm));
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
        type = i;
        bottom_photo.setVisibility(View.INVISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        btnIdentifySubmit.setVisibility(View.VISIBLE);
        Intent intent = new Intent(IdentifyActivity.this, CameraCrop.class);
        if (i == 0){
            intent.putExtra("type", 0);
        }else if (i == 1){
            intent.putExtra("type", 1);
        }
        startActivityForResult(intent, ActivityCodes.CROP_IMAGE);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_TAKE_PHOTO_FACE) {
                    try {
                        mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                        Log.i("IdentifyActivity", "mPhotoFile = " + mPhotoFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyFace.setVisibility(View.INVISIBLE);
                    Glide.with(IdentifyActivity.this).load(mPhotoFile).apply(new RequestOptions().placeholder(R.drawable.id_obverse_icon)).into(imgIdentyFace);
                }else if (requestCode == REQUEST_TAKE_PHOTO_BACK){
                    try {
                        mPhotoBackFile = mCompressor.compressToFile(mPhotoBackFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyBack.setVisibility(View.INVISIBLE);
                    Glide.with(IdentifyActivity.this).load(mPhotoBackFile).apply(new RequestOptions().placeholder(R.drawable.id_back_icon)).into(imgIdentyBack);
                }else if (requestCode == REQUEST_GALLERY_PHOTO_FACE) {
                    Uri selectedImage = data.getData();
                    try {
                        mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyFace.setVisibility(View.INVISIBLE);
                    Glide.with(IdentifyActivity.this).load(mPhotoFile).apply(new RequestOptions().placeholder(R.drawable.id_obverse_icon)).into(imgIdentyFace);
                }else if (requestCode == REQUEST_GALLERY_PHOTO_BACK){
                    Uri selectedImage = data.getData();
                    try {
                        mPhotoBackFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgBtnIdentifyBack.setVisibility(View.INVISIBLE);
                    Glide.with(IdentifyActivity.this).load(mPhotoBackFile).apply(new RequestOptions().placeholder(R.drawable.id_back_icon)).into(imgIdentyBack);
                }else if (requestCode == ActivityCodes.CROP_IMAGE){
                    if (data.getStringExtra("cropImagePath") != null) {
//

                        String imagePath = data.getStringExtra("cropImagePath");
                        File imgFile = new File(imagePath);
                        if (type == 0){
                            mPhotoFile = imgFile;
                            imgIdentyFace.setImageURI(Uri.fromFile(imgFile));
                        }else{
                            mPhotoBackFile = imgFile;
                            imgIdentyBack.setImageURI(Uri.fromFile(imgFile));
                        }
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
                                    IdentifyActivity.super.onBackPressed();

                                } else {
                                    Toast.makeText(IdentifyActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
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

}
