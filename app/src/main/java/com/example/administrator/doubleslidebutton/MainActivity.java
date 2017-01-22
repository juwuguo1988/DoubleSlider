package com.example.administrator.doubleslidebutton;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;




public class MainActivity extends Activity {

    DoubleProgressView cropVideoControlView;
    Button btn_left;
    Button btn_right;

    TextView tv_endTime;
    TextView tv_startTime;

    public DisplayMetrics dm;

    int k = 0;
    private String TAG = getClass().getSimpleName();
    private int witchViewOnTouch = 0;
    private int dmw;
    private int dmh;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    if (witchViewOnTouch == 0) {

                        cropVideoControlView.setLeftChange(-0.1);
                    } else if (witchViewOnTouch == 1) {
                        cropVideoControlView.setRightChange(-0.1);

                    }
                    handler.sendEmptyMessageDelayed(0, 20);

                    break;
                case 1:

                    if (witchViewOnTouch == 0) {

                        cropVideoControlView.setLeftChange(0.1);
                    } else if (witchViewOnTouch == 1) {
                        cropVideoControlView.setRightChange(0.1);

                    }
                    handler.sendEmptyMessageDelayed(1, 20);

                    break;

            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        findViewById();
        initData();

        initListener();

    }

    private void findViewById() {
        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_left = (Button) findViewById(R.id.btn_left);
        cropVideoControlView = (DoubleProgressView) findViewById(R.id.cropVideoControlView);

    }

    private void initData() {
        dmw = dm.widthPixels;
        dmh = dm.heightPixels;


        /*滑块的图片资源*/
        ArrayList<Integer> imageList = new ArrayList<Integer>();

        imageList.add(R.drawable.crop_one_rightpull_icon);
        imageList.add(R.drawable.crop_one_rightpull_icon);

        imageList.add(R.drawable.red);
        imageList.add(R.drawable.red);

        //整个双滑块的View的布局设置
        RelativeLayout.LayoutParams params = UI.getRelativeLayoutPararmWH(dmw, dmh, UI.ORG_SCREEN_WIDTH, 140);

        double oneCropVideoFrameHeight = dmw * 100 / UI.ORG_SCREEN_WIDTH;
        //左右滑块的布局设置
        LinearLayout.LayoutParams leftButtonparams = UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.3), (int) oneCropVideoFrameHeight);
        LinearLayout.LayoutParams rightButtonparams = UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.3), (int) oneCropVideoFrameHeight);

        /*单双滑块*/
//        cropVideoControlView.initView(params, leftButtonparams, rightButtonparams, imageList).initDataForSingle(15.2, 100, 40);
//        cropVideoControlView.initView(params, leftButtonparams, rightButtonparams, imageList).initData(15.2, 35,100,10, 40);

    }

    private void initListener() {

        initButtonListener(btn_left, true);
        initButtonListener(btn_right, false);

        cropVideoControlView.setProgressListener(new DoubleProgressView.ProgressListener() {
            @Override
            public void onProgressBefore() {

            }

            @Override
            public void onProgressChanged(DoubleProgressView controlView, double startCurrent, double endCurrent, int witchOnTouch) {

                tv_startTime.setText(startCurrent + "秒");
                tv_endTime.setText(endCurrent + "秒");
                Log.e(TAG, "moveAction: 开始时间=" + startCurrent + "秒");
                Log.e(TAG, "moveAction: 结束时间=" + endCurrent + "秒");
                Log.e(TAG, "moveAction: witchOnTouch=" + witchOnTouch);
                witchViewOnTouch = witchOnTouch;
            }

            @Override
            public void onProgressAfter() {

            }
        });

    }

    private void initButtonListener(Button btn_view, final boolean isLeftButton) {

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLeftButton) {

                    //点击左滑块
                    handler.removeCallbacksAndMessages(null);
                    if (witchViewOnTouch == 0) {
                        cropVideoControlView.setLeftChange(-0.1);
                    } else if (witchViewOnTouch == 1) {

                        cropVideoControlView.setRightChange(-0.1);

                    }
                } else {
                    handler.removeCallbacksAndMessages(null);

                    //点击右滑块
                    if (witchViewOnTouch == 0) {

                        cropVideoControlView.setLeftChange(0.1);
                    } else if (witchViewOnTouch == 1) {
                        cropVideoControlView.setRightChange(0.1);

                    }
                }

            }
        });

        /*下面的不用管，值关注 OnclickListener 就可以了*/
        btn_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        btn_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                handler.removeMessages(isLeftButton ? 1 : 0);

                handler.sendEmptyMessageDelayed(isLeftButton ? 0 : 1, 5);
                return false;
            }
        });

    }

}
