package com.tgp.skin_core;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.tgp.skin_core.utils.SkinPreference;
import com.tgp.skin_core.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020-03-12 17:18
 */
public class SkinManager extends Observable {

    private Application application;

    private SkinManager(Application application) {
        this.application = application;
        //记录当前使用的皮肤
        SkinPreference.init(application);
        //皮肤包的资源管理类,用于从app/皮肤中加载资源
        SkinResources.init(application);

        application.registerActivityLifecycleCallbacks(new SkinActivityLifecycle());

        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public static SkinManager getInstance() {
        return sInstance;
    }

    private static SkinManager sInstance;

    public static void init(Application application) {
        if (sInstance == null) {
            synchronized (SkinManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinManager(application);
                }
            }
        }
    }

    public void loadSkin(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            //记录使用的皮肤，没有路径的话清空
            SkinPreference.getInstance().setSkin("");
            //清空资源管理器，皮肤属性等
            SkinResources.getInstance().reset();
        } else {
            try {
                //反射创建AssetManager对象
                AssetManager assetManager = AssetManager.class.newInstance();
                //拿到添加路径方法
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                //执行方法
                addAssetPath.invoke(assetManager, skinPath);
                //拿到当前应用的resources
                Resources resources = application.getResources();
                //拿到当前资源的维度以及配置 hdpi,xhdpi
                Resources resources1 = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
                //记录加载的皮肤包
                SkinPreference.getInstance().setSkin(skinPath);
                //获取外部apk包名
                PackageManager packageManager = this.application.getPackageManager();
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                String packageName = packageArchiveInfo.packageName;

                SkinResources.getInstance().applySkin(resources1, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //通知观察者进行刷新
        setChanged();
        notifyObservers();
    }

}
