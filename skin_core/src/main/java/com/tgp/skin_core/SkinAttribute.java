package com.tgp.skin_core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tgp.skin_core.utils.SkinResources;
import com.tgp.skin_core.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewCompat;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020-03-13 10:43
 */
public class SkinAttribute {

    private static final List<String> sAttribute = new ArrayList<>();

    static {
        sAttribute.add("background");
        sAttribute.add("src");

        sAttribute.add("textColor");
        sAttribute.add("drawableLeft");
        sAttribute.add("drawableTop");
        sAttribute.add("drawableRight");
        sAttribute.add("drawableBottom");
    }

    private List<SkinView> skinViews = new ArrayList<>();
    private Typeface mSkinTypeface;

    public SkinAttribute(Typeface skinTypeface) {
        mSkinTypeface = skinTypeface;
    }

    public void load(View view, AttributeSet attrs) {
        List<SkinPain> skinPains = new ArrayList<>();
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            //获取属性名字
            String attributeName = attrs.getAttributeName(i);
            if (sAttribute.contains(attributeName)) {
                //获取属性值
                String attributeValue = attrs.getAttributeValue(i);
                //写死的情况
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                //系统自带的 attributeValue = "?1313123"
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    //存在数组的可能
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    //attributeValue = "@21321213"
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId != 0) {
                    SkinPain skinPain = new SkinPain(attributeName, resId);
                    skinPains.add(skinPain);
                }
            }
        }
        if (!skinPains.isEmpty() || view instanceof TextView || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, skinPains);
            skinView.applySkin(mSkinTypeface);
            skinViews.add(skinView);
        }
    }

    public void applySkin() {
        for (SkinView skinView : skinViews) {
            skinView.applySkin(mSkinTypeface);
        }
    }

    public void setTypeface(Typeface skinTypeface) {
        this.mSkinTypeface = skinTypeface;
    }


    static class SkinView {
        View view;
        List<SkinPain> skinPains;

        public SkinView(View view, List<SkinPain> skinPains) {
            this.view = view;
            this.skinPains = skinPains;
        }

        public void applySkin(Typeface typeface) {
            applyTypeface(typeface);
            applySkinSupport();
            for (SkinPain skinPair : skinPains) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        //Color
                        if (background instanceof Integer) {
                            view.setBackgroundColor((Integer) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }

        //自定义view替换
        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }

        //字体换肤
        private void applyTypeface(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    static class SkinPain {
        String attributeName;
        int resId;

        public SkinPain(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
