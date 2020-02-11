package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.Student;
import viroyal.com.base.model.StudentData;

/**
 * @author chenjunwei
 * @desc 家长相关学生信息
 * @date 2019/4/28
 */
public class ParentLinkStudentsResponseS extends BaseResponse {

  @SerializedName("extra")
  public List<Student> students;

  @SerializedName("pick_up_status")
  public String pick_up_status;
}
