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
@Table(name = "announced")
public class Announced {
  /**
   * 通告id
   */
  @Id(column = "id")
  public int id;

  /**
   * 通告标题
   */
  @SerializedName("title")
  @Property(column = "title")
  public String title;
  /**
   * 通告内容
   */
  @SerializedName("value")
  @Property(column = "value")
  public String value;
  /**
   * 通告网页地址
   */
  @SerializedName("url")
  @Property(column = "url")
  public String url;
}
