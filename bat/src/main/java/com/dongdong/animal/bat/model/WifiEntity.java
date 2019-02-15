package com.dongdong.animal.bat.model;


import static com.dongdong.animal.bat.model.WifiEntity.WifiCipherType.WIFICIPHER_INVALID;
import static com.dongdong.animal.bat.model.WifiEntity.WifiCipherType.WIFICIPHER_NOPASS;
import static com.dongdong.animal.bat.model.WifiEntity.WifiCipherType.WIFICIPHER_WEP;
import static com.dongdong.animal.bat.model.WifiEntity.WifiCipherType.WIFICIPHER_WPA;

/**
 * Created by dongdongzheng on 2018/12/26.
 */

public class WifiEntity {

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_EAP, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    private String SSID;

    private String BSSID;

    private int level;

    private int iconId;

    private String status;

    private String safeType;

    private boolean isSafe;

    private WifiCipherType cipherType;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getSafeType() {
        return safeType;
    }

    public void setSafeType(String safeType) {
        this.safeType = safeType;
        if (safeType.contains("WPA")) {
            cipherType = WIFICIPHER_WPA;
        } else if (safeType.contains("WEP")) {
            cipherType = WIFICIPHER_WEP;
        } else if (safeType.contains("EAP")) {
            cipherType = WIFICIPHER_INVALID;
        } else {
            cipherType = WIFICIPHER_NOPASS;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public WifiCipherType getCipherType() {
        return cipherType;
    }
}
