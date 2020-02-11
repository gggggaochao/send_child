package viroyal.com.base;


import android.content.Context;

import com.amap.api.location.AMapLocationListener;
import com.baidu.tts.BaiDuTtsUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.suntiago.baseui.App;
import com.suntiago.baseui.utils.file.StorageHelper;
import com.suntiago.baseui.utils.file.StorageManagerHelper;
import com.suntiago.baseui.utils.log.CrashHandler;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.network.network.Api;
import com.suntiago.network.network.download.DownloadConfig;
import com.suntiago.network.network.upload.UploadRepository;

import org.kymjs.kjframe.KJDB;

import java.io.File;
import java.lang.ref.WeakReference;

import viroyal.com.base.face.aiwinn.AiwinnManager;
import viroyal.com.base.listener.OnAiwinnSuccessListener;
import viroyal.com.base.util.AMapUtil;
import viroyal.com.dev.splash.ConfigDevice;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/8/12
 */
public class MyApp extends App {
  public static final String COM = "viroyal";
  public static final String appNAme = "sendchild";

  private static WeakReference<Context> mContext;
  public static final int DATABASE_VERSION = 5;

  @Override
  public void onCreate() {
    Slog.d("MyApp", "---------------onCreate");
    super.onCreate();
    mContext = new WeakReference<>(getApplicationContext());
    KJDB.create(this, appNAme + "db", true, DATABASE_VERSION, new KJDB_UpdateHelper());
    StorageHelper storageHelper = StorageManagerHelper.getStorageHelper();
    storageHelper.initPath(COM, appNAme);
    //配置下载路径
    DownloadConfig.DOWNLOAD_PATH = storageHelper.getStoragePath(this) + "download" + File.separator;
    //初始化异常log保存策略
    CrashHandler crashHandler = CrashHandler.getInstance();
    crashHandler.init(getApplicationContext());
    Slog.enableSaveLog(true);
    Slog.init(getApplicationContext());
    Api.init(this);

    AppConfig.initDefault(this);

    initCalligraphyConfig();
    initDownloader();
  }

  private void initCalligraphyConfig() {
    // https://github.com/chrisjenx/Calligraphy
    //这里可以设置默认的字体
//    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//            .setDefaultFontPath("fonts/Cartoon.ttf")
//            .setFontAttrId(R.attr.fontPath)
//            .build());
  }

  private void initDownloader() {
    /**
     * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
     * by below code, so please do not worry about performance.
     * @see FileDownloader#init(Context)
     */
    FileDownloader.setupOnApplicationOnCreate(this)
            .maxNetworkThreadCount(12)
            .connectionCreator(new FileDownloadUrlConnection
                    .Creator(new FileDownloadUrlConnection.Configuration()
                    .connectTimeout(15_000) // set connection timeout.
                    .readTimeout(15_000) // set read timeout.
            ))
            .commit();
  }

  /**
   * 初始化人脸识别sdk
   */
  public static void initAiwinnFace(OnAiwinnSuccessListener onAiwinnSuccessListener) {
    AiwinnManager.get().initDirAndDB(mContext.get(), onAiwinnSuccessListener);
  }

  public static void initBaiDuTts() {
    //百度語音
    BaiDuTtsUtil baiDuTtsUtil = BaiDuTtsUtil.getInstance();
    baiDuTtsUtil.setDebug(BuildConfig.DEBUG);
    baiDuTtsUtil.setConfig(AppConfig.APP_ID, AppConfig.APP_KEY, AppConfig.SECRET_KEY, ConfigDevice.serial_number);
    BaiDuTtsUtil.getInstance().initialTts(mContext.get());
  }

  public static void initAMap(AMapLocationListener mLocationListener) {
    AMapUtil.initAMap(mContext.get(), mLocationListener);
  }

  public static void initUpload() {
    //初始化文件上传的信息
    String sAliHostName = "http://viroyalcampus.oss-cn-shanghai.aliyuncs.com";
    String sBucketOther = "其他";
    String sSchoolId = ConfigDevice.school_id;
    String sSchoolName = sSchoolId + "/接送系统";
    String sApiHost = "https://mcpapi.iyuyun.net:18443/home/ossinfo";
    String sMasterKey = "tUnmjGTZglI49CQWmsqhJQmSs83V2Y1e";
    UploadRepository.initAlioss(sAliHostName, sBucketOther, sSchoolName, sApiHost, sMasterKey, sSchoolId);
  }

  public static Context getContext() {
    return mContext.get();
  }

}
