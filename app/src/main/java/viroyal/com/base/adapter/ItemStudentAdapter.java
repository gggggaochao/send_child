package viroyal.com.base.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import viroyal.com.base.R;
import viroyal.com.base.model.Student;
import viroyal.com.base.util.Utils;


/**
 * @author chenjunwei
 * @desc 选择学生
 * @date 2019/5/22
 */
public class ItemStudentAdapter extends BaseItemDraggableAdapter<Student> {
  private Context mContext;
  private Map<Integer, Boolean> map = new HashMap<>();
  private ArrayList<Student> selectStudents = new ArrayList<>();

  public ItemStudentAdapter(Context context, List<Student> data) {
    super(R.layout.item_student_layout, data);
    mContext = context;
    for (int position = 0; position < data.size(); position++) {
      map.put(position, true);
    }
  }

  public List<Student> getStudents() {
    selectStudents.clear();
    for (int j = 0; j < map.size(); j++) {
      if (map.get(j)) {
        selectStudents.add(mData.get(j));
      }
    }
    return selectStudents;
  }

  @Override
  protected void convert(BaseViewHolder helper, Student student) {
    if (null == student) {
      return;
    }
    helper.setText(R.id.tv_student_name, mContext.getResources().getString(R.string.student, TextUtils.isEmpty(student.name) ? "" : student.name));
    helper.setText(R.id.tv_class_name, mContext.getResources().getString(R.string.class_name, TextUtils.isEmpty(student.class_name) ? "" : student.class_name));
    Utils.loadImage(mContext,student.pic_url,student.pic_url_local_path,helper.getView(R.id.iv_student_avatar),R.mipmap.bg_default_avatar);
    int position = helper.getAdapterPosition();
    if (position < map.size()) {
      if (map.get(position)) {
        helper.setBackgroundRes(R.id.ll_student, R.drawable.bg_student_select);
      } else {
        helper.setBackgroundRes(R.id.ll_student, R.drawable.bg_student_unselect);
      }
    }

    helper.setOnClickListener(R.id.ll_student, v -> {
      if (position < map.size()) {
        if (map.get(position)) {
          map.put(position, false);
        } else {
          map.put(position, true);
        }
      }
      notifyDataSetChanged();
    });
  }
}
