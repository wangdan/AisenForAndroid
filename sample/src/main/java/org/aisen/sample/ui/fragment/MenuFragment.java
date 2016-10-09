package org.aisen.sample.ui.fragment;

/**
 * Created by wangdan on 15/4/23.
 */
public class MenuFragment {
//        extends AListFragment<MenuBean, ArrayList<MenuBean>, Serializable> {
//
//    public static MenuFragment newInstance() {
//        return new MenuFragment();
//    }
//
//    @Override
//    protected void setupRefreshConfig(RefreshConfig config) {
//        super.setupRefreshConfig(config);
//
//        config.footerMoreEnable = false;
//    }
//
//    @Override
//    public IItemViewCreator<MenuBean> configItemViewCreator() {
//        return new IItemViewCreator<MenuBean>() {
//
//            @Override
//            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
//                return inflater.inflate(R.layout.item_main, parent, false);
//            }
//
//            @Override
//            public IITemView<MenuBean> newItemView(View convertView, int viewType) {
//                return new MainItemView(getActivity(), convertView);
//            }
//
//        };
//    }
//
//    @Override
//    public void requestData(RefreshMode mode) {
//        setItems(generateItems());
//
//        onItemClick(getRefreshView(), null, 0, 0);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ((MainActivity) getActivity()).onMenuSelected(getAdapterItems().get(position));
//    }
//
//    private ArrayList<MenuBean> generateItems() {
//        ArrayList<MenuBean> items = new ArrayList<MenuBean>();
//
//        items.add(new MenuBean(0, R.string.a_base_fragment, R.string.a_base_fragment, "0"));
//
//        return items;
//    }
//
//    class MainItemView extends ARecycleViewItemView<MenuBean> {
//
//        @ViewInject(id = R.id.txtTitle)
//        TextView txtTitle;
//
//        public MainItemView(Activity context, View itemView) {
//            super(context, itemView);
//        }
//
//        @Override
//        public void onBindData(View convertView, MenuBean data, int position) {
//            txtTitle.setText(data.getTitleRes());
//        }
//
//    }

}
