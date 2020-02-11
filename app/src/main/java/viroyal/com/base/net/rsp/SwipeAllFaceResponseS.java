package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.Swipe;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/19
 */
public class SwipeAllFaceResponseS extends BaseResponse {
  @SerializedName("extra")
  public List<Swipe> data;
}
