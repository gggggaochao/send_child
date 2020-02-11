package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/4/28
 */
public class BroadcastData {
  public String created_at;
  public int id;
  public String name;
  public int status;
  public String thumbnail;
  public String title;
  public int date_flag;
  @SerializedName("medias")
  public List<BroadcastMedia> mBroadcasrMediaList;
  public String start_date;
  public String end_date;
  public String start_time;
  public String end_time;

  public int is_top;
}
