package viroyal.com.base.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suntiago.baseui.utils.DensityUtil;
import com.suntiago.baseui.utils.ScreenUtils;
import com.suntiago.baseui.utils.ToastUtils;

import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.adapter.ItemStudentAdapter;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.model.Student;
import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;


/**
 * @author chenjunwei
 * @desc 学生家长信息
 * @date 2019/6/5
 */
public class SelectStudentDialogFragment extends BaseDialogFragment {

  private ParentLinkStudentsResponseS responseS;

  private OnStudentSignListener onStudentSignListener;

  private ItemStudentAdapter itemStudentAdapter;

  public ItemStudentAdapter getItemStudentAdapter() {
    return itemStudentAdapter;
  }

  public void setDialogContent(ParentLinkStudentsResponseS responseS, OnStudentSignListener onStudentSignListener) {
    this.responseS = responseS;
    this.onStudentSignListener = onStudentSignListener;
  }

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_student_layout, null);
    LinearLayout ll_dialog_select_student = view.findViewById(R.id.ll_dialog_select_student);
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_dialog_select_student.getLayoutParams();
    layoutParams.width = (int) (ScreenUtils.getScreenWidth(mContext) * 0.87);
    ll_dialog_select_student.setLayoutParams(layoutParams);

    TextView tv_pick_up_status = view.findViewById(R.id.tv_pick_up_status);
    ImageView iv_close = view.findViewById(R.id.iv_close);

    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
    itemStudentAdapter = new ItemStudentAdapter(getActivity(), responseS.students);
    GridLayoutManager glm = new GridLayoutManager(mContext, 2);
    recyclerView.setLayoutManager(glm);
    GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(2, DensityUtil.dip2px(mContext, 13f), false);
    recyclerView.addItemDecoration(gridSpacingItemDecoration);
    recyclerView.setAdapter(itemStudentAdapter);
    String signStatus = TextUtils.equals(responseS.pick_up_status, "0") ? mContext.getResources().getString(R.string.sign_in)
            : mContext.getResources().getString(R.string.sign_out);
    tv_pick_up_status.setText(signStatus);

    tv_pick_up_status.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        if (null != onStudentSignListener && null != itemStudentAdapter) {
          List<Student> selectStudents = itemStudentAdapter.getStudents();
          if (selectStudents.size() > 0) {
            getDialog().dismiss();
            onStudentSignListener.setOnStudentSignListener(selectStudents);
          } else {
            ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.select_student));
          }
        }
      }
    });
    iv_close.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        getDialog().dismiss();
      }
    });
    return view;
  }

  public interface OnStudentSignListener {
    void setOnStudentSignListener(List<Student> students);
  }
}
