package ysg.gdcp.cn.qqslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

import ysg.gdcp.cn.qqslidemenu.ui.MyLinearLayout;
import ysg.gdcp.cn.qqslidemenu.ui.SlideMenu;

public class MainActivity extends AppCompatActivity {

    private ListView menuLv;
    private ListView mainLv;
    private SlideMenu slideMenu;
    private ImageView ivHead;
    private MyLinearLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initData() {
        menuLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            /**
             * 此方法可以得到系统定义的View
             * @param position
             * @param convertView
             * @param parent
             * @return
             */
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        mainLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(360).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(360).start();
                return view;
            }
        });
        slideMenu.setOnDragStateChangeListener(new SlideMenu.onDragStateChangeListener() {
            @Override
            public void open() {
                menuLv.smoothScrollToPosition(new Random().nextInt(menuLv.getCount()));
            }

            @Override
            public void close() {
                ViewPropertyAnimator.animate(ivHead).translationXBy(15).setInterpolator(new CycleInterpolator(6)).setDuration(500).start();

            }

            @Override
            public void draging(float offsets) {
                ViewHelper.setAlpha(ivHead, 1 - offsets);

            }
        });
        myLayout.setSlideMenu(slideMenu);
    }

    private void initViews() {
        menuLv = (ListView) findViewById(R.id.menu_listview);
        mainLv = (ListView) findViewById(R.id.main_listview);
        slideMenu = (SlideMenu) findViewById(R.id.slidemenu);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        myLayout = (MyLinearLayout)findViewById(R.id.my_layout);

    }
}
