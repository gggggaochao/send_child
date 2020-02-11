package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.StudentSignRecord;

/**
 * @author chenjunwei
 * @desc 学生信息
 * @date 2019/4/28
 */
public class StudentSignRecordResponseS extends BaseResponse {

  @SerializedName("extra")
  public List<StudentSignRecord> data;
}
