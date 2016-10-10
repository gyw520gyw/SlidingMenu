package com.gyw.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * author: gyw
 * date: 2016/10/9.
 */
public class SlidingMenu extends ViewGroup {


    private View menuView;
    private View mainView;

    private int menuWidth;

    //按下的x轴的偏移量
    private int downX;

    private static final int SCREEN_MENU = 0;
    private static final int SCREEN_MAIN = 1;

    //默认是主界面
    private int currentScreen = SCREEN_MAIN;

    private Scroller mScroller;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);

    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        MeasureSpec.getSize(widthMeasureSpec)


        Log.d("gyw", "width： " + MeasureSpec.getSize(widthMeasureSpec));

       int width = MeasureSpec.getSize(widthMeasureSpec);
       menuWidth = (int)(width * 0.6);


        //1.测量菜单的宽和高, 宽 ： 布局中的宽度， 高 ： 屏幕的高
        menuView = getChildAt(0);
//        menuView.measure(menuView.getLayoutParams().width, heightMeasureSpec);
        menuView.measure(menuWidth, heightMeasureSpec);

        //2.测量主界面的宽和高
        mainView = getChildAt(1);
        mainView.measure(widthMeasureSpec, heightMeasureSpec);

    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //放置主界面的在屏幕坐上角
        mainView.layout(l, t, r, b);

        //放置菜单到屏幕左侧
        menuView.layout(-menuWidth, t, 0, b);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int scrollX;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                downX = (int) event.getX();

                break;

            case MotionEvent.ACTION_MOVE:

                int moveX = (int) event.getX();

                //x差值
                int deltaX = downX - moveX;

                //判断当前给定的变量是否超出
                //当前x值+增量
                scrollX = getScrollX() + deltaX;
                if(scrollX < -menuWidth) {  //超出了左边界
                    scrollTo(-menuWidth, 0);
                } else if(scrollX > 0) {
                    scrollTo(0, 0);
                } else {
                    scrollBy(deltaX, 0);
                }


                downX = moveX;

                break;

            case MotionEvent.ACTION_UP:

                //获取菜单宽度的一半
                int enterX = -(int) (menuWidth * 0.5);

                scrollX = getScrollX();

                if(scrollX > enterX) {
                    currentScreen = SCREEN_MAIN;
                } else {
                    currentScreen = SCREEN_MENU;
                }

                switchScreen(currentScreen);

                break;

        }

        return true;
    }

    //根据currentScreen来切换界面
    private void switchScreen(int currentScreen) {
        //增量值 = 目标位置 - 开始位置
        int dX = 0;
        //开始位置
        int startX = getScrollX();

        switch (currentScreen) {

            case SCREEN_MAIN :

//                scrollTo(0, 0);

                dX = 0 - startX;

                break;

            case SCREEN_MENU:

                dX = -menuWidth - startX;

                break;
        }
        //模拟数据
        mScroller.startScroll(startX, 0, dX, 0, Math.abs(dX));


        invalidate();

    }


    @Override
    public void computeScroll() {
        //把当前scroller正在模拟的数据取出来,使用scrolltO方法切换屏幕
        if(mScroller.computeScrollOffset()) {   //当前正在切换
            int currX = mScroller.getCurrX();

            scrollTo(currX, 0);

            invalidate();
        }
    }


    /**
     *
     * @return true 显示的菜单, false 没有显示菜单
     */
    public boolean isShowMenu() {

        return currentScreen == SCREEN_MENU;

    }

    /**
     * 隐藏菜单
     */
    public void hideMenu() {
        currentScreen = SCREEN_MAIN;
        switchScreen(currentScreen);
    }

    /**
     * 显示菜单
     */
    public void showMenu() {
        currentScreen = SCREEN_MENU;
        switchScreen(currentScreen);
    }
}
