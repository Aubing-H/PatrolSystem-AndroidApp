package com.example.patrolapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.patrolapplication.network.ApiService;
import com.example.patrolapplication.network.response.UniformResponse;
import com.example.patrolapplication.utils.LocalDataController;
import com.example.patrolapplication.utils.MyData;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccidentActivity extends Activity {
    private static final String TAG = "## AccidentActivity ## ";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imageView;
    private EditText locNameEdt;
    private EditText accidentDetailEdt;
    private Spinner mySpinner;

    private String currentLocId;
    private String currentLocName;
    private List<String> abnormalItems;
    private String currentAbnormalItem;

    private String currentPhotoPath;
    private File photoFile;
    private String lastPhotoPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);

        imageView = findViewById(R.id.photoImg);
        locNameEdt = findViewById(R.id.accident_loc_edit);
        accidentDetailEdt = findViewById(R.id.accident_detail_edit);

        Intent intent = getIntent();
        currentLocId = intent.getStringExtra("currentLocId");
        currentLocName = intent.getStringExtra("currentLocName");
        locNameEdt.setText(currentLocName);
        String items = intent.getStringExtra("abnormalItems");
        abnormalItems = new Gson().fromJson(items, new TypeToken<List<String>>(){}.getType());
        currentAbnormalItem = MyData.otherItem;

        initSpinner();
    }

    private void initSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_selected, abnormalItems);
        adapter.setDropDownViewResource(R.layout.item_dropdown);
        mySpinner = findViewById(R.id.abnormalSpinner);
        mySpinner.setPrompt("请选择");
        mySpinner.setAdapter(adapter);
        mySpinner.setSelection(0);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mySpinner.setSelection(position);
                currentAbnormalItem = abnormalItems.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setPic() {
        // 获取图片布局的长和宽
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // 调整放缩比例
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = Math.min(photoW/targetW, photoH/targetH);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 获取一个小型的bitmap
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
            setPic();
        }
    }

    // 创建Android端保存图片的路径
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "patrolApp_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, /* prefix */".jpg",/* suffix */
                getCacheDir());
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        photoFile = image;
        return image;
    }

    public void onPhotoButtonClick(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.patrolapplication.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private String getAbnormalRecord(){
        Map<String, Object> map = new HashMap<>();
        map.put("username", LocalDataController.getUsername(getApplicationContext()));
        map.put("patrolLocId", currentLocId);
        map.put("time", SyncData.dateToString(new Date()));
        map.put("abnormalItem", currentAbnormalItem);
        map.put("abnormalDetail", accidentDetailEdt.getText().toString());
        String res = new Gson().toJson(map);
        System.out.println(TAG + "MAP: " + res);
        return res;
    }

    /** 将图片传输给后端 */
    public void onUploadButtonClick(View v) throws IOException{
        if(currentPhotoPath == null){
            MyData.makeToast(getApplicationContext(), "请拍摄故障照片");
            return;
        }
        if(lastPhotoPath.equals(currentPhotoPath)){
            MyData.makeToast(getApplicationContext(), "已经发送该图片");
            return;
        }

        if(photoFile.length() < 1){
            MyData.makeToast(getApplicationContext(), "图片未保存，请重新拍照");
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SyncData.ipAddress)
                .client(SyncData.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        RequestBody fileByte = RequestBody.create(MediaType.parse("multipart/*"), photoFile);
        MultipartBody.Part partPhoto = MultipartBody.Part.createFormData("photo",
                photoFile.getName(), fileByte);

        String data = getAbnormalRecord();
        apiService.uploadPhoto(data, partPhoto).enqueue(new Callback<UniformResponse>() {
            @Override
            public void onResponse(Call<UniformResponse> call, Response<UniformResponse> response) {
                if(response.body() != null){
                    MyData.makeToast(getApplicationContext(), response.body().msg);
                    if(response.body().state == SyncData.STATE_OK) {
                        lastPhotoPath = currentPhotoPath;
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<UniformResponse> call, Throwable t) {
                MyData.timeoutToast(getApplicationContext());
            }
        });
    }
}
