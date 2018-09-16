package com.witness.user.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.witness.user.App;
import com.witness.user.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class ItemList {

    private static final String STORAGE = "shop";

    public static ItemList get() {
        return new ItemList();
    }

    private SharedPreferences storage;

    private ItemList() {
        storage = App.getInstance().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public List<Item> getData() {
        return Arrays.asList(
                new Item(1, "학교폭력 신고", "117", R.drawable.bullynotice),
                new Item(2, "위험상황 신고", "112", R.drawable.handcuffs),
                new Item(3, "응급상황 신고", "119", R.drawable.firealarm));
    }

    public boolean isRated(int itemId) {
        return storage.getBoolean(String.valueOf(itemId), false);
    }

    public void setRated(int itemId, boolean isRated) {
        storage.edit().putBoolean(String.valueOf(itemId), isRated).apply();
    }
}
