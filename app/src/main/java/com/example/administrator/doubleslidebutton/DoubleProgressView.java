package com.example.administrator.doubleslidebutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DoubleProgressView extends RelativeLayout {

    private String TAG = getClass().getSimpleName();

    private LinearLayout ll_crop_one_l_button;
    private LinearLayout ll_crop_one_r_button;
    private RelativeLayout rl_photo_edit_tool_bar;
    private LinearLayout ll_video_crop_middle;
    private ImageView iv_crop_one_l_button;
    private ImageView iv_crop_one_r_button;
    /* 变量清单 */
    private Context mContext;
    // 是否是单滑块
    private boolean isSingleButton = false;

    private List<Integer> imageList;
    private int dmw, dmh;

	/* 滑块 */

    private boolean isFirstOnTouch = true;// 防止第一次点击的时候误滑动

    // 滑块的宽度，总控件的宽度
    private double measuredWidth = 50;
    private double totalWidth = -1;
    // 最长的宽度，最小的宽度
    private double minWidth = 62;
    private double maxWidth = 100;

    // 最长的时间，最小的时间
    private double minCurrent = 62;
    private double maxCurrent = 100;

    private double totalCurrent = 1000;

    private ProgressListener progressListener;
    private InitData initData;

    private double finalLeftTime = -1;// 最后输出给外部的开始时间
    private double finalRightTime = -1;// 最后输出给外部的结束时间

    // 计算滑动距离
    private int lastX = 0;
    private int lastY = 0;

    /* 判断类型数值 */
    private int witchView = 0;// 判断当前点击的是哪一个滑块
    public static final int LEFT_ON_TOUCH = 0;
    public static final int RIGHT_ON_TOUCH = 1;

    private final int WITCH_SINGLE = 0;//
    private final int WITCH_DOUBLE_LEFT = 1;
    private final int WITCH_DOUBLE_RIGHT = 2;

    /* 计算滑动的时间 */
    private int actionType = -1;

    private final int ACTIONTYPE_LEFT_PULL = 0;
    private final int ACTIONTYPE_LEFT_PUSH = 1;
    private final int ACTIONTYPE_RIGHT_PULL = 2;
    private final int ACTIONTYPE_RIGHT_PUSH = 3;
    private final int ACTIONTYPE_RIGHT_NO_CHANGE = 4;
    private final int ACTIONTYPE_LEFT_NO_CHANGE = 5;

    private final int ACTIONTYPE_LEFT_START = 6;
    private final int ACTIONTYPE_RIGHT_END = 7;
    private final int ACTIONTYPE_SINGLE_NOMAL = 8;

    public DoubleProgressView(Context context) {
        super(context);
        this.mContext = context;
        init();

    }

    public DoubleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();

    }

    public DoubleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();

    }

    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        dmw = dm.widthPixels;
        dmh = dm.heightPixels;

        View root = LayoutInflater.from(mContext).inflate(R.layout.progress_layout, null);
        ll_crop_one_l_button = (LinearLayout) root.findViewById(R.id.ll_crop_one_l_button);
        ll_crop_one_r_button = (LinearLayout) root.findViewById(R.id.ll_crop_one_r_button);
        ll_video_crop_middle = (LinearLayout) root.findViewById(R.id.ll_video_crop_middle);
        rl_photo_edit_tool_bar = (RelativeLayout) root.findViewById(R.id.rl_photo_edit_tool_bar);
        iv_crop_one_l_button = (ImageView) root.findViewById(R.id.iv_crop_one_l_button);
        iv_crop_one_r_button = (ImageView) root.findViewById(R.id.iv_crop_one_r_button);

        addView(root);

        setListener();

        //initDataForSingle(25, 100, 40);

    }

    private void initUI(int resultWidth, int resultHeight) {
        imageList = new ArrayList<Integer>();

        imageList.add(R.drawable.crop_leftfreepull_icon_selected);
        imageList.add(R.drawable.crop_rightpull_icon);

        imageList.add(R.drawable.crop_leftpull_icon);
        imageList.add(R.drawable.crop_rightfreepull_icon_selected);

        LayoutParams layoutParams = (LayoutParams) rl_photo_edit_tool_bar.getLayoutParams();
        layoutParams.width = resultWidth;
        layoutParams.height = resultHeight;
        rl_photo_edit_tool_bar.setLayoutParams(layoutParams);

        double oneCropVideoFrameHeight = dmw * 50 / UI.ORG_SCREEN_WIDTH;

        ll_crop_one_l_button.setLayoutParams(UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.4), (int) oneCropVideoFrameHeight));
        ll_crop_one_r_button.setLayoutParams(UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.4), (int) oneCropVideoFrameHeight));
        iv_crop_one_l_button.setLayoutParams(UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.4), (int) oneCropVideoFrameHeight));
        iv_crop_one_r_button.setLayoutParams(UI.getLinearLayoutPararmWHTrue((int) (oneCropVideoFrameHeight * 0.4), (int) oneCropVideoFrameHeight));

        iv_crop_one_l_button.setImageResource(imageList.get(0));
        iv_crop_one_r_button.setImageResource(imageList.get(1));

        initData(25, 52, 100, 10, 40);

    }

    //初始化双滑块Button的属性，布局 和 图片类型  //图片按照 左边点击状态 ，右边正常状态 ，左边正常状态 ，右边点击状态 依次排好 OR  单滑块的时候 传入两个 左边正常 ，右边正常状态
    public InitData initView(LayoutParams toolBarParams, LinearLayout.LayoutParams leftParams, LinearLayout.LayoutParams rightParams, List<Integer> buttonImageResource) {



        rl_photo_edit_tool_bar.setLayoutParams(toolBarParams);

        ll_crop_one_l_button.setLayoutParams(leftParams);
        ll_crop_one_r_button.setLayoutParams(rightParams);

        if (buttonImageResource != null && buttonImageResource.size() == 4) {
            imageList = buttonImageResource;
        }

        if (buttonImageResource != null && buttonImageResource.size() == 2) {
            imageList = buttonImageResource;

        }
        Log.e(TAG, "initView: 设置布局");

        iv_crop_one_l_button.setImageResource(imageList.get(0));
        iv_crop_one_r_button.setImageResource(imageList.get(1));

        return initData;
    }

    private void setListener() {

        /*
         * 左滑块
		 */

        ll_crop_one_l_button.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                getParent().requestDisallowInterceptTouchEvent(true);

                if (!isSingleButton) {
                    iv_crop_one_l_button.setImageResource(imageList.get(0));
                    iv_crop_one_r_button.setImageResource(imageList.get(1));
                }

                if (witchView == RIGHT_ON_TOUCH) {
                    isFirstOnTouch = true;
                    witchView = LEFT_ON_TOUCH;
                    formatTimeForProgressChanged(finalLeftTime, finalRightTime);

                }

                return moveAction(event, WITCH_DOUBLE_LEFT);

            }

        });

        ll_crop_one_r_button.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                getParent().requestDisallowInterceptTouchEvent(true);

                if (!isSingleButton) {
                    iv_crop_one_l_button.setImageResource(imageList.get(2));
                    iv_crop_one_r_button.setImageResource(imageList.get(3));
                }

                if (witchView == LEFT_ON_TOUCH) {
                    isFirstOnTouch = true;
                    witchView = RIGHT_ON_TOUCH;

                    formatTimeForProgressChanged(finalLeftTime, finalRightTime);

                }

                return moveAction(event, WITCH_DOUBLE_RIGHT);

            }

        });

        ll_video_crop_middle.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                getParent().requestDisallowInterceptTouchEvent(true);

                return moveAction(event, WITCH_SINGLE);

            }

        });

		/*
         * 右滑块
		 */

    }

    private boolean moveAction(MotionEvent event, int witch) {

        View view = ll_crop_one_l_button;

        //检测到触摸事件后 第一时间得到相对于父控件的触摸点坐标 并赋值给x,y
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            //触摸事件中绕不开的第一步，必然执行，将按下时的触摸点坐标赋值给 lastX 和 last Y
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                progressListener.onProgressBefore();
                break;
            //触摸事件的第二步，这时候的x,y已经随着滑动操作产生了变化，用变化后的坐标减去首次触摸时的坐标得到 相对的偏移量
            case MotionEvent.ACTION_MOVE:

                int offsetX = x - lastX;
                int offsetY = y - lastY;
                //使用 layout 进行重新定位

                switch (witch) {
                    case WITCH_SINGLE:

                        view = ll_video_crop_middle;

                        break;
                    case WITCH_DOUBLE_LEFT:
                        view = ll_crop_one_l_button;
                        break;
                    case WITCH_DOUBLE_RIGHT:
                        view = ll_crop_one_r_button;

                        break;

                }

                if (isFirstOnTouch) {
                    if (Math.abs(offsetX) > 2) {//第一点点击要偏移量大于2才允许移动
                        isFirstOnTouch = false;
                    }

                } else {
                    if (Math.abs(offsetX) > 1) {

                        interactiveView(view, offsetX, true);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                progressListener.onProgressAfter();

                break;

        }
        return true;
    }

    private void interactiveView(View view, int offsetX, boolean isChangeView) {



        /*左边滑块的布局信息*/
        LinearLayout.LayoutParams leftLayoutParams = (LinearLayout.LayoutParams) ll_crop_one_l_button.getLayoutParams();

        /*左边滑块的布局信息*/
        LinearLayout.LayoutParams rightlayoutParams = (LinearLayout.LayoutParams) ll_crop_one_r_button.getLayoutParams();
        double totalmeasuredWidth = totalWidth - 2 * measuredWidth;

        if (view == ll_video_crop_middle) {

            if (leftLayoutParams.leftMargin + offsetX <= 0) {
                actionType = ACTIONTYPE_LEFT_START;

                rightlayoutParams.rightMargin = rightlayoutParams.rightMargin + leftLayoutParams.leftMargin;
                leftLayoutParams.leftMargin = 0;
            } else if (rightlayoutParams.rightMargin - offsetX <= 0) {
                actionType = ACTIONTYPE_RIGHT_END;

                leftLayoutParams.leftMargin = leftLayoutParams.leftMargin + rightlayoutParams.rightMargin;
                rightlayoutParams.rightMargin = 0;

            } else {//正常

                actionType = ACTIONTYPE_SINGLE_NOMAL;

                leftLayoutParams.leftMargin = leftLayoutParams.leftMargin + offsetX;

                rightlayoutParams.rightMargin = rightlayoutParams.rightMargin - offsetX;

            }

        } else if (view == ll_crop_one_l_button) {

            /*左边的最大距离*/
            double minLeftmargin = totalmeasuredWidth - rightlayoutParams.rightMargin - minWidth;

            double maxLeftmargin = totalmeasuredWidth - rightlayoutParams.rightMargin - maxWidth;

            //超过最大允许距离，推动右边
            if (leftLayoutParams.leftMargin + offsetX > minLeftmargin && offsetX > 0) {

                actionType = ACTIONTYPE_LEFT_PUSH;
                if (leftLayoutParams.leftMargin + offsetX >= totalmeasuredWidth - 0 - minWidth) {
                    rightlayoutParams.rightMargin = 0;

                    leftLayoutParams.leftMargin = (int) (totalmeasuredWidth - 0 - minWidth);

                } else {
                    leftLayoutParams.leftMargin = (int) (leftLayoutParams.leftMargin + offsetX);

                    rightlayoutParams.rightMargin = (int) (rightlayoutParams.rightMargin - offsetX);

                }
                //超过最大允许距离，拉动右边
            } else if (leftLayoutParams.leftMargin + offsetX <= maxLeftmargin && offsetX < 0) {
                actionType = ACTIONTYPE_LEFT_PULL;

                if (leftLayoutParams.leftMargin + offsetX <= 0) {

                    leftLayoutParams.leftMargin = 0;
                    if (rightlayoutParams.rightMargin < totalmeasuredWidth - maxWidth) {

                        rightlayoutParams.rightMargin = (int) (totalmeasuredWidth - maxWidth);
                    }

                } else {

                    leftLayoutParams.leftMargin = (int) (maxLeftmargin + offsetX);

                    rightlayoutParams.rightMargin = (int) (rightlayoutParams.rightMargin - offsetX);

                }

                //越界
            } else if (leftLayoutParams.leftMargin + offsetX < 0) {

                leftLayoutParams.leftMargin = 0;

                //正常范围
            } else {

                actionType = ACTIONTYPE_RIGHT_NO_CHANGE;

                leftLayoutParams.leftMargin = (int) (leftLayoutParams.leftMargin + offsetX);
                //最小距离
            }
            //右边滑块
        } else if (view == ll_crop_one_r_button) {

            double minRightmargin = totalmeasuredWidth - leftLayoutParams.leftMargin - minWidth;

            double maxRightmargin = totalmeasuredWidth - leftLayoutParams.leftMargin - maxWidth;
            //距离右边最大距离，拉动左边

            if (rightlayoutParams.rightMargin - offsetX <= maxRightmargin && offsetX > 0) {
                actionType = ACTIONTYPE_RIGHT_PULL;

                if (rightlayoutParams.rightMargin - offsetX <= 0) {

                    if (leftLayoutParams.leftMargin < (totalmeasuredWidth - 0 - maxWidth)) {

                        leftLayoutParams.leftMargin = (int) (totalmeasuredWidth - 0 - maxWidth);
                    }

                    rightlayoutParams.rightMargin = 0;
                } else {
                    rightlayoutParams.rightMargin = (int) (rightlayoutParams.rightMargin - offsetX);
                    leftLayoutParams.leftMargin = (int) (leftLayoutParams.leftMargin + offsetX);

                }

                //距离右边最大距离，推动左边
            } else if (rightlayoutParams.rightMargin - offsetX > minRightmargin && offsetX < 0) {
                actionType = ACTIONTYPE_RIGHT_PUSH;

                if (rightlayoutParams.rightMargin - offsetX >= totalmeasuredWidth - 0 - minWidth) {

                    leftLayoutParams.leftMargin = 0;

                    rightlayoutParams.rightMargin = (int) (totalmeasuredWidth - 0 - minWidth);
                } else {

                    leftLayoutParams.leftMargin = (int) (leftLayoutParams.leftMargin + offsetX);

                    rightlayoutParams.rightMargin = (int) (minRightmargin - offsetX);
                }

                //越界
            } else if (rightlayoutParams.rightMargin - offsetX < 0) {

                rightlayoutParams.rightMargin = 0;
            } else {
                actionType = ACTIONTYPE_LEFT_NO_CHANGE;

                rightlayoutParams.rightMargin = (int) (rightlayoutParams.rightMargin - offsetX);

                //距离右边最小距离
            }

        }

        if (leftLayoutParams.leftMargin < 0) {
            leftLayoutParams.leftMargin = 0;
        }
        if (rightlayoutParams.rightMargin < 0) {
            rightlayoutParams.rightMargin = 0;
        }
        ll_crop_one_l_button.setLayoutParams(leftLayoutParams);

        ll_crop_one_r_button.setLayoutParams(rightlayoutParams);

        //计算时间
        calculateTime(isChangeView, leftLayoutParams, rightlayoutParams, totalmeasuredWidth);

    }

    private void calculateTime(boolean isChangeView, LinearLayout.LayoutParams leftLayoutParams, LinearLayout.LayoutParams rightlayoutParams, double totalmeasuredWidth) {
        double leftxx = leftLayoutParams.leftMargin * totalCurrent / totalmeasuredWidth;

        double rightxx = (totalmeasuredWidth - rightlayoutParams.rightMargin) * totalCurrent / totalmeasuredWidth;

        if (progressListener != null) {
            if (isChangeView) {
                double tempTime = finalRightTime - finalLeftTime;

                switch (actionType) {
                    case ACTIONTYPE_LEFT_PULL:
                        finalLeftTime = formatTimeForDouble(leftxx);

                        finalRightTime = finalLeftTime + maxCurrent;

                        break;
                    case ACTIONTYPE_LEFT_PUSH:

                        finalLeftTime = formatTimeForDouble(leftxx);

                        if (rightlayoutParams.rightMargin == 0) {
                            finalRightTime = totalCurrent;
                            finalLeftTime = totalCurrent - minCurrent;
                        } else {

                            finalRightTime = finalLeftTime + minCurrent;
                        }

                        break;
                    case ACTIONTYPE_RIGHT_PULL:

                        finalRightTime = formatTimeForDouble(rightxx);

                        finalLeftTime = finalRightTime - maxCurrent;

                        break;
                    case ACTIONTYPE_RIGHT_PUSH:

                        finalRightTime = formatTimeForDouble(rightxx);

                        if (leftLayoutParams.leftMargin == 0) {
                            finalLeftTime = 0;
                            finalRightTime = finalLeftTime + minCurrent;
                        } else {

                            finalLeftTime = finalRightTime - minCurrent;
                        }

                        break;

                    case ACTIONTYPE_LEFT_NO_CHANGE:

                        finalRightTime = formatTimeForDouble(rightxx);

                        break;

                    case ACTIONTYPE_RIGHT_NO_CHANGE:
                        finalLeftTime = formatTimeForDouble(leftxx);

                        break;

                    case ACTIONTYPE_SINGLE_NOMAL:

                        finalLeftTime = formatTimeForDouble(leftxx);
                        finalRightTime = finalLeftTime + tempTime;

                        break;

                    case ACTIONTYPE_LEFT_START:

                        finalLeftTime = 0;

                        finalRightTime = finalLeftTime + tempTime;

                        break;

                    case ACTIONTYPE_RIGHT_END:
                        finalRightTime = totalCurrent;

                        finalLeftTime = totalCurrent - tempTime;

                        break;
                }
                actionType = -1;

                formatTimeForProgressChanged(finalLeftTime, finalRightTime);

            }

        }
    }

    private boolean initDataForSingle(double startCurrent, double totalCurrent, double unChangeCurrent) {

        if (startCurrent + unChangeCurrent > totalCurrent) {

            Toast.makeText(mContext, "裁剪时间大于总的时间:" + startCurrent + "+" + unChangeCurrent + "> " + totalCurrent, Toast.LENGTH_SHORT).show();

            return false;
        }

        initData(startCurrent, startCurrent + unChangeCurrent, totalCurrent, unChangeCurrent, unChangeCurrent);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (totalWidth != -1) {
            return;
        }

        // 声明一个临时变量来存储计算出的测量值
        int resultWidth = 0;
        // 获取宽度测量规格中的mode
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        // 获取宽度测量规格中的size
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        /*高度*/
        int resultHeight = 0;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        //如果父布局有布局信息

        if (modeWidth == MeasureSpec.EXACTLY) {
            resultWidth = sizeWidth;
            resultHeight = sizeHeight;

        } else {  // 如果父布局没有布局信息

            resultWidth = getWidth();
            resultHeight = getHeight();
            //如果爹给儿子的是一个限制值

            if (modeWidth == MeasureSpec.AT_MOST) {
                // 那么儿子自己的需求就要跟爹的限制比比看谁小要谁
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
            if (modeHeight == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, sizeHeight);
            }
        }

        // 设置测量尺寸
        setMeasuredDimension(resultWidth, resultHeight);

        initUI(resultWidth, resultHeight);

    }



    public boolean initData(double startCurrent, double endCurrent, double totalCurrent, double minCurrent, double maxCurrent) {
        Log.e(TAG, "initData: 设置布局数据B");

        formatTimeForDouble(startCurrent);
        formatTimeForDouble(endCurrent);
        formatTimeForDouble(totalCurrent);
        formatTimeForDouble(minCurrent);
        formatTimeForDouble(maxCurrent);

        double middle = endCurrent - startCurrent;

        LayoutParams layoutparms = (LayoutParams) rl_photo_edit_tool_bar.getLayoutParams();
        LinearLayout.LayoutParams leftLayoutParams = (LinearLayout.LayoutParams) ll_crop_one_l_button.getLayoutParams();
        LinearLayout.LayoutParams rightlayoutParams = (LinearLayout.LayoutParams) ll_crop_one_r_button.getLayoutParams();

        this.totalCurrent = totalCurrent;
        this.minCurrent = minCurrent;
        this.maxCurrent = maxCurrent;

        totalWidth = layoutparms.width;

        measuredWidth = leftLayoutParams.width;

        minWidth = ((totalWidth - 2 * measuredWidth) * minCurrent / totalCurrent);

        maxWidth = ((totalWidth - 2 * measuredWidth) * maxCurrent / totalCurrent);
        double scale = (totalWidth - 2 * measuredWidth) / totalCurrent;//根据屏幕宽度 和 需要裁剪的 总时间长  计算转化值

        finalLeftTime = startCurrent;
        finalRightTime = endCurrent;

        if (minCurrent > middle && middle > maxCurrent) {

            Toast.makeText(mContext, "不能小于最小值:" + middle + "<" + minWidth, Toast.LENGTH_SHORT).show();

            return false;
        }
        if (minCurrent == maxCurrent) {
            isSingleButton = true;
        }

        leftLayoutParams.leftMargin = (int) (startCurrent * scale);

        ll_crop_one_l_button.setLayoutParams(leftLayoutParams);

        rightlayoutParams.rightMargin = (int) (totalWidth - 2 * measuredWidth - endCurrent * scale);

        ll_crop_one_r_button.setLayoutParams(rightlayoutParams);

        formatTimeForProgressChanged(finalLeftTime, finalRightTime);

        return true;

    }

    /*设置左边的滑动*/
    public void setLeftChange(double setLeftChange) {

        getLeftTime(setLeftChange);

        LinearLayout.LayoutParams leftLayoutParams = (LinearLayout.LayoutParams) ll_crop_one_l_button.getLayoutParams();

        double change = formatTimeForInt(finalLeftTime, true) - leftLayoutParams.leftMargin;

        if (Math.abs(change) >= 1) {
            interactiveView(ll_crop_one_l_button, (int) change, false);

        }

    }

    private void getLeftTime(double setLeftChange) {

        finalLeftTime += setLeftChange;
        finalLeftTime = formatTimeForDouble(finalLeftTime);
        finalRightTime = formatTimeForDouble(finalRightTime);

        if (finalLeftTime < 0) {
            finalLeftTime = 0;

        } else if (finalLeftTime + minCurrent >= totalCurrent) {

            finalLeftTime = totalCurrent - minCurrent;
            finalRightTime = totalCurrent;

        } else {

            if (finalLeftTime + minCurrent >= finalRightTime) {

                finalRightTime = finalLeftTime + minCurrent;
            } else if (finalRightTime - finalLeftTime >= maxCurrent) {

                finalRightTime = finalLeftTime + maxCurrent;

            }

        }
        formatTimeForProgressChanged(finalLeftTime, finalRightTime);

    }

    /*设置左边的滑动*/
    public void setRightChange(double setLeftChange) {

        getRightTime(setLeftChange);

        LinearLayout.LayoutParams rightLayoutParams = (LinearLayout.LayoutParams) ll_crop_one_r_button.getLayoutParams();

        double change = rightLayoutParams.rightMargin - ((totalWidth - 2 * measuredWidth) - formatTimeForInt(finalRightTime, false));

        if (Math.abs(change) >= 1) {

            interactiveView(ll_crop_one_r_button, (int) change, false);

        }

    }

    private void getRightTime(double setLeftChange) {
        finalRightTime += setLeftChange;
        finalLeftTime = formatTimeForDouble(finalLeftTime);
        finalRightTime = formatTimeForDouble(finalRightTime);
        if (finalRightTime > totalCurrent) {
            finalRightTime = totalCurrent;
        } else if (finalRightTime - minCurrent <= finalLeftTime) {
            finalLeftTime = finalRightTime - minCurrent;
            if (finalLeftTime <= 0) {
                finalLeftTime = 0;
                finalRightTime = minCurrent;
            }

        } else {
            if (finalRightTime - finalLeftTime >= maxCurrent) {

                finalLeftTime = finalRightTime - maxCurrent;
            }

        }
        formatTimeForProgressChanged(finalLeftTime, finalRightTime);
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;

    }

    //统一处理输出后的时间，防止小数点转型产生的误差
    private void formatTimeForProgressChanged(double finalLeftTime, double finalRightTime) {

        finalLeftTime = formatTimeForDouble(finalLeftTime);
        finalRightTime = formatTimeForDouble(finalRightTime);

        if (progressListener != null) {
            progressListener.onProgressChanged(this, finalLeftTime, finalRightTime, witchView);

        }

    }

    //格式化时间，统一格式化为 小数点后带一位（可以根据需求修改）
    private double formatTimeForDouble(double valueTime) {

        valueTime = (double) Math.round(valueTime * 10) / 10;
        return valueTime;

    }

    private int formatTimeForInt(double valueTime, boolean isLeft) {

        int change = 0;

        if (isLeft) {

            change = (int) Math.round(valueTime * (totalWidth - 2 * measuredWidth) / totalCurrent);
        } else {

            change = (int) Math.round(valueTime * (totalWidth - 2 * measuredWidth) / totalCurrent);

        }

        return change;

    }

    interface InitData {
        boolean initData(double startCurrent, double endCurrent, double totalCurrent, double minCurrent, double maxCurrent);

        boolean initDataForSingle(double startCurrent, double totalCurrent, double unChangeCurrent);
    }

    public interface ProgressListener {
        //滑动前
        void onProgressBefore();

        //滑动时
        void onProgressChanged(DoubleProgressView controlView, double startCurrent, double endCurrent, int witchView);

        //滑动后
        void onProgressAfter();
    }
}