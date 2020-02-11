package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/6/24
 */
public class QrCodeResponseS extends BaseResponse {

  @SerializedName("extra")
  public String data;
}
