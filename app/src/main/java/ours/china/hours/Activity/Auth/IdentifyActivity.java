package ours.china.hours.Activity.Auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.BuildConfig;
import ours.china.hours.Common.Utils.FileCompressor;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Model.UploadIdentify;
import ours.china.hours.R;
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

                File file = mPhotoFile;
                File backFile = mPhotoBackFile;
                if (file != null && backFile != null){
                    Log.i("IdentifyActivity", "FileName = " + file.getName());
                    Log.i("IdentifyActivity", "backFile =" + backFile.getName());

                    List<RequestBody> mFile = new ArrayList<>();
                    List<MultipartBody.Part> fileToServer = new ArrayList<>();
                    List<RequestBody> fileName = new ArrayList<>();


                    mFile.add(RequestBody.create(MediaType.parse("image/*"), file));
                    mFile.add(RequestBody.create(MediaType.parse("image/*"), backFile));
                    fileToServer.add(MultipartBody.Part.createFormData("file", Global.mobile + file.getName(), mFile.get(0)));
                    fileToServer.add(MultipartBody.Part.createFormData("file", Global.mobile + backFile.getName(), mFile.get(1)));
                    fileName.add(RequestBody.create(MediaType.parse("text/plain"), file.getName()));
                    fileName.add(RequestBody.create(MediaType.parse("text/plain"), backFile.getName()));


//                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
//                    MultipartBody.Part fileToServer = MultipartBody.Part.createFormData("file", file.getName(), mFile);
//                    RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                    Global.showLoading(IdentifyActivity.this,"generate_report");
                    for (int j = 0; j < 2; j++){
                        try {

                            Call<UploadIdentify> call = apiInterface.uploadIdentify(fileToServer.get(j), fileName.get(j));
                            call.enqueue(new Callback<UploadIdentify>() {
                                @Override
                                public void onResponse(Call<UploadIdentify> call, Response<UploadIdentify> response) {
                                    Global.hideLoading();

                                    if (response.code() == 404){
                                        Toast.makeText(IdentifyActivity.this, "404", Toast.LENGTH_SHORT).show();
                                    }else if (response.code() == 422){
                                        Toast.makeText(IdentifyActivity.this, "422", Toast.LENGTH_SHORT).show();
                                    }else if (response.code() == 500){
                                        Toast.makeText(IdentifyActivity.this, "500", Toast.LENGTH_SHORT).show();
                                    }else if (response.code() == 200){
                                        String res = response.body().res;
                                        Log.i("Register", res.toString());
                                        Global.identify = getResources().getString(R.string.identify_success);
                                        Intent intent = new Intent(IdentifyActivity.this, PerfectInforActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(IdentifyActivity.this, "Response " + res.toString(), Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override public void onFailure(Call<UploadIdentify> call, Throwable t) {
                                    Global.hideLoading();
                                    Toast.makeText(IdentifyActivity.this, "fail2", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception e){
                            Log.i("IdentyActivity", e.getMessage());
                            Global.hideLoading();
                    }



                    }
                }
                else {
                    Toast.makeText(IdentifyActivity.this, R.string.insert_photo, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent(int i) {
        if (i == 0){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);

                    mPhotoFile = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_FACE);
                }
            }
        }else if (i == 1){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);

                    mPhotoBackFile = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_BACK);
                }
            }
        }

    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent(int i) {

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
                }
        }
    }
}
