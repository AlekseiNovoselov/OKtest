package com.lexaloris.recyclevideoview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

public class Utils {

    SharedPreferences sPref;
    final static private String myPreferencesName = "myPref";

    public String readPreferences(Context context, String url, String aFalse) {
        sPref = context.getSharedPreferences(myPreferencesName, context.MODE_PRIVATE);
        String savedText = sPref.getString(url, aFalse);
        if (savedText.equals("true")) {
            return "true";
        }
        return aFalse;
    }

    public void savePreferences(Context context, String url, String aTrue) {
        sPref = context.getSharedPreferences(myPreferencesName, context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(url, aTrue);
        ed.apply();
    }

    public int convertDpToPixel(int screenWidthDp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = (float)screenWidthDp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    public ArrayList<Integer> calculateVisibleElements(RecyclerView recyclerView, int firstVisiblePosition, int lastVisibleposition) {
        ArrayList<Integer> visibleElements = new ArrayList<>();
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

        View fv = layoutManager.getChildAt(0);
        if (fv != null) {
            int offsetTop = fv.getTop();
            int height = fv.getHeight();
            if (height / 2 > Math.abs(offsetTop)) {
                visibleElements.add(firstVisiblePosition);
            }

        }
        View lv = layoutManager.getChildAt(lastVisibleposition-firstVisiblePosition-1);
        if (lv != null) {
            int offsetBottom = lv.getBottom();
            int height = lv.getHeight();
            if (height > Math.abs(offsetBottom)) {
                visibleElements.add(lastVisibleposition);
            }
        }
        for (int i = firstVisiblePosition + 1; i < lastVisibleposition; i++) {
            visibleElements.add(i);
        }

        return visibleElements;
    }
}
