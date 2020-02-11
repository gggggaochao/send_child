package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.StudentData;

/**
 * @author chenjunwei
 * @desc 学生信息
 * @date 2019/4/28
 */
public class StudentsResponseS extends BaseResponse {

  @SerializedName("extra")
  public List<StudentData> students;
}
