package com.pzj.iconsearchview.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.pzj.iconsearchview.R;
import java.util.HashMap;

/**
 * 带图标的搜索框
 *
 * @author PengZhenjin
 * @date 2017-4-1
 */
public class IconSearchView extends LinearLayout {

    /**
     * 储存已添加的图标view
     * key：tag
     * value：view
     */
    private HashMap<String, View> mViewMap = new HashMap<>();

    private HorizontalScrollView mHorizontalScrollView; // 横向滚动条
    private LinearLayout         mIconContainerLayout;  // 图标容器布局文件
    private ImageView            mSearchIv; // 搜索框提示图标
    private EditText             mSearchEt; // 搜索框

    private OnIconRemoveListener mOnIconRemoveListener; // 图标移除监听器

    private View mDeleteView;    // 标记删除的view

    private boolean mChangedText;   // 搜索框内容是否有变化

    private int mClickDeleteNum = 0;    // 点击输入框的删除按钮次数

    private TextWatcher mTextWatcher;   // 文本观察者

    public IconSearchView(Context context) {
        this(context, null);
    }

    public IconSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    /**
     * 初始化视图
     *
     * @param context
     */
    private void initView(Context context) {
        View rootView = View.inflate(context, R.layout.icon_search_view, this);
        this.mHorizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.horizontal_sv);
        this.mIconContainerLayout = (LinearLayout) rootView.findViewById(R.id.icon_container_layout);
        this.mSearchIv = (ImageView) rootView.findViewById(R.id.search_iv);
        this.mSearchEt = (EditText) rootView.findViewById(R.id.search_et);
        this.initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        this.mSearchEt.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mClickDeleteNum++;
                if (mClickDeleteNum == 2) {
                    mClickDeleteNum = 0;
                    if (keyCode == KeyEvent.KEYCODE_DEL && TextUtils.isEmpty(mSearchEt.getText().toString()) && mViewMap.size() != 0 && !mChangedText) {    // 删除按钮
                        View lastIconView = mIconContainerLayout.getChildAt(mIconContainerLayout.getChildCount() - 1);  // 最后一个图标view
                        if (mDeleteView == lastIconView) {
                            removeViewAction((String) mDeleteView.getTag());
                        }
                        else {
                            mDeleteView = lastIconView;
                            lastIconView.setAlpha(0.5f);   // 设置alpha
                        }
                        return true;
                    }
                }
                mChangedText = false;
                return false;
            }
        });

        this.mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mTextWatcher != null) {
                    mTextWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && mDeleteView != null) {
                    mDeleteView.setAlpha(1.0f);
                    mDeleteView = null;
                }

                mChangedText = true;

                if (mTextWatcher != null) {
                    mTextWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcher != null) {
                    mTextWatcher.afterTextChanged(s);
                }
            }
        });
    }

    /**
     * 添加图标view
     *
     * @param iconView 图标view
     * @param iconTag  图标view的标记
     *
     * @return 如果添加成功则返回true，如果已经存在了返回false
     */
    public boolean addIconView(View iconView, final String iconTag) {
        if (this.mDeleteView != null) {
            this.mDeleteView.setAlpha(1.0f);
            this.mDeleteView = null;
        }

        if (this.mViewMap.containsKey(iconTag)) {
            return false;
        }

        // 将要添加的view绑定tag
        iconView.setTag(iconTag);

        // 将图标view存入map中
        this.mViewMap.put(iconTag, iconView);

        // 添加到布局容器中展示
        this.mIconContainerLayout.addView(iconView);

        this.mIconContainerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 最后一个显示最新的一个
                mHorizontalScrollView.smoothScrollTo(mIconContainerLayout.getMeasuredWidth(), 0);
                mIconContainerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        if (this.mViewMap.size() > 0) {
            this.mSearchIv.setVisibility(View.GONE);    // 隐藏搜索框提示图标
        }

        iconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeViewAction(iconTag);
                if (mOnIconRemoveListener != null) {
                    mOnIconRemoveListener.onIconRemoved(IconSearchView.this, iconTag);
                }
            }
        });

        return true;
    }

    /**
     * 移除图标view
     *
     * @param iconTag 图标view的标记
     *
     * @return 如果移除成功则返回true，如果已经存在了返回false
     */
    public boolean removeIconView(String iconTag) {
        View view = this.mViewMap.get(iconTag);
        if (view == null) {
            return false;
        }
        this.removeViewAction(iconTag);
        return true;
    }

    /**
     * 移除图标view的动作
     *
     * @param iconTag 图标view的标记
     */
    private void removeViewAction(String iconTag) {
        View view = this.mViewMap.get(iconTag);

        if (this.mDeleteView == view) { // 将标记要删除的view置空
            this.mDeleteView = null;
        }

        this.mIconContainerLayout.removeView(view);

        this.mViewMap.remove(iconTag);

        if (this.mViewMap.size() == 0) {
            this.mSearchIv.setVisibility(View.VISIBLE); // 显示搜索框提示图标
        }
    }

    /**
     * 图标移除监听器
     */
    public interface OnIconRemoveListener {
        void onIconRemoved(View v, String tag);
    }

    /**
     * 设置图标移除监听器
     *
     * @param listener
     */
    public void setOnIconRemoveListener(OnIconRemoveListener listener) {
        this.mOnIconRemoveListener = listener;
    }

    /**
     * 添加TextWatcher
     *
     * @param watcher
     */
    public void addTextChangedListener(TextWatcher watcher) {
        this.mTextWatcher = watcher;
    }

    /**
     * 获得当前图标view的总数
     *
     * @return
     */
    public int getIconViewCount() {
        return mIconContainerLayout.getChildCount();
    }

    /**
     * 搜索框前面的提示图标
     *
     * @return
     */
    public ImageView getSearchHintIcon() {
        return this.mSearchIv;
    }

    /**
     * 获取搜索框
     *
     * @return
     */
    public EditText getSearchEditText() {
        return this.mSearchEt;
    }

    /**
     * 设置搜索框文本字体大小
     *
     * @param size
     */
    public void setSearchTextSize(float size) {
        if (this.mSearchEt != null) {
            this.mSearchEt.setTextSize(size);
        }
    }

    /**
     * 设置搜索框文本字体颜色
     *
     * @param color
     */
    public void setSearchTextColor(@ColorInt int color) {
        if (this.mSearchEt != null) {
            this.mSearchEt.setTextColor(color);
        }
    }

    /**
     * 设置搜索框文本提示字体颜色
     *
     * @param color
     */
    public void setSearchHintTextColor(@ColorInt int color) {
        if (this.mSearchEt != null) {
            this.mSearchEt.setHintTextColor(color);
        }
    }

    /**
     * 设置搜索框前面的提示图标
     *
     * @param resId
     */
    public void setSearchHintIcon(@DrawableRes int resId) {
        if (this.mSearchIv != null) {
            this.mSearchIv.setImageResource(resId);
        }
    }

    /**
     * 隐藏搜索框前面的提示图标
     *
     * @return
     */
    public void hideSearchHintIcon() {
        if (this.mSearchIv != null) {
            this.mSearchIv.setVisibility(GONE);
        }
    }

    /**
     * 显示搜索框前面的提示图标
     *
     * @return
     */
    public void showSearchHintIcon() {
        if (this.mSearchIv != null) {
            this.mSearchIv.setVisibility(VISIBLE);
        }
    }
}
