package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/8
 */
@Table(name = "food")
public class Food {
  @Id(column = "id")
  public int id;

  @SerializedName("name")
  @Property(column = "name")
  public String name;

  @SerializedName("week")
  @Property(column = "week")
  public int week;
}
