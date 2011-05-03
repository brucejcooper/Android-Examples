package com.eightbitcloud.pagingscroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PageIndicator extends View {
    private Pager pager;
    private Paint textPaint;
    private Paint dotPaint;
    private int textHeight;
    private int ascent;
    private int cellSize;
    
    private OnPageChangeListener scrollListener = new OnPageChangeListener() {
        public void onPageChange(Pager scroller) {
            update();
        }

        public void onPageCountChange(Pager scroller) {
            updatePageCount();
        }
    };

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }
    

    public PageIndicator(Context context) {
        super(context);
        initPaints();
    }

    
    
    private final void initPaints() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(10);
        textPaint.setColor(Color.BLACK);
        
        dotPaint = new Paint();
        textPaint.setAntiAlias(true);
        dotPaint.setColor(Color.GRAY);
        
        ascent = -(int) textPaint.ascent();
        textHeight = (int) (ascent + textPaint.descent());
        cellSize = textHeight + 6;
    }

        
    public void setPager(Pager pager) {
        if (pager != null) {
            pager.removeOnPageChangeListener(scrollListener);
        }
        this.pager = pager;
        if (pager != null) {
            pager.addOnPageChangeListener(scrollListener );
        }
    }
    
    
    public void update() {
        invalidate();
    }
    public void updatePageCount() {
        requestLayout();
        invalidate();
    }
    
    private int getNumPages() {
        return pager == null ? 1 : pager.getPageCount();
    }
    
    private int getActivePage() {
        return pager == null ? 0 : pager.getCurrentPage();
    }


    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     * 
     * @param measureSpec
     *            A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            result = getNumPages() * cellSize;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     * 
     * @param measureSpec
     *            A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            result = cellSize; 
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Render the pager
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = getNumPages();
        int current = getActivePage();
        
        int x = 0;
        for (int i = 0; i < count; i++, x += cellSize) {
            if (i == current) {
                String txt = Integer.toString(i+1);
                Rect bounds = new Rect(); 
                textPaint.getTextBounds(txt, 0, txt.length(), bounds);
                RectF oval = new RectF(x+1, 1, x+cellSize-2, cellSize-2);
                canvas.drawOval(oval, dotPaint);
                canvas.drawText(txt, x + (cellSize-bounds.width())/2, (cellSize - textHeight)/2 + ascent, textPaint);
            } else {
                int dotSize = 5;
                int dotOffset = (cellSize - dotSize)/2;
                RectF oval = new RectF(x+dotOffset, dotOffset, x+dotOffset+dotSize, dotOffset+dotSize);
                canvas.drawOval(oval, dotPaint);
            }
        }
    }
}
