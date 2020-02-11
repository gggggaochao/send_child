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
@Table(name = "volume")
public class Volume {

  @Id(column = "id", autoInc = true)
  public int id;
  /**
   * 名称
   */
  @SerializedName("name")
  @Property(column = "name")
  public String name;
  /**
   * 开始
   */
  @SerializedName("start")
  @Property(column = "start")
  public String start;
  /**
   * 结束
   */
  @SerializedName("end")
  @Property(column = "end")
  public String end;
  /**
   * 音量
   */
  @SerializedName("volume")
  @Property(column = "audio")
  public int audio = 20;
}
