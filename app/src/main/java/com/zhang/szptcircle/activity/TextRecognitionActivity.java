package com.zhang.szptcircle.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.zhang.szptcircle.R;

import java.util.List;

public class TextRecognitionActivity extends AppCompatActivity {

    private ImageView imgOCR;
    private Button btnOCR;
    private EditText tvInform;
    private Bitmap selectedBmp;
    private TextView tvBack;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        imgOCR = findViewById(R.id.imgOCR);
        btnOCR = findViewById(R.id.btnOCR);
        tvInform = findViewById(R.id.etInform);
        tvBack = findViewById(R.id.tv_back);
        tvInform.setSingleLine(false);


        imgOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TextRecognitionActivity.this);
                builder.setItems(new String[]{"拍照", "从相册中选择"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        //动态授权
                                        checkPermission();
                                        //调用相机
                                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        ContentValues values = new ContentValues();
                                        //把原图存到Uri中
                                        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                                        camera.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                                        startActivityForResult(camera,1000);

                                        break;
                                    case 1:
                                        //从相册中选择
                                        Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(picIntent,2000);
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });

        btnOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedBmp==null){
                    showToast("请拍照或选择要识别的图片");
                }
                else{
                    // Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
                    MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                            .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                            // Specify languages that can be recognized.
                            .setLanguage("zh")
                            .create();
                    MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
                    // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
                    MLFrame frame = MLFrame.fromBitmap(selectedBmp);
                    Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
                    task.addOnSuccessListener(new OnSuccessListener<MLText>() {
                        @Override
                        public void onSuccess(MLText text) {
                            // Processing for successful recognition.
                            String ocrText = displaySuccess(text);
                            tvInform.setText(ocrText.toCharArray(),0,ocrText.length());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            // Processing logic for recognition failure.
                        }
                    });

                }



            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private String displaySuccess(MLText mlText) {
        String result = "";
        List<MLText.Block> blocks = mlText.getBlocks();
        for (MLText.Block block : blocks) {
            for (MLText.TextLine line : block.getContents()) {
                result += line.getStringValue() + "\n";
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1000:
                if(resultCode==RESULT_OK && photoUri!=null)
                {

                    imgOCR.setImageURI(photoUri);
                    BitmapDrawable bd = (BitmapDrawable) imgOCR.getDrawable();
                    selectedBmp = bd.getBitmap();
                }
                break;
            case 2000:
                if(data!=null){
                    imgOCR.setImageURI(data.getData());
                    BitmapDrawable bd = (BitmapDrawable) imgOCR.getDrawable();
                    selectedBmp = bd.getBitmap();
                }

                break;
        }
    }

    //动态授权相机
    private void checkPermission()
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=0){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
        }
    }

    //授权结果回显
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permission,int[] grantResults)
    {
        if(requestCode==100){
            if(grantResults.length==0 || grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"请授予相机权限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //消息提示框
    public void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        //显示消息框时去除包名
        toast.setText(msg);
        toast.show();
    }

}