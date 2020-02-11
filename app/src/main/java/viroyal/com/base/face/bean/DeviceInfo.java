package viroyal.com.base.face.bean;

import java.util.List;

import viroyal.com.base.face.rsp.DeviceInfoRsp;

/**
 * Created by zy on 2019/3/8.
 */
public class DeviceInfo {
  public int dpi;
  public int width;
  public int height;
  /**
   * nfc模式 1：不支持nfc 2：标准nfc 3：后加式nfc
   */
  public int nfc_mode;
  public List<Camera> cameras;
  public DeviceInfoRsp.VerificationMode verification_mode;

  public class Camera {
    public int id;
    /**
     * 是否支持红外 1：支持 0：不支持
     */
    public int infrared;
    /**
     * 摄像头位置(方向) 1：上 2：右 3：下 4：左
     */
    public int orientation;
    /**
     * 摄像头id 0：前置 1：后置
     */
    public int position;
  }
}
