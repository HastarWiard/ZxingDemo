package com.xzq.easy_zxing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScanActivity extends CaptureActivity {
    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_scan);
        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
    }
}
