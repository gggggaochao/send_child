package viroyal.com.base.face.aiwinn;

import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;

import com.aiwinn.adv.library.verifylicense.VLUtils;
import com.aiwinn.base.util.FileUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.NetListener;
import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.log.Slog;

import java.util.ArrayList;
import java.util.List;

import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.aiwinn.common.AttConstants;
import viroyal.com.base.face.aiwinn.common.Config;
import viroyal.com.base.face.bean.DeviceInfo;
import viroyal.com.base.face.bean.FaceInfoBean;
import viroyal.com.base.face.rsp.FeatureUser;
import viroyal.com.base.listener.OnAiwinnSuccessListener;


/**
 * @author chenjunwei
 * @desc
 * @date 2019-09-18
 */
public class AiwinnManager implements FaceI {
  private final String TAG = getClass().getSimpleName();

  private static AiwinnManager sAiwinnManager;

  public static AiwinnManager get() {
    if (sAiwinnManager == null) {
      synchronized (AiwinnManager.class) {
        if (sAiwinnManager == null) {
          sAiwinnManager = new AiwinnManager();
        }
      }
    }
    return sAiwinnManager;
  }

  public void initDirAndDB(Context context, OnAiwinnSuccessListener onAiwinnSuccessListener) {
    com.aiwinn.base.AiwinnManager.getInstance().init((Application) context);
    initConfig(context, onAiwinnSuccessListener);
    initAwDir();
    initPdDir();
    authorization(context, onAiwinnSuccessListener);
  }

  /**
   * 初始化人脸sdk
   */
  private void initSDK(Context context, OnAiwinnSuccessListener onAiwinnSuccessListener) {
    com.aiwinn.base.AiwinnManager.getInstance().setDebug(AttConstants.DEBUG);
    FaceDetectManager.setDebug(AttConstants.DEBUG);
    Status status = FaceDetectManager.init(context);
    if (status == Status.Ok) {
      AttConstants.INIT_STATE = true;
      if (!FaceDetectManager.initDb(AttConstants.EXDB)) {
        Slog.e("MyApp", "init ex db fail");
      } else {
        if (null != onAiwinnSuccessListener) {
          onAiwinnSuccessListener.setOnAiwinnSuccessListener();
        }
      }
    } else if (status == Status.AlreadyInitialized) {
      if (null != onAiwinnSuccessListener) {
        onAiwinnSuccessListener.setOnAiwinnSuccessListener();
      }
    } else {
      AttConstants.INIT_STATE = false;
      AttConstants.INIT_STATE_ERROR = status;
    }
  }

  public void release() {
    try {
      FaceDetectManager.release();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取摄像机预览角度
   */
  private int getCameraDegree(int orientation) {
    switch (orientation) {
      case 1:
        return 0;
      case 2:
        return 90;
      case 3:
        return 180;
      default:
        return 270;
    }
  }

  /**
   * 初始化人脸配置项
   */
  private void initConfig(Context context, OnAiwinnSuccessListener onAiwinnSuccessListener) {
    FileUtils.createOrExistsDir(Constants.PATH_LIVE_SAVE);
    FileUtils.createOrExistsDir(AttConstants.PATH_AIWINN);
    FileUtils.createOrExistsDir(AttConstants.PATH_ATTENDANCE);
    FileUtils.createOrExistsDir(AttConstants.PATH_BULK_REGISTRATION);
    FileUtils.createOrExistsDir(AttConstants.PATH_CARD);
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    int cameraCount = Camera.getNumberOfCameras();
    for (int i = 0; i < cameraCount; i++) {
      if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        AttConstants.hasBackCamera = true;
      } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        AttConstants.hasFrontCamera = true;
      }
    }
//        if (AttConstants.hasBackCamera) {
//          AttConstants.CAMERA_ID = AttConstants.CAMERA_BACK_ID;
//          AttConstants.CAMERA_DEGREE = AttConstants.CAMERA_BACK_DEGREE;
//        }
//        if (!AttConstants.hasBackCamera && AttConstants.hasFrontCamera) {
//          AttConstants.CAMERA_ID = AttConstants.CAMERA_FRONT_ID;
//          AttConstants.CAMERA_DEGREE = AttConstants.CAMERA_FRONT_DEGREE;
//        }
    AttConstants.cameraCount = cameraCount;
    AttConstants.CAMERA_ID = SPUtils.getInstance(context).get(AttConstants.PREFS_CAMERA_ID, AttConstants.CAMERA_ID);
    DeviceInfo deviceInfo = SPUtils.getInstance(context).get(ConstantsYJ.SpTag.DEVICE_INFO, DeviceInfo.class);
    AttConstants.CAMERA_PREVIEW_HEIGHT = SPUtils.getInstance(context).get(AttConstants.PREFS_CAMERA_PREVIEW_SIZE, AttConstants.CAMERA_PREVIEW_HEIGHT);
    if (deviceInfo != null && deviceInfo.cameras != null && !deviceInfo.cameras.isEmpty()) {
//        AttConstants.CAMERA_PREVIEW_WIDTH = deviceInfo.width;
//        AttConstants.CAMERA_PREVIEW_HEIGHT = deviceInfo.height;
      AttConstants.CAMERA_FRONT_ID = deviceInfo.cameras.get(0).position;
      AttConstants.CAMERA_FRONT_DEGREE = getCameraDegree(deviceInfo.cameras.get(0).orientation);
      if (deviceInfo.cameras.size() > 1) {
        AttConstants.CAMERA_BACK_ID = deviceInfo.cameras.get(1).position;
        AttConstants.CAMERA_BACK_DEGREE = getCameraDegree(deviceInfo.cameras.get(1).orientation);
      }
    }
    AttConstants.CAMERA_ID = AttConstants.CAMERA_FRONT_ID;
    AttConstants.CAMERA_DEGREE = AttConstants.CAMERA_FRONT_DEGREE ;
    AttConstants.PREVIEW_DEGREE = AttConstants.CAMERA_FRONT_DEGREE;
    ConfigLib.dbUserIDUseDefault = false;// userId不用默认，方便随时更改
    ConfigLib.similarityFilter = false;// 注册时是否过滤相似人脸
    ConfigLib.saveHeadPic = false;
    ConfigLib.picScaleSize = SPUtils.getInstance(context).get(AttConstants.PREFS_TRACKER_SIZE, ConfigLib.picScaleSize);
    ConfigLib.picScaleRate = SPUtils.getInstance(context).get(AttConstants.PREFS_DETECT_RATE, ConfigLib.picScaleRate);
    ConfigLib.Nv21ToBitmapScale = SPUtils.getInstance(context).get(AttConstants.PREFS_FEATURE_SIZE, ConfigLib.Nv21ToBitmapScale);
    ConfigLib.minRecognizeRect = SPUtils.getInstance(context).get(AttConstants.PREFS_FACE_SIZE, ConfigLib.minRecognizeRect);
    FaceDetectManager.setFaceMinRect(SPUtils.getInstance(context).get(AttConstants.PREFS_DETECT_SIZE, FaceDetectManager.getFaceMinRect()));
    AttConstants.CAMERA_DEGREE = SPUtils.getInstance(context).get(AttConstants.PREFS_CAMERA_DEGREE, AttConstants.CAMERA_DEGREE);
    AttConstants.PREVIEW_DEGREE = SPUtils.getInstance(context).get(AttConstants.PREFS_PREVIEW_DEGREE, AttConstants.PREVIEW_DEGREE);
    AttConstants.LEFT_RIGHT = SPUtils.getInstance(context).get(AttConstants.PREFS_LR, AttConstants.LEFT_RIGHT);
    AttConstants.TOP_BOTTOM = SPUtils.getInstance(context).get(AttConstants.PREFS_TB, AttConstants.TOP_BOTTOM);
    Constants.DEBUG_SAVE_FAKE = SPUtils.getInstance(context).get(AttConstants.PREFS_FAKE, Constants.DEBUG_SAVE_FAKE);
    Constants.DEBUG_SAVE_LIVE = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVE, Constants.DEBUG_SAVE_LIVE);
    ConfigLib.detectWithRecognition = SPUtils.getInstance(context).get(AttConstants.PREFS_RECOGNITION, ConfigLib.detectWithRecognition);
    ConfigLib.detectWithLiveness = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVENESS, ConfigLib.detectWithLiveness);
    ConfigLib.featureThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_UNLOCK, ConfigLib.featureThreshold);
    ConfigLib.livenessThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVENESST, ConfigLib.livenessThreshold);
    ConfigLib.livenessThreshold2 = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVENESST2, ConfigLib.livenessThreshold2);
    ConfigLib.livenessThreshold3 = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVENESST3, ConfigLib.livenessThreshold3);
    ConfigLib.livenessLiveNum = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVECOUNT, ConfigLib.livenessLiveNum);
    ConfigLib.livenessFakeNum = SPUtils.getInstance(context).get(AttConstants.PREFS_FAKECOUNT, ConfigLib.livenessFakeNum);
    ConfigLib.registerPicRect = SPUtils.getInstance(context).get(AttConstants.PREFS_FACEMINIMA, ConfigLib.registerPicRect);
    // 单帧最大识别人脸个数
    ConfigLib.maxShowDetectNum = SPUtils.getInstance(context).get(AttConstants.MAX_SHOW_DETECT_NUM, AttConstants.maxShowDetectNum);
    // 识别过滤人脸宽高值
    ConfigLib.minRecognizeRect = SPUtils.getInstance(context).get(AttConstants.MIN_RECOGNIZED_RECT, AttConstants.minRecognizeRect);
    ConfigLib.maxRegisterBrightness = SPUtils.getInstance(context).get(AttConstants.PREFS_MAXREGISTERBRIGHTNESS, ConfigLib.maxRegisterBrightness);
    ConfigLib.minRegisterBrightness = SPUtils.getInstance(context).get(AttConstants.PREFS_MINREGISTERBRIGHTNESS, ConfigLib.minRegisterBrightness);
    ConfigLib.blurRegisterThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_BLURREGISTERTHRESHOLD, ConfigLib.blurRegisterThreshold);
    ConfigLib.maxRecognizeBrightness = SPUtils.getInstance(context).get(AttConstants.PREFS_MAXRECOGNIZEBRIGHTNESS, ConfigLib.maxRecognizeBrightness);
    ConfigLib.minRecognizeBrightness = SPUtils.getInstance(context).get(AttConstants.PREFS_MINRECOGNIZEBRIGHTNESS, ConfigLib.minRecognizeBrightness);
    ConfigLib.blurRecognizeThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_BLURECOGNIZETHRESHOLD, ConfigLib.blurRecognizeThreshold);
    ConfigLib.blurRecognizeNewThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_BLURECOGNIZENEWTHRESHOLD, ConfigLib.blurRecognizeNewThreshold);
    Constants.DEBUG_SAVE_BLUR = SPUtils.getInstance(context).get(AttConstants.PREFS_SAVEBLURDATA, Constants.DEBUG_SAVE_BLUR);
    Constants.DEBUG_SAVE_LIVEPIC_SDK = SPUtils.getInstance(context).get(AttConstants.PREFS_ST, Constants.DEBUG_SAVE_LIVEPIC_SDK);
    Constants.DEBUG_SAVE_ERROR = SPUtils.getInstance(context).get(AttConstants.PREFS_SE, Constants.DEBUG_SAVE_ERROR);
    ConfigLib.mouthDebug = SPUtils.getInstance(context).get(AttConstants.PREFS_SC, ConfigLib.mouthDebug);
    Constants.DEBUG_SAVE_NOFACE = SPUtils.getInstance(context).get(AttConstants.PREFS_SAVENOFACEDATA, Constants.DEBUG_SAVE_NOFACE);
    Constants.DEBUG_SAVE_SIMILARITY_SMALL = SPUtils.getInstance(context).get(AttConstants.PREFS_SAVESSDATA, Constants.DEBUG_SAVE_SIMILARITY_SMALL);
    AttConstants.DEBUG = SPUtils.getInstance(context).get(AttConstants.PREFS_DEBUG, AttConstants.DEBUG);
    Constants.LIVENESS_MODE = SPUtils.getInstance(context).get(AttConstants.PREFS_LIVE_MODE, Constants.LIVENESS_MODE);
    Constants.RECOGNITION_MODE = SPUtils.getInstance(context).get(AttConstants.PREFS_TRACKER_MODE, Constants.RECOGNITION_MODE);
    Constants.DEBUG_SAVE_TRACKER = SPUtils.getInstance(context).get(AttConstants.PREFS_TRACKER, Constants.DEBUG_SAVE_TRACKER);
    AttConstants.REGISTER_DEFAULT = SPUtils.getInstance(context).get(AttConstants.PREFS_REGISTER_DEFAULT, AttConstants.REGISTER_DEFAULT);
    AttConstants.DETECT_DEFAULT = SPUtils.getInstance(context).get(AttConstants.PREFS_DETECT_DEFAULT, AttConstants.DETECT_DEFAULT);
    ConfigLib.detectWithInfraredLiveness = SPUtils.getInstance(context).get(AttConstants.PREFS_DETECTINFRARED, ConfigLib.detectWithInfraredLiveness);
    Constants.DEBUG_SAVE_INFRARED_LIVE = SPUtils.getInstance(context).get(AttConstants.PREFS_SAVEINFRAREDLIVE, Constants.DEBUG_SAVE_INFRARED_LIVE);
    Constants.DEBUG_SAVE_INFRARED_FAKE = SPUtils.getInstance(context).get(AttConstants.PREFS_SAVEINFRAREDFAKE, Constants.DEBUG_SAVE_INFRARED_FAKE);
    ConfigLib.livenessInFraredThreshold = SPUtils.getInstance(context).get(AttConstants.PREFS_INFRAREDVALUE, ConfigLib.livenessInFraredThreshold);
    ConfigLib.detectWithLivenessModeUseNew = SPUtils.getInstance(context).get(AttConstants.PREFS_USENEWLIVEVERSION, ConfigLib.detectWithLivenessModeUseNew);
    ConfigLib.detectWithLivenessModeUseMix = SPUtils.getInstance(context).get(AttConstants.PREFS_USEFIXLIVEVERSION, ConfigLib.detectWithLivenessModeUseMix);
    Constants.INFRARED_MODE = SPUtils.getInstance(context).get(AttConstants.PREFS_INFRAREDMODE, Constants.INFRARED_MODE);
    ConfigLib.enhanceMode = SPUtils.getInstance(context).get(AttConstants.PREFS_ENHANCEMODE, ConfigLib.enhanceMode);
    ConfigLib.detectWithCovStatus = SPUtils.getInstance(context).get(AttConstants.PREFS_COVERMODE, ConfigLib.detectWithCovStatus);
    int arg2 = SPUtils.getInstance(context).get(AttConstants.PREFS_DETECT_MODE, 0);
    int mapValue;
    switch (arg2) {
      case 5:
        mapValue = 16;
        break;
      case 6:
        mapValue = 17;
        break;
      case 7:
        mapValue = 18;
        break;
      case 8:
        mapValue = 19;
        break;
      case 9:
        mapValue = 20;
        break;
      default:
        mapValue = arg2;
    }
    FaceDetectManager.setDetectFaceMode(mapValue);
    initSDK(context, onAiwinnSuccessListener);
    AttConstants.Detect_Exception = false;
    Log.d(TAG, "initConfig -> Detect_Exception " + AttConstants.Detect_Exception + " INIT_STATE " + AttConstants.INIT_STATE);
  }

  private void initAwDir() {
    Slog.d(TAG, "initAw_Enter");
    boolean retsd = FileUtils.createOrExistsDir(Config.SD_AIWINN_DIR);
    Slog.d(TAG, "retsd= " + retsd);
    boolean retsd_dir = FileUtils.createOrExistsDir(Config.SD_ATT_DIR);
    Slog.d(TAG, "retsd_dir= " + retsd_dir);
    boolean retsd_pic = FileUtils.createOrExistsDir(Config.SD_ATT_PIC_DIR);
    Slog.d(TAG, "retsd_pic= " + retsd_pic);
    boolean retlog = FileUtils.createOrExistsDir(Config.SD_LOG_DIR);  // 创建保存 log的文件夹
    Slog.d(TAG, "retlog= " + retlog);
  }

  private void initPdDir() {
    Slog.d(TAG, "initPd_Enter");
    boolean retsd = FileUtils.createOrExistsDir(Config.SD_AIWINN_DIR);
    Slog.d(TAG, "retsd= " + retsd);
    boolean retlock_dir = FileUtils.createOrExistsDir(Config.SD_LOCK_DIR);
    Slog.d(TAG, "ret_LOCK_DIR_dir= " + retlock_dir);
    boolean retpd_pic = FileUtils.createOrExistsDir(Config.SD_LOCK_PD_DIR);
    Slog.d(TAG, "retsd_LOCK_PD= " + retpd_pic);
    boolean retpd_att_pic = FileUtils.createOrExistsDir(Config.SD_LOCK_ATT_PIC);
    Slog.d(TAG, "retpd_att_pic= " + retpd_att_pic);
  }

  /**
   * 设备授权
   */
  @Override
  public void authorization(Context context, OnAiwinnSuccessListener onAiwinnSuccessListener) {
    if (!VLUtils.verify()) {
      FaceDetectManager.networkAuthorization(new NetListener() {
        @Override
        public void onComplete() {
          Slog.d(TAG, "retnetwork_networkAuthorization_OK");
          // 授权成功 再初始化
          initSDK(context, onAiwinnSuccessListener);
        }

        @Override
        public void onError(Status code, String msg) {
          Slog.d(TAG, "retnetwork_error=" + code + ", msg=" + code);
        }
      });
    }
  }

  /**
   * 人脸采集
   */
  @Override
  public void collectFaceData() {

  }

  /**
   * 人脸同步上行
   */
  @Override
  public List<FaceInfoBean> syncToServer() {
    List<FaceInfoBean> faceInfoBeanList = new ArrayList<>();
    List<UserBean> faceList = FaceDetectManager.queryAll(AttConstants.EXDB);
    for (UserBean face : faceList) {
      StringBuilder sb = new StringBuilder();
      ArrayList<Float> features = face.features;
      for (int i = 0; i < features.size(); i++) {
        sb.append(String.valueOf(features.get(i))).append(",");
      }
      String feature = sb.toString();
      FaceInfoBean bean = new FaceInfoBean(face.urlImagePath, face.userId, feature);
      faceInfoBeanList.add(bean);
    }
    return faceInfoBeanList;
  }

  /**
   * 人脸同步下行
   */
  @Override
  public List<UserBean> syncToLocal(List<FeatureUser> downloadedUsers, Context mContext) {
    if (null == downloadedUsers || downloadedUsers.size() == 0) {
      return null;
    }
    //新增的list
    List<UserBean> saveReadySyncUsers = new ArrayList<>();
    for (FeatureUser downloadedUser : downloadedUsers) {
      if (downloadedUser == null || TextUtils.isEmpty(downloadedUser.name)) {
        continue;
      }
      List<UserBean> userBeanList = FaceDetectManager.queryByUserId(downloadedUser.union_id, AttConstants.EXDB);

      //bean转换成UserBean
      UserBean user = (null != userBeanList && userBeanList.size() > 0) ? userBeanList.get(0) : new UserBean();
      user.userId = downloadedUser.union_id;
      user.name = downloadedUser.name;
      user.urlImagePath = downloadedUser.image_url;
      String[] strings = downloadedUser.value.split(",");
      ArrayList<Float> floats = new ArrayList<>();
      for (String str : strings) {
        floats.add(Float.valueOf(str));
      }
      user.features = floats;

      try {
        //直接删除 在判断要不要保存
        FaceDetectManager.deleteByUserInfo(user, AttConstants.EXDB);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (TextUtils.isEmpty(downloadedUser.deleted_at)) {
        saveReadySyncUsers.add(user);
      }

    }
    return saveReadySyncUsers;
  }
}
