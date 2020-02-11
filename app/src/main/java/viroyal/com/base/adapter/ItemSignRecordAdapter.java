package viroyal.com.base.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.suntiago.baseui.utils.date.DateStyle;
import com.suntiago.baseui.utils.date.DateUtils;

import viroyal.com.base.R;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.model.AllLocalSignRecord;
import viroyal.com.base.util.Utils;
import viroyal.com.base.widget.smart.adapter.BaseSimpleSmartRecyclerAdapter;
import viroyal.com.base.widget.smart.adapter.RVBaseViewHolder;

public class ItemSignRecordAdapter extends BaseSimpleSmartRecyclerAdapter<AllLocalSignRecord, RVBaseViewHolder> {

  public ItemSignRecordAdapter(Context mContext) {
    super(mContext);
  }

  @Override
  public RVBaseViewHolder getViewHolder(View view) {
    return new RVBaseViewHolder(view);
  }

  @Override
  public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
    View view = inflater.inflate(R.layout.item_sign_record_layout, parent, false);
    return new RVBaseViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RVBaseViewHolder holder, int position, boolean isItem) {
    super.onBindViewHolder(holder, position, isItem);
    AllLocalSignRecord allLocalSignRecord = items.get(position);
    holder.setTextView(R.id.tv_student_name, mContext.getResources().getString(R.string.student_name, allLocalSignRecord.name));
    holder.setTextView(R.id.tv_union_id, mContext.getResources().getString(R.string.student_union_id, allLocalSignRecord.union_id));
    holder.setTextView(R.id.tv_date_time, mContext.getResources().getString(R.string.student_time,
            DateUtils.formatSecondTimestamp(allLocalSignRecord.swipe_time, DateStyle.YYYY_MM_DD_HH_MM)));

    if (TextUtils.isEmpty(allLocalSignRecord.localImagePath)) {
      holder.setImageResource(R.id.iv_take_photo, R.mipmap.bg_default_avatar);
    } else {
      Utils.loadImageFile(mContext, allLocalSignRecord.localImagePath, holder.retrieveView(R.id.iv_take_photo),
              R.mipmap.bg_default_avatar, R.mipmap.bg_default_avatar);
    }

    if (TextUtils.equals(allLocalSignRecord.sign_status, ConstantsYJ.ParamsTag.SIGN_IN)) {
      holder.setVisibiity(R.id.iv_sign_in, true);
      holder.setVisibiity(R.id.iv_sign_out, false);
    } else if (TextUtils.equals(allLocalSignRecord.sign_status, ConstantsYJ.ParamsTag.SIGN_OUT)) {
      holder.setVisibiity(R.id.iv_sign_in, false);
      holder.setVisibiity(R.id.iv_sign_out, true);
    }

    holder.itemView.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {

      }
    });
  }
}
