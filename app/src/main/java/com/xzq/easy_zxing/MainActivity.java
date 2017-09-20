package com.xzq.easy_zxing;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import utils.CommonUtil;
import utils.runtimepermissions.PermissionsManager;
import utils.runtimepermissions.PermissionsResultAction;
import zxing.EncodingHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBarCode, mBtnQrCode, mBtnGetCode;
    private EditText mEdtText;
    private TextView mTvText;
    private ImageView mIvTemp, mIvCode;
    private ProgressDialog progressDialog;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnBarCode = (Button) findViewById(R.id.btn_barcode);
        mBtnQrCode = (Button) findViewById(R.id.btn_qrcode);
        mBtnGetCode = (Button) findViewById(R.id.btn_getcode);
        mEdtText = (EditText) findViewById(R.id.edt_text);
        mTvText = (TextView) findViewById(R.id.tv_text);
        mIvCode = (ImageView) findViewById(R.id.iv_code);
        mIvTemp = (ImageView) findViewById(R.id.iv_temp);
        mBtnBarCode.setOnClickListener(this);
        mBtnGetCode.setOnClickListener(this);
        mBtnQrCode.setOnClickListener(this);
        mIvTemp.setOnClickListener(this);
        mIvCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                savePictureDiaolog();
                return true;
            }
        });


        requestPermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_barcode:
                onScanBarcode();
                break;
            case R.id.btn_qrcode:
                onScanQrcode();
                break;
            case R.id.btn_getcode:
                generateCode();
                break;
            case R.id.iv_temp:
                break;
            default:
                break;
        }

    }

    //条形码
    private void onScanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("扫描条形码");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();

//
//        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
//// 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//        integrator.setCaptureActivity(ScanActivity.class);
//        integrator.setPrompt("请扫描"); //底部的提示文字，设为""可以置空
//        integrator.setCameraId(0); //前置或者后置摄像头
//        integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
//        integrator.setBarcodeImageEnabled(true);
//        integrator.initiateScan();
    }

    //二维码
    private void onScanQrcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("扫描二维码");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();
    }

    //生成二维码
    private void generateCode() {
        if (mEdtText.getText().toString().equals("")) {
            Toast.makeText(this, "先输入内容", Toast.LENGTH_SHORT).show();
        } else {
            EncodingHelper encodingHelper = new EncodingHelper(MainActivity.this, mEdtText.getText().toString(), 350);
            Bitmap bitmap = encodingHelper.getBitmapWithSingOrBK(R.drawable.eyes, true);
            mTvText.setVisibility(View.GONE);
            mIvCode.setVisibility(View.VISIBLE);
            mIvCode.setImageBitmap(bitmap);
            mBitmap = bitmap;
        }
        mIvTemp.setImageBitmap(mBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show();
            } else {
                String msg = result.getContents();
                mIvCode.setVisibility(View.GONE);
                mTvText.setVisibility(View.VISIBLE);
                mTvText.setText(msg);
                Toast.makeText(this, "扫描成功，条码值: " + result.getContents(), Toast.LENGTH_LONG).show();
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents())));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /*
    * 保存图片对话框
    * */
    public void savePictureDiaolog() {
        final String[] stringItems = {"保存到本地"};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title("选择操作")//
                .titleTextSize_SP(13.5f)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //开启线程保存图片到本地
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("保存中...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        new TaskThread().start();
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    progressDialog.dismiss();
//                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        }

    };

    private class TaskThread extends Thread {
        public void run() {
            //保存图片到本地
            if (CommonUtil.isSDCardEnable()) {
                CommonUtil.saveImageToGallery(MainActivity.this, CommonUtil.drawableToBitmap(mIvCode.getDrawable()), "/ZQ/PIC/");
            } else {
//                Toast.makeText(MainActivity.this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }

    }


    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//                Log.d(TAG, "All permissions have been granted");
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
//                Log.d(TAG, "Permission  + permission +  has been denied");
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
