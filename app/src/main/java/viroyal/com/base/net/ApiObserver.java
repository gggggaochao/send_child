package viroyal.com.base.net;

import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import viroyal.com.base.face.rsp.DeviceInfoRsp;
import viroyal.com.base.face.rsp.FeaturesSyncRsp;
import viroyal.com.base.net.req.SwipeRequest;
import viroyal.com.base.net.req.TakePhotoRequest;
import viroyal.com.base.net.rsp.AnnouncedResponseS;
import viroyal.com.base.net.rsp.BroadcastResponseS;
import viroyal.com.base.net.rsp.FoodResponseS;
import viroyal.com.base.net.rsp.HomeInfoResponseS;
import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;
import viroyal.com.base.net.rsp.QrCodeResponseS;
import viroyal.com.base.net.rsp.StudentSignRecordResponseS;
import viroyal.com.base.net.rsp.StudentsResponseS;
import viroyal.com.base.net.rsp.StyleResponseS;
import viroyal.com.base.net.rsp.SwipeAllFaceResponseS;
import viroyal.com.base.net.rsp.SwipeResponseS;
import viroyal.com.base.net.rsp.WeatherResponseS;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/4
 */
public interface ApiObserver {
  /**
   * 获取特征值列表
   *
   * @param master_key
   * @param school_id
   * @param device
   * @return
   */
  @GET("app/face/v2/feature/list/device")
  Observable<FeaturesSyncRsp> getFeatureList(@Header("master_key") String master_key,
                                             @Header("school_id") String school_id,
                                             @Query("device") String device,
                                             @Query("update_time") String update_time,
                                             @Query("next_id") String next_id);

  /**
   * 获取设备硬件信息
   *
   * @param school_id
   * @param dev_mac
   * @return
   */
  @GET("device/common/v1/device-info")
  Observable<DeviceInfoRsp> getDeviceInfo(@Header("school_id") String school_id,
                                          @Header("dev_mac") String dev_mac);
  /**
   * 刷卡签到
   * @param school_id
   * @param param
   * @return
   */
  @POST("app/card/v1/onecard/studentsign")
  Observable<SwipeResponseS> swipeSign(@Header("school_id") String school_id,
                                       @Body SwipeRequest param);

  /**
   * 刷脸签到
   *
   * @param school_id
   * @param param
   * @return
   */
  @POST("app/face/v1/feature/studentsignlist")
  Observable<SwipeAllFaceResponseS> allFaceSign(@Header("school_id") String school_id,
                                                @Body List<SwipeRequest> param);

  /**
   * 刷脸签到
   *
   * @param school_id
   * @param param
   * @return
   */
  @POST("app/face/v1/feature/studentsign")
  Observable<SwipeResponseS> faceSign(@Header("school_id") String school_id,
                                      @Body SwipeRequest param);

  /**
   * 菜谱
   *
   * @param school_id
   * @return
   */
  @GET("device/media/v1/dishes")
  Observable<FoodResponseS> foods(@Header("school_id") String school_id);

  /**
   * 界面风格
   *
   * @param school_id
   * @param code
   * @return
   */
  @GET("device/media/course/v1/style")
  Observable<StyleResponseS> style(@Header("school_id") String school_id,
                                   @Query("code") String code);

  /**
   * 通知通告
   *
   * @param mac
   * @param device_id
   * @param screen
   * @return
   */
  @GET("device/course/v1/announce")
  Observable<AnnouncedResponseS> announced(@Header("dev_mac") String mac,
                                           @Query("device_id") String device_id,
                                           @Query("screen") String screen);

  /**
   * 轮播图
   *
   * @param school_id
   * @param dev_mac
   * @param type
   * @return
   */
  @GET("device/info-publish/device/publishes")
  Observable<BroadcastResponseS> broadcast(@Header("school_id") String school_id,
                                           @Header("dev_mac") String dev_mac,
                                           @Query("type") int type);

  /**
   * 3天天气
   */
  @GET("app/weather")
  Observable<WeatherResponseS> get3DaysWeather(@Header("master_key") String master_key,
                                               @Query("area") String area);


  /**
   * 首页接口
   *
   * @param mac
   * @param device_id
   * @return
   */
  @GET("device/course/v1/home")
  Observable<HomeInfoResponseS> homeinfo(@Header("dev_mac") String mac,
                                         @Query("device_id") String device_id);

  /**
   * 学生列表
   *
   * @param dev_mac
   * @param school_id
   * @return
   */
  @GET("app/card/v2/onecard/getstudent")
  Observable<StudentsResponseS> students(@Header("dev_mac") String dev_mac,
                                         @Header("school_id") String school_id,
                                         @Query("update_time") String update_time);

  /**
   * 学生签到记录
   *
   * @param dev_mac
   * @param school_id
   * @return
   */
  @GET("app/card/v1/onecard/getstudentlog")
  Observable<StudentSignRecordResponseS> getStudentSignRecord(@Header("dev_mac") String dev_mac,
                                                              @Header("school_id") String school_id);

  /**
   * 获取二维码url
   *
   * @param school_id
   * @return
   */
  @GET("app/smart/v1/qrcode/scan_sign_config")
  Observable<QrCodeResponseS> getQrCodeUrl(@Header("school_id") String school_id);

  /**
   * 上传异常拍照
   *
   * @param schoolId
   * @param param
   * @return
   */
  @POST("app/card/v1/onecard/uploadfailphoto")
  Observable<BaseResponse> uploadFailPhoto(@Header("school_id") String schoolId,
                                           @Body TakePhotoRequest param);

  /**
   * 获取刷脸相关信息
   *
   * @param union_id
   * @param swipe_time
   * @return
   */
  @GET("app/face/v1/feature/getparentlinkstudent")
  Observable<ParentLinkStudentsResponseS> getParentLinkStudent(@Header("school_id") String school_id,
                                                               @Query("union_id") String union_id,
                                                               @Query("swipe_time") String swipe_time);
}
