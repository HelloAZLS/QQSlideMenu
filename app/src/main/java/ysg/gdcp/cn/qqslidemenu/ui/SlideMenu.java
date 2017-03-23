package ysg.gdcp.cn.qqslidemenu.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Administrator on 2017/3/22 20:42.
 *
 * @author ysg
 */

public class SlideMenu extends FrameLayout {

    private View menuView;
    private View mainView;
    private ViewDragHelper viewDragHelper;
    private int width;
    private float mDragRange;
    private FloatEvaluator floatEvaluator;
    private IntEvaluator intEvaluator;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    enum DragState {
        Open, Close;
    }

    private DragState mCurrentState = DragState.Close;

    private void init() {
        viewDragHelper = ViewDragHelper.create(SlideMenu.this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    public DragState getmCurrentState() {
        return mCurrentState;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideMenu only hava tow children View");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     * 该方法onMeasure方法执行之后，在此View初始化自己和子View的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        mDragRange = width / 2f;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainView || child == menuView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) mDragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0) {
                    left = 0;
                } else if (left > mDragRange) {
                    left = (int) mDragRange;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                menuView.layout(0, 0, menuView.getWidth(), menuView.getHeight());
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) {
                    newLeft = 0;
                }
                if (newLeft > mDragRange) {
                    newLeft = (int) mDragRange;
                }
                mainView.layout(newLeft, mainView.getTop() + dy, newLeft + mainView.getMeasuredWidth(), mainView.getBottom() + dy);

            }
            float offsets = mainView.getLeft() / mDragRange;
            excuteAnim(offsets);
            if (offsets == 0 && mCurrentState != DragState.Close) {
                mCurrentState = DragState.Close;
                if (listener != null) {
                    listener.close();
                }
            }
            if (offsets == 1 && mCurrentState != DragState.Open) {
                mCurrentState = DragState.Open;
                if (listener != null) {
                    listener.open();
                }
            }
            if (listener != null) {
                listener.draging(offsets);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < mDragRange / 2) {
                //左边
                close();
            } else {
                //右边
                open();
            }
            if (xvel>200&&mCurrentState!=DragState.Open){
                open();
            }else if (xvel<-200&&mCurrentState!=DragState.Close){
                close();
            }
        }

    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, (int) mDragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void excuteAnim(float offsets) {
        //mainView缩小
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(offsets, 1f, 0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(offsets, 1f, 0.8f));
        //移动menuView
        ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(offsets, -menuView.getWidth() / 2, 0));
        //放大menuView
        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(offsets, 0.5f, 1f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(offsets, 0.5f, 1f));
        //设置menuView的透明度
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(offsets, 0.2f, 1));
        //加上过滤
        //     getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(offsets, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private onDragStateChangeListener listener;

    public void setOnDragStateChangeListener(onDragStateChangeListener listener) {
        this.listener = listener;
    }

    public interface onDragStateChangeListener {
        void open();

        void close();

        void draging(float offsets);
    }
}
