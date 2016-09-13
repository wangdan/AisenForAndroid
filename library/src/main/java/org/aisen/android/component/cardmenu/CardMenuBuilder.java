package org.aisen.android.component.cardmenu;

import android.app.Activity;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by wangdan on 16/9/13.
 */
public class CardMenuBuilder extends MenuBuilder {

    int dropDownGravity = -1;
    // 设置ListPopup的Offset
    int dropDownHorizontalOffset;
    int dropDownVerticalOffset;

    private CardMenuPresenter mPresenter;
    private OnCardMenuCallback onCardMenuCallback;

    public CardMenuBuilder(Activity context, View anchorView) {
        super(context);

        mPresenter = new CardMenuPresenter(context, anchorView);
        mPresenter.initForMenu(context, this);
        addMenuPresenter(mPresenter);
    }

    @Override
    public MenuItem add(int group, int id, int categoryOrder, CharSequence title) {
        MenuItem item = super.add(group, id, categoryOrder, title);

        if (item instanceof MenuItemImpl) {
            ((MenuItemImpl) item).setOnMenuItemClickListener(onMenuItemClickListener);
        }

        return item;
    }

    public CardMenuBuilder setGravity(int gravity) {
        dropDownGravity = gravity;
        return this;
    }

    public CardMenuBuilder setDropDownHorizontalOffset(int offset) {
        dropDownHorizontalOffset = offset;
        return this;
    }

    public CardMenuBuilder setDropDownVerticalOffset(int offset) {
        dropDownVerticalOffset = offset;
        return this;
    }

    public CardMenuBuilder inflate(int menuRes) {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).getMenuInflater().inflate(menuRes, this);
        }

        return this;
    }

    public CardMenuBuilder add(int id, int titleRes) {
        add(1, id, 1, titleRes);

        return this;
    }

    public CardMenuBuilder add(int id, String title) {
        add(1, id, 1, title);

        return this;
    }

    public CardMenuBuilder setOnCardMenuCallback(OnCardMenuCallback onCardMenuCallback) {
        this.onCardMenuCallback = onCardMenuCallback;

        return this;
    }

    public void show() {

        mPresenter.showCardMenu();
    }

    private MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (onCardMenuCallback != null) {
                return onCardMenuCallback.onCardMenuItemSelected(item);
            }

            return false;
        }

    };

    public interface OnCardMenuCallback {

        boolean onCardMenuItemSelected(MenuItem menuItem);

    }

}
