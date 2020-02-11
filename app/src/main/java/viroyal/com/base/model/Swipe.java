package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;


/**
 * @author chenjunwei
 * @desc 刷卡刷脸返回
 * @date 2019/6/20
 */
public class Swipe {
  @SerializedName("card_no")
  public String card_no;
  @SerializedName("union_id")
  public String union_id = "";
  @SerializedName("username")
  public String username;
  @SerializedName("swipe_time")
  public String swipe_time;
  @SerializedName("stu_url")
  public String stu_url;
  @SerializedName("parent_url")
  public String parent_url;
  @SerializedName("parent_name")
  public String parent_name;
  @SerializedName("take_pictures")
  public int take_pictures;
}
