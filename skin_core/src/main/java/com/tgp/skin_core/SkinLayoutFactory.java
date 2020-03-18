package com.tgp.skin_core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.tgp.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020-03-12 17:27
 */
public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {

    private static final String[] mClassPrefixList = {"android.widget.", "android.view.", "android.webkit.",};
    private static final Class[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();
    /**
     *  属性处理类
     */
    private SkinAttribute mSkinAttribute;
    private Activity mActivity;

    public SkinLayoutFactory(Activity activity, Typeface skinTypeface) {
        mActivity = activity;
        mSkinAttribute = new SkinAttribute(skinTypeface);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //反射获取
        View view = createViewFromTag(name, context, attrs);
        //自定义view
        if (view == null) {
            view = createView(name, context, attrs);
        }
        //筛选符合属性的attrs
        mSkinAttribute.load(view, attrs);
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
        //包含自定义控件
        if (-1 != name.indexOf(".")) {
            return null;
        }
        View view = null;
        for (String s : mClassPrefixList) {
            //mClassPrefixList[i] + name 组成全类名
            view = createView(s + name, context, attrs);
            if (view != null) {
                break;
            }
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                //通过全类名拿到class
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                //获取构造方法
                constructor = aClass.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (constructor != null) {
            try {
                return constructor.newInstance(context, attrs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    //观察到变化,更换皮肤
    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(mActivity);
        Typeface skinTypeface = SkinThemeUtils.getSkinTypeface(mActivity);
        mSkinAttribute.setTypeface(skinTypeface);
        mSkinAttribute.applySkin();
    }
}
