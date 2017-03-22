package ysg.gdcp.cn.qqslidemenu.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Administrator on 2017/3/22 15:07.
 *
 * @author ysg
 */

public class DragLayout extends FrameLayout {

    private View redView;
    private View blueView;
    private ViewDragHelper mViewDragHelper;

    public DragLayout(Context context) {
        super(context);
        init();
    }


    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);

    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        int l = getPaddingLeft() + getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;
        int l = 0;
//        int t = getPaddingTop()+getMeasuredHeight()/2-redView.getMeasuredHeight();
        int t=0;
        redView.layout(l, t, l + redView.getMeasuredWidth(), t + redView.getMeasuredHeight());
        blueView.layout(l, redView.getBottom(), l + blueView.getMeasuredWidth(), redView.getBottom() + blueView.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         *判断是否捕获child的触摸事件
         * @param child 当前触摸的view
         * @param pointerId
         * @return true 捕获并解析 false 不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == redView || child == blueView;
        }

        /**
         * 当view被开始捕获和解析的回调
         * @param capturedChild 当前捕获的view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 控制child 在水平方向的移动
         * @param child  移动的view
         * @param left 移动的距离
         * @param dx 本次水平方向移动的距离
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 获取view 水平的拖拽范围，但目前不能限制边界
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 控制child 在垂直方向的移动
         * @param child  移动的view
         * @param top 移动的距离
         * @param dy 本次垂直方向移动的距离
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }
        /**
         * 获取view 垂直的拖拽范围
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 当child 的位置改变的时候执行 一般做其他子Viw的伴随移动
         * @param changedView  位置改变的View
         * @param left child当前最新的left
         * @param top child当前最新的top
         * @param dx 本次水平移动的距离
         * @param dy  本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView==blueView){
                redView.layout(redView.getLeft()+dx,redView.getTop()+dy,redView.getRight()+dx,redView.getBottom()+dy);
            }
            if (changedView==redView){
                blueView.layout(blueView.getLeft()+dx,blueView.getTop()+dy,blueView.getRight()+dx,blueView.getBottom()+dy);
            }
            float offesX =changedView.getLeft()*1.0f/(getMeasuredWidth()-changedView.getMeasuredWidth());
            float offesY =changedView.getTop()*1.0f/(getMeasuredHeight()-changedView.getMeasuredHeight());
            executeAnim(offesX,offesY);
        }

        /**
         * s鼠标抬起执行该方法
         * @param releasedChild 当前抬起的VIew
         * @param xvel x方向的移动速度 正 右移 负 左移
         * @param yvel  y方向的移动速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerPosition =getMeasuredWidth()/2-releasedChild.getMeasuredWidth()/2;
            if (releasedChild.getLeft()<centerPosition){
                mViewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
            }else {
                mViewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getMeasuredWidth(),releasedChild.getTop());
            }
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    };

    private void executeAnim(float offesX,float offesY) {
        ViewHelper.setScaleX(redView,1f-0.5f*offesX);
        ViewHelper.setScaleX(blueView,1f+0.5f*offesX);
//        ViewHelper.setScaleY(redView,1f-0.5f*offesY);
//        ViewHelper.setScaleY(blueView,1f+0.5f*offesY);
        ViewHelper.setRotation(redView,72000*offesX);
        ViewHelper.setRotation(blueView,-72000*offesX);
        ViewHelper.setRotationY(redView,7200*offesY);
        ViewHelper.setRotationY(blueView,-7200*offesY);
        ViewHelper.setAlpha(redView,1.2f-offesX);
        ViewHelper.setAlpha(blueView,1.2f-offesY);
        ViewHelper.setTranslationX(redView,redView.getMeasuredWidth()*offesX);
        ViewHelper.setTranslationY(redView,2*redView.getMeasuredWidth()*offesY);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
