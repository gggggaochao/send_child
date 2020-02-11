package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import viroyal.com.base.model.OwnStyle;


/**
 * @author chenjunwei
 * @desc 样式
 * @date 2019/6/18
 */
public class StyleResponseS extends BaseResponse {

  @SerializedName("extra")
  public OwnStyle data;
}
