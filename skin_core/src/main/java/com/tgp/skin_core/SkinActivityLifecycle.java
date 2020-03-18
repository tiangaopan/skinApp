package com.tgp.skin_core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.tgp.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020-03-12 17:25
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    HashMap<Activity, SkinLayoutFactory> mFactoryMap = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //更新状态栏
        SkinThemeUtils.updateStatusBarColor(activity);
        //更新字体
        Typeface skinTypeface = SkinThemeUtils.getSkinTypeface(activity);

        try {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);

            Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            mFactorySet.setBoolean(layoutInflater, false);

            SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity, skinTypeface);
            //添加自定义创建view 工厂
            layoutInflater.setFactory2(skinLayoutFactory);
            //注册观察者
            SkinManager.getInstance().addObserver(skinLayoutFactory);
            mFactoryMap.put(activity, skinLayoutFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //删除观察者
        SkinLayoutFactory remove = mFactoryMap.remove(activity);
        SkinManager.getInstance().deleteObserver(remove);
    }
}
