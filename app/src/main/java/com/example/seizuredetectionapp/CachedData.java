package com.example.seizuredetectionapp;

import java.util.ArrayList;

public class CachedData {
    private static String userKey = "";
    public static String getUserKey() {
        return userKey;
    }
    public static void setUserKey(String s) {
        userKey = s;
    }

    static class CacheNode {
        public boolean exists;
        public int timestamp;
        public float value;
        public CacheNode(int stamp, float val) {
            timestamp = stamp;
            value = val;
            exists = false;
        }
    }

    public static ArrayList<CacheNode> EDAReadings = new ArrayList<>();
    public static ArrayList<CacheNode> HRReadings = new ArrayList<>();
    public static ArrayList<CacheNode> MMReadings = new ArrayList<>();


    public static void addEDA(int timestamp, float reading) {
        CacheNode n = new CacheNode(timestamp, reading);
        EDAReadings.add(n);
        while (EDAReadings.size() > 30) {
            EDAReadings.remove(0);
        }
    }

    public static void addHR(int timestamp, float reading) {
        CacheNode n = new CacheNode(timestamp, reading);
        HRReadings.add(n);
        while (HRReadings.size() > 30) {
            HRReadings.remove(0);
        }
    }

    public static void addMM(int timestamp, float reading) {
        CacheNode n = new CacheNode(timestamp, reading);
        MMReadings.add(n);
        while (MMReadings.size() > 30) {
            MMReadings.remove(0);
        }
    }

    public static ArrayList listForGraphType(RealtimeFragment.GraphType type) {
        switch (type) {
            case GraphType_EDA:
                return EDAReadings;
            case GraphType_HR:
                return HRReadings;
            case GraphType_MM:
                return MMReadings;
        }
        return null;
    }
}
