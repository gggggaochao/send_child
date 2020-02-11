package viroyal.com.base.model;


import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * @author chenjunwei
 * @desc 抓拍记录
 * @date 2019/7/9
 */
@Table(name = "take_photo_record")
public class TakePhotoRecord {
  @Id(column = "id")
  public int id;

  /**
   * 学号
   */
  @Property(column = "union_id")
  public String union_id;

  /**
   * 卡号
   */
  @Property(column = "card_no")
  public String card_no;

  /**
   * 本地图片地址
   */
  @Property(column = "localImagePath")
  public String localImagePath = "";

  @Property(column = "fileName")
  public String fileName;

  /**
   * 在线生成地址
   */
  @Property(column = "networkImagePath")
  public String networkImagePath = "";

  @Property(column = "swipe_time")
  public long swipe_time;

  /**
   * 签到状态 0刷卡、1刷脸 2扫码
   */
  @Property(column = "swipe_status")
  public int swipe_status;
}
