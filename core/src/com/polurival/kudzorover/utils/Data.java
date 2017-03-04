package com.polurival.kudzorover.utils;

import com.boontaran.DataManager;

/**
 * Created by Polurival
 * on 04.03.2017.
 */

public class Data {

    private DataManager manager;

    private static final String PREFERENCE_NAME = "lunar_rover_data";
    private static final String PROGRESS_KEY = "progress_key";

    public Data() {
        manager = new DataManager(PREFERENCE_NAME);
    }

    public int getProgress() {
        return manager.getInt(PROGRESS_KEY, 1);
    }

    public void setProgress(int progress) {
        manager.saveInt(PROGRESS_KEY, progress);
    }
}
