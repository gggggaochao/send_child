package viroyal.com.base.common;

/**
 * Created by zaiyu on 2016/8/2.
 */
public class ConstantsYJ {

  public static String LOCATION_ADDRESS = "南京";

  public interface ParamsTag {
    /**
     * 无效卡
     */
    int SWIPE_INVALID_CARD = 3001;
    /**
     * 没有绑定一卡通主账户
     */
    int SWIPE_NO_USER = 3002;
    /**
     * 过期卡
     */
    int SWIPE_OUT_OF_DATE = 3003;
    /**
     * 没有配置刷卡规则
     */
//    int SWIPE_NO_SIGN_RULE = 3004;
    /**
     * 签到时间不足10分钟
     */
    int SWIPE_SIGN_IN_TIME_FAIL = 3005;
    /**
     * 无效用户、非本班学生或者用户不存在（刷脸）
     */
    int SWIPE_INVALID_USER = 3006;
    /**
     * 正常签到 1001---参数错误 1002---用户不存在
     */
    int SWIPE_SIGN_IN_SUCCESS = 1000;
    /**
     * 正常签退 第n次签退 n>1
     */
    int SWIPE_SIGN_OUT_SUCCESS = 1004;
    /**
     * 正常签退 第n次签退 n=1
     */
    int SWIPE_SIGN_OUT_FIRST_SUCCESS = 1005;

    int SWIPE_CARD_STATUS = 0;

    int SWIPE_FACE_STATUS = 1;

    int SWIPE_CODE_STATUS = 2;

    /**
     * 入园
     */
    String SIGN_IN = "1";
    /**
     * 出园
     */
    String SIGN_OUT = "2";

    int DEFAULT_COUNT = 20;

    int DEFAULT_FEATURE_COUNT = 500;

    String STUDENT = "_student";

    String PARENT = "_parent";

    String FACE = "_face";
  }

  public interface RxTag {
    String RX_TAG_SOCKET_ON_MESSAGE = "rx_tag_socket_on_message";
    String IS_NOTICE_FINISH = "is_notice_finish";
    String RX_TAG_CLOSE_BROADCAST = "rx_tag_close_broadcast";
    String RX_TAG_CONFIRM_USER = "rx_tag_confirm_user";
  }

  public interface SpTag {
    String FIRST_START = "first_start";
    String DEVICE_INFO = "device_info";
    String DEVICE_APP_INFO = "device_app_info";
    String HOME_INFO = "home_info";
    String WEATHER_INFO = "weather_info";
    String PASS_WORD = "password";
    String STUDENT_INFO_UPDATE_TIME = "student_info_update_time";
    String FEATURE_INFO_UPDATE_TIME = "feature_info_update_time";
  }
}
