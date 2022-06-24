package com.omsvision;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class ScanDataActivity extends BaseActivity {
    Spinner spinqrtype, spinbarcodetype;
    ImageView btnqrscan, btnbarcodescan;
    String condition = "", type = "";
    Typeface myFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_data);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setText("Scan");

        myFont = ResourcesCompat.getFont(this, R.font.poppins_medium);

        spinqrtype = (Spinner) findViewById(R.id.spinqrtype);
        spinbarcodetype = (Spinner) findViewById(R.id.spinbarcode);
        btnqrscan = (ImageView) findViewById(R.id.btnqr);
        btnbarcodescan = (ImageView) findViewById(R.id.btnbarcode);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.QRScantype));
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinqrtype.setAdapter(dataAdapter);

        setSpinbarcodetype(getResources().getStringArray(R.array.BarcodeScantypenone));

        spinqrtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                type = parent.getItemAtPosition(position).toString();
                if (type.equals(getResources().getString(R.string.scanreturn))) {
                    setSpinbarcodetype(getResources().getStringArray(R.array.BarcodeScantype));
                } else {
                    setSpinbarcodetype(getResources().getStringArray(R.array.BarcodeScantypenone));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spinbarcodetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                condition = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        btnqrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ScanDataActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanDataActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

                } else {
                    Intent intent = new Intent(ScanDataActivity.this, Barcode_Scanner.class);
                    intent.putExtra("condition", condition);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }

            }
        });
        btnbarcodescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanDataActivity.this, BarcodeEntryActivity.class);
                intent.putExtra("condition", condition);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    public void setSpinbarcodetype(String[] data) {
        // Creating adapter for spinner
        ArrayAdapter<String> barcodeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        // Drop down layout style - list view with radio button
        barcodeAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        spinbarcodetype.setAdapter(barcodeAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ScanDataActivity.this, Barcode_Scanner.class);
                    intent.putExtra("condition", condition);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }

             }
        }
    }
}