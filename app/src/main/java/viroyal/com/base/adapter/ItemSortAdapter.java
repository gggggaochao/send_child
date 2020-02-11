package viroyal.com.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntiago.baseui.utils.ScreenUtils;

import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.activity.main.MainDelegate;
import viroyal.com.base.face.rsp.DeviceInfoRsp;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.model.Announced;
import viroyal.com.base.util.Utils;


/**
 * @author chenjunwei
 * @desc 分类列表
 * @date 2019/5/22
 */
public class ItemSortAdapter extends RecyclerView.Adapter<ItemSortAdapter.MyViewHolder> {
  private Context context;
  private List<DeviceInfoRsp.Apps> mDeviceApps;
  private MainDelegate mainDelegate;

  public ItemSortAdapter(Context context, List<DeviceInfoRsp.Apps> mDeviceApps, MainDelegate mainDelegate) {
    this.context = context;
    this.mDeviceApps = mDeviceApps;
    this.mainDelegate = mainDelegate;
  }

  public void adapterNotifyDataSetChanged(List<DeviceInfoRsp.Apps> mDeviceApps) {
    this.mDeviceApps = mDeviceApps;
    notifyDataSetChanged();
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_sort_layout, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    final DeviceInfoRsp.Apps apps = mDeviceApps.get(position);
    Utils.loadImage(context, apps.icon_url, holder.iv_sort, 0, 0);
    holder.tv_sort_name.setText(apps.name.ch);
    holder.ll_sort_layout.setLayoutParams(new RecyclerView.LayoutParams(ScreenUtils.getScreenWidth(context) / 6, RecyclerView.LayoutParams.MATCH_PARENT));
    holder.itemView.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        if (null != mainDelegate) {
          Announced announced = new Announced();
          announced.title = apps.name.ch;
          announced.url = apps.href_url;
          mainDelegate.showNoticeDetailsDialogFragment(announced);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return null != mDeviceApps ? mDeviceApps.size() : 0;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout ll_sort_layout;
    private ImageView iv_sort;
    private TextView tv_sort_name;

    public MyViewHolder(View itemView) {
      super(itemView);
      ll_sort_layout = (LinearLayout) itemView.findViewById(R.id.ll_sort_layout);
      iv_sort = (ImageView) itemView.findViewById(R.id.iv_sort);
      tv_sort_name = (TextView) itemView.findViewById(R.id.tv_sort_name);
    }
  }
}
