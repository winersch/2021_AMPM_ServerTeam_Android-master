package kr.ac.jbnu.sw.ServerTeamTestApplication.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalStorage {
    public static GlobalStorage globalStorage = null;

    private Map<String, Object> receiveServerDataMap = new HashMap<String, Object>();

    public static GlobalStorage getInstance() {
        if (globalStorage == null) {
            globalStorage = new GlobalStorage();
        }

        return globalStorage;
    }

    private GlobalStorage() { }

    public Map<String, Object> getReceiveServerDataMap() {
        return receiveServerDataMap;
    }

    public ArrayList<String> getReceiveServerDataMapKeySetArrayList() {
        if (receiveServerDataMap.size() > 0) {
            return new ArrayList<>(receiveServerDataMap.keySet());
        } else {
            return null;
        }
    }

    public void setReceiveServerDataMap(Map<String, Object> receiveServerDataMap) {
        this.receiveServerDataMap = receiveServerDataMap;
    }
}
