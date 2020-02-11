package viroyal.com.base.widget;

import android.app.Dialog;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiwinn.base.util.SizeUtils;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.suntiago.baseui.utils.ScreenUtils;
import com.suntiago.baseui.utils.ToastUtils;
import com.suntiago.baseui.utils.date.DateUtils;

import org.kymjs.kjframe.KJDB;

import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.adapter.ItemSignRecordAdapter;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.model.AllLocalSignRecord;
import viroyal.com.base.widget.smart.SmartRecyclerViewLayout;
import viroyal.com.base.widget.smart.listener.SmartRecyclerDataLoadListener;
import viroyal.com.base.widget.smart.listener.SmartRecyclerListener;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/8/20
 */
public class SignSearchDialogFragment extends BaseDialogFragment implements SmartRecyclerDataLoadListener {
  public static final int START_TIME_STATUS = 0;
  public static final int END_TIME_STATUS = 1;
  public int timeStatus;
  private TimePickerView pvTime;

  private ImageView iv_close_notice;
  private LinearLayout ll_dialog_notice;
  private EditText et_search;
  private TextView tv_search;
  private ImageView iv_sign_in;
  private ImageView iv_sign_out;
  private TextView tv_start_time;
  private TextView tv_end_time;
  private SmartRecyclerViewLayout smartRecyclerViewLayout;
  private ItemSignRecordAdapter itemSignRecordAdapter;
  private String strWhere;
  private String startTime;
  private String endTime;

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_sign_search_layout, null);
    initView(view);
    setListener();
    searchSignRecord();
    return view;
  }

  private void initView(View view) {
    ll_dialog_notice = view.findViewById(R.id.ll_dialog_notice);
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_dialog_notice.getLayoutParams();
    layoutParams.width = (int) (ScreenUtils.getScreenWidth(mContext) * 0.9314);
    layoutParams.height = (int) (ScreenUtils.getScreenHeight(mContext) * 0.73);
    ll_dialog_notice.setLayoutParams(layoutParams);

    iv_close_notice = view.findViewById(R.id.iv_close_notice);
    et_search = view.findViewById(R.id.et_search);
    tv_search = view.findViewById(R.id.tv_search);
    iv_sign_in = view.findViewById(R.id.iv_sign_in);
    iv_sign_out = view.findViewById(R.id.iv_sign_out);
    tv_start_time = view.findViewById(R.id.tv_start_time);
    tv_end_time = view.findViewById(R.id.tv_end_time);
    smartRecyclerViewLayout = view.findViewById(R.id.smartRecyclerViewLayout);
    smartRecyclerViewLayout.setSmartRecycleDataLoadListener(this);
    itemSignRecordAdapter = new ItemSignRecordAdapter(mContext);
    smartRecyclerViewLayout.setAdapter(itemSignRecordAdapter);
    smartRecyclerViewLayout.setPullRefreshEnable(false);
    smartRecyclerViewLayout.getRecyclerView().addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override
      public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = SizeUtils.dp2px(15);
      }
    });
    initTimePicker();
  }

  private void setListener() {
    iv_close_notice.setOnClickListener(v -> getDialog().dismiss());
    tv_search.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        searchSignRecord();
      }
    });

    iv_sign_in.setOnClickListener(v -> {
      if (iv_sign_in.isSelected()) {
        iv_sign_in.setSelected(false);
      } else {
        iv_sign_in.setSelected(true);
        iv_sign_out.setSelected(false);
      }
      searchSignRecord();
    });
    iv_sign_out.setOnClickListener(v -> {
      if (iv_sign_out.isSelected()) {
        iv_sign_out.setSelected(false);
      } else {
        iv_sign_out.setSelected(true);
        iv_sign_in.setSelected(false);
      }
      searchSignRecord();
    });
    tv_start_time.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        timeStatus = START_TIME_STATUS;
        pvTime.show();
      }
    });
    tv_end_time.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        timeStatus = END_TIME_STATUS;
        pvTime.show();
      }
    });
  }

  private void initTimePicker() {
    //Dialog 模式下，在底部弹出
    pvTime = new TimePickerBuilder(mContext, (date, v) -> {
      String dateTime = DateUtils.format(date, "yyyy-MM-dd");
      if (timeStatus == START_TIME_STATUS) {
        tv_start_time.setText(dateTime);
        startTime = dateTime;
      } else if (timeStatus == END_TIME_STATUS) {
        tv_end_time.setText(dateTime);
        endTime = dateTime;
      }
      searchSignRecord();
    })
            .setSubmitColor(R.color.colorPrimary)
            .setCancelColor(R.color.colorPrimary)
            .setType(new boolean[]{true, true, true, false, false, false})
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .addOnCancelClickListener(view -> Log.i("pvTime", "onCancelClickListener"))
            .build();

    Dialog mDialog = pvTime.getDialog();
    if (mDialog != null) {
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT,
              Gravity.BOTTOM);

      params.leftMargin = 0;
      params.rightMargin = 0;
      pvTime.getDialogContainerLayout().setLayoutParams(params);

      Window dialogWindow = mDialog.getWindow();
      if (dialogWindow != null) {
        dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
        dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
        dialogWindow.setDimAmount(0.1f);
      }
    }
  }

  private void searchSignRecord() {
    try {
      //姓名或者学号
      String nameUnionId = et_search.getText().toString().trim();
      //签到状态
      String signStatus = getSignStatus();

      StringBuilder strWhereSb = new StringBuilder();
      if (!TextUtils.isEmpty(nameUnionId)) {
        if (!TextUtils.isEmpty(strWhereSb)) {
          strWhereSb.append(" and ");
        }
        strWhereSb.append("(name like '%" + nameUnionId + "%' or union_id like '%" + nameUnionId + "%')");
      }

      if (!TextUtils.isEmpty(signStatus)) {
        if (!TextUtils.isEmpty(strWhereSb)) {
          strWhereSb.append(" and ");
        }
        strWhereSb.append("sign_status='" + signStatus + "'");
      }

      if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
        if (!TextUtils.isEmpty(strWhereSb)) {
          strWhereSb.append(" and ");
        }
        strWhereSb.append("(sign_date between '" + startTime + "' and '" + endTime + "')");
      }
      strWhere = strWhereSb.toString();
      smartRecyclerViewLayout.showLoading();
      smartRecyclerViewLayout.startRefresh();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取选择状态 1入园 2出园 空代表不做筛选
   *
   * @return
   */
  private String getSignStatus() {
    if (iv_sign_in.isSelected()) {
      return ConstantsYJ.ParamsTag.SIGN_IN;
    }
    if (iv_sign_out.isSelected()) {
      return ConstantsYJ.ParamsTag.SIGN_OUT;
    }
    return "";
  }

  @Override
  public void onLoadMore(SmartRecyclerListener smartRecycleListener, boolean isRefresh) {
    try {
      if (null == itemSignRecordAdapter) {
        return;
      }
      if (isRefresh) {
        itemSignRecordAdapter.clearData();
      }
      int itemCount = itemSignRecordAdapter.getItemCount();
      String orderBy = "swipe_time desc limit " + itemCount + "," + (itemCount + ConstantsYJ.ParamsTag.DEFAULT_COUNT);
      List<AllLocalSignRecord> allLocalSignRecordList = KJDB.getDefaultInstance().findAllByWhere(AllLocalSignRecord.class, strWhere, orderBy);
      if (null != allLocalSignRecordList && allLocalSignRecordList.size() > 0) {
        itemSignRecordAdapter.appendData(allLocalSignRecordList);
      } else {
        if (!isRefresh) {
          ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.no_more_data));
        }
      }
      smartRecycleListener.setPullLoadEnable(allLocalSignRecordList.size() >= ConstantsYJ.ParamsTag.DEFAULT_COUNT);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      smartRecycleListener.showData();
    }
  }
}
