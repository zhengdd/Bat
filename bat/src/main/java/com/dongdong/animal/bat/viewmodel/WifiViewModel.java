package com.dongdong.animal.bat.viewmodel;


import com.dongdong.animal.bat.model.WifiEntity;

import java.util.ArrayList;
import java.util.List;

public class WifiViewModel {

    WifiEntity connectEntity;

    List<WifiEntity> list;


    public WifiEntity getConnectEntity() {
        return connectEntity;
    }

    public void setConnectEntity(WifiEntity connectEntity) {
        this.connectEntity = connectEntity;
    }

    public List<WifiEntity> getList() {
        return list;
    }

    public void setList(List<WifiEntity> list) {
        this.list = list;
    }

    public void addWifiEntity(WifiEntity entity) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(entity);
    }

    public void addWifiEntityAll(List<WifiEntity> entities) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.addAll(entities);
    }

    public void clear() {
        connectEntity = null;
        if (list != null) {
            list.clear();
        }
    }

    public void addVM(WifiViewModel vm) {
        if (vm != null) {
            clear();
            if (vm.list != null) {
                addWifiEntityAll(vm.list);
            }
            if (vm.connectEntity != null) {
                setConnectEntity(vm.connectEntity);
            }
        }
    }

}
