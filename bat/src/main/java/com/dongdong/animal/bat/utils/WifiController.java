package com.dongdong.animal.bat.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;


import com.dongdong.animal.bat.R;
import com.dongdong.animal.bat.model.WifiEntity;
import com.dongdong.animal.bat.viewmodel.WifiViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiController {


    private static volatile WifiController instance;

    private Context appContext;
    private WifiManager wm;
    private ConnectivityManager cm;

    WifiController(Context context) {
        appContext = context.getApplicationContext();
    }

    public static WifiController getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiController.class) {
                if (instance == null) {
                    instance = new WifiController(context);
                }
            }
        }

        return instance;
    }


    private WifiManager getWifiManager() {
        if (wm == null) {
            wm = (WifiManager) appContext.getSystemService(WIFI_SERVICE);
        }
        return wm;
    }

    private ConnectivityManager getConnectivityManager() {
        if (cm == null) {
            cm = (ConnectivityManager) appContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return cm;
    }

    public boolean isWifiOpen() {
        NetworkInfo activeNetInfo = getConnectivityManager().getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public void turnSwitch(boolean start) {
        if (start && !getWifiManager().isWifiEnabled()) {
            getWifiManager().setWifiEnabled(start);
        } else if (start != getWifiManager().isWifiEnabled()) {
            getWifiManager().setWifiEnabled(start);
        }
    }

    public WifiInfo getWifiInfo() {
        if (getWifiManager() != null) {
            WifiInfo winfo = getWifiManager().getConnectionInfo();
            if (winfo != null) {
                return winfo;
            } else {
                return null;
            }
        }
        return null;
    }

    private List<ScanResult> getWifiList() {

        List<ScanResult> scanWifiList = getWifiManager().getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        return wifiList;
    }

    public WifiViewModel getWifiViewMode() {

        WifiViewModel vm = new WifiViewModel();

        WifiInfo info = getWifiInfo();
        String ssid = null;
        if (info != null) {
            ssid = cleanSSID(info.getSSID());
        }

        List<ScanResult> list = getWifiList();
        for (ScanResult result : list) {
            WifiEntity entity = new WifiEntity();
            entity.setSSID(cleanSSID(result.SSID));
            entity.setBSSID(result.BSSID);
            entity.setLevel(result.level);
            entity.setSafeType(getSafeMode(result.capabilities));
            entity.setSafe(!TextUtils.isEmpty(entity.getSafeType()));
            entity.setIconId(getRssiIcon(result.level));

            if (!TextUtils.isEmpty(ssid) && ssid.equals(entity.getSSID())) {
                entity.setStatus(appContext.getResources().getString(R.string.hint_status_connect));
                vm.setConnectEntity(entity);
            }

            vm.addWifiEntity(entity);

        }
        return vm;
    }

    private String cleanSSID(String ssid) {
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            return ssid.replace("\"", "");
        } else {
            return ssid;
        }
    }

    public String getSafeMode(String mode) {
        if (TextUtils.isEmpty(mode)) {
            return "";
        }
        String status = "";
        if (mode.contains("WPA2")) {
            status = "WPA2";
        } else if (mode.contains("WPA")) {
            status = "WPA";
        } else if (mode.contains("WEP")) {
            status = "WEP";
        } else if (mode.contains("EAP")) {
            status = "EAP";
        } else {
            status = "ESS";
        }
        if (!TextUtils.isEmpty(status) && !"ESS".equals(status)) {
            return appContext.getResources().getString(R.string.hint_safe_mode).replace("%s",
                    status);
        }
        return "";
    }

    public static int getRssiIcon(int rssi) {

        if (rssi <= 0 && rssi >= -50) {
            return R.drawable.icon_wifi3;
        } else if (rssi < -50 && rssi >= -70) {
            return R.drawable.icon_wifi2;
        }
        if (rssi < -70 && rssi >= -90) {
            return R.drawable.icon_wifi1;
        }
        if (rssi < -90) {
            return R.drawable.icon_wifi0;
        } else {
            return R.drawable.icon_wifi0;
        }


    }


    /**
     * 获取连接配置信息
     *
     * @param SSID
     * @return
     */
    public WifiConfiguration isExists(String SSID) {
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }

        return null;
    }

    /**
     * 进行连接
     *
     * @param config
     */
    public void enableNetwork(WifiConfiguration config, boolean isUp) {
        int networkId = config.networkId;
        if (networkId == -1) {
            networkId = wm.addNetwork(config);
        } else if (isUp) {
            networkId = wm.updateNetwork(config);
        }
        wm.enableNetwork(networkId, true);
    }


    public WifiConfiguration createWifiInfo(String SSID, String password, WifiEntity.WifiCipherType
            type) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfiguration = isExists(SSID);
        if (tempConfiguration != null) {
            configuration.networkId = tempConfiguration.networkId;
        }

        if (type == WifiEntity.WifiCipherType.WIFICIPHER_NOPASS) {
            configuration.wepKeys[0] = "";
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            configuration.wepTxKeyIndex = 0;
        }

        if (type == WifiEntity.WifiCipherType.WIFICIPHER_WEP) {
            configuration.hiddenSSID = true;
            configuration.wepKeys[0] = "\"" + password + "\"";
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            configuration.wepTxKeyIndex = 0;
        }
        if (type == WifiEntity.WifiCipherType.WIFICIPHER_WPA) {
            configuration.preSharedKey = "\"" + password + "\"";
            configuration.hiddenSSID = true;
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.status = WifiConfiguration.Status.ENABLED;
        }

        return configuration;
    }


}
