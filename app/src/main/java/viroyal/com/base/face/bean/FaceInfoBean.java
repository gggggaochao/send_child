package viroyal.com.base.face.bean;

import java.io.Serializable;

/**
 * Created by bz on 2018/5/8.
 */

public class FaceInfoBean implements Serializable {

  public String image_url;
  public String union_id;
  public String value;
  public UserPO userPO;

  public FaceInfoBean() {
  }

  public FaceInfoBean(String image_url, String union_id, String value) {
    this.image_url = image_url;
    this.union_id = union_id;
    this.value = value;
  }

  public String getImage_url() {
    return image_url;
  }

  public void setImage_url(String image_url) {
    this.image_url = image_url;
  }

  public String getUnion_id() {
    return union_id;
  }

  public void setUnion_id(String union_id) {
    this.union_id = union_id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "FaceInfoBean{" +
        "image_url='" + image_url + '\'' +
        ", union_id='" + union_id + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
