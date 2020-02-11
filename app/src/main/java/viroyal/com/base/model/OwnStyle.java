package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/4/28
 */
@Table(name = "style")
public class OwnStyle {
  @Id(column = "id", autoInc = true)
  public int id;

  @SerializedName("head_bg")
  @Property(column = "head_bg")
  public String head_bg;
  @SerializedName("head_url")
  @Property(column = "head_url")
  public String head_url;
  @SerializedName("min_bg")
  @Property(column = "mid_bg")
  public String mid_bg;
  @SerializedName("show_photo")
  @Property(column = "show_photo")
  public int show_photo;
  @SerializedName("announce")
  @Property(column = "announce")
  public int announce;
  @SerializedName("photograph")
  @Property(column = "photograph")
  public int photograph;
  @SerializedName("swipe_interval_time")
  @Property(column = "swipe_interval_time")
  public String swipe_interval_time;
  @SerializedName("pick_time_config")
  @Property(column = "pick_time_config")
  public String pick_time_config;

}
