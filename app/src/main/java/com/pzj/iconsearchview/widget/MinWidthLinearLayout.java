package com.pzj.iconsearchview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 保留最小宽度的LinearLayout
 *
 * @author PengZhenjin
 * @date 2017-4-1
 */
public class MinWidthLinearLayout extends LinearLayout {

    /**
     * 总宽度
     */
    private int mTotalWidth;

    /**
     * 编辑框保留的最小宽度
     */
    private int mEditTextMinWidth;

    public MinWidthLinearLayout(Context context) {
        super(context);
    }

    public MinWidthLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinWidthLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mTotalWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.mEditTextMinWidth = (int) (this.mTotalWidth * 0.2);

        if (getChildCount() == 2) {
            View childView0 = getChildAt(0);
            View childView1 = getChildAt(1);

            int childView0Width = childView0.getMeasuredWidth();

            if (this.mTotalWidth - childView0Width < this.mEditTextMinWidth) {
                childView0.measure(MeasureSpec.makeMeasureSpec(mTotalWidth - mEditTextMinWidth, MeasureSpec.EXACTLY), 0);
                childView1.measure(MeasureSpec.makeMeasureSpec(mEditTextMinWidth, MeasureSpec.EXACTLY), 0);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 2) {
            View childView0 = getChildAt(0);
            View childView1 = getChildAt(1);

            int childView0Width = childView0.getMeasuredWidth();

            if (this.mTotalWidth - childView0Width < this.mEditTextMinWidth) {
                childView0.layout(l, t, l + this.mTotalWidth - this.mEditTextMinWidth, b);
                childView1.layout(r - this.mEditTextMinWidth, t, r, b);
            }
            else {
                super.onLayout(changed, l, t, r, b);
            }
        }
        else {
            super.onLayout(changed, l, t, r, b);
        }
    }
}
