package org.aisen.wen.ui.view;

import android.view.ViewGroup;

import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.itemview.IItemViewCreator;
import org.aisen.wen.ui.presenter.impl.APagingPresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdan on 16/10/12.
 */
public interface IPaingView<Item extends Serializable, Result extends Serializable, Header extends Serializable, V extends ViewGroup> extends IContentView {

    class RefreshConfig implements Serializable {

        private static final long serialVersionUID = 6244426943442129360L;

        public boolean pagingEnd = false;// 分页是否结束

        public String positionKey = null;// 最后阅读坐标的Key，null-不保存，针对缓存数据有效

        public boolean displayWhenScrolling = true;// 滚动的时候加载图片

        public int releaseDelay = 5 * 1000;// 当配置了releaseItemIds参数时，离开页面后自动释放资源

        public int[] releaseItemIds = null;// 离开页面时，释放图片的控件，针对ItemView

        public String emptyHint = "数据为空";// 如果EmptyLayout中有R.id.txtLoadEmpty这个控件，将这个提示绑定显示

        public boolean footerMoreEnable = true;// FooterView加载更多

    }

    interface IPagingViewCallback {

        void onPullDownToRefresh();

        void onPullUpToRefresh();

    }

    RefreshConfig getRefreshConfig();

    int getLastReadPosition();

    void putLastReadPosition(int position);

    int getLastReadTop();

    void putLastReadTop(int top);

    void toLastReadPosition();

    void setupRefreshViewWithConfig(RefreshConfig config);

    void bindAdapter(IPagingAdapter adapter);

    IPagingAdapter getAdapter();

    IPagingAdapter<Item> newAdapter(ArrayList<Item> datas);

    void setPagingViewCallback(IPagingViewCallback callback);

    boolean setRefreshViewToLoading();

    /**
     * 每次调用接口，获取新的数据时调用这个方法
     *
     * @param mode
     *            当次拉取数据的类型
     * @param datas
     *            当次拉取的数据
     * @return <tt>false</tt> 如果mode={@link APagingPresenter.RefreshMode#reset}
     *         默认清空adapter中的数据
     */
    boolean handleResult(APagingPresenter.RefreshMode mode, List<Item> datas);

    IItemViewCreator<Item> newItemViewCreator();

    /**
     * 列表控件
     *
     * @return
     */
    V getRefreshView();

}
