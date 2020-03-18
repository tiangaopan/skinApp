package com.tgp.skin_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import com.tgp.skin_core.R;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020-03-13 10:58
 */
public class SkinThemeUtils {

    private static int[] TYPEFACE_ATTRS = {
            R.attr.skinTypeface
    };

    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            R.attr.colorPrimaryDark
    };
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor, android.R.attr
            .navigationBarColor};


    public static int[] getResId(Context context, int[] attrids){
        int[] ints = new int[attrids.length];
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        for (int i = 0; i < typedArray.length(); i++) {
            ints[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return ints;
    }

    //替换状态栏
    public static void updateStatusBarColor(Activity activity) {
        //5.0 以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        //获取statusBarColor与navigationBarColor  颜色值
        int[] statusResId = getResId(activity, STATUSBAR_COLOR_ATTRS);
        //如果statusBarColor 配置颜色值， 就换肤
        if (statusResId[0] != 0) {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusResId[0]));
        } else {
            //获取colorPrimaryDark
            int resId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if(resId != 0){
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resId));
            }
        }

        if(statusResId[1] != 0){
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(statusResId[1]));
        }
    }

    public static Typeface getSkinTypeface(Activity activity) {
        //获取字体id
        int skinTypefaceId = getResId(activity, TYPEFACE_ATTRS)[0];
        return SkinResources.getInstance().getTypeface(skinTypefaceId);
    }
}
