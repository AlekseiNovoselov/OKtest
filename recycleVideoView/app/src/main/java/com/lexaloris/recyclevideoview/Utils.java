package com.lexaloris.recyclevideoview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {

    SharedPreferences sPref;

    public String readPreferences(Context context, String url, String aFalse) {
        sPref = context.getSharedPreferences("myPref", context.MODE_PRIVATE);
        String savedText = sPref.getString(url, aFalse);
        Log.d("Utils", "savedText " + savedText);
        if (savedText.equals("true")) {
            return "true";
        }
        return aFalse;
    }

    public void savePreferences(Context context, String url, String aTrue) {
        sPref = context.getSharedPreferences("myPref", context.MODE_PRIVATE);
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
}
