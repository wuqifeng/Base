package trunk.doi.base.ui.fragment.classify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.base.baseui.view.status.Gloading;
import com.base.lib.base.LazyLoadFragment;
import com.base.lib.di.component.AppComponent;
import com.base.paginate.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import trunk.doi.base.R;
import trunk.doi.base.adapter.GankItemAdapter;
import trunk.doi.base.bean.GankItemData;
import trunk.doi.base.ui.activity.utils.WebViewActivity;
import trunk.doi.base.ui.fragment.classify.di.DaggerClassifyComponent;
import trunk.doi.base.ui.fragment.classify.mvp.ClassifyContract;
import trunk.doi.base.ui.fragment.classify.mvp.ClassifyPresenter;

/**
 * Author:
 * Time: 2016/8/12 14:28
 */
public class ClassifyFragment extends LazyLoadFragment<ClassifyPresenter> implements ClassifyContract.View, SwipeRefreshLayout.OnRefreshListener {

    private static final String SUB_TYPE = "SUB_TYPE";
    private int mPage = 1;//页数
    private final static int PAGE_COUNT = 10;//每页条数
    private String mSubtype;//分类
    private GankItemAdapter mGankItemAdapter;//适配器

    @BindView(R.id.type_item_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.type_item_swipfreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;//进度条

    protected Gloading.Holder mHolder;


    @Override
    public int initLayoutId() {
        return R.layout.layout_base_refresh_recycler;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

        DaggerClassifyComponent
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);

    }


    @Override
    public void initView(@NonNull View view, @Nullable Bundle savedInstanceState) {


        mHolder = Gloading.getDefault().wrap(mRecyclerView).withRetry(() -> {
            mHolder.showLoading();
            loadData();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //刷新控件
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.cff3e19));
        mGankItemAdapter = new GankItemAdapter(mContext, new ArrayList<>());


        //条目点击
        mGankItemAdapter.setOnMultiItemClickListener((viewHolder, data, position, viewType) -> mContext.startActivity(new Intent(mContext, WebViewActivity.class)
                .putExtra("title", data.getDesc())
                .putExtra("url", data.getUrl())));

        mGankItemAdapter.setOnLoadMoreListener(isReload -> loadData());
        assert getArguments() != null;
        mSubtype = getArguments().getString(SUB_TYPE);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHolder.showLoading();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mGankItemAdapter);
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {


    }


    private void loadData() {
        if (null != mPresenter)
            mPresenter.getGankItemData(String.format(Locale.CHINA, "data/%s/" + PAGE_COUNT + "/%d", mSubtype, mPage));
    }


    public static ClassifyFragment newInstance(String subtype) {
        ClassifyFragment fragment = new ClassifyFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SUB_TYPE, subtype);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public void onSuccess(List<GankItemData> data) {

        mHolder.showLoadSuccess();

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (data.size() > 0) {

            if (mPage == 1) {
                mGankItemAdapter.setNewData(data);
            } else {
                mGankItemAdapter.setLoadMoreData(data);
            }
            if (data.size() < PAGE_COUNT) {//如果小于10个
                mGankItemAdapter.loadEnd();
            }
            mPage++;
        } else {
            if (mPage > 1) {
                mGankItemAdapter.showNormal();
            } else {
                mHolder.showLoadFailed();
            }
        }

    }

    @Override
    public void onError() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mPage > 1) {
            mGankItemAdapter.showNormal();
        } else {
            mHolder.showLoadFailed();
        }
    }


    @Override
    protected void lazyLoadData() {

        loadData();
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadData();
    }

    public void scrollToTop() {

        mRecyclerView.smoothScrollToPosition(0);
    }


}
