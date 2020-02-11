package viroyal.com.base.activity.main;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.suntiago.a.AudioUtil;
import com.suntiago.a.ChinaDateUtil;
import com.suntiago.a.RangeUtil;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.network.network.Api;
import com.suntiago.network.network.BaseRspObserver;
import com.suntiago.network.network.ErrorCode;
import com.suntiago.network.network.rsp.BaseResponse;

import org.kymjs.kjframe.KJDB;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import viroyal.com.base.AppConfig;
import viroyal.com.base.R;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.aiwinn.AiwinnManager;
import viroyal.com.base.face.aiwinn.common.AttConstants;
import viroyal.com.base.face.bean.DeviceInfo;
import viroyal.com.base.face.rsp.DeviceInfoRsp;
import viroyal.com.base.face.rsp.FeaturesSyncRsp;
import viroyal.com.base.listener.OnSyncToLocalSuccessListener;
import viroyal.com.base.model.Announced;
import viroyal.com.base.model.BroadcastExtra;
import viroyal.com.base.model.BroadcastMedia;
import viroyal.com.base.model.Food;
import viroyal.com.base.model.HomeInfo;
import viroyal.com.base.model.OwnStyle;
import viroyal.com.base.model.QrCode;
import viroyal.com.base.model.Student;
import viroyal.com.base.model.StudentSignRecord;
import viroyal.com.base.model.Volume;
import viroyal.com.base.net.ApiObserver;
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
import viroyal.com.dev.broadcast.BroadcastData;
import viroyal.com.dev.splash.ConfigDevice;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/4
 */
public class MainModel implements IModel {

  /**
   * 加载首页信息
   */
  Subscription loadHomeInfo(MainDelegate appDelegateBase, Context context) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .homeinfo(ConfigDevice.getDeviceId(), ConfigDevice.getDeviceId())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(HomeInfoResponseS.class, rsp -> {
              KJDB.getDefaultInstance().deleteByWhere(Volume.class, "1=1");
              if (ErrorCode.isSuccess(rsp.error_code)) {
                SPUtils.getInstance(context).put(ConstantsYJ.SpTag.HOME_INFO, rsp.data);
                HomeInfo homeInfo = rsp.data;
                SPUtils.getInstance(context).put(ConstantsYJ.SpTag.PASS_WORD, homeInfo.password);
                appDelegateBase.updatePassword();
                KJDB.getDefaultInstance().save(homeInfo.volumes);
                adjustVolume(context);
              }
            }));
  }

  /**
   * 调整音量
   */
  void adjustVolume(Context context) {
    List<Volume> volumes = KJDB.getDefaultInstance().findAll(Volume.class);
    if (volumes == null || volumes.size() == 0) {
      Slog.d(TAG, "adjustVolume  []:" + " no volume settings");
      return;
    }
    AudioUtil audioUtil = AudioUtil.getInstance(context);
    boolean tagchange0 = true;
    int volumenow = AudioUtil.getInstance(context).getMediaVolume();

    for (Volume volume : volumes) {
      long start = ChinaDateUtil.formateDataTolongMs(volume.start);
      long end = ChinaDateUtil.formateDataTolongMs(volume.end);
      long now = System.currentTimeMillis();
      boolean b = RangeUtil.INSTANCE.between(now, start, end);
      if (b && tagchange0) {
        tagchange0 = false;
        int volumeT = AudioUtil.getInstance(context).getMediaMaxVolume() * volume.audio / 100;

        if (volumeT == volumenow) {
          continue;
        }
        audioUtil.setMediaVolume(volumeT);
        Slog.d(TAG, "adjustVolume  [volumes]:" + volume.start + "~" + volume.end + "" + volumeT + " setSuccess");
      }
    }

    if (tagchange0 && volumenow != 0) {
      audioUtil.setMediaVolume(0);
    }
  }

  /**
   * 同步数据使用
   *
   * @param appDelegateBase
   * @return
   */
  Subscription syncStudentInfo(MainDelegate appDelegateBase, Action1<StudentsResponseS> action1) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .students(ConfigDevice.getDeviceId(), ConfigDevice.school_id, null)
            .subscribeOn(Schedulers.newThread())
            .subscribe(new BaseRspObserver<>(StudentsResponseS.class, action1));
  }

  /**
   * 正常更新数据使用 需要传入时间戳
   *
   * @param appDelegateBase
   * @return
   */
  Subscription loadStudentInfo(MainDelegate appDelegateBase, Context context, Action1<StudentsResponseS> action1) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    String updateTime = SPUtils.getInstance(context).get(ConstantsYJ.SpTag.STUDENT_INFO_UPDATE_TIME, "");
    return Api.get().getApi(ApiObserver.class)
            .students(ConfigDevice.getDeviceId(), ConfigDevice.school_id, TextUtils.isEmpty(updateTime) ? null : updateTime)
            .subscribeOn(Schedulers.newThread())
            .subscribe(new BaseRspObserver<>(StudentsResponseS.class, action1));
  }


  /**
   * 学生签到记录
   */
  public Subscription getStudentSignRecord(MainDelegate appDelegateBase) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .getStudentSignRecord(ConfigDevice.getDeviceId(), ConfigDevice.school_id)
            .subscribeOn(Schedulers.newThread())
            .subscribe(new BaseRspObserver<>(StudentSignRecordResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                KJDB.getDefaultInstance().deleteByWhere(StudentSignRecord.class, "1==1");
                if (rsp.data != null && !rsp.data.isEmpty()) {
                  KJDB.getDefaultInstance().save(rsp.data);
                }
              }
            }));
  }

  /**
   * 上传抓拍记录
   */
  public Subscription uploadFailPhoto(TakePhotoRequest request, Action1<BaseResponse> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .uploadFailPhoto(ConfigDevice.school_id, request)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(BaseResponse.class, action1));
  }

  /**
   * 刷卡签到
   */
  public Subscription swipeSign(SwipeRequest request, Action1<SwipeResponseS> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .swipeSign(ConfigDevice.school_id, request)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(SwipeResponseS.class, action1));
  }

  /**
   * 刷脸签到
   */
  public Subscription allFaceSign(List<SwipeRequest> request, Action1<SwipeAllFaceResponseS> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .allFaceSign(ConfigDevice.school_id, request)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(SwipeAllFaceResponseS.class, action1));
  }

  /**
   * 刷脸签到
   */
  public Subscription faceSign(SwipeRequest request, Action1<SwipeResponseS> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .faceSign(ConfigDevice.school_id, request)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(SwipeResponseS.class, action1));
  }

  /**
   * 获取家长相关学生
   *
   * @param union_id
   * @param swipe_time
   * @param action1
   * @return
   */
  public Subscription getParentLinkStudent(String union_id, String swipe_time, Action1<ParentLinkStudentsResponseS> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .getParentLinkStudent(ConfigDevice.school_id, union_id, swipe_time)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(ParentLinkStudentsResponseS.class, action1));
  }

  /**
   * 轮播图
   *
   * @param type
   * @param action1
   * @return
   */
  Subscription loadBroadcast(int type, Action1<List<BroadcastData>> action1) {
    if (ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .broadcast(ConfigDevice.school_id, ConfigDevice.getDeviceId(), type)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(BroadcastResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                List<BroadcastData> broadcastVViewData = new ArrayList<>();
                BroadcastExtra extra = rsp.mBroadcastExtra;
                if (extra != null) {
                  List<viroyal.com.base.model.BroadcastData> list = extra.mBroadcastData;
                  if (list != null && list.size() > 0) {
                    for (viroyal.com.base.model.BroadcastData broadcastData : list) {
                      if (broadcastData.mBroadcasrMediaList != null && broadcastData.mBroadcasrMediaList.size() > 0) {
                        if (broadcastData.status != 1) {
                          continue;
                        }
                        for (int i = 0; i < broadcastData.mBroadcasrMediaList.size() && i < 1000; i++) {
                          BroadcastMedia bm = broadcastData.mBroadcasrMediaList.get(i);
                          BroadcastData b = new BroadcastData();
                          b.id = broadcastData.id * 1000 + i;
                          b.type = bm.types;
                          String bmurl = bm.url;
                          try {
                            bmurl = URLDecoder.decode(bmurl, "utf-8");
                          } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                          }
                          b.image_url = bmurl.replace("{}",
                                  "device_id=" + ConfigDevice.getDeviceId());
                          b.tvtitle = bm.title;
                          b.tvcontent = bm.content;
                          b.tvtitleSize = bm.titleSize;
                          b.tvcontSize = bm.contSize;
                          b.tvtitleColor = bm.titleColor;
                          b.tvcontColor = bm.contColor;

                          b.end_time = broadcastData.end_time;
                          b.start_time = broadcastData.start_time;
                          if (broadcastData.date_flag == 1) {
                            b.end_date = broadcastData.end_date;
                            b.start_date = broadcastData.start_date;
                          } else {
                            b.end_date = null;
                            b.start_date = null;
                          }

                          if (!TextUtils.isEmpty(bm.titleWidth) && bm.titleWidth.length() > 3) {
                            String e = bm.titleWidth.substring(0, bm.titleWidth.length() - 3);
                            b.maxwidthpercent = Integer.parseInt(e);
                          } else {
                            b.maxwidthpercent = 0;
                          }

                          if (broadcastData.is_top == 1) {
                            b.top = 1;
                          } else {
                            b.top = 0;
                          }
                          try {
                            b.duration = Integer.parseInt(bm.duration);
                          } catch (NumberFormatException e) {
                            e.printStackTrace();
                            b.duration = 12;
                          }
                          broadcastVViewData.add(b);
                        }
                      }
                    }
                  }
                  if (action1 != null) {
                    action1.call(broadcastVViewData);
                  }
                }
              }
            }));
  }

  public Subscription style(MainDelegate appDelegateBase, Context context) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .style(ConfigDevice.school_id, "kindergarten")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(StyleResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                KJDB.getDefaultInstance().deleteByWhere(OwnStyle.class, "1==1");
                if (rsp.data != null) {
                  KJDB.getDefaultInstance().save(rsp.data);
                  AppConfig.setSpeak(rsp.data.announce, context);
                  AppConfig.setTakePhoto(rsp.data.photograph, context);
                  AppConfig.setShowPhoto(rsp.data.show_photo, context);
                  AppConfig.setSwipeIntervalTime(rsp.data.swipe_interval_time, context);
                  AppConfig.setPickTimeConfig(rsp.data.pick_time_config, context);
                }
              } else {
                appDelegateBase.showShortToast(rsp.error_code, rsp.error_msg);
              }
              appDelegateBase.updateStyle();
            }));

  }

  /**
   * 菜谱
   */
  Subscription foods(MainDelegate appDelegateBase) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .foods(ConfigDevice.school_id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(FoodResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                KJDB.getDefaultInstance().deleteByWhere(Food.class, "1==1");
                if (rsp.foods != null && !rsp.foods.isEmpty()) {
                  KJDB.getDefaultInstance().save(rsp.foods);
                }
              } else {
                appDelegateBase.showShortToast(rsp.error_code, rsp.error_msg);
              }
              appDelegateBase.updateFoods();
            }));
  }

  /**
   * 加载通知通告
   */
  Subscription loadAnnounced(MainDelegate appDelegateBase) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .announced(ConfigDevice.getDeviceId(), ConfigDevice.getDeviceId(), "")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(AnnouncedResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                KJDB.getDefaultInstance().deleteByWhere(Announced.class, "1=1");
                KJDB.getDefaultInstance().save(rsp.data);
              }
              appDelegateBase.updateNotices();
            }));
  }

  /**
   * 二维码
   */
  Subscription getQrCodeUrl(MainDelegate appDelegateBase) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .getQrCodeUrl(ConfigDevice.school_id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(QrCodeResponseS.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                KJDB.getDefaultInstance().deleteByWhere(QrCode.class, "1==1");
                if (!TextUtils.isEmpty(rsp.data)) {
                  KJDB.getDefaultInstance().save(new QrCode(rsp.data));
                }
              }
              appDelegateBase.updateQrCode();
            }));
  }

  /**
   * 同步特征值列表 不管成功失败直接删除之前的记录 重新拉取
   *
   * @param appDelegateBase
   * @param context
   * @return
   */
  Subscription syncFeaturesFromService(MainDelegate appDelegateBase, Context context, String nextId, OnSyncToLocalSuccessListener onSyncToLocalSuccessListener) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .getFeatureList(AppConfig.MASTER_KEY, ConfigDevice.school_id, ConfigDevice.getDeviceId(), null, TextUtils.isEmpty(nextId) ? null : nextId)
            .subscribeOn(Schedulers.newThread())
            .subscribe(new BaseRspObserver<>(FeaturesSyncRsp.class, rsp -> {
              //接口请求成功  数据同步成功  进行下一页的请求
              if (ErrorCode.isSuccess(rsp.error_code)) {
                List<UserBean> saveReadySyncUsers = AiwinnManager.get().syncToLocal(rsp.users, context);
                if (null != onSyncToLocalSuccessListener) {
                  //所有特征值同步完成 进行图片的下载
                  onSyncToLocalSuccessListener.setOnSyncToLocalSuccessListener(saveReadySyncUsers);
                }
                if (TextUtils.equals("-1", rsp.next_id) || TextUtils.isEmpty(rsp.next_id)) {
                  //保存上一次请求成功的时间
                  SPUtils.getInstance(context).put(ConstantsYJ.SpTag.FEATURE_INFO_UPDATE_TIME, System.currentTimeMillis() / 1000 + "");
                  // 保存并设置阈值
                  SPUtils.getInstance(context).put(AttConstants.PREFS_UNLOCK, rsp.threshold);
                  ConfigLib.featureThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_UNLOCK, ConfigLib.featureThreshold);
                  return;
                } else {
                  //再次请求
                  syncFeaturesFromService(appDelegateBase, context, rsp.next_id, onSyncToLocalSuccessListener);
                }
              } else {
                appDelegateBase.showShortToast(rsp.error_code, rsp.error_msg);
              }
            }));
  }

  /**
   * 获取特征值列表 根据时间戳来获取最新更新的特征值 （包括删除、保存操作）
   * 分页加载
   *
   * @param appDelegateBase
   * @param context
   * @return
   */
  Subscription getFeaturesFromService(MainDelegate appDelegateBase, Context context, String nextId, OnSyncToLocalSuccessListener onSyncToLocalSuccessListener) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    String updateTime = SPUtils.getInstance(context).get(ConstantsYJ.SpTag.FEATURE_INFO_UPDATE_TIME, "");
    return Api.get().getApi(ApiObserver.class)
            .getFeatureList(AppConfig.MASTER_KEY, ConfigDevice.school_id, ConfigDevice.getDeviceId(),
                    TextUtils.isEmpty(updateTime) ? null : updateTime, TextUtils.isEmpty(nextId) ? null : nextId)
            .subscribeOn(Schedulers.newThread())
            .subscribe(new BaseRspObserver<>(FeaturesSyncRsp.class, rsp -> {
              //接口请求成功  数据同步成功  进行下一页的请求
              if (ErrorCode.isSuccess(rsp.error_code)) {
                List<UserBean> saveReadySyncUsers = AiwinnManager.get().syncToLocal(rsp.users, context);
                if (null != onSyncToLocalSuccessListener) {
                  //所有特征值同步完成 进行图片的下载
                  onSyncToLocalSuccessListener.setOnSyncToLocalSuccessListener(saveReadySyncUsers);
                }
                if (TextUtils.equals("-1", rsp.next_id) || TextUtils.isEmpty(rsp.next_id)) {
                  //保存上一次请求成功的时间
                  SPUtils.getInstance(context).put(ConstantsYJ.SpTag.FEATURE_INFO_UPDATE_TIME, System.currentTimeMillis() / 1000 + "");
                  // 保存并设置阈值
                  SPUtils.getInstance(context).put(AttConstants.PREFS_UNLOCK, rsp.threshold);
                  ConfigLib.featureThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_UNLOCK, ConfigLib.featureThreshold);
                  return;
                } else {
                  ((Activity)context).runOnUiThread(() -> {
                    //再次请求
                    getFeaturesFromService(appDelegateBase, context, rsp.next_id, onSyncToLocalSuccessListener);
                  });
                }
              } else {
                appDelegateBase.showShortToast(rsp.error_code, rsp.error_msg);
              }
            }));
  }

  /**
   * 获取设备硬件信息
   */
  Subscription getDeviceInfo(MainDelegate appDelegateBase, Context context, Action1<DeviceInfoRsp> action1) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .getDeviceInfo(ConfigDevice.school_id, ConfigDevice.getDeviceId())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(DeviceInfoRsp.class, rsp -> {
              if (ErrorCode.isSuccess(rsp.error_code)) {
                if (rsp.extra == null
                        || rsp.extra.device_info == null
                        || rsp.extra.device_info.cameras == null
                        || rsp.extra.device_info.cameras.isEmpty()) {
                  Slog.e(TAG, "getDeviceInfo(): device_info is empty!");
                  appDelegateBase.showToast(context.getString(R.string.success_but_no_device_info));
                  return;
                }
                DeviceInfo deviceInfo = rsp.extra.device_info;
                // 保存硬件信息
                SPUtils.getInstance(context).put(ConstantsYJ.SpTag.DEVICE_INFO, deviceInfo);
                AppConfig.NFC_mode = deviceInfo.nfc_mode;
                // 小应用 保存整个设备信息
                SPUtils.getInstance(context).put(ConstantsYJ.SpTag.DEVICE_APP_INFO, rsp);
                // 消费模式支持：刷卡、刷脸、扫二维码
                DeviceInfoRsp.VerificationMode mo = deviceInfo.verification_mode;
                if (mo != null) {
                  AppConfig.setCode(mo.code, context);
                  AppConfig.setNfc(mo.nfc, context);
                  AppConfig.setFace(mo.face, context);
                } else {
                  AppConfig.setCode(0, context);
                  AppConfig.setNfc(0, context);
                  AppConfig.setFace(0, context);
                }
                // 重新刷新相机、二维码、刷卡模式的状态，避免缓存原因导致显示错误
                appDelegateBase.updateCameraStatusBySignTime();
                appDelegateBase.updateQrCode();
                if (null != action1) {
                  action1.call(rsp);
                }
              } else {
                appDelegateBase.showShortToast(rsp.error_code, rsp.error_msg);
              }
              appDelegateBase.updateApps();
            }));
  }

  /**
   * 获取最近三天的天气
   */
  Subscription get3DaysWeather(String location, MainDelegate appDelegateBase, Action1<WeatherResponseS> responseAction1) {
    if (appDelegateBase == null
            || ConfigDevice.DEMO_MODE
            || TextUtils.isEmpty(Api.get().getApi())) {
      return null;
    }
    return Api.get().getApi(ApiObserver.class)
            .get3DaysWeather(AppConfig.MASTER_KEY, location)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseRspObserver<>(WeatherResponseS.class, responseAction1));
  }


  /**
   * 有些接口全局只需调用一次，但有失败的可能，可调用此方法重新获取数据
   *
   * @param action 到时自动回调
   */
//  private void reGetData(Context context, Action1<Long> action) {
//    Subscription subscription = Observable.timer(10L, TimeUnit.SECONDS).subscribe(action);
//    ((MainActivity) context).addRxSubscription(subscription);
//  }

  private final String TAG = getClass().getSimpleName();

  public List<BroadcastData> data;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

  }

  private MainModel(Parcel in) {
  }

  public MainModel() {
  }

  public static final Creator<MainModel> CREATOR = new Creator<MainModel>() {
    @Override
    public MainModel createFromParcel(Parcel in) {
      return new MainModel(in);
    }

    @Override
    public MainModel[] newArray(int size) {
      return new MainModel[size];
    }
  };
}
