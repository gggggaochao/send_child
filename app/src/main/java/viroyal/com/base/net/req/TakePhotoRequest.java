package viroyal.com.base.net.req;

import com.google.gson.annotations.SerializedName;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/19
 */
public class TakePhotoRequest {

  @SerializedName("union_id")
  public String union_id;

  @SerializedName("card_no")
  public String card_no;

  @SerializedName("fail_date")
  public long fail_date;

  @SerializedName("fail_photo")
  public String fail_photo;

  /**
   * 签到状态 0刷卡、1刷脸 2扫码
   */
  @SerializedName("swipe_status")
  public int swipe_status;
}
