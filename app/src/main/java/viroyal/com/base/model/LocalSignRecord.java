package viroyal.com.base.model;


import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * @author chenjunwei
 * @desc 签到记录
 * @date 2019/6/20
 */
@Table(name = "local_sign_record")
public class LocalSignRecord {
  @Id(column = "id")
  public int id;

  /**
   * IC卡号
   */
  @SerializedName("card_no")
  @Property(column = "card_no")
  public String card_no;

  /**
   * 学号 用户id
   */
  @SerializedName("phone_union_id")
  @Property(column = "phone_union_id")
  public String phone_union_id;

  /**
   * 学号 用户id
   */
  @SerializedName("union_id")
  @Property(column = "union_id")
  public String union_id;

  /**
   * 时间
   */
  @SerializedName("swipe_time")
  @Property(column = "swipe_time")
  public long swipe_time;

  /**
   * 签到状态 0刷卡、1刷脸 2扫码
   */
  @SerializedName("swipe_status")
  @Property(column = "swipe_status")
  public int swipe_status;

  /**
   * 签退日期
   */
  @SerializedName("sign_date")
  @Property(column = "sign_date")
  public String sign_date;
}
