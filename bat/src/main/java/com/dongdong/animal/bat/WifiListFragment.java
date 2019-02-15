package com.dongdong.animal.bat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dongdong.animal.bat.model.WifiEntity;
import com.dongdong.animal.bat.utils.WifiController;
import com.dongdong.animal.bat.viewmodel.WifiViewModel;
import com.dongdong.animal.bat.viewmodel.exts.Act2;
import com.dongdong.animal.bat.widget.WifiPwdSetDialog;

import static com.dongdong.animal.bat.model.WifiEntity.WifiCipherType.WIFICIPHER_NOPASS;


public class WifiListFragment extends Fragment {

    private WifiViewModel vm;
    private TextView tvWifiIcon;
    private TextView tvSafeIcon;
    private TextView tvWifiName;
    private TextView tvWifiStatus;


    private RecyclerView recyclerView;
    private WifiAdapter adapter;

    private WifiStateReceiver mWiFiReceiver;
    private WifiPwdSetDialog dialog;

    private Context mContext;
    private int positio = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_wifi_box, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopReceiver();
    }

    private void initView(View v) {
        mContext = getContext();
        if (vm == null) {
            vm = new WifiViewModel();
        }
        tvWifiIcon = $(v, R.id.tvWifiIcon);
        tvSafeIcon = $(v, R.id.tvSafeIcon);
        tvWifiName = $(v, R.id.tvWifiName);
        tvWifiStatus = $(v, R.id.tvWifiStatus);
        recyclerView = $(v, R.id.recyWifiBox);


    }

    private void initData() {
        tvWifiStatus.setVisibility(View.GONE);
        tvWifiName.setGravity(Gravity.CENTER_VERTICAL);
        adapter = new WifiAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        refreshView();
    }

    private void initListener() {
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                WifiEntity wifiEntity = vm.getList().get(index);
                String ssid = wifiEntity.getSSID();
                WifiConfiguration wifiConfiguration = WifiController.getInstance(mContext)
                        .isExists(ssid);
                if (wifiConfiguration == null) {
                    reViewToConnect(index);
                    if (WIFICIPHER_NOPASS == vm.getList().get(positio).getCipherType()) {
                        wifiConfiguration = WifiController.getInstance(mContext).createWifiInfo(ssid
                                , "",
                                WIFICIPHER_NOPASS);
                        WifiController.getInstance(mContext).enableNetwork(wifiConfiguration, false);
                    } else {
                        showPwdDialog(ssid);
                    }
                } else if (wifiConfiguration.status == WifiConfiguration.Status.ENABLED) {
                    reViewToConnect(index);
                    WifiController.getInstance(mContext).enableNetwork(wifiConfiguration, false);
                } else if (wifiConfiguration.status == WifiConfiguration.Status.DISABLED) {
                    reViewToConnect(index);
                    showPwdDialog(ssid);
                }
            }
        });
    }

    public void reViewToConnect(int index) {
        if (positio != -1) {
            vm.getList().get(positio).setStatus("");
        } else {
            for (int i = 0; i < vm.getList().size(); i++) {
                vm.getList().get(i).setStatus("");
            }
        }
        vm.getList().get(index).setStatus("正在连接。。。");
        positio = index;
        adapter.notifyDataSetChanged();
    }


    private void showPwdDialog(String ssid) {
        if (positio != -1) {
            dialog = null;
            dialog = new WifiPwdSetDialog();
            dialog.setSSID(ssid);
            dialog.setListener(wifiListener);
            dialog.show(getFragmentManager(), "WifiPwdSetDialog");
        }

    }

    Act2<String, String> wifiListener = new Act2<String, String>() {
        @Override
        public void run(String ssid, String pwd) {
            if (positio != -1) {
                WifiConfiguration wifiConfiguration = WifiController.getInstance(mContext)
                        .createWifiInfo(ssid, pwd, vm.getList().get
                                (positio).getCipherType());
                if (wifiConfiguration != null) {
                    WifiController.getInstance(mContext).enableNetwork(wifiConfiguration, true);
                }
            }
        }
    };


    private void refreshView() {
        positio = -1;
        if (vm != null) {
            vm.clear();
        }
        vm.addVM(WifiController.getInstance(this.getContext()).getWifiViewMode());
        if (vm.getConnectEntity() != null) {
            tvWifiName.setText(vm.getConnectEntity().getSSID());
            if (vm.getConnectEntity().isSafe()) {
                tvSafeIcon.setVisibility(View.VISIBLE);
            } else {
                tvSafeIcon.setVisibility(View.GONE);
            }
        }
        adapter.setList(vm.getList());

    }

    private <T> T $(View v, int id) {
        return (T) v.findViewById(id);
    }

    /**
     * 注册wifi广播监听
     */
    private void sendReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mWiFiReceiver = new WifiStateReceiver();
        mContext.registerReceiver(mWiFiReceiver, filter);
    }

    /**
     * 注销wifi广播监听
     */
    private void stopReceiver() {
        if (mWiFiReceiver != null) {
            mContext.unregisterReceiver(mWiFiReceiver);
            mWiFiReceiver = null;
        }
    }

    public void upStatus(String text) {
        if (positio >= 0 && positio < vm.getList().size()) {
            vm.getList().get(positio).setStatus(text);
            adapter.notifyDataSetChanged();
        }
    }

    public class WifiStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 111);
                //对wifi的状态进行处理
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //wifi已经打开..
                        Log.d("WIFI广播", "wifi已经打开");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        //wifi打开中..
                        Log.d("WIFI广播", "wifi打开中。。。");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //wifi关闭了..
                        Log.d("WIFI广播", "wifi已经关闭");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        //wifi关闭中..
                        Log.d("WIFI广播", "wifi关闭中。。。");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.d("WIFI广播", "wifi未知状态");
                        //未知状态..
                        break;
                    default:
                        break;
                }

            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                switch (info.getDetailedState()) {
                    case IDLE:
                        Log.d("WIFI广播", "闲置状态");
                        break;
                    case FAILED:
                        Log.d("WIFI广播", "失败状态");
                        upStatus("连接失败");
                        break;
                    case BLOCKED:
                        Log.d("WIFI广播", "阻塞状态");
                        break;
                    case SCANNING:
                        Log.d("WIFI广播", "扫描状态");
                        break;
                    case CONNECTED:
                        Log.d("WIFI广播", "连接状态");
                        refreshView();
                        break;
                    case SUSPENDED:
                        Log.d("WIFI广播", "暂停状态");
                        break;
                    case CONNECTING:
                        Log.d("WIFI广播", "连接中。。。");
                        upStatus("正在连接。。。");
                        break;
                    case DISCONNECTED:
                        Log.d("WIFI广播", "断开连接：" + info.getDetailedState());
                        break;
                    case DISCONNECTING:
                        Log.d("WIFI广播", "开始断开连接。。。");
                        break;
                    case AUTHENTICATING:
                        Log.d("WIFI广播", "进行身份验证");
                        upStatus("进行身份验证");
                        break;
                    case OBTAINING_IPADDR:
                        Log.d("WIFI广播", "获取ip地址");
                        upStatus("获取ip地址");
                        break;
                    case VERIFYING_POOR_LINK:
                        Log.d("WIFI广播", "验证链接地址");
                        upStatus("验证链接地址");
                        break;
                    case CAPTIVE_PORTAL_CHECK:
                        Log.d("WIFI广播", "获得检查");
                        break;
                    default:
                        break;
                }
            }
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                }
            }
        }
    }

}
