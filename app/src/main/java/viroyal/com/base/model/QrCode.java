package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * Created by qjj on 2017/9/12.
 */

@Table(name = "qr_code")
public class QrCode {
  @Id(column = "id")
  public int id;

  @SerializedName("extra")
  @Property(column = "url")
  public String url;

  public QrCode() {
  }

  public QrCode(String url) {
    this.url = url;
  }
}
