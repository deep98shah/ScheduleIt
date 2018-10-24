package edu.charusat.scheduleit;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by HP on 17-09-2018.
 */

public class Utils {
    public static void changeStatusBarColor(Activity activity,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            activity.getWindow().setStatusBarColor(
                    changeColor(ContextCompat.getColor(activity,color)));
        }

    }
    public static int changeColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

}
