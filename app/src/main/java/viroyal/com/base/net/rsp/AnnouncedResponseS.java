package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.Announced;


/**
 * @author chenjunwei
 * @desc 通告
 * @date 2019/6/18
 */
public class AnnouncedResponseS extends BaseResponse {
  @SerializedName("extra")
  public List<Announced> data;
}
