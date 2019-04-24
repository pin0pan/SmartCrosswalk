package com.example.hs.smartcrosswalk;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingActivity extends AppCompatActivity {

        private static final String TAG = "Setting";
        private static final int ENABLE_BLUETOOTH_REQUEST = 17;

        RadioGroup shake_rg, client_rg;
        Button set_confirm;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        shake_rg = findViewById(R.id.shake_rg);
        client_rg = findViewById(R.id.client_rg);
        set_confirm = findViewById(R.id.set_confirm_btn);

        set_confirm.setOnClickListener(v -> {
            int shake_id = shake_rg.getCheckedRadioButtonId();
            int client_id = client_rg.getCheckedRadioButtonId();

            switch(shake_id) {
                case R.id.shake_on: {
                    Log.d(TAG, "shake_on");
                    startService(new Intent(getApplicationContext(), ShakeService.class));
                    break;
                }
                case R.id.shake_off: {
                    Log.d(TAG, "shake_off");
                    stopService(new Intent(getApplicationContext(), ShakeService.class));
                    break;
                }
            }
            switch(client_id) {
                case R.id.client_on: {
                    Log.d(TAG, "client_on");
                    startBleService();
                    break;
                }
                case R.id.client_off: {
                    Log.d(TAG, "client_off");
                    stopService(new Intent(SettingActivity.this, GattService.class));
                    break;
                }
            }
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    /**/
    private void startBleService() {

        /*시스템 서비스에서 블루투스 서비스를 가져와 블루투스 매니저에 저장*/
        BluetoothManager bluetoothManager = (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);

        /*블루투스 매니저에서 getAdapter()를 이용하여 장치의 기본 어댑터를 가져와 블루투스 어댑터에 저장
        블루투스 어댑터가 탐색, 연결을 담당*/
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        /*블루투스를 지원하지 않는 장치*/
        if (bluetoothAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("BLE 실패");
            builder.setMessage("BLE를 지원하지 않습니다.").setPositiveButton("확인", (dialog, which) -> {
                finish();
            });
            builder.show();

        /*블루투스 활성화 여부 확인*/
        } else if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            SettingActivity.this.startActivityForResult(enableBtIntent,ENABLE_BLUETOOTH_REQUEST);     //사용자에게 블루투스 활성 여부를 묻는 다이얼로그

        } else {
            start();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK) {
                start();    //블루투스 활성화 성공
            } else {
                finish();   //블루투스 활성화 실패 RESULT_CANCELED
            }
        }
    }

    /*GATTService 시작*/
    private void start() {
        startService(new Intent(this, GattService.class));
        finish();
    }

}
