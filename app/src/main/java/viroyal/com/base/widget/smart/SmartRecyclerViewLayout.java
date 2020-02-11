package viroyal.com.base.widget.smart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import viroyal.com.base.R;
import viroyal.com.base.util.Utils;
import viroyal.com.base.widget.smart.adapter.BaseSimpleSmartRecyclerAdapter;
import viroyal.com.base.widget.smart.listener.SmartRecyclerDataLoadListener;
import viroyal.com.base.widget.smart.listener.SmartRecyclerListener;

/**
 * @author chenjunwei
 * @date 2019/1/29
 * @des 刷新列表:包含失败、加载、列表
 */
public class SmartRecyclerViewLayout extends RelativeLayout implements SmartRecyclerListener {
  private Context mContext;

  private LayoutInflater layoutInflater;
  /**
   * 刷新布局
   */
  private SmartRefreshLayout smartRefreshLayout;
  private RecyclerView recyclerView;
  /**
   * 空数据的布局
   */
  private LinearLayout empty_layout;
  private ImageView empty_tip_iv;
  private TextView empty_tip_tv;
  /**
   * 正在加载
   */
  private LinearLayout request_layout;
  private ImageView request_loading_iv;
  /**
   * 失败
   */
  private LinearLayout failure_layout;
  private ImageView failure_iv;
  private TextView failure_tip_tv;
  private TextView failure_try_tv;

  private BaseSimpleSmartRecyclerAdapter adapter;
  private RecyclerView.LayoutManager layoutManager;
  /**
   * 头部的View
   */
  private View headerView;

  private SmartRecyclerDataLoadListener smartRecycleDataLoadListener;

  public void setSmartRecycleDataLoadListener(SmartRecyclerDataLoadListener smartRecycleDataLoadListener) {
    this.smartRecycleDataLoadListener = smartRecycleDataLoadListener;
  }

  public SmartRecyclerViewLayout(Context context) {
    this(context, null);
  }

  public SmartRecyclerViewLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SmartRecyclerViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    this.layoutInflater = LayoutInflater.from(mContext);
    initView(mContext);
  }

  private void initView(Context mContext) {
    recyclerView = new RecyclerView(mContext);
    layoutManager = new LinearLayoutManager(mContext);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    smartRefreshLayout = new SmartRefreshLayout(mContext);
    smartRefreshLayout.setDragRate(1.0f);
    smartRefreshLayout.setReboundInterpolator(new LinearInterpolator());
    // 下拉刷新提示
    RefreshHeader refreshHeader = new ClassicsHeader(mContext);
    smartRefreshLayout.setRefreshHeader(refreshHeader);
    // 上拉加载提示
    RefreshFooter refreshFooter = new ClassicsFooter(mContext);
    smartRefreshLayout.setRefreshFooter(refreshFooter);
    smartRefreshLayout.setRefreshContent(recyclerView);
    smartRefreshLayout.setEnableLoadMore(false);
    //loading
    request_layout = (LinearLayout) layoutInflater.inflate(R.layout.smart_recycler_request, null);
    request_loading_iv = request_layout.findViewById(R.id.request_loading_iv);
    //empty
    empty_layout = (LinearLayout) layoutInflater.inflate(R.layout.smart_recycler_empty, null);
    empty_tip_iv = empty_layout.findViewById(R.id.empty_tip_iv);
    empty_tip_tv = empty_layout.findViewById(R.id.empty_tip_tv);
    //fail
    failure_layout = (LinearLayout) layoutInflater.inflate(R.layout.smart_recycler_failure, null);
    failure_iv = failure_layout.findViewById(R.id.failure_iv);
    failure_tip_tv = failure_layout.findViewById(R.id.failure_tip_tv);
    failure_try_tv = failure_layout.findViewById(R.id.failure_try_tv);

    //add
    addView(smartRefreshLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    addView(empty_layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    addView(request_layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    addView(failure_layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    showLoading();
    setListener();
  }


  private void setListener() {
    smartRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
      @Override
      public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (null != smartRecycleDataLoadListener) {
          smartRecycleDataLoadListener.onLoadMore(SmartRecyclerViewLayout.this, false);
        }
      }

      @Override
      public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (null != smartRecycleDataLoadListener) {
          smartRecycleDataLoadListener.onLoadMore(SmartRecyclerViewLayout.this, true);
        }
      }

      @Override
      public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
      }
    });


    //点此重试
    failure_try_tv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.setVisibility(empty_layout, View.GONE);
        Utils.setVisibility(failure_layout, View.GONE);
        Utils.setVisibility(smartRefreshLayout, View.GONE);
        Utils.setVisibility(request_layout, View.VISIBLE);
        showLoading();
        startRefresh();
      }
    });
  }

  /**
   * 展示内容layout
   */
  @Override
  public void showSuccess() {
    stopRefresh();
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(failure_layout, View.GONE);
    Utils.setVisibility(smartRefreshLayout, VISIBLE);
    empty_layout.setOnClickListener(null);
  }

  @Override
  public void showData() {
    if (adapter == null) {
      return;
    }
    //包括头布局
    int childCount = adapter.getItemCount();
    if (childCount == 0) {
      showEmpty();
    } else {
      showSuccess();
    }
  }

  /**
   * 显示空数据页面
   */
  @Override
  public void showEmpty() {
    stopRefresh();
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(failure_layout, GONE);
    // 设置显示 让显示empty_layout 也能刷新请求
    Utils.setVisibility(smartRefreshLayout, GONE);
    empty_layout.setOnClickListener(null);
    Utils.setVisibility(empty_layout, VISIBLE);
  }

  /**
   * 显示数据获取失败页面  如网络失败等..
   */
  @Override
  public void showFailure() {
    stopRefresh();
    Utils.setVisibility(failure_layout, View.VISIBLE);
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(smartRefreshLayout, GONE);
    Utils.setText(failure_tip_tv, Utils.getString(mContext, R.string.smart_failure_tip));
    failure_iv.setImageResource(R.mipmap.ic_failure);
  }

  @Override
  public void showNoNetFailure() {
    stopRefresh();
    Utils.setVisibility(failure_layout, View.VISIBLE);
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(smartRefreshLayout, GONE);
    Utils.setText(failure_tip_tv, Utils.getString(mContext, R.string.smart_no_net_failure_tip));
    failure_iv.setImageResource(R.mipmap.ic_failure_no_network);
  }

  @Override
  public void showNetFailure() {
    stopRefresh();
    Utils.setVisibility(failure_layout, View.VISIBLE);
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(smartRefreshLayout, GONE);
    Utils.setText(failure_tip_tv, Utils.getString(mContext, R.string.smart_net_failure_tip));
    failure_iv.setImageResource(R.mipmap.ic_failure_network);
  }

  public void stopLoadMore() {
    stopRefresh();
    Utils.setVisibility(failure_layout, GONE);
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(request_layout, GONE);
    Utils.setVisibility(smartRefreshLayout, VISIBLE);
    smartRefreshLayout.finishLoadMoreWithNoMoreData();
  }

  /**
   * 显示加载中的页面
   */
  public void showLoading() {
    stopRefresh();
    Glide.with(mContext).load(R.drawable.request_layout_gif).into(request_loading_iv);
    Utils.setVisibility(request_layout, VISIBLE);
    Utils.setVisibility(empty_layout, GONE);
    Utils.setVisibility(failure_layout, View.GONE);
    Utils.setVisibility(smartRefreshLayout, GONE);
    empty_layout.setOnClickListener(null);
  }

  /**
   * 指定页面需要设置背景色  如：短视评论背景色是黑的
   *
   * @param color
   */
  public void setViewBackGround(int color) {
    request_layout.setBackgroundColor(color);
    failure_layout.setBackgroundColor(color);
    empty_layout.setBackgroundColor(color);
    smartRefreshLayout.setBackgroundColor(color);
  }

  public RecyclerView getRecyclerView() {
    return recyclerView;
  }

  /**
   * 自动刷新
   * 指定高度为 1.75f:刷新头高度
   */
  public void autoRefresh() {
    smartRefreshLayout.autoRefresh();
  }

  public SmartRefreshLayout getSmartRefreshLayout() {
    return smartRefreshLayout;
  }

  @Override
  public BaseSimpleSmartRecyclerAdapter getAdapter() {
    return adapter;
  }

  @Override
  public void stopRefresh() {
    smartRefreshLayout.finishRefresh();
    smartRefreshLayout.finishLoadMore();
  }

  @Override
  public void startRefresh() {
    if (null != smartRecycleDataLoadListener) {
      smartRecycleDataLoadListener.onLoadMore(SmartRecyclerViewLayout.this, true);
    }
  }

  /**
   * 设置适配器
   *
   * @param adapter
   */
  public void setAdapter(BaseSimpleSmartRecyclerAdapter adapter) {
    this.adapter = adapter;
    recyclerView.setAdapter(adapter);
  }

  /**
   * 是否可以加载更多
   *
   * @param loadEnable
   */
  @Override
  public void setPullLoadEnable(final boolean loadEnable) {
    smartRefreshLayout.post(new Runnable() {
      @Override
      public void run() {
        smartRefreshLayout.setEnableLoadMore(loadEnable);
      }
    });

  }

  /**
   * 是否可以刷新
   *
   * @param pullRefreshEnable
   */
  @Override
  public void setPullRefreshEnable(boolean pullRefreshEnable) {
    smartRefreshLayout.setEnableRefresh(pullRefreshEnable);
  }

  /**
   * 设置RecycleView的布局类型，线性还是网格类型
   *
   * @param layoutManager
   */
  public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    recyclerView.setLayoutManager(layoutManager);
  }

  public void setEmptyTip(String emptyTip) {
    Utils.setText(empty_tip_tv, emptyTip);
  }

  public void setEmptyTipIv(int emptyTipId) {
    empty_tip_iv.setImageResource(emptyTipId);
  }

  public View getHeaderView() {
    return headerView;
  }

  public void setHeaderView(View headerView) {
    this.headerView = headerView;
    if (null != adapter && null != headerView) {
      adapter.addHeaderView(headerView);
    }
  }

  public void setHeaderView(View headerView, int index) {
    this.headerView = headerView;
    if (null != adapter && null != headerView) {
      adapter.addHeaderView(headerView, index);
    }
  }

  public void removeHeaderView() {
    if (null != adapter && null != headerView) {
      adapter.removeHeaderView(headerView);
      headerView = null;
    }
  }

}
