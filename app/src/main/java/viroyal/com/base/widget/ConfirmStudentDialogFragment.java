package viroyal.com.base.widget;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.listener.OnConfirmDialogListener;
import viroyal.com.base.model.Student;
import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/18
 */
public class ConfirmStudentDialogFragment extends BaseDialogFragment {

  private ParentLinkStudentsResponseS responseS;

  private OnConfirmDialogListener onConfirmDialogListener;

  public void setDialogContent(ParentLinkStudentsResponseS responseS, OnConfirmDialogListener onConfirmDialogListener) {
    this.responseS = responseS;
    this.onConfirmDialogListener = onConfirmDialogListener;
  }

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_student_layout, null);
    TextView tv_confirm_student_cancel = view.findViewById(R.id.tv_confirm_student_cancel);
    TextView tv_confirm_student_confirm = view.findViewById(R.id.tv_confirm_student_confirm);
    TextView tv_confirm_student_tip = view.findViewById(R.id.tv_confirm_student_tip);
    String signStatus = TextUtils.equals(responseS.pick_up_status, "0") ? mContext.getResources().getString(R.string.sign_in)
            : mContext.getResources().getString(R.string.sign_out);
    tv_confirm_student_tip.setText(mContext.getResources().getString(R.string.student_confirm_tip, getStudents(), signStatus));
    tv_confirm_student_cancel.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        getDialog().dismiss();
      }
    });
    tv_confirm_student_confirm.setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        getDialog().dismiss();
        if(null != onConfirmDialogListener){
          onConfirmDialogListener.setOnConfirmDialogListener(responseS);
        }
      }
    });
    return view;
  }

  public String getStudents() {
    StringBuilder sb = new StringBuilder();
    List<Student> students = responseS.students;
    if (null != students && students.size() > 0) {
      for (int i = 0; i < students.size(); i++) {
        Student student = students.get(i);
        if (i == students.size() - 1) {
          sb.append(student.name);
        } else {
          sb.append(student.name + "、");
        }
      }
    }
    return sb.toString().trim();
  }
}
