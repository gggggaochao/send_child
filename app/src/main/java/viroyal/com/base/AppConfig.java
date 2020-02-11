package viroyal.com.base;

import android.content.Context;
import android.text.TextUtils;

import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.date.DateUtils;

import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.bean.DeviceInfo;
import viroyal.com.base.util.Utils;

/**
 * Created by zy on 2019/3/29.
 * 应用全局定义参数放置在这里
 */
public class AppConfig {
  /**
   * 配置主接口地址
   */
  public final static String DEV_HOST = BuildConfig.API_HOST;

  public final static String MASTER_KEY = BuildConfig.MASTER_KEY;

  /**
   * 标明app类型名称，后台会根据此返回数据
   */
  public final static String DEV_APP_TYPE = "sendchild";

  /**
   * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
   * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
   */
  public final static String APP_ID = "17925243";

  public final static String APP_KEY = "DtQMZ0pAAkRGvjaT0ZNzWdnu";

  public final static String SECRET_KEY = "IuhWrCXPKLCc4ZcV5zbR0L0NYc95ITUh";

  /**
   * 当通知内容过短，停留的时间
   */
  public final static int STAY_TIMES = 10 * 1000;

  //设备的尺寸
  public static int screenWidth_pix = 1080;
  public static int screenHeight_pix = 1920;

  /**
   * 1 不支持nfc
   * 2 标准nfc
   * 3 后加式nfc
   */
  public static int NFC_mode = 1;

  /**
   * 没有用户操作，超时时间, 检查是否跳转全屏广告界面 接口返回
   */
  public static long LOCAL_TIMEOUT_DELAY_NO_TOUCH = 60000 * 2;
  /**
   * 二维码更新时长
   */
  public final static int TIME_SYNC_QR_CODE_DELAY_MS = 60000 * 5;
  /**
   * 天气更新时长
   */
  public final static int TIME_SYNC_WEATHER_INFO_DELAY_MS = 60000 * 60 * 4;

  /**
   * 特征值、签到记录、界面风格style、设备信息deviceinfo、食谱更新时长
   */
  public final static int TIME_SYNC_FEATURES_DELAY_MS = 60000 * 60;
  /**
   * 首页信息 天气、日期
   */
  public final static int TIME_SYNC_HOME_INFO_DELAY_MS = 60000 * 60 * 3;
  /**
   * 学生列表
   */
  public final static int TIME_SYNC_STUDENT_INFO_DELAY_MS = 60000 * 60 * 3;
  /**
   * 轮播图刷新的时间间隔
   */
  public final static int TIME_SYNC_BROADCAST_DELAY_MS = 60000 * 3;
  /**
   * 轮转通告
   */
  public final static int TIME_SYNC_ANNOUNCE_DELAY_MS = 60000 * 3;
  /**
   * 食谱轮转
   */
  public final static int TIME_SYNC_FOOD_DELAY_MS = 10000;
  /**
   * 学生信息弹窗时间
   */
  public final static int TIME_SYNC_STUDENT_DIALOG_DELAY_MS = 2;
  /**
   * 选择学生信息弹窗时间
   */
  public final static int TIME_SYNC_SELECT_STUDENT_DIALOG_DELAY_MS = 5;
  public static boolean isNfc(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_Nfc", 0) == 1;

  }

  public static void setNfc(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_Nfc", is);
  }

  public static boolean isFace(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_Face", 1) == 1;

  }

  public static void setFace(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_Face", is);
  }

  public static boolean isCode(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_Code", 0) == 1;
  }

  public static void setCode(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_Code", is);
  }

  public static boolean isSpeak(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_Speak", 0) == 1;
  }

  public static void setSpeak(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_Speak", is);
  }

  public static boolean isTakePhoto(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_TakePhoto", 0) == 1;
  }

  public static void setTakePhoto(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_TakePhoto", is);
  }

  public static boolean isShowPhoto(Context context) {
    return SPUtils.getInstance(context).get("AppConfig_ShowPhoto", 0) == 1;
  }

  public static void setShowPhoto(int is, Context context) {
    SPUtils.getInstance(context).put("AppConfig_ShowPhoto", is);
  }

  public static void setSwipeIntervalTime(String swipeIntervalTime,Context context){
    SPUtils.getInstance(context).put("AppConfig_SwipeIntervalTime", swipeIntervalTime);
  }

  public static long getSwipeIntervalTime(Context context){
    String swipeIntervalTime = SPUtils.getInstance(context).get("AppConfig_SwipeIntervalTime");
    return Utils.toLong(swipeIntervalTime) * 60;
  }

  public static void setPickTimeConfig(String pickTimeConfig,Context context){
    SPUtils.getInstance(context).put("AppConfig_PickTimeConfig", pickTimeConfig);
  }

  public static long getPickTimeConfig(Context context){
    String pickTimeConfig = SPUtils.getInstance(context).get("AppConfig_PickTimeConfig");
    pickTimeConfig = TextUtils.isEmpty(pickTimeConfig) ? "23:59" : pickTimeConfig;
    return Utils.getTime(DateUtils.currentDateTime() + " " + pickTimeConfig);
  }


  public static void initDefault(Context context) {
    //初始化设备宽高
    DeviceInfo deviceInfo = SPUtils.getInstance(context).get(ConstantsYJ.SpTag.DEVICE_INFO, DeviceInfo.class);
    if (deviceInfo != null) {
      NFC_mode = deviceInfo.nfc_mode;
    }
  }
}
