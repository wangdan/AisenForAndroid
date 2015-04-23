package com.example.aisensample.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.example.aisensample.R;
import com.example.aisensample.support.bean.MenuBean;
import com.example.aisensample.ui.fragment.ABaseSample;
import com.example.aisensample.ui.fragment.MenuFragment;
import com.m.support.inject.ViewInject;
import com.m.ui.activity.basic.BaseActivity;

/**
 * Created by wangdan on 15/4/23.
 */
public class MainActivity extends BaseActivity {

    @ViewInject(id = R.id.drawer)
    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private MenuFragment menuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                getToolbar(), R.string.draw_open, R.string.draw_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        menuFragment = MenuFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.menu_frame, menuFragment, "MenuFragment").commit();
    }

    public void onMenuSelected(MenuBean bean) {
        Fragment fragment = null;

        switch (Integer.parseInt(bean.getType())) {
        case 0:
            fragment = ABaseSample.newInstance();
            break;
        }

        closeDrawer();

        getSupportActionBar().setTitle(bean.getTitleRes());

        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "Main").commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

}
