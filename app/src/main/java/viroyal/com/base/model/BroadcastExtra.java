package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * @author chenjunwei
 * @desc 轮转返回
 * @date 2019/4/28
 */
public class BroadcastExtra {
  @SerializedName("data")
  public List<BroadcastData> mBroadcastData;
}
