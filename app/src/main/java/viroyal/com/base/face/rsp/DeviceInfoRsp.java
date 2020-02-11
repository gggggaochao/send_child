package viroyal.com.base.face.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.face.bean.DeviceInfo;

/**
 * Created by bz on 2018/5/10.
 */

public class DeviceInfoRsp extends BaseResponse {
  @SerializedName("extra")
  public ExtraBean extra;

  public class ExtraBean {
    public DeviceInfo device_info;
    public List<Apps> device_apps;
  }

  public class Apps {
    public String id;

    public String href_url;

    public String icon_url;

    public Name name;

    public class Name {
      public String ch;
      public String us;
    }
  }

  public class VerificationMode {
    public int nfc = 0;
    public int face = 0;
    public int code = 0;
  }
}
