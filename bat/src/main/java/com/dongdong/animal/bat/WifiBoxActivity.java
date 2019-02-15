package com.dongdong.animal.bat;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dongdong.animal.bat.utils.WifiController;


public class WifiBoxActivity extends AppCompatActivity {

    private CheckBox cbWifiCheck;
    private WifiListFragment fragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_box);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        fm = getSupportFragmentManager();
        cbWifiCheck = $(R.id.cbWifiCheck);



    }

    private void initData() {
        if (WifiController.getInstance(this).isWifiOpen()) {
            cbWifiCheck.setChecked(true);
            showFragment();
        } else {
            cbWifiCheck.setChecked(false);
            removeFragmnet();
        }


    }

    private void initListener() {
        cbWifiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("切换Wi-Fi状态", "" + isChecked);
                WifiController.getInstance(WifiBoxActivity.this).turnSwitch(isChecked);
                if (isChecked) {
                    showFragment();
                } else {
                    removeFragmnet();
                }
            }
        });

    }

    private void showFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment == null) {
            fragment = new WifiListFragment();
        }
        if (!fragment.isAdded()) {
            ft.add(R.id.flWifiBox, fragment, WifiListFragment.class.getName());
            ft.commit();
        }

    }

    private void removeFragmnet() {
        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            if (fragment.isAdded()) {
                ft.remove(fragment).commit();
            }
        }
    }

    private <T> T $(int id) {
        return (T) findViewById(id);
    }


}
