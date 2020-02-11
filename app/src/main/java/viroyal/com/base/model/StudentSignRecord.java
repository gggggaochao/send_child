package viroyal.com.base.model;


import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * @author chenjunwei
 * @desc 签到记录网络获取
 * @date 2019/6/20
 */
@Table(name = "student_sign_record")
public class StudentSignRecord {
  @Id(column = "id")
  public int id;

  /**
   * 学号 用户id
   */
  @SerializedName("union_id")
  @Property(column = "union_id")
  public String union_id;

  /**
   * 签到时间
   */
  @SerializedName("in_time")
  @Property(column = "in_time")
  public String in_time;

  /**
   * 签退时间
   */
  @SerializedName("out_time")
  @Property(column = "out_time")
  public String out_time;

  /**
   * 签退日期
   */
  @SerializedName("sign_date")
  @Property(column = "sign_date")
  public String sign_date;
}
