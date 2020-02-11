package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import viroyal.com.base.model.BroadcastExtra;


/**
 * @author chenjunwei
 * @desc 轮播
 * @date 2019/4/28
 */
public class BroadcastResponseS extends BaseResponse {
  @SerializedName("extra")
  public BroadcastExtra mBroadcastExtra;

}
