package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;


/**
 * @author chenjunwei
 * @desc 学生信息
 * @date 2019/4/28
 */
@Table(name = "student")
public class Student{

  @Id(column = "id")
  public int id;

  /**
   * 学号
   */
  @SerializedName("phone_union_id")
  @Property(column = "phone_union_id")
  public String phone_union_id;

  /**
   * 学号
   */
  @SerializedName("union_id")
  @Property(column = "union_id")
  public String union_id;

  /**
   * 卡号
   */
  @SerializedName("card_no")
  @Property(column = "card_no")
  public String card_no;

  /**
   * 姓名
   */
  @SerializedName("name")
  @Property(column = "name")
  public String name;

  /**
   * 家长 监护人
   */
  @SerializedName("parent_name")
  @Property(column = "parent_name")
  public String parent_name;

  /**
   * 家长 监护人头像
   */
  @SerializedName("parent_url")
  @Property(column = "parent_url")
  public String parent_url;

  /**
   * 学生照片url
   */
  @SerializedName("pic_url")
  @Property(column = "pic_url")
  public String pic_url;

  /**
   * 本地家长 监护人头像路径
   */
  @SerializedName("parent_url_local_path")
  @Property(column = "parent_url_local_path")
  public String parent_url_local_path;

  /**
   * 本地保存学生照片路径
   */
  @SerializedName("pic_url_local_path")
  @Property(column = "pic_url_local_path")
  public String pic_url_local_path;


  @SerializedName("status")
  @Property(column = "status")
  public int status = 0;

  /**
   * 有效期
   */
  @SerializedName("kyxq")
  @Property(column = "kyxq")
  public String kyxq;

  /**
   * 班級
   */
  @SerializedName("class_name")
  @Property(column = "class_name")
  public String class_name;


}
