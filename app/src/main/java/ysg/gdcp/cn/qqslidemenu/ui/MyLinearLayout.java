package ysg.gdcp.cn.qqslidemenu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/3/23 07:53.
 *
 * @author ysg
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu!=null&&slideMenu.getmCurrentState()== SlideMenu.DragState.Open){
            return  true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu!=null&&slideMenu.getmCurrentState()== SlideMenu.DragState.Open){
            if (event.getAction()==MotionEvent.ACTION_UP){
               slideMenu.close();
            }
            return  true;
        }
        return super.onTouchEvent(event);
    }
}
