package viroyal.com.base.face.rsp;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/16
 */
@Table(name = "FeatureUser")
public class FeatureUser {
  @Id(column = "id")
  public int id;

  @SerializedName("union_id")
  @Property(column = "union_id")
  public String union_id;

  @SerializedName("value")
  @Property(column = "value")
  public String value;

  @SerializedName("name")
  @Property(column = "name")
  public String name;

  @SerializedName("image_url")
  @Property(column = "image_url")
  public String image_url;

  /**
   * 是否删除
   */
  @SerializedName("deleted_at")
  @Property(column = "deleted_at")
  public String deleted_at;
}
