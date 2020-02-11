package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

import java.util.List;


/**
 * @author chenjunwei
 * @desc 学生信息
 * @date 2019/4/28
 */
public class StudentData {
  @Id(column = "id", autoInc = true)
  public int id;
  /**
   * 学号
   */
  @SerializedName("union_id")
  public String union_id;

  /**
   * 姓名
   */
  @SerializedName("name")
  public String name;

  /**
   * 学生照片url
   */
  @SerializedName("pic_url")
  public String pic_url;


  @SerializedName("status")
  public int status = 0;

  /**
   * 班級
   */
  @SerializedName("class_name")
  public String class_name;

  @SerializedName("cards")
  public List<Card> cards;

  /**
   * 删除
   */
  @SerializedName("stu_delete_at")
  public String stu_delete_at;

  public class Card{
    /**
     * 卡号
     */
    @SerializedName("card_no")
    public String card_no;

    /**
     * 家长 监护人
     */
    @SerializedName("parent_name")
    public String parent_name;

    /**
     * 家长 监护人头像
     */
    @SerializedName("parent_url")
    public String parent_url;

    /**
     * 有效期
     */
    @SerializedName("kyxq")
    public String kyxq;

    /**
     * 是否删除
     */
    @SerializedName("deleted_at")
    public String deleted_at;
  }
}
