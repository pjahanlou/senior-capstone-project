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
            exists = true;
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
    public static void addBlankEDA() {
        CacheNode n = new CacheNode(0, 0.f);
        n.exists = false;
        EDAReadings.add(n);
        while (EDAReadings.size() > 30) {
            EDAReadings.remove(0);
        }
    }
    public static void setEDAFalse(int idx) {
        CacheNode n = EDAReadings.get(idx);
        n.exists = false;
        EDAReadings.set(idx, n);
    }

    public static void addHR(int timestamp, float reading) {
        CacheNode n = new CacheNode(timestamp, reading);
        HRReadings.add(n);
        while (HRReadings.size() > 30) {
            HRReadings.remove(0);
        }
    }
    public static void addBlankHR() {
        CacheNode n = new CacheNode(0, 0.f);
        n.exists = false;
        HRReadings.add(n);
        while (HRReadings.size() > 30) {
            HRReadings.remove(0);
        }
    }
    public static void setHRFalse(int idx) {
        CacheNode n = HRReadings.get(idx);
        n.exists = false;
        HRReadings.set(idx, n);
    }

    public static void addMM(int timestamp, float reading) {
        CacheNode n = new CacheNode(timestamp, reading);
        MMReadings.add(n);
        while (MMReadings.size() > 30) {
            MMReadings.remove(0);
        }
    }
    public static void addBlankMM() {
        CacheNode n = new CacheNode(0, 0.f);
        n.exists = false;
        MMReadings.add(n);
        while (MMReadings.size() > 30) {
            MMReadings.remove(0);
        }
    }
    public static void setMMFalse(int idx) {
        CacheNode n = MMReadings.get(idx);
        n.exists = false;
        MMReadings.set(idx, n);
    }
}
