package com.example.administrator.doubleslidebutton;

import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class UI {
    public final static int ORG_SCREEN_WIDTH = 480;
    public final static int ORG_SCREEN_HEIGHT = 800;

    public static LinearLayout.LayoutParams getLinearLayoutPararmWHTrue(int w, int h) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.width = w;
        params.height = h;

        return params;
    }

    public static RelativeLayout.LayoutParams getRelativeLayoutPararmWH(int dmw, int dmh, int w, int h) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.width = dmw * w / ORG_SCREEN_WIDTH;
        params.height = dmw * h / ORG_SCREEN_WIDTH;

        return params;

    }
}
