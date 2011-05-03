package com.eightbitcloud.pagingscroller;

import java.util.LinkedList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class Pager extends HorizontalScrollView {
    private LinearLayout contents;
    private LinkedList<OnPageChangeListener> listeners;

    public Pager(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        contents = new LinearLayout(ctx);
        contents.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

        addView(contents);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        for (int i = 0; i < contents.getChildCount(); i++) {
            View child = contents.getChildAt(i);
            if (child.getLayoutParams().width != specSize) {
                child.setLayoutParams(new LinearLayout.LayoutParams(specSize, LayoutParams.FILL_PARENT));
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return 0.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return 0.0f;
    }

    public void addPage(View child) {
        int width = getWidth();
        child.setLayoutParams(new LayoutParams(width, LayoutParams.FILL_PARENT));
        contents.addView(child);
        contents.requestLayout();
        
        firePageCountChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        boolean result = super.onTouchEvent(evt);

        int width = getWidth();

        if (evt.getAction() == MotionEvent.ACTION_UP) {
            int pg = (getScrollX() + width / 2) / width;
            smoothScrollTo(pg * width, 0);
        }

        
        return result;
    }
    
    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (listeners != null) {
            for (OnPageChangeListener list: listeners) {
                list.onPageChange(this);
            }
        }
    }

    public boolean hasPage(View v) {
        return contents.indexOfChild(v) != -1;
    }

    public void removePage(View v) {
        contents.removeView(v);
        firePageCountChanged();

    }
    
    public int getCurrentPage() {
        int width = getWidth();
        return (getScrollX() + width/2) / width;
    }

    public int getPageCount() {
        return contents.getChildCount();
    }

    public void removeAllPages() {
        contents.removeAllViews();
        firePageCountChanged();

    }

    private void firePageCountChanged() {
        if (listeners != null) {
            for (OnPageChangeListener list: listeners) {
                list.onPageCountChange(this);
            }
        }
    }


    
    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (listeners == null) {
            listeners = new LinkedList<OnPageChangeListener>();
        }
        listeners.add(onPageChangeListener);
    }
    
    public boolean removeOnPageChangeListener(OnPageChangeListener l) {
        if (listeners != null) {
            return listeners.remove(l);
        } else
            return false;
    }

}
