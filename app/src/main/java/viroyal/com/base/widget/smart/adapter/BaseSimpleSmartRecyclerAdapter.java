package viroyal.com.base.widget.smart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author chenjunwei
 * @date 2019/1/29
 */
public class BaseSimpleSmartRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends BaseSmartRecyclerAdapter<T, VH> {
    protected LayoutInflater inflater;
    protected Context mContext;
    protected int selected = -1;

    public BaseSimpleSmartRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void appendData(@NonNull Collection<? extends T> newData) {
        if (newData == null) {
            return;
        }
        insert(newData);
    }

    public void clearData() {
        items.clear();
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @Override
    public VH getViewHolder(View view) {
        return null;
    }

    /**
     * 引入布局  设置ViewHolder
     *
     * @param parent
     * @param viewType
     * @param isItem
     * @return
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        return null;
    }

    /**
     * 设置单个view的数据
     *
     * @param holder
     * @param position
     * @param isItem
     */
    @Override
    public void onBindViewHolder(VH holder, int position, boolean isItem) {

    }


    @Override
    public int getAdapterItemCount() {
        return items == null ? 0 : items.size();
    }
}
