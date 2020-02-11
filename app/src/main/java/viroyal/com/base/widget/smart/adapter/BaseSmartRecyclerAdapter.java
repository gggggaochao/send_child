package viroyal.com.base.widget.smart.adapter;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author chenjunwei
 * @Description An abstract adapter which can be extended for Recyclerview
 * @date 2018/8/27
 */
public abstract class BaseSmartRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    /**
     * 头部
     */
    protected LinearLayout mHeaderLayout;
    /**
     * 底部
     */
    protected LinearLayout mFooterLayout;
    /**
     * 列表数据
     */
    protected ArrayList<T> items = new ArrayList<>();

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPES.FOOTER) {
            removeViewFromParent(mFooterLayout);
            VH viewHolder = getViewHolder(mFooterLayout);
            return viewHolder;
        } else if (viewType == VIEW_TYPES.HEADER) {
            removeViewFromParent(mHeaderLayout);
            VH viewHolder = getViewHolder(mHeaderLayout);
            return viewHolder;
        }
        return onCreateViewHolder(parent, viewType, true);
    }

    public void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == VIEW_TYPES.FOOTER || type == VIEW_TYPES.HEADER) {
            setFullSpan(holder);
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == VIEW_TYPES.HEADER || type == VIEW_TYPES.FOOTER) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * add one new data in to certain location
     *
     * @param position
     */
    public void insert(@IntRange(from = 0) int position, @NonNull T data) {
        items.add(position, data);
        notifyItemInserted(position + getStart());
    }

    /**
     * add one new data
     */
    public void insert(@NonNull T data) {
        items.add(data);
        notifyItemInserted(items.size() + getStart());
    }

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param position
     */
    public void remove(@IntRange(from = 0) int position) {
        items.remove(position);
        int internalPosition = position + getStart();
        notifyItemRemoved(internalPosition);
        notifyItemRangeChanged(internalPosition, items.size() - internalPosition);
    }

    /**
     * change data
     */
    public void changeData(@IntRange(from = 0) int index, @NonNull T data) {
        items.set(index, data);
        notifyItemChanged(index + getStart());
    }

    /**
     * add new data in to certain location
     *
     * @param position the insert position
     * @param newData  the new data collection
     */
    public void insert(@IntRange(from = 0) int position, @NonNull Collection<? extends T> newData) {
        items.addAll(position, newData);
        notifyItemRangeInserted(position + getStart(), newData.size());
    }

    /**
     * add new data to the end of items
     *
     * @param newData the new data collection
     */
    public void insert(@NonNull Collection<? extends T> newData) {
        items.addAll(newData);
        notifyItemRangeInserted(items.size() - newData.size() + getStart(), newData.size());
    }

    /**
     * use data to replace all item in items.
     * it doesn't change the items reference
     *
     * @param data data collection
     */
    public void replaceData(@NonNull Collection<? extends T> data) {
        // 不是同一个引用才清空列表
        if (data != items) {
            items.clear();
            items.addAll(data);
        }
        notifyDataSetChanged();
    }


    /**
     * Get the data of list
     *
     * @return 列表数据
     */
    public ArrayList<T> getItems() {
        return items;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Nullable
    public T getItem(@IntRange(from = 0) int position) {
        if (position < items.size()) {
            return items.get(position);
        } else {
            return null;
        }

    }

    /**
     * Return root layout of header
     */

    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    /**
     * Return root layout of footer
     */
    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    /**
     * Append header to the rear of the mHeaderLayout.
     *
     * @param header
     */
    public int addHeaderView(View header) {
        return addHeaderView(header, -1);
    }

    /**
     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
     * When index = -1 or index big= child count in mHeaderLayout,
     * the effect of this method is the same as that of {@link #addHeaderView(View)}.
     *
     * @param header
     * @param index  the position in mHeaderLayout of this header.
     *               When index = -1 or index big= child count in mHeaderLayout,
     *               the effect of this method is the same as that of {@link #addHeaderView(View)}.
     */
    public int addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    /**
     * @param header
     * @param index
     * @param orientation
     */
    public int addHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mHeaderLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mHeaderLayout.addView(header, index);
        if (mHeaderLayout.getChildCount() == 1) {
            //第一次需要插入 插入第0个位置
            notifyItemInserted(0);
        }
        return index;
    }

    public int setHeaderView(View header) {
        return setHeaderView(header, 0, LinearLayout.VERTICAL);
    }

    public int setHeaderView(View header, int index) {
        return setHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public int setHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() <= index) {
            return addHeaderView(header, index, orientation);
        } else {
            mHeaderLayout.removeViewAt(index);
            mHeaderLayout.addView(header, index);
            return index;
        }
    }

    /**
     * Append footer to the rear of the mFooterLayout.
     *
     * @param footer
     */
    public int addFooterView(View footer) {
        return addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    /**
     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
     * When index = -1 or index big= child count in mFooterLayout,
     * the effect of this method is the same as that of {@link #addFooterView(View)}.
     *
     * @param footer
     * @param index  the position in mFooterLayout of this footer.
     *               When index = -1 or index big= child count in mFooterLayout,
     *               the effect of this method is the same as that of {@link #addFooterView(View)}.
     */
    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            notifyItemInserted(getStart() + items.size());
        }
        return index;
    }

    public int setFooterView(View header) {
        return setFooterView(header, 0, LinearLayout.VERTICAL);
    }

    public int setFooterView(View header, int index) {
        return setFooterView(header, index, LinearLayout.VERTICAL);
    }

    public int setFooterView(View header, int index, int orientation) {
        if (mFooterLayout == null || mFooterLayout.getChildCount() <= index) {
            return addFooterView(header, index, orientation);
        } else {
            mFooterLayout.removeViewAt(index);
            mFooterLayout.addView(header, index);
            return index;
        }
    }

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    public void removeHeaderView(View header) {
        if (getStart() == 0) {
            return;
        }
        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            notifyItemRemoved(0);
        }
    }

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    public void removeFooterView(View footer) {
        if (getFooter() == 0) {
            return;
        }

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            notifyItemRemoved(getStart() + items.size());
        }
    }

    /**
     * remove all header view from mHeaderLayout and set null to mHeaderLayout
     */
    public void removeAllHeaderView() {
        if (getStart() == 0) {
            return;
        }

        mHeaderLayout.removeAllViews();
        notifyItemRemoved(0);
    }

    /**
     * remove all footer view from mFooterLayout and set null to mFooterLayout
     */
    public void removeAllFooterView() {
        if (getFooter() == 0) {
            return;
        }
        mFooterLayout.removeAllViews();
        notifyItemRemoved(getStart() + items.size());
    }


    public abstract VH getViewHolder(View view);

    /**
     * 会调用此方法来判断是否显示空布局，返回true就会显示空布局<br/>
     * 如有特殊需要，可重写此方法
     *
     * @return
     */
    public boolean isEmpty() {
        return getAdapterItemCount() == 0;
    }

    /**
     * @param parent
     * @param viewType
     * @param isItem   如果是true，才需要做处理 ,但是这个值总是true
     */
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem);

    /**
     * 替代onBindViewHolder方法，实现这个方法就行了
     *
     * @param holder
     * @param position
     */
    public abstract void onBindViewHolder(VH holder, int position, boolean isItem);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int start = getStart();
        if (!isHeader(position) && !isFooter(position)) {
            onBindViewHolder(holder, position - start, true);
        }
    }

    public boolean isFooter(int position) {
        int start = getStart();
        return mFooterLayout != null && position >= getAdapterItemCount() + start;
    }

    public boolean isHeader(int position) {
        return getStart() > 0 && position == 0;
    }


    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_TYPES.HEADER;
        } else if (isFooter(position)) {
            return VIEW_TYPES.FOOTER;
        } else {
            position = getStart() > 0 ? position - 1 : position;
            return getAdapterItemViewType(position);
        }
    }

    /**
     * 实现此方法来设置viewType
     *
     * @param position
     * @return viewType
     */
    public int getAdapterItemViewType(int position) {
        return VIEW_TYPES.NORMAL;
    }

    public int getStart() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    public int getFooter() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        int count = getAdapterItemCount();
        count += getStart();
        if (mFooterLayout != null) {
            count++;
        }
        return count;
    }

    /**
     * Returns the number of items in the adapter bound to the parent
     * RecyclerView.
     *
     * @return The number of items in the bound adapter
     */
    public abstract int getAdapterItemCount();

    /**
     * 类型
     */
    protected class VIEW_TYPES {
        public static final int FOOTER = -1;
        public static final int HEADER = -3;
        public static final int NORMAL = -4;
    }
}