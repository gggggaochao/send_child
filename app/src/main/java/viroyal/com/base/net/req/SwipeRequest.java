package viroyal.com.base.net.req;

import com.google.gson.annotations.SerializedName;

import viroyal.com.dev.splash.ConfigDevice;


/**
 * @author chenjunwei
 * @desc 签到请求参数
 * @date 2019/6/21
 */
public class SwipeRequest {
  @SerializedName("school_id")
  public String school_id;
  @SerializedName("device_id")
  public String device_id;
  @SerializedName("card_no")
  public String card_no;
  @SerializedName("union_id")
  public String union_id;
  @SerializedName("swipe_time")
  public long swipe_time;
  @SerializedName("heart_beat")
  public int heart_beat;

  /**
   * @param card_no    卡号
   * @param union_id     工号
   * @param swipe_time 刷卡（扫码）时间(秒)
   * @param heart_beat  心跳时间    连接成功时：1    刷卡、扫码时：0
   */
  public SwipeRequest(String card_no, String union_id, long swipe_time, int heart_beat) {
    this.school_id = ConfigDevice.school_id;
    this.device_id = ConfigDevice.getDeviceId();
    this.card_no = card_no;
    this.union_id = union_id;
    this.swipe_time = swipe_time;
    this.heart_beat = heart_beat;
  }
}
