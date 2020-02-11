package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import viroyal.com.base.model.HomeInfo;


/**
 * @author chenjunwei
 * @desc 首页信息
 * @date 2019/6/18
 */
public class HomeInfoResponseS extends BaseResponse {
  @SerializedName("extra")
  public HomeInfo data;
}
