package viroyal.com.base.face.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/16
 */
public class FeaturesSyncRsp extends BaseResponse {
  @SerializedName("extra")
  public List<FeatureUser> users;
  public float threshold;
  public String next_id;
}
