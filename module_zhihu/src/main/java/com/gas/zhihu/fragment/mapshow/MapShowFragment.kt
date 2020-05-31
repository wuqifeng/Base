package com.gas.zhihu.fragment.mapshow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.base.lib.base.BaseFragment
import com.base.lib.di.component.AppComponent
import com.base.lib.util.ArmsUtils
import com.base.paginate.interfaces.EmptyInterface
import com.base.paginate.interfaces.OnMultiItemClickListeners
import com.base.paginate.viewholder.PageViewHolder

import com.gas.zhihu.fragment.mapshow.di.DaggerMapShowComponent
import com.gas.zhihu.fragment.mapshow.di.MapShowModule
import com.gas.zhihu.fragment.mapshow.mvp.MapShowContract
import com.gas.zhihu.fragment.mapshow.mvp.MapShowPresenter

import com.gas.zhihu.R
import com.gas.zhihu.app.ZhihuConstants
import com.gas.zhihu.bean.PaperShowBean
import com.gas.zhihu.fragment.mapshow.bean.ISortBean
import com.gas.zhihu.utils.OfficeHelper
import com.lib.commonsdk.utils.Utils
import kotlinx.android.synthetic.main.zhihu_fragment_map_show.*
import kotlinx.android.synthetic.main.zhihu_fragment_map_show.guideTitle
import kotlinx.android.synthetic.main.zhihu_fragment_map_show.itemRecycler
import kotlinx.android.synthetic.main.zhihu_fragment_map_show.itemRefresh
import kotlinx.android.synthetic.main.zhihu_fragment_pager.*
import java.io.File
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by GasMvpFragment on 05/31/2020 15:45
 * ================================================
 */
/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class MapShowFragment : BaseFragment<MapShowPresenter>(), MapShowContract.View {
    companion object {

        const val TYPE="type"

        fun setPagerArgs(type:Int): Bundle? {
            val args = Bundle()
            args.putInt(TYPE, type)
            return args
        }

        fun newInstance(): MapShowFragment {
            val fragment = MapShowFragment()
            return fragment
        }

    }

    @Inject
    lateinit var mAdapter: MapShowAdapter

    @Inject
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var mType:Int =0

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerMapShowComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mapShowModule(MapShowModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.zhihu_fragment_map_show, container, false);
    }

    override fun onResume() {
        super.onResume()
        mPresenter!!.initOriginData(mType)
        mPresenter!!.getFilterData("")
    }


    override fun initData(savedInstanceState: Bundle?) {

        mType=activity!!.intent.getIntExtra(TYPE,0)
        when(mType){
            0->{}
            1->{}
        }
        guideTitle.setOnBackListener {
            killMyself()
        }
        mAdapter.setOnMultiItemClickListener(object : OnMultiItemClickListeners<ISortBean?> {
            override fun onItemClick(viewHolder: PageViewHolder?, data: ISortBean?, position: Int, viewType: Int) {
                data?.let {
                    Log.e("TAG",data.showChar)
                }
            }
        })
        itemRefresh.isEnabled=false
        ArmsUtils.configRecyclerView(itemRecycler,mLayoutManager)
        itemRecycler.adapter=mAdapter
        mAdapter.setEmptyView(EmptyInterface.STATUS_LOADING)

    }

    override fun setData(data: Any?) {

    }

    override fun getWrapActivity(): Activity {
        return activity!!
    }

    override fun setMapData(t: List<ISortBean>) {
        mAdapter.setNewData(t)
        if(t.isNotEmpty()){
            itemRecycler.smoothScrollToPosition(0)
            mAdapter.loadEnd()
        }else{
            mAdapter.setEmptyView(EmptyInterface.STATUS_EMPTY)
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }
}
