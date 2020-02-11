package viroyal.com.base.widget;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suntiago.baseui.utils.ScreenUtils;

import java.util.Random;

import viroyal.com.base.R;
import viroyal.com.base.face.aiwinn.util.Util;
import viroyal.com.base.model.Student;
import viroyal.com.base.util.Utils;


/**
 * @author chenjunwei
 * @desc 学生家长信息
 * @date 2019/6/5
 */
public class StudentDialogFragment extends BaseDialogFragment {

  private Student student;

  private int[] array = {R.mipmap.bg_parent_student1, R.mipmap.bg_parent_student2};

  public void setDialogContent(Student student) {
    this.student = student;
  }

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_student_layout, null);
    LinearLayout ll_dialog_student = view.findViewById(R.id.ll_dialog_student);
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_dialog_student.getLayoutParams();
    layoutParams.width = (int) (ScreenUtils.getScreenWidth(mContext) * 0.755);
    layoutParams.height = (int) (ScreenUtils.getScreenHeight(mContext) * 0.583);
    ll_dialog_student.setLayoutParams(layoutParams);

    int avatarWidth = (int) (ScreenUtils.getScreenWidth(mContext) * 0.35);
    LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(avatarWidth, avatarWidth);
    RelativeLayout rl_student_avatar = view.findViewById(R.id.rl_student_avatar);
    rl_student_avatar.setLayoutParams(avatarLp);

    //生成0 1随机数
    Random rnd = new Random();
    int index = rnd.nextInt(2);
    ll_dialog_student.setBackgroundResource(array[index]);

    ImageView iv_student = view.findViewById(R.id.iv_student);
    TextView tv_student_name = view.findViewById(R.id.tv_student_name);
    if (null != student) {
      try {
        Utils.loadImage(mContext,student.pic_url,student.pic_url_local_path,iv_student,R.mipmap.bg_default_avatar);
        tv_student_name.setText(mContext.getResources().getString(R.string.student, TextUtils.isEmpty(student.name) ? "" : student.name));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return view;
  }
}
