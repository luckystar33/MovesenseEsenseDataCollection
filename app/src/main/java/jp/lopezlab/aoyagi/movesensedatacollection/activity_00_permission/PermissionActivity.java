package jp.lopezlab.aoyagi.movesensedatacollection.activity_00_permission;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import jp.lopezlab.aoyagi.movesensedatacollection.BuildConfig;
import jp.lopezlab.aoyagi.movesensedatacollection.R;
import jp.lopezlab.aoyagi.movesensedatacollection.activity_01_connection.ConnectionActivity;

public class PermissionActivity extends AppCompatActivity {

    // Request code（任意の値でOK）
    private static final int REQUEST_ENABLE_BLUETOOTH = 1; // Bluetooth 設定のアクティビティに渡す
    private static final int REQUEST_CODE_BLUETOOTH_SCAN = 2;
    private static final int REQUEST_CODE_BLUETOOTH_CONNECT = 3;
    private static final int REQUEST_CODE_FINE_LOCATION = 4;
    private static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 5;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        checkPermissions();
    }

    private void checkPermissions() {
        Log.d("PermissionActivity", "checkPermissions");
        if (checkBluetoothScanPermission() &&
                checkBluetoothConnectPermission() &&
                checkFineLocationPermission() &&
                checkWriteExternalStoragePermission() &&
                checkBluetooth() &&
                checkManageExternalStoragePermission()
        ) {
            startActivity(new Intent(PermissionActivity.this, ConnectionActivity.class));
            finish();
        }
    }

    private boolean checkBluetoothScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public boolean checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_BLUETOOTH_SCAN:
            case REQUEST_CODE_BLUETOOTH_CONNECT:
            case REQUEST_CODE_FINE_LOCATION:
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private boolean checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // デバイスが Bluetooth を搭載しているかチェック
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Bluetooth が現在有効かどうかチェックし、無効であれば有効にするダイアログを表示
        assert bluetoothAdapter != null;
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkManageExternalStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    checkPermissions();
                }
            }
        }
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            checkPermissions();
        }
    }
}