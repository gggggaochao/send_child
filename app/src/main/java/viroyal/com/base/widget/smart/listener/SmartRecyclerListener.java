package viroyal.com.base.widget.smart.listener;


import viroyal.com.base.widget.smart.adapter.BaseSimpleSmartRecyclerAdapter;

/**
 * @author chenjunwei
 * @Description SmartRecycleListener接口便于使用
 * @date 2018/8/2
 */
public interface SmartRecyclerListener {
    /**
     * 获取适配器
     * @return
     */
    BaseSimpleSmartRecyclerAdapter getAdapter();

    /**
     * 设置是否可刷新
     * @param enable
     */
    void setPullRefreshEnable(boolean enable);

    /**
     * 设置是否可刷新 加载
     * @param enable
     */
    void setPullLoadEnable(boolean enable);

    /**
     * 展示数据
     */
    void showData();

    /**
     * 展示空数据
     */
    void showEmpty();

    /**
     * 展示没有网络失败
     */
    void showNoNetFailure();

    /**
     * 展示有网络失败
     */
    void showNetFailure();

    /**
     * 服务器请求失败
     */
    void showFailure();

    /**
     * 结束刷新
     */
    void stopRefresh();

    /**
     * 开始刷新
     */
    void startRefresh();

    void showSuccess();
}
