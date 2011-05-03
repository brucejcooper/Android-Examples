package com.eightbitcloud.pagingscroller;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Pager scroller;
    private PageIndicator indicator;
    
    private static final int NUM_PAGES = 5;
    private static int[] COLORS = new int[] {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
        Color.YELLOW
    };

    
    /** 
     * Called when the activity is first created.  Simply sets up an example Pager with 5 pages, 
     * each of which shows the page number in a label 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        scroller = ((Pager)findViewById(R.id.scrollView));
        indicator = ((PageIndicator)findViewById(R.id.indicator));
        indicator.setPager(scroller);
        
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (int i = 0; i < NUM_PAGES; i++) {
            View pageView = layoutInflater.inflate(R.layout.page, null);
            ((TextView) pageView.findViewById(R.id.pageText)).setText("Page " + (i+1));
            pageView.setBackgroundColor(COLORS[i % COLORS.length]);
            
            scroller.addPage(pageView);
        }
    }
}