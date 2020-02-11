package viroyal.com.base.activity.main;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aiwinn.base.util.AppUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.alibaba.fastjson.JSON;
import com.baidu.tts.BaiDuTtsUtil;
import com.google.gson.Gson;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.ToastUtils;
import com.suntiago.baseui.utils.date.DateUtils;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.network.network.ErrorCode;
import com.suntiago.network.network.download.DownloadConfig;
import com.suntiago.network.network.rsp.BaseResponse;
import com.suntiago.network.network.rsp.FileUploadResponse;
import com.suntiago.network.network.upload.UploadRepository;
import com.wonderkiln.camerakit.CameraView;

import org.kymjs.kjframe.KJDB;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import viroyal.com.base.AppConfig;
import viroyal.com.base.MyApp;
import viroyal.com.base.R;
import viroyal.com.base.activity.broadcast.BroadcastActivity;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.aiwinn.AiwinnManager;
import viroyal.com.base.face.aiwinn.common.AttConstants;
import viroyal.com.base.model.AllLocalSignRecord;
import viroyal.com.base.model.LocalSignRecord;
import viroyal.com.base.model.Student;
import viroyal.com.base.model.StudentData;
import viroyal.com.base.model.StudentSignRecord;
import viroyal.com.base.model.Swipe;
import viroyal.com.base.model.TakePhotoRecord;
import viroyal.com.base.model.Weather;
import viroyal.com.base.net.req.SwipeRequest;
import viroyal.com.base.net.req.TakePhotoRequest;
import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;
import viroyal.com.base.net.rsp.SwipeAllFaceResponseS;
import viroyal.com.base.net.rsp.SwipeResponseS;
import viroyal.com.base.net.socket.WebClient;
import viroyal.com.base.receiver.WifiReceiver;
import viroyal.com.base.service.NfcService;
import viroyal.com.base.util.TimerManagerUtil;
import viroyal.com.base.util.Utils;
import viroyal.com.base.widget.dialog.OnDialogKeyListener;
import viroyal.com.base.widget.dialog.OnProgressDialog;
import viroyal.com.dev.NFCMonitorBaseActivity;
import viroyal.com.dev.broadcast.BroadcastData;
import viroyal.com.dev.broadcast.BroadcastView;

import static viroyal.com.base.AppConfig.LOCAL_TIMEOUT_DELAY_NO_TOUCH;
import static viroyal.com.base.AppConfig.TIME_SYNC_ANNOUNCE_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_BROADCAST_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_FEATURES_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_HOME_INFO_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_QR_CODE_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_STUDENT_INFO_DELAY_MS;
import static viroyal.com.base.AppConfig.TIME_SYNC_WEATHER_INFO_DELAY_MS;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_INVALID_CARD;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_INVALID_USER;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_NO_USER;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_OUT_OF_DATE;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_SIGN_IN_SUCCESS;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_SIGN_IN_TIME_FAIL;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_SIGN_OUT_FIRST_SUCCESS;
import static viroyal.com.base.common.ConstantsYJ.ParamsTag.SWIPE_SIGN_OUT_SUCCESS;

/**
 * @author chenjunwei
 * @desc
 * @date 2019-09-03
 */
public class MainActivity extends NFCMonitorBaseActivity<MainDelegate, MainModel> {

  private final String TAG = getClass().getSimpleName();
  private final String PKG_PATH = "/sdcard/viroyal/sendchild/pic/";
  public static final String DATA_STYLE = "yyyy_MM_dd_HH_mm_ss";
  public static final int MAX_COUNT = 10000;

  private boolean isFirstLoadNet = true;
  protected boolean isWifiConnected = false;
  private boolean is_processing_off_line_data = false;

  public String success_sign_in;
  public String success_sign_out;
  public String fail_card_except;
  public String fail_sign_in_time;
  public String fail_user_invalid;
  public String fail_card_invalid;
  public String fail_sign_in;

  public final static int LOCAL_TIMEOUT_MSG_NO_TOUCH = 10000;
  private BroadcastView mBroadcastViewAdsPre;
  private CameraView cameraKitView;


  /**
   * 处理签到记录、拍照记录 队列处理
   */
  private ExecutorService singleThreadExecutorRecord = Executors.newSingleThreadExecutor();
  /**
   * 处理文件
   */
  private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    WebClient.connectWebSocket(this);
    mBroadcastViewAdsPre = new BroadcastView(this);
    mBroadcastViewAdsPre.setbroadcastIndex(2);
    MyApp.initUpload();
    initTip();
    registerWifiReceiver();
    viewDelegate.initWidget();
    viewDelegate.setListener();
    enableNFC();
    startTimerTask();
    getDataFromDb();
    startNfcService();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  /**
   * 读取数据库数据
   */
  private void getDataFromDb() {
    viewDelegate.updatePassword();
    viewDelegate.updateStyle();
    viewDelegate.updateBroadcastView();
    viewDelegate.updateApps();
    viewDelegate.updateFoods();
    viewDelegate.updateNotices();
    viewDelegate.updateCameraStatusBySignTime();
  }

  private void refreshData() {
    //第一次启动 删除本地学生信息和人脸信息
    boolean firstStart = SPUtils.getInstance(this).get(ConstantsYJ.SpTag.FIRST_START, true);
    if (firstStart) {
      resetFile();
      SPUtils.getInstance(this).put(ConstantsYJ.SpTag.FIRST_START, false);
    }
    if (isFirstLoadNet) {
      isFirstLoadNet = false;
      syncDefaultData();
      syncFeatures();
      syncHomeInfo();
      syncStudentInfo();
      syncAdjustVolume();
      syncBroadcast();
      syncAdsBroadcast();
      syncAnnounced();
      syncQrCode();
      sync3DaysWeather(ConstantsYJ.LOCATION_ADDRESS);
    }

  }

  /**
   * 删除学生数据、本地图片、特征值数据等
   */
  public void resetFile() {
    singleThreadExecutor.execute(() -> {
      //直接删除记录 成功之后保存数据
      KJDB.getDefaultInstance().deleteByWhere(Student.class, "1==1");
      try {
        // 先删除本地的人脸特征值
        FaceDetectManager.deleteAll(AttConstants.EXDB);
        Utils.deleteFile(new File(DownloadConfig.DOWNLOAD_PATH));
      } catch (Exception e) {
        e.printStackTrace();
      }
      finalCounts = 0;
      totalCounts = 0;
    });

  }

  @Override
  public void onBackPressed() {
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    super.initView(savedInstanceState);
    cameraKitView = findViewById(R.id.cameraKitView);
    viewDelegate.initDateShow();
    viewDelegate.initCameraData();
    //点击时间展示日历
    viewDelegate.showCalendarPopwindow(MainActivity.this);
  }

  @Override
  protected void initData(Bundle savedInstanceState) {
    super.initData(savedInstanceState);
    mHandler = new Handler(getMainLooper(), this::handleMsg);
  }

  @Override
  protected void onResume() {
    super.onResume();
    bindService(new Intent(MainActivity.this, NfcService.class), conn, Context.BIND_AUTO_CREATE);
    onStartCaptureImage();
    //广告如果没有处理，
    mHandler.removeMessages(LOCAL_TIMEOUT_MSG_NO_TOUCH);
    mHandler.sendEmptyMessageDelayed(LOCAL_TIMEOUT_MSG_NO_TOUCH, LOCAL_TIMEOUT_DELAY_NO_TOUCH);
  }

  @Override
  protected void onPause() {
    onStopCaptureImage();
    super.onPause();
  }

  /**
   * 点击次数
   */
  private int COUNTS = 5;
  /**
   * 记录点击次数
   */
  private long[] mHits = new long[COUNTS];

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      Utils.fullScreen(getWindow());
      //所有元素左移一个位置
      System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
      mHits[mHits.length - 1] = SystemClock.uptimeMillis();
      if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
        long count = FaceDetectManager.queryCount(AttConstants.EXDB);
        viewDelegate.showToast("当前特征值数量：" + count);
        //重置点击次数
        mHits = new long[COUNTS];
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  Handler mHandler;

  private boolean handleMsg(Message msg) {
    if (msg.what == LOCAL_TIMEOUT_MSG_NO_TOUCH) {
      handleLocalNotouch();
    }
    return true;
  }

  /**
   * 处理广告长时间没有触摸的情况
   */
  private void handleLocalNotouch() {
    if (mBroadcastViewAdsPre != null) {
      boolean b = mBroadcastViewAdsPre.checkHaveMediaToPlay();
      Slog.d(TAG, "handleNotouch  []:" + b);
      if (b) {
        if (activityIsResume) {
          startActivity(new Intent(this, BroadcastActivity.class));
        }
      } else {
        //通知关闭全屏广播界面
        RxBus.get().post(ConstantsYJ.RxTag.RX_TAG_CLOSE_BROADCAST, "");
      }
    }
    //如果没有处理，
    mHandler.removeMessages(LOCAL_TIMEOUT_MSG_NO_TOUCH);
    mHandler.sendEmptyMessageDelayed(LOCAL_TIMEOUT_MSG_NO_TOUCH, LOCAL_TIMEOUT_DELAY_NO_TOUCH);
  }

  private synchronized void takePhoto(Bitmap bitmap, List<Student> mCurrentStudents, long mCurrentTime, String mCurrentCardNo, int swipeStatus) {
    if (null == bitmap) {
      return;
    }
    //多个学生上传多次 图片上传只需要一次
    String currentTime = new SimpleDateFormat(DATA_STYLE).format(new Date());
    String name = currentTime + ".jpg";
    String path = PKG_PATH + name;
    if (Utils.saveBitmap2File(bitmap, path, MainActivity.this)) {
      //删除超过指定个数的本地图片
      Utils.deleteFurthestFile(PKG_PATH, DATA_STYLE, "", MAX_COUNT);
      UploadRepository.upload(MainActivity.this, path, AppConfig.DEV_APP_TYPE, name, new UploadRepository.ApiCallback() {
        @Override
        public void onResult(FileUploadResponse response) {
          if (ErrorCode.isSuccess(response.error_code)) {
            uploadLocalTakePhoto(saveTakePhotoRecord(path, name, response, mCurrentStudents, mCurrentTime, mCurrentCardNo, swipeStatus));
          } else {
            saveTakePhotoRecord(path, name, response, mCurrentStudents, mCurrentTime, mCurrentCardNo, swipeStatus);
          }
        }

        @Override
        public void onResult(String s) {

        }
      });
    }
  }

  /**
   * 这里尽量不适用全局变量存在多个同时请求的情况
   *
   * @param path
   * @param name
   * @param response
   */
  public synchronized List<TakePhotoRecord> saveTakePhotoRecord(String path, String name, FileUploadResponse response, List<Student> mCurrentStudents,
                                                                long mCurrentTime, String mCurrentCardNo, int swipeStatus) {
    List<TakePhotoRecord> takePhotoRecordList = new ArrayList<>();
    //保存到本地统一上传
    if (null == mCurrentStudents || mCurrentStudents.size() == 0) {
      //这里保证一定要有一个学生、假学生 刷卡异常使用
      Student student = new Student();
      student.card_no = mCurrentCardNo;
      mCurrentStudents.add(student);
    }
    for (Student student : mCurrentStudents) {
      //图片上传成功 上传多个学生拍照记录 图片地址一样
      TakePhotoRecord takePhotoRecord = new TakePhotoRecord();
      takePhotoRecord.swipe_time = mCurrentTime;
      takePhotoRecord.localImagePath = path;
      takePhotoRecord.fileName = name;
      takePhotoRecord.swipe_status = swipeStatus;
      String userId = "";
      String cardNO = mCurrentCardNo;
      if (null != student) {
        userId = student.union_id;
        cardNO = student.card_no;
      }
      takePhotoRecord.card_no = cardNO;
      takePhotoRecord.union_id = userId;
      if (null != response && !TextUtils.isEmpty(response.extra)) {
        takePhotoRecord.networkImagePath = response.extra;
      }
      KJDB.getDefaultInstance().save(takePhotoRecord);
      String strWhere = "swipe_time='" + mCurrentTime + "' and ";
      if (ConstantsYJ.ParamsTag.SWIPE_CARD_STATUS == swipeStatus) {
        //刷卡
        strWhere = strWhere + "card_no='" + takePhotoRecord.card_no + "'";
      } else if (ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS == swipeStatus) {
        //刷脸
        strWhere = strWhere + "union_id='" + takePhotoRecord.union_id + "'";
      }
      //这里由于takePhotoRecord只有保存了才会有id（自增）所以在此需要重新查询一次
      List<TakePhotoRecord> localTakePhotoRecordList = KJDB.getDefaultInstance().findAllByWhere(TakePhotoRecord.class, strWhere);

      if (null != localTakePhotoRecordList && localTakePhotoRecordList.size() > 0) {
        takePhotoRecordList.add(localTakePhotoRecordList.get(localTakePhotoRecordList.size() - 1));
      }

      //更新本地记录
      List<AllLocalSignRecord> allLocalSignRecordList = KJDB.getDefaultInstance().findAllByWhere(AllLocalSignRecord.class,
              "swipe_time='" + mCurrentTime + "' and union_id='" + takePhotoRecord.union_id + "'");
      if (null != allLocalSignRecordList && allLocalSignRecordList.size() > 0) {
        AllLocalSignRecord allLocalSignRecord = allLocalSignRecordList.get(0);
        allLocalSignRecord.localImagePath = path;
        KJDB.getDefaultInstance().update(allLocalSignRecord);
      }
    }
    return takePhotoRecordList;
  }


  /**
   * 开启定时任务 每天早上0点删除原来的数据 保存最新信息
   */
  private void startTimerTask() {
    TimerManagerUtil.timerTask(() -> {
      addRxSubscription(iModel.getStudentSignRecord(viewDelegate));
    });
  }

  private void initTip() {
    success_sign_in = getString(R.string.success_sign_in);
    success_sign_out = getString(R.string.success_sign_out);
    fail_card_except = getString(R.string.fail_card_except);
    fail_sign_in_time = getString(R.string.fail_sign_in_time);
    fail_user_invalid = getString(R.string.fail_user_invalid);
    fail_card_invalid = getString(R.string.fail_card_invalid);
    fail_sign_in = getString(R.string.fail_sign_in);
  }

  private WifiReceiver mWifiReceiver;
  private long lastWifiReceiverTime;

  private void registerWifiReceiver() {
    mWifiReceiver = new WifiReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(mWifiReceiver, filter);
    mWifiReceiver.setOnWifiConnectedListener(isWifiConnected -> {
      MainActivity.this.isWifiConnected = isWifiConnected;
      viewDelegate.updateQrCode();
      if (isWifiConnected) {
        //这里会存在多次调用的情况 2秒不在重复接收
        if (System.currentTimeMillis() - lastWifiReceiverTime < 2000) {
          lastWifiReceiverTime = System.currentTimeMillis();
          return;
        }
        lastWifiReceiverTime = System.currentTimeMillis();
        //有网的时候请求接口数据
        refreshData();
        uploadLocalSignRecordNet();
        uploadLocalTakePhotoNet();
      }
    });
  }

  private int lastTakePhotoRecordSize;
  private int takePhotoRecordCount = 0;
  private int lastUploadLocalSignRecordSize;
  private int uploadLocalSignRecordCount = 0;
  private final int REPEAT_COUNT = 3;

  /**
   * 离线数据处理 每次请求3次机会 重复3次之后还未成功 删除当前记录
   */
  private synchronized void uploadLocalSignRecordNet() {
    Slog.d(TAG, "start clearSignRecord!");
    is_processing_off_line_data = false;
    if (!isWifiConnected) {
      Slog.d(TAG, "clearSignRecord abort: network disconnect!");
      is_processing_off_line_data = false;
      return;
    }
    //查询当天的记录
    List<LocalSignRecord> signRecordList = KJDB.getDefaultInstance().findAll(LocalSignRecord.class, "swipe_time asc");
    if (signRecordList == null || signRecordList.isEmpty()) {
      Slog.d(TAG, "clearSignRecord abort: no signRecord!");
      if (is_processing_off_line_data) {
        // 处理完离线数据，为保证数据正确，需要重新同步最新的签到规则、签到数据
        Slog.d(TAG, "clearSignRecord finish, start getSignRules");
        addRxSubscription(iModel.getStudentSignRecord(viewDelegate));
      }
      is_processing_off_line_data = false;
      return;
    }

    // 一次只操作一个
    LocalSignRecord signRecord = signRecordList.get(0);
    if (lastUploadLocalSignRecordSize == signRecordList.size()) {
      uploadLocalSignRecordCount++;
      if (uploadLocalSignRecordCount == REPEAT_COUNT) {
        Slog.d(TAG, "clearSignRecord abort: try three");
        uploadLocalSignRecordCount = 0;
        KJDB.getDefaultInstance().delete(signRecord);
        uploadLocalSignRecordNet();
        return;
      }
    }
    lastUploadLocalSignRecordSize = signRecordList.size();
    if (null != signRecord) {
      is_processing_off_line_data = true;
      Slog.d(TAG, "success start clearSignRecord [signRecord]:" + JSON.toJSONString(signRecord));
      Subscription subscription;
      SwipeRequest swipeRequest;
      if (signRecord.swipe_status == 0) {
        swipeRequest = new SwipeRequest(signRecord.card_no, null, signRecord.swipe_time, 0);
        subscription = iModel.swipeSign(swipeRequest, rsp -> offlineSwipeBack(rsp, signRecord));
      } else {
        swipeRequest = new SwipeRequest(null, signRecord.phone_union_id, signRecord.swipe_time, 0);
        subscription = iModel.faceSign(swipeRequest, rsp -> offlineSwipeBack(rsp, signRecord));
      }
      addRxSubscription(subscription);
    }
  }

  /**
   * 刷卡、刷脸、扫码响应之后的处理
   */
  public void offlineSwipeBack(SwipeResponseS rsp, LocalSignRecord signRecord) {
    if (rsp == null || rsp.data == null) {
      is_processing_off_line_data = false;
      return;
    }
    switch (rsp.error_code) {
      case SWIPE_INVALID_CARD:
      case SWIPE_OUT_OF_DATE:
      case SWIPE_INVALID_USER:
      case SWIPE_SIGN_IN_SUCCESS:
      case SWIPE_SIGN_OUT_SUCCESS:
      case SWIPE_NO_USER:
      case SWIPE_SIGN_IN_TIME_FAIL:
        deleteSignRecord(signRecord);
        break;
      default:
        break;
    }
    // 处理完一个离线记录之后，接着处理下一个
    uploadLocalSignRecordNet();
  }

  /**
   * 根据签到时间删除记录
   */
  private void deleteSignRecord(LocalSignRecord signRecord) {
    KJDB.getDefaultInstance().delete(signRecord);
  }

  /**
   * 离线数据处理 每次请求3次机会 重复3次之后还未成功 删除当前记录
   */
  private synchronized void uploadLocalTakePhotoNet() {
    if (!isWifiConnected) {
      Slog.d(TAG, "clearSignRecord abort: network disconnect!");
      return;
    }
    Slog.d(TAG, "try uploadLocalTakePhoto");
    List<TakePhotoRecord> takePhotoRecords = KJDB.getDefaultInstance().findAllByWhere(TakePhotoRecord.class, "1==1");
    if (null != takePhotoRecords && takePhotoRecords.size() > 0) {
      TakePhotoRecord takePhotoRecord = takePhotoRecords.get(0);
      if (lastTakePhotoRecordSize == takePhotoRecords.size()) {
        takePhotoRecordCount++;
        if (takePhotoRecordCount == REPEAT_COUNT) {
          takePhotoRecordCount = 0;
          deleteLocalPhoto(takePhotoRecord);
          uploadLocalTakePhotoNet();
          return;
        }
      }
      lastTakePhotoRecordSize = takePhotoRecords.size();
      if (null != takePhotoRecord) {
        if (!TextUtils.isEmpty(takePhotoRecord.networkImagePath)) {
          //上传后台记录 存在图片网络地址
          Slog.d(TAG, "uploadLocalTakePhoto networkImagePath:" + takePhotoRecord.networkImagePath);
          uploadFailPhoto(takePhotoRecord, baseResponse -> {
            if (ErrorCode.isSuccess(baseResponse.error_code)) {
              //这里不管失败成功都删除本地记录 以免陷入死循环
              deleteLocalPhoto(takePhotoRecord);
            }
            uploadLocalTakePhotoNet();
          });
        } else {
          UploadRepository.upload(MainActivity.this, takePhotoRecord.localImagePath,
                  AppConfig.DEV_APP_TYPE, takePhotoRecord.fileName, new UploadRepository.ApiCallback() {
                    @Override
                    public void onResult(FileUploadResponse response) {
                      if (ErrorCode.isSuccess(response.error_code)) {
                        takePhotoRecord.networkImagePath = response.extra;
                        //上传后台记录 成功不保存记录 失败保存记录
                        Slog.d(TAG, "uploadLocalTakePhoto networkImagePath:" + takePhotoRecord.networkImagePath);
                        uploadFailPhoto(takePhotoRecord, baseResponse -> {
                          if (ErrorCode.isSuccess(baseResponse.error_code)) {
                            //这里不管失败成功都删除本地记录 以免陷入死循环
                            deleteLocalPhoto(takePhotoRecord);
                          }
                          uploadLocalTakePhotoNet();
                        });
                      } else {
                        uploadLocalTakePhotoNet();
                      }
                    }

                    @Override
                    public void onResult(String s) {

                    }
                  });
        }
      }
    }
  }

  /**
   * 为了处理多个扫码数据过来 方法被调用多次问题
   */
  private synchronized void uploadLocalTakePhoto(List<TakePhotoRecord> takePhotoRecordList) {
    Slog.d(TAG, "try uploadLocalTakePhoto");
    if (null != takePhotoRecordList && takePhotoRecordList.size() > 0) {
      for (TakePhotoRecord takePhotoRecord : takePhotoRecordList) {
        if (null != takePhotoRecord) {
          if (!TextUtils.isEmpty(takePhotoRecord.networkImagePath)) {
            //上传后台记录 存在图片网络地址
            Slog.d(TAG, "uploadLocalTakePhoto networkImagePath:" + takePhotoRecord.networkImagePath);
            uploadFailPhoto(takePhotoRecord, baseResponse -> {
              if (ErrorCode.isSuccess(baseResponse.error_code)) {
                //这里不管失败成功都删除本地记录 以免陷入死循环
                deleteLocalPhoto(takePhotoRecord);
              }
            });
          } else {
            UploadRepository.upload(MainActivity.this, takePhotoRecord.localImagePath,
                    AppConfig.DEV_APP_TYPE, takePhotoRecord.fileName, new UploadRepository.ApiCallback() {
                      @Override
                      public void onResult(FileUploadResponse response) {
                        if (ErrorCode.isSuccess(response.error_code)) {
                          takePhotoRecord.networkImagePath = response.extra;
                          //上传后台记录 成功不保存记录 失败保存记录
                          Slog.d(TAG, "uploadLocalTakePhoto networkImagePath:" + takePhotoRecord.networkImagePath);
                          uploadFailPhoto(takePhotoRecord, baseResponse -> {
                            if (ErrorCode.isSuccess(baseResponse.error_code)) {
                              //这里不管失败成功都删除本地记录 以免陷入死循环
                              deleteLocalPhoto(takePhotoRecord);
                            }
                          });
                        }
                      }

                      @Override
                      public void onResult(String s) {

                      }
                    });
          }
        }
      }
    }
  }

  /**
   * 上传记录到后台
   *
   * @param takePhotoRecord
   */
  private synchronized void uploadFailPhoto(TakePhotoRecord takePhotoRecord, Action1<BaseResponse> action1) {
    TakePhotoRequest request = new TakePhotoRequest();
    request.card_no = takePhotoRecord.card_no;
    request.union_id = takePhotoRecord.union_id;
    request.fail_date = takePhotoRecord.swipe_time;
    request.fail_photo = takePhotoRecord.networkImagePath;
    request.swipe_status = takePhotoRecord.swipe_status;
    Subscription subscription = iModel.uploadFailPhoto(request, action1);
    addRxSubscription(subscription);
  }

  /**
   * 删除本地记录、本地图片
   *
   * @param takePhotoRecord
   */
  private synchronized void deleteLocalPhoto(TakePhotoRecord takePhotoRecord) {
    try {
      KJDB.getDefaultInstance().delete(takePhotoRecord);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 结束抓拍
   */
  public void onStopCaptureImage() {
    if (null != cameraKitView && !AppConfig.isFace(MainActivity.this)) {
      cameraKitView.stop();
    }
  }

  public void onStartCaptureImage() {
    if (null != cameraKitView && !AppConfig.isFace(MainActivity.this)) {
      cameraKitView.start();
    }
  }


  /**
   * 获取网络状态
   */
  public boolean isNetConnected() {
    return isWifiConnected;
  }

  /**
   * 加载首页轮播图
   */
  private void syncBroadcast() {
    addRxSubscription(iModel.loadBroadcast(1, rsp -> viewDelegate.updateBroadcastView(rsp)));
    Subscription syncBroadcast = Observable.timer(TIME_SYNC_BROADCAST_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncBroadcast());
    addRxSubscription(syncBroadcast);
  }

  /**
   * 加载首页轮播图
   */
  private void syncAdsBroadcast() {
    addRxSubscription(iModel.loadBroadcast(2, rsp -> refreshAdData(rsp)));
    Subscription syncAdsBroadcast = Observable.timer(TIME_SYNC_BROADCAST_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncAdsBroadcast());
    addRxSubscription(syncAdsBroadcast);
  }

  private void refreshAdData(List<BroadcastData> rsp) {
    if (mBroadcastViewAdsPre != null) {
      mBroadcastViewAdsPre.refreshDataPreLoad(rsp);
    }
  }

  /**
   * 同步通知通告
   */
  private void syncAnnounced() {
    addRxSubscription(iModel.loadAnnounced(viewDelegate));
    Subscription syncAnnounced = Observable.timer(TIME_SYNC_ANNOUNCE_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncAnnounced());
    addRxSubscription(syncAnnounced);
  }

  /**
   * 二维码
   */
  private void syncQrCode() {
    addRxSubscription(iModel.getQrCodeUrl(viewDelegate));
    Subscription syncQrCode = Observable.timer(TIME_SYNC_QR_CODE_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncQrCode());
    addRxSubscription(syncQrCode);
  }

  /**
   * 同步首页信息
   */
  private void syncHomeInfo() {
    addRxSubscription(iModel.loadHomeInfo(viewDelegate, MainActivity.this));
    Subscription subscription = Observable.timer(TIME_SYNC_HOME_INFO_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncHomeInfo());
    addRxSubscription(subscription);
  }

  /**
   * 学生信息
   */
  private void syncStudentInfo() {
    addRxSubscription(iModel.loadStudentInfo(viewDelegate, this, rsp -> {
      if (ErrorCode.isSuccess(rsp.error_code)) {
        //保存上一次请求成功的时间
        SPUtils.getInstance(this).put(ConstantsYJ.SpTag.STUDENT_INFO_UPDATE_TIME, System.currentTimeMillis() / 1000 + "");
        if (null != rsp.students && rsp.students.size() > 0) {
          List<StudentData> studentDataList = rsp.students;
          List<Student> studentList = new ArrayList<>();
          for (StudentData studentData : studentDataList) {
            //说明是保存数据 不是删除
            studentList.addAll(getAllStudentList(studentData));
          }
          KJDB.getDefaultInstance().save(studentList);
          startDownloadStudentPic(studentList, studentDataList);
        }
      }
    }));
    Subscription subscription = Observable.timer(TIME_SYNC_STUDENT_INFO_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncStudentInfo());
    addRxSubscription(subscription);
  }

  /**
   * 获取出厂设置 直接删除所有图片、特征值、学生信息
   */
  private void resetData() {
    resetFile();
    addRxSubscription(iModel.syncStudentInfo(viewDelegate, rsp -> {
      if (ErrorCode.isSuccess(rsp.error_code)) {
        //保存上一次请求成功的时间
        SPUtils.getInstance(this).put(ConstantsYJ.SpTag.STUDENT_INFO_UPDATE_TIME, System.currentTimeMillis() / 1000 + "");
        if (null != rsp.students && rsp.students.size() > 0) {
          List<StudentData> studentDataList = rsp.students;
          List<Student> studentList = new ArrayList<>();
          for (StudentData studentData : studentDataList) {
            studentList.addAll(getAllStudentList(studentData));
          }
          KJDB.getDefaultInstance().save(studentList);
          //下载图片
          startDownloadStudentPic(studentList, studentDataList);
        }
      }
    }));

    addRxSubscription(iModel.syncFeaturesFromService(viewDelegate, MainActivity.this, "", saveReadySyncUsers -> runOnUiThread(() -> {
      startDownloadFacePic(saveReadySyncUsers);
    })));

    addRxSubscription(iModel.getStudentSignRecord(viewDelegate));
  }

  /**
   * 获取所有学生 一个学生可能有多张卡 这里进行数据的拆分
   *
   * @param studentData
   * @return
   */
  private List<Student> getAllStudentList(StudentData studentData) {
    List<Student> studentList = new ArrayList<>();
    if (TextUtils.isEmpty(studentData.stu_delete_at)) {
      //这里需要根据状态判断 删除还是保存 多张卡分多条记录保存
      List<StudentData.Card> cards = studentData.cards;
      if (null != cards && cards.size() > 0) {
        for (StudentData.Card card : cards) {
          //先删除本地数据
          KJDB.getDefaultInstance().deleteByWhere(Student.class, "card_no='" + card.card_no + "'");
          Student student = new Student();
          student.union_id = studentData.union_id;
          student.name = studentData.name;
          student.pic_url = studentData.pic_url;
          student.status = studentData.status;
          student.class_name = studentData.class_name;
          student.parent_name = card.parent_name;
          student.parent_url = card.parent_url;
          student.card_no = card.card_no;
          student.kyxq = card.kyxq;
          if (TextUtils.isEmpty(card.deleted_at)) {
            //保存当前数据
            studentList.add(student);
          }
        }
      } else {
        Student student = new Student();
        student.union_id = studentData.union_id;
        student.name = studentData.name;
        student.pic_url = studentData.pic_url;
        student.status = studentData.status;
        student.class_name = studentData.class_name;
        //保存当前数据
        studentList.add(student);
      }
    } else {
      //先删除本地数据
      KJDB.getDefaultInstance().deleteByWhere(Student.class, "union_id='" + studentData.union_id + "'");
    }


    return studentList;
  }

  /**
   * 同步音量
   */
  private void syncAdjustVolume() {
    iModel.adjustVolume(MainActivity.this);
    Subscription subscription = Observable.timer(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncAdjustVolume());
    addRxSubscription(subscription);
  }

  /**
   * 3天天气
   */
  private void sync3DaysWeather(String location) {
    get3DaysWeather(location);
    Subscription sync3DaysWeather = Observable.timer(TIME_SYNC_WEATHER_INFO_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.sync3DaysWeather(location));
    addRxSubscription(sync3DaysWeather);
  }

  private void syncDefaultData() {
    addRxSubscription(iModel.getStudentSignRecord(viewDelegate));
    // 界面风格
    addRxSubscription(iModel.style(viewDelegate, this));
    // Camera等硬件信息
    addRxSubscription(iModel.getDeviceInfo(viewDelegate, this, deviceInfoRsp -> {
      if (AppConfig.isNfc(MainActivity.this)) {
        enableNFC();
      }
    }));
    addRxSubscription(iModel.foods(viewDelegate));
    Subscription subscription = Observable.timer(TIME_SYNC_FEATURES_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncDefaultData());
    addRxSubscription(subscription);
  }

  /**
   * 获取课表
   */
  private void syncFeatures() {
    addRxSubscription(iModel.getFeaturesFromService(viewDelegate, MainActivity.this, "", saveReadySyncUsers -> runOnUiThread(() -> startDownloadFacePic(saveReadySyncUsers))));
    Subscription subscription = Observable.timer(TIME_SYNC_FEATURES_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> MainActivity.this.syncFeatures());
    addRxSubscription(subscription);
  }

  /**
   * 处理3天的天气数据
   */
  private void get3DaysWeather(String location) {
    if (TextUtils.isEmpty(location)) {
      return;
    }
    if (location.contains("市")) {
      location.replace("市", "");
    }
    addRxSubscription(iModel.get3DaysWeather(location, viewDelegate, weatherResponse -> {
      if (weatherResponse.error_code == 1000) {
        SPUtils.getInstance(this).put(ConstantsYJ.SpTag.WEATHER_INFO, JSON.toJSONString(weatherResponse.extra));
      }
      String weatherData = SPUtils.getInstance(this).get(ConstantsYJ.SpTag.WEATHER_INFO);
      if (!TextUtils.isEmpty(weatherData)) {
        Weather weather = JSON.parseObject(weatherData, Weather.class);
        //更新天气
        viewDelegate.updateWeather(MainActivity.this, weather);
        viewDelegate.initDateShow();
        //点击弹出天气popwindow
        viewDelegate.showWeatherPopwindow(MainActivity.this, weather);
      }
    }));
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    Utils.fullScreen(getWindow());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (null != singleThreadExecutorRecord) {
      singleThreadExecutorRecord.shutdown();
    }
    if (null != singleThreadExecutor) {
      singleThreadExecutor.shutdown();
    }
    if (null != mWifiReceiver) {
      unregisterReceiver(mWifiReceiver);
    }
    if (null != conn) {
      unbindService(conn);
    }
    if (null != binder) {
      binder.endLoop();
    }
    if (null != serviceIntent) {
      stopService(serviceIntent);
    }
    WebClient.closeWebSocket();
    AiwinnManager.get().release();
    BaiDuTtsUtil.release();
    FileDownloader.getImpl().pauseAll();
    AppUtils.exitApp();
  }

  @Override
  protected Class<MainDelegate> getDelegateClass() {
    return MainDelegate.class;
  }

  @Override
  protected Class<MainModel> getModelClass() {
    return MainModel.class;
  }

  /*-----------------------------------------NFC串口开始------------------------------------------------*/

  private Intent serviceIntent;
  private NfcService.MyBinder binder;

  private ServiceConnection conn = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      binder = (NfcService.MyBinder) service;
      Log.i(TAG, "onServiceConnected: binder" + binder);
      //打开串口，进行读卡
      if (binder != null)
        binder.startLoop(handler);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      binder = null;
    }
  };

  Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      Log.i(TAG, "读到NFC数据了");
      switch (msg.what) {
        case 0:
          //数据回调
          String data = (String) msg.obj;
          if (data != null && data.length() > 0) {
            Slog.d(TAG, "NFC数据 = " + data);
            String sixteenStr = data.substring(4, 12);
            Slog.d(TAG, "NFC数据 16进制= " + sixteenStr);
            String tenStr = new BigInteger(sixteenStr, 16).toString();
            if (!TextUtils.isEmpty(tenStr)) {
              try {
                String cardNo = String.format("%010d", Long.parseLong(tenStr));
                Slog.d(TAG, "NFC数据 10进制= " + cardNo);
                swipeCardSign(cardNo);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
          break;
        case 1:
          break;
      }
    }
  };

  private void startNfcService() {
    serviceIntent = new Intent(MainActivity.this, NfcService.class);
    startService(serviceIntent);
  }

  /*-----------------------------------------NFC串口结束------------------------------------------------*/


  /*-----------------------------------------下载操作开始------------------------------------------------*/

  private int totalCounts = 0;
  private int finalCounts = 0;

  private FileDownloadListener createListener() {
    return new FileDownloadListener() {

      @Override
      protected boolean isInvalid() {
        return isFinishing();
      }

      @Override
      protected void pending(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
      }

      @Override
      protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
      }

      @Override
      protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
      }

      @Override
      protected void blockComplete(final BaseDownloadTask task) {
      }

      @Override
      protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
        super.retry(task, ex, retryingTimes, soFarBytes);
      }

      @Override
      protected void completed(BaseDownloadTask task) {
        Log.e("filePath", task.getFilename());
        finalCounts++;
        updateDisplay();
        updateLocalPath(task);
      }

      @Override
      protected void paused(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
        finalCounts++;
        updateDisplay();
        insertUserBean(task);
      }

      @Override
      protected void error(BaseDownloadTask task, Throwable e) {
        finalCounts++;
        updateDisplay();
        insertUserBean(task);
      }

      @Override
      protected void warn(BaseDownloadTask task) {
        finalCounts++;
        updateDisplay();
        insertUserBean(task);
      }
    };
  }

  /**
   * 同步提示信息
   */
  private void updateDisplay() {
    runOnUiThread(() -> showDialog("正在同步网络照片：{" + finalCounts + "/" + totalCounts + "}"));
    if (finalCounts == totalCounts) {
      dissmisDialog();
    }
  }

  /**
   * 更新本地图片记录
   *
   * @param task
   */
  private void updateLocalPath(BaseDownloadTask task) {
    if (TextUtils.isEmpty(task.getPath())) {
      return;
    }
    if (task.getPath().contains(ConstantsYJ.ParamsTag.PARENT)) {
      try {
        //家长照片
        String strWhere = "parent_url='" + task.getUrl() + "'";
        List<Student> subParentList = KJDB.getDefaultInstance().findAllByWhere(Student.class, strWhere);
        for (Student subParent : subParentList) {
          subParent.parent_url_local_path = task.getPath();
          KJDB.getDefaultInstance().update(subParent);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (task.getPath().contains(ConstantsYJ.ParamsTag.STUDENT)) {
      try {
        //学生照片
        String strWhere = "pic_url='" + task.getUrl() + "'";
        List<Student> subStudentList = KJDB.getDefaultInstance().findAllByWhere(Student.class, strWhere);
        for (Student subStudent : subStudentList) {
          subStudent.pic_url_local_path = task.getPath();
          KJDB.getDefaultInstance().update(subStudent);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (task.getPath().contains(ConstantsYJ.ParamsTag.FACE)) {
      try {
        //人脸照片
        UserBean userBean = (UserBean) task.getTag();
        if (null != userBean) {
          userBean.localImagePath = task.getPath();
        }
        FaceDetectManager.insert(userBean, AttConstants.EXDB);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 不管下载失败成功都做插入操作
   *
   * @param task
   */
  private void insertUserBean(BaseDownloadTask task) {
    if (task.getPath().contains(ConstantsYJ.ParamsTag.FACE)) {
      try {
        //人脸照片
        UserBean userBean = (UserBean) task.getTag();
        FaceDetectManager.insert(userBean, AttConstants.EXDB);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 开始下载 可执行手动更新本地照片
   * 1.学生的照片命名为学号+student 后期也可以手动更新
   * 2.家长的照片命名为学号+parent 后期也可以手动更新
   *
   * @param studentList
   */
  private void startDownloadStudentPic(List<Student> studentList, List<StudentData> studentDataList) {
    //循环下载图片这个在线程中执行不影响界面显示 后台操作
    // 以相同的listener作为target，将不同的下载任务绑定起来
    final FileDownloadListener parallelTarget = createListener();
    final List<BaseDownloadTask> taskList = new ArrayList<>();
    for (Student student : studentList) {
      if (!TextUtils.isEmpty(student.parent_url)) {
        // 下载图片
        taskList.add(FileDownloader.getImpl().create(student.parent_url)
                .setPath(DownloadConfig.DOWNLOAD_PATH + student.union_id + "_" + student.card_no + ConstantsYJ.ParamsTag.PARENT + getImageSuffix(student.parent_url)));
      }
      if (!TextUtils.isEmpty(student.pic_url)) {
        // 下载图片
        taskList.add(FileDownloader.getImpl().create(student.pic_url)
                .setPath(DownloadConfig.DOWNLOAD_PATH + student.union_id + ConstantsYJ.ParamsTag.STUDENT + getImageSuffix(student.pic_url)));
      }
    }
    totalCounts += taskList.size();

    runOnUiThread(() -> {
      new FileDownloadQueueSet(parallelTarget)
              .setForceReDownload(true)
              .setAutoRetryTimes(1)
              .disableCallbackProgressTimes()
              .downloadTogether(taskList)
              .start();
      ToastUtils.showToast(MainActivity.this, getString(R.string.success_sync_students, studentDataList.size()));
    });

  }


  /**
   * 下载人脸照片
   *
   * @param saveReadySyncUsers
   */
  private void startDownloadFacePic(List<UserBean> saveReadySyncUsers) {
    if (null != saveReadySyncUsers && saveReadySyncUsers.size() > 0) {
      //循环下载图片这个在线程中执行不影响界面显示 后台操作
      // 以相同的listener作为target，将不同的下载任务绑定起来
      final FileDownloadListener parallelTarget = createListener();
      final List<BaseDownloadTask> taskList = new ArrayList<>();
      for (UserBean userBean : saveReadySyncUsers) {
        if (!TextUtils.isEmpty(userBean.urlImagePath)) {
          // 下载图片
          taskList.add(FileDownloader.getImpl().create(userBean.urlImagePath)
                  .setPath(DownloadConfig.DOWNLOAD_PATH + userBean.userId + ConstantsYJ.ParamsTag.FACE + getImageSuffix(userBean.urlImagePath))
                  .setTag(userBean));
        }
      }
      totalCounts += taskList.size();
      runOnUiThread(() -> {
        new FileDownloadQueueSet(parallelTarget)
                .setForceReDownload(true)
                .setAutoRetryTimes(3)
                .disableCallbackProgressTimes()
                .downloadTogether(taskList)
                .start();
        ToastUtils.showToast(MainActivity.this, MainActivity.this.getString(R.string.success_sync_features, saveReadySyncUsers.size()));
      });
    }
  }

  private OnProgressDialog dialog;

  public void showDialog(String var1) {
    if (this.dialog == null) {
      this.dialog = new OnProgressDialog(this);
      this.dialog.setCanceledOnTouchOutside(false);
      this.dialog.setOnKeyListener(new OnDialogKeyListener() {
        @Override
        public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
          MainActivity.this.onKeyDown(keyCode, event);
        }

        @Override
        public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
          MainActivity.this.onKeyUp(keyCode, event);
        }
      });
      this.dialog.setCancelable(true);
    }

    this.dialog.setMessage(var1);
    if (!this.dialog.isShowing()) {
      this.dialog.show();
    }
  }

  public void dissmisDialog() {
    if (this.dialog != null) {
      this.dialog.dismiss();
    }

  }

  public String getImageSuffix(String url) {
    if (url.endsWith(".jpeg")) {
      return ".jpeg";
    } else if (url.endsWith(".png")) {
      return ".png";
    } else if (url.endsWith(".jpg")) {
      return ".jpg";
    } else if (url.endsWith(".gif")) {
      return ".gif";
    }
    return ".jpg";
  }

  /*-----------------------------------------下载操作结束------------------------------------------------*/

  @Override
  protected NFCSwitch NFCSwitch() {
    return getNfcMode(AppConfig.NFC_mode);
  }

  NFCSwitch getNfcMode(int mode) {
    Slog.d(TAG, "getNfcMode [mode]:" + mode);
    if (!AppConfig.isNfc(this)) {
      // 不提供刷卡业务
      Slog.d(TAG, "getNfcMode abort: nonsupport nfc!");
      return NFCSwitch.DEFAULT;
    }
    switch (mode) {
      case 2:
        return NFCSwitch.STANDARD;
      case 3:
        return NFCSwitch.ADDED;
      default:
        return NFCSwitch.DEFAULT;
    }
  }

  @Override
  protected void readAddedNfcId(String nfcId) {
    super.readAddedNfcId(nfcId);
    if (TextUtils.isEmpty(nfcId) || nfcId.length() != 10) {
      return;
    }
    swipeCardSign(nfcId);
  }

  @Override
  protected void readStandardNfcId(String nfcId) {
    super.readStandardNfcId(nfcId);
    if (TextUtils.isEmpty(nfcId) || nfcId.length() != 10) {
      return;
    }
    swipeCardSign(nfcId);
  }

  /**
   * 刷卡 同步执行 最好不要出现全局变量 会存在覆盖的情况
   *
   * @param cardNo
   */
  public void swipeCardSign(String cardNo) {
    if (TextUtils.isEmpty(cardNo) || !checkSign()) {
      return;
    }
    cardSignBack(isWifiConnected, System.currentTimeMillis() / 1000, cardNo, ConstantsYJ.ParamsTag.SWIPE_CARD_STATUS);
  }

  /**
   * 只做接口请求 不做回调处理
   *
   * @param cardNo
   * @param mCurrentTime
   */
  public void swipeCardSignRequest(boolean isWifiConnected, String cardNo, long mCurrentTime, Action1<SwipeResponseS> action1) {
    //里面不请求接口
    if (!isWifiConnected) {
      return;
    }
    SwipeRequest swipeRequest = new SwipeRequest(cardNo, null, mCurrentTime, 0);
    Subscription subscription = iModel.swipeSign(swipeRequest, action1);
    addRxSubscription(subscription);
  }

  public boolean checkSign() {
    if (is_processing_off_line_data) {
      // 正在处理离线数据，请稍后重试
      Slog.d(TAG, "performScanSuccess abort: isProcessingOfflineData!");
      viewDelegate.setUserInfoTextOnLine(getString(R.string.processing_offline_data), Color.RED, null);
      Utils.speak(getString(R.string.processing_offline_data), MainActivity.this);
      return false;
    }
    return true;
  }

  public void cardSignBack(boolean isWifiConnected, long mCurrentTime, String mCurrentCardNo, int swipeStatus) {
    //一张卡对应一个学生
    List<Student> students = KJDB.getDefaultInstance().findAllByWhere(Student.class, "card_no='" + mCurrentCardNo + "' and status=0");
    Student student = (null != students && students.size() > 0) ? students.get(0) : null;
    String username = null != student ? student.name : "";
    switch (saveCardYetCount(mCurrentTime, students)) {
      case SWIPE_SIGN_IN_SUCCESS:
        // 正常签到
        viewDelegate.showParentStudentInfo(student);
        viewDelegate.setUserInfoTextOnLine(username + success_sign_in, Color.WHITE, students);
        Utils.speak(username + success_sign_in, MainActivity.this);

        swipeCardSignRequest(isWifiConnected, mCurrentCardNo, mCurrentTime, rsp -> {
          if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
            //接口返回失败 保存本地记录
            saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, "", false);
          }
        });
        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_IN, isWifiConnected, true, true, mCurrentTime, swipeStatus, "", mCurrentCardNo);
        break;
      case SWIPE_SIGN_OUT_SUCCESS:
        // 正常签退
        viewDelegate.showParentStudentInfo(student);
        viewDelegate.setUserInfoTextOnLine(username + success_sign_out, Color.WHITE, students);
        Utils.speak(username + success_sign_out, MainActivity.this);

        swipeCardSignRequest(isWifiConnected, mCurrentCardNo, mCurrentTime, rsp -> {
          if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
            //接口返回失败 保存本地记录
            saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, "", false);
          }
        });
        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_OUT, isWifiConnected, false, true, mCurrentTime, swipeStatus, "", mCurrentCardNo);
        break;
      case SWIPE_SIGN_OUT_FIRST_SUCCESS:
        // 正常签退 第一次签退
        viewDelegate.showParentStudentInfo(student);
        viewDelegate.setUserInfoTextOnLine(username + success_sign_out, Color.WHITE, students);
        Utils.speak(username + success_sign_out, MainActivity.this);

        swipeCardSignRequest(isWifiConnected, mCurrentCardNo, mCurrentTime, rsp -> {
          if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
            //接口返回失败 保存本地记录
            saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, "", false);
          }
        });
        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_OUT, isWifiConnected, true, true, mCurrentTime, swipeStatus, "", mCurrentCardNo);
        break;
      case SWIPE_SIGN_IN_TIME_FAIL:
        // 签到时间不足10分钟
        viewDelegate.setUserInfoTextOnLine(fail_sign_in_time, Color.RED, students);
        Utils.speak(fail_sign_in_time, MainActivity.this);
        break;
      case SWIPE_INVALID_CARD:
        //保存到本地统一上传
        if (null == students || students.size() == 0) {
          //这里保证一定要有一个学生、假学生 刷卡异常使用
          Student student1 = new Student();
          student1.card_no = mCurrentCardNo;
          students.add(student1);
        }
        // 卡片无效
        viewDelegate.setUserInfoTextOnLine(fail_card_invalid, Color.RED, students);
        Utils.speak(fail_card_invalid, MainActivity.this);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, "", mCurrentCardNo);
        break;
      case SWIPE_OUT_OF_DATE:
        // 卡片已过期
        viewDelegate.setUserInfoTextOnLine(fail_card_invalid, Color.RED, students);
        Utils.speak(fail_card_invalid, MainActivity.this);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, "", mCurrentCardNo);
        break;
      default:
        Utils.speak(fail_sign_in, MainActivity.this);
        viewDelegate.setUserInfoTextOnLine(fail_sign_in, Color.RED, students);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, "", mCurrentCardNo);
    }
  }

  /**
   * 处理记录 拍照逻辑 目前给5秒休眠处理
   *
   * @param students                  学生列表
   * @param signStatus                签到状态
   * @param isWifiConnected           wifi是否连接
   * @param isCaptureImage            是否拍照
   * @param isUpdateStudentSignRecord 是否更新签到记录
   * @param mCurrentTime              当前时间
   * @param swipeStatus               签到类型
   * @param mCurrentPhone             手机号
   * @param mCurrentCardNo            卡号
   */
  public void captureSignRecord(List<Student> students, String signStatus, boolean isWifiConnected, boolean isCaptureImage,
                                boolean isUpdateStudentSignRecord, long mCurrentTime, int swipeStatus, String mCurrentPhone, String mCurrentCardNo) {

    //拍照 抓取图片的逻辑同步进行
    final Bitmap[] bitmap = new Bitmap[1];
    if (isCaptureImage && AppConfig.isTakePhoto(MainActivity.this)) {
      if (AppConfig.isFace(MainActivity.this)) {
        //通过人脸识别获取照片
        bitmap[0] = viewDelegate.getFaceBitmap();
      } else {
        if (null != cameraKitView) {
          cameraKitView.captureImage(cameraKitImage -> bitmap[0] = BitmapFactory.decodeByteArray(cameraKitImage.getJpeg(),
                  0, cameraKitImage.getJpeg().length));
        }
      }
    }
    singleThreadExecutorRecord.execute(() -> {
      //处理上传图片 本地记录更新操作 UI显示与数据处理分离
      if (isCaptureImage && AppConfig.isTakePhoto(MainActivity.this)) {
        takePhoto(bitmap[0], students, mCurrentTime, mCurrentCardNo, swipeStatus);
      }
      if (isUpdateStudentSignRecord) {
        updateStudentSignRecord(mCurrentTime + "", students, swipeStatus, mCurrentPhone, signStatus);
        //扫码 刷卡需要保存本地记录
        if (swipeStatus == ConstantsYJ.ParamsTag.SWIPE_CARD_STATUS || swipeStatus == ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS) {
          saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, mCurrentPhone, isWifiConnected);
        }
      }
      try {
        Thread.sleep(5);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * 获取签到状态 及状态验证
   *
   * @param currentTime
   * @param students
   * @return
   */
  public int saveCardYetCount(long currentTime, List<Student> students) {
    Student student = (null != students && students.size() > 0) ? students.get(students.size() - 1) : null;
    // 刷卡
    if (null == student) {
      // 卡片无效
      return SWIPE_INVALID_CARD;
    }
    if (currentTime > Utils.toLong(student.kyxq)) {
      return SWIPE_OUT_OF_DATE;
    }

    return saveYetCount(currentTime, student);
  }

  /**
   * 刷卡刷脸共同处理部分 验证
   *
   * @param currentTime
   * @param student
   * @return
   */
  public int saveYetCount(long currentTime, Student student) {
    String signDate = DateUtils.currentDateTime("yyyy-MM-dd");
    List<StudentSignRecord> studentSignRecords = KJDB.getDefaultInstance().findAllByWhere(StudentSignRecord.class,
            "union_id='" + student.union_id + "' and sign_date='" + signDate + "'");

    //接口返回签到 签到信息都会返回 没有签到也会返回 取最后一条记录
    StudentSignRecord studentSignRecord = (null != studentSignRecords && studentSignRecords.size() > 0) ?
            studentSignRecords.get(studentSignRecords.size() - 1) : null;
    if (null == studentSignRecord) {
      return SWIPE_SIGN_IN_SUCCESS;
    } else {
      if (!TextUtils.isEmpty(studentSignRecord.in_time)) {
        if (TextUtils.isEmpty(studentSignRecord.out_time)) {
          //只有签到 没有签退 与签到比较
          if (isInSameTime(Utils.toLong(studentSignRecord.in_time), currentTime, AppConfig.getPickTimeConfig(MainActivity.this))) {
            //本地有记录 签退处理
            if (currentTime - Utils.toLong(studentSignRecord.in_time) <= AppConfig.getSwipeIntervalTime(MainActivity.this)) {
              //签到不足10分钟
              return SWIPE_SIGN_IN_TIME_FAIL;
            } else {
              return SWIPE_SIGN_OUT_FIRST_SUCCESS;
            }
          } else {
            return SWIPE_SIGN_IN_SUCCESS;
          }
        } else {
          //签到 签退都有
          if (isInSameTime(Utils.toLong(studentSignRecord.out_time), currentTime, AppConfig.getPickTimeConfig(MainActivity.this))) {
            return SWIPE_SIGN_OUT_SUCCESS;
          } else {
            return SWIPE_SIGN_IN_SUCCESS;
          }
        }
      } else {
        return SWIPE_SIGN_IN_SUCCESS;
      }
    }
  }

  /**
   * 更新本地记录 这里只有签到成功、签退成功才能进入这个方法
   *
   * @param swipeTime
   * @param students
   */
  private synchronized void updateStudentSignRecord(String swipeTime, List<Student> students, int swipeStatus, String mCurrentPhone, String signStatus) {
    for (int i = 0; i < students.size(); i++) {
      Student student = students.get(i);
      String signDate = DateUtils.currentDateTime("yyyy-MM-dd");
      if (TextUtils.isEmpty(swipeTime)) {
        return;
      }

      String unionId = TextUtils.isEmpty(student.union_id) ? "" : student.union_id;
      String cardNo = TextUtils.isEmpty(student.card_no) ? "" : student.card_no;
      saveAllLocalSignRecord(unionId, cardNo, Utils.toLong(swipeTime), swipeStatus, signDate, mCurrentPhone + "_" + unionId, student.name, signStatus);

      List<StudentSignRecord> studentSignRecords = KJDB.getDefaultInstance().findAllByWhere(StudentSignRecord.class,
              "union_id='" + student.union_id + "' and sign_date='" + signDate + "'");
      StudentSignRecord studentSignRecord = (null != studentSignRecords && studentSignRecords.size() > 0) ? studentSignRecords.get(studentSignRecords.size() - 1) : null;
      if (null == studentSignRecord) {
        studentSignRecord = new StudentSignRecord();
        studentSignRecord.union_id = TextUtils.isEmpty(student.union_id) ? "" : student.union_id;
        studentSignRecord.sign_date = signDate;
        studentSignRecord.in_time = swipeTime;
        studentSignRecord.out_time = "";
        KJDB.getDefaultInstance().save(studentSignRecord);
      } else {
        if (!TextUtils.isEmpty(studentSignRecord.in_time)) {
          if (TextUtils.isEmpty(studentSignRecord.out_time)) {
            //只有签到 没有签退 与签到比较
            if (isInSameTime(Utils.toLong(studentSignRecord.in_time), Utils.toLong(swipeTime), AppConfig.getPickTimeConfig(MainActivity.this))) {
              studentSignRecord.out_time = swipeTime;
              KJDB.getDefaultInstance().update(studentSignRecord);
            } else {
              studentSignRecord.in_time = swipeTime;
              studentSignRecord.out_time = "";
              KJDB.getDefaultInstance().save(studentSignRecord);
            }
          } else {
            //签到 签退都有
            if (isInSameTime(Utils.toLong(studentSignRecord.out_time), Utils.toLong(swipeTime), AppConfig.getPickTimeConfig(MainActivity.this))) {
              studentSignRecord.out_time = swipeTime;
              KJDB.getDefaultInstance().update(studentSignRecord);
            } else {
              studentSignRecord.in_time = swipeTime;
              studentSignRecord.out_time = "";
              KJDB.getDefaultInstance().save(studentSignRecord);
            }
          }
        } else {
          studentSignRecord.in_time = swipeTime;
          studentSignRecord.out_time = "";
          KJDB.getDefaultInstance().update(studentSignRecord);
        }
      }
    }
  }

  /**
   * 1.离线需要保存本地记录
   * 2.在线不需要保存本地记录 拍照和更新记录在同一个有序队列中处理
   *
   * @param swipeTime
   * @param students
   * @param swipeStatus
   * @param mCurrentPhone
   * @param isWifiConnected
   */
  private void saveStudentLocalSignRecord(String swipeTime, List<Student> students, int swipeStatus, String mCurrentPhone, boolean isWifiConnected) {
    if (isWifiConnected) {
      return;
    }
    for (int i = 0; i < students.size(); i++) {
      Student student = students.get(i);
      String signDate = DateUtils.currentDateTime("yyyy-MM-dd");
      if (TextUtils.isEmpty(swipeTime)) {
        return;
      }
      String unionId = TextUtils.isEmpty(student.union_id) ? "" : student.union_id;
      String cardNo = TextUtils.isEmpty(student.card_no) ? "" : student.card_no;
      //离线模式需要保存本地记录
      saveLocalSignRecord(unionId, cardNo, Utils.toLong(swipeTime), swipeStatus, signDate, mCurrentPhone + "_" + unionId);
    }
  }

  /**
   * 保存本地记录
   *
   * @param unionId
   * @param cardNo
   * @param swipeTime
   * @param swipeStatus
   * @param signDate
   * @param phoneUnionId
   */
  public void saveLocalSignRecord(String unionId, String cardNo, long swipeTime, int swipeStatus, String signDate, String phoneUnionId) {
    //离线模式 需要保存本地记录
    LocalSignRecord signRecord = new LocalSignRecord();
    signRecord.union_id = unionId;
    signRecord.card_no = cardNo;
    signRecord.swipe_time = swipeTime;
    signRecord.swipe_status = swipeStatus;
    signRecord.sign_date = signDate;
    signRecord.phone_union_id = phoneUnionId;
    KJDB.getDefaultInstance().save(signRecord);
  }

  /**
   * 保存本地所有记录
   *
   * @param unionId
   * @param cardNo
   * @param swipeTime
   * @param swipeStatus
   * @param signDate
   * @param phoneUnionId
   * @param name
   * @param signStatus
   */
  public void saveAllLocalSignRecord(String unionId, String cardNo, long swipeTime, int swipeStatus, String signDate, String phoneUnionId, String name, String signStatus) {
    AllLocalSignRecord allLocalSignRecord = new AllLocalSignRecord();
    allLocalSignRecord.union_id = unionId;
    allLocalSignRecord.card_no = cardNo;
    allLocalSignRecord.swipe_time = swipeTime;
    allLocalSignRecord.swipe_status = swipeStatus;
    allLocalSignRecord.sign_date = signDate;
    allLocalSignRecord.phone_union_id = phoneUnionId;
    allLocalSignRecord.name = name;
    allLocalSignRecord.sign_status = signStatus;
    KJDB.getDefaultInstance().save(allLocalSignRecord);
  }

  /*-----------------------------------------刷卡结束------------------------------------------------*/


  /*-----------------------------------------刷脸开始------------------------------------------------*/

  /**
   * 刷脸 并发执行
   *
   * @param userId
   */
  public void swipeFaceSign(String userId) {
    if (TextUtils.isEmpty(userId) || !checkSign()) {
      return;
    }
    //直接走本地学生读取
    getStudents(getCurrentPhone(userId), System.currentTimeMillis() / 1000, ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS);
  }

  /**
   * 通过特征值匹配、查询学生信息
   *
   * @return
   */
  public void getStudents(String mCurrentPhone, long mCurrentTime, int swipeStatus) {
    if (TextUtils.isEmpty(mCurrentPhone)) {
      return;
    }
    List<Student> studentList = new ArrayList<>();
    //查询所有特征值 这里由于第三方数据库不支持模糊查询 for循环代替 后期可以修改
    List<UserBean> userBeanList = FaceDetectManager.queryAll(AttConstants.EXDB);
    if (null != userBeanList && userBeanList.size() > 0) {
      for (UserBean userBean : userBeanList) {
        if (!TextUtils.isEmpty(userBean.userId) && userBean.userId.startsWith(mCurrentPhone)) {
          String unionId = getUnionId(userBean.userId);
          List<Student> students = KJDB.getDefaultInstance().findAllByWhere(Student.class, "union_id='" + unionId + "' and status=0");
          if (null != students && students.size() > 0) {
            //获取第一个就ok
            Student student = students.get(0);
            student.phone_union_id = userBean.userId;
            student.parent_url = userBean.urlImagePath;
            student.parent_url_local_path = userBean.localImagePath;
            student.parent_name = userBean.name;
            //刷脸不要卡号
            student.card_no = "";
            studentList.add(student);
          }
        }
      }
    }
//    //匹配本地特征值 查询手机号开头的特征值列表
//    List<FeatureUser> featureUsers = KJDB.getDefaultInstance().findAllByWhere(FeatureUser.class, "union_id like '%" + mCurrentPhone + "%'");
//
//    //获取学生信息
//    if (null != featureUsers && featureUsers.size() > 0) {
//      for (FeatureUser featureUser : featureUsers) {
//        String unionId = getUnionId(featureUser.union_id);
//        List<Student> students = KJDB.getDefaultInstance().findAllByWhere(Student.class, "union_id='" + unionId + "' and status=0");
//        if (null != students && students.size() > 0) {
//          //获取第一个就ok
//          Student student = students.get(0);
//          student.phone_union_id = featureUser.union_id;
//          //刷脸不要卡号
//          student.card_no = "";
//          studentList.add(student);
//        }
//      }
//    }
    if (null != studentList && studentList.size() > 0) {
      Student student = studentList.get(0);
      String pick_up_status = getPickUpStatus(mCurrentTime + "", student);
      ParentLinkStudentsResponseS responseS = new ParentLinkStudentsResponseS();
      responseS.students = studentList;
      responseS.pick_up_status = pick_up_status;
      showSelectDialog(responseS, mCurrentPhone, mCurrentTime, swipeStatus);
    }

  }

  public String getCurrentPhone(String phoneUnionId) {
    //获取学号 20061426206_2019804 本地记录全部学号处理，刷脸时
    if (!TextUtils.isEmpty(phoneUnionId) && phoneUnionId.contains("_")) {
      String[] str = phoneUnionId.split("_");
      if (str.length > 1) {
        return str[0];
      }
    }
    return "";
  }

  public String getUnionId(String phoneUnionId) {
    //获取学号 20061426206_2019804 本地记录全部学号处理，刷脸时
    if (!TextUtils.isEmpty(phoneUnionId) && phoneUnionId.contains("_")) {
      String[] str = phoneUnionId.split("_");
      if (str.length > 1) {
        return str[1];
      }
    }
    return "";
  }

  /**
   * 是否是入园 0入园 1出园
   *
   * @param swipeTime
   * @param student
   */
  private String getPickUpStatus(String swipeTime, Student student) {
    String signDate = DateUtils.currentDateTime("yyyy-MM-dd");
    List<StudentSignRecord> studentSignRecords = KJDB.getDefaultInstance().findAllByWhere(StudentSignRecord.class,
            "union_id='" + student.union_id + "' and sign_date='" + signDate + "'");
    StudentSignRecord studentSignRecord = (null != studentSignRecords && studentSignRecords.size() > 0) ? studentSignRecords.get(studentSignRecords.size() - 1) : null;
    if (null == studentSignRecord) {
      return "0";
    } else {
      if (!TextUtils.isEmpty(studentSignRecord.in_time)) {
        if (TextUtils.isEmpty(studentSignRecord.out_time)) {
          //只有签到 没有签退 与签到比较
          if (isInSameTime(Utils.toLong(studentSignRecord.in_time), Utils.toLong(swipeTime), AppConfig.getPickTimeConfig(MainActivity.this))) {
            return "1";
          } else {
            return "0";
          }
        } else {
          //签到 签退都有
          if (isInSameTime(Utils.toLong(studentSignRecord.out_time), Utils.toLong(swipeTime), AppConfig.getPickTimeConfig(MainActivity.this))) {
            return "1";
          } else {
            return "0";
          }
        }
      } else {
        return "0";
      }
    }
  }

  /**
   * 选择学生弹框提示
   *
   * @param responseS
   * @param mCurrentPhone
   * @param mCurrentTime
   * @param swipeStatus
   */
  public void showSelectDialog(ParentLinkStudentsResponseS responseS, String mCurrentPhone, long mCurrentTime, int swipeStatus) {
    List<Student> students = responseS.students;
    if (null != students && students.size() > 0) {
      if (students.size() == 1) {
        faceSignBack(students, mCurrentPhone, mCurrentTime, swipeStatus);
      } else {
        //显示弹框
        viewDelegate.showSelectStudentDialogFragment(responseS, mCurrentPhone, mCurrentTime, swipeStatus,
                (responseS1, mCurrentPhone1, mCurrentTime1, swipeStatus1)
                        -> faceSignBack(responseS1.students, mCurrentPhone1, mCurrentTime1, swipeStatus1));
      }
    }
  }

  /**
   * 批量刷脸签到 只做接口请求
   *
   * @param isWifiConnected
   * @param students
   * @param mCurrentTime
   * @param action1
   */
  public void swipeFaceSignRequest(boolean isWifiConnected, List<Student> students, long mCurrentTime, Action1<SwipeAllFaceResponseS> action1) {
    if (!isWifiConnected) {
      return;
    }
    List<SwipeRequest> requests = new ArrayList<>();
    for (Student student : students) {
      SwipeRequest swipeRequest = new SwipeRequest(null, student.phone_union_id, mCurrentTime, 0);
      requests.add(swipeRequest);
    }
    // 刷脸
    Subscription subscription = iModel.allFaceSign(requests, action1);
    addRxSubscription(subscription);
  }

  /**
   * 获取拼接学生姓名
   *
   * @param students
   * @return
   */
  public String getNameStr(List<Student> students) {
    //多个学生 字符串拼接
    StringBuilder nameSb = new StringBuilder();
    if (null != students && students.size() > 0) {
      for (int i = 0; i < students.size(); i++) {
        Student student = students.get(i);
        if (i == students.size() - 1) {
          nameSb.append(TextUtils.isEmpty(student.name) ? "" : student.name);
        } else {
          nameSb.append(TextUtils.isEmpty(student.name) ? "" : (student.name + "、"));
        }
      }
    }
    return nameSb.toString();
  }

  /**
   * 刷脸 扫码 并发执行 扫码只需要展示页面和抓拍、更新本地记录 不需要请求接口
   *
   * @param students
   */
  public void faceSignBack(List<Student> students, String mCurrentPhone, long mCurrentTime, int swipeStatus) {
    String username = getNameStr(students);
    switch (saveFaceYetCount(mCurrentTime, students)) {
      case SWIPE_SIGN_IN_SUCCESS:
        //刷脸签到成功
        if (students.size() == 1) {
          viewDelegate.showParentStudentInfo(students.get(0));
        }
        // 正常签到
        viewDelegate.setUserInfoTextOnLine(username + success_sign_in, Color.WHITE, students);
        Utils.speak(username + success_sign_in, MainActivity.this);

        if (swipeStatus == ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS) {
          swipeFaceSignRequest(isWifiConnected, students, mCurrentTime, rsp -> {
            if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
              //接口返回失败 保存本地记录
              saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, mCurrentPhone, false);
            }
          });
        }

        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_IN, isWifiConnected, true, true, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      case SWIPE_SIGN_OUT_SUCCESS:
        //刷脸签退成功
        if (students.size() == 1) {
          viewDelegate.showParentStudentInfo(students.get(0));
        }
        // 正常签退
        viewDelegate.setUserInfoTextOnLine(username + success_sign_out, Color.WHITE, students);
        Utils.speak(username + success_sign_out, MainActivity.this);

        if (swipeStatus == ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS) {
          swipeFaceSignRequest(isWifiConnected, students, mCurrentTime, rsp -> {
            if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
              //接口返回失败 保存本地记录
              saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, mCurrentPhone, false);
            }
          });
        }

        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_OUT, isWifiConnected, false, true, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      case SWIPE_SIGN_OUT_FIRST_SUCCESS:
        //刷脸签退成功
        if (students.size() == 1) {
          viewDelegate.showParentStudentInfo(students.get(0));
        }
        // 正常签退
        viewDelegate.setUserInfoTextOnLine(username + success_sign_out, Color.WHITE, students);
        Utils.speak(username + success_sign_out, MainActivity.this);

        if (swipeStatus == ConstantsYJ.ParamsTag.SWIPE_FACE_STATUS) {
          swipeFaceSignRequest(isWifiConnected, students, mCurrentTime, rsp -> {
            if (rsp == null || rsp.data == null || rsp.error_code < 1000) {
              //接口返回失败 保存本地记录
              saveStudentLocalSignRecord(mCurrentTime + "", students, swipeStatus, mCurrentPhone, false);
            }
          });
        }

        captureSignRecord(students, ConstantsYJ.ParamsTag.SIGN_OUT, isWifiConnected, true, true, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      case SWIPE_SIGN_IN_TIME_FAIL:
        // 签到时间不足10分钟
        viewDelegate.setUserInfoTextOnLine(fail_sign_in_time, Color.RED, students);
        Utils.speak(fail_sign_in_time, MainActivity.this);
        break;
      case SWIPE_INVALID_USER:
        // 无效用户
        viewDelegate.setUserInfoTextOnLine(fail_user_invalid, Color.RED, students);
        Utils.speak(fail_user_invalid, MainActivity.this);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      case SWIPE_INVALID_CARD:
        // 卡片无效
        viewDelegate.setUserInfoTextOnLine(fail_card_invalid, Color.RED, students);
        Utils.speak(fail_card_invalid, MainActivity.this);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      case SWIPE_OUT_OF_DATE:
        // 卡片已过期
        viewDelegate.setUserInfoTextOnLine(fail_card_invalid, Color.RED, students);
        Utils.speak(fail_card_invalid, MainActivity.this);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, mCurrentPhone, "");
        break;
      default:
        Utils.speak(fail_sign_in, MainActivity.this);
        viewDelegate.setUserInfoTextOnLine(fail_sign_in, Color.RED, students);

        captureSignRecord(students, "", isWifiConnected, true, false, mCurrentTime, swipeStatus, mCurrentPhone, "");
    }
  }

  public synchronized int saveFaceYetCount(long currentTime, List<Student> students) {
    //取最后一个学生
    Student student = (null != students && students.size() > 0) ? students.get(students.size() - 1) : null;
    // 刷脸
    if (null == student) {
      // 无效用户
      return SWIPE_INVALID_USER;
    }

    return saveYetCount(currentTime, student);
  }

  /**
   * 判断签到时间和上一次签到时间（签到、签退）是否在同一时间区域内
   *
   * @param recordTime
   * @param currentTime
   * @param endTime
   * @return
   */
  private boolean isInSameTime(long recordTime, long currentTime, long endTime) {
    if ((recordTime < endTime && currentTime < endTime)
            || (recordTime > endTime && currentTime > endTime)) {
      return true;
    }
    return false;
  }

  /*-----------------------------------------刷脸结束------------------------------------------------*/

  /*-----------------------------------------扫码开始------------------------------------------------*/

  @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(ConstantsYJ.RxTag.RX_TAG_SOCKET_ON_MESSAGE)})
  public void SocketCallback(String data) {
    Gson gson = new Gson();
    SwipeAllFaceResponseS rsp = gson.fromJson(data, SwipeAllFaceResponseS.class);
    if (rsp.error_code != 4001) {
      //扫码处理逻辑
      //读取接口数据
      List<Swipe> swipes = rsp.data;
      List<Student> students = new ArrayList<>();
      String swipeTime = "";
      if (null != swipes && swipes.size() > 0) {
        for (int i = 0; i < swipes.size(); i++) {
          Swipe swipe = swipes.get(i);
          Student student = new Student();
          student.name = swipe.username;
          student.union_id = swipe.union_id;
          student.parent_url = swipe.parent_url;
          student.parent_name = swipe.parent_name;
          student.pic_url = swipe.stu_url;
          students.add(student);
          swipeTime = swipe.swipe_time;
        }
      }
      faceSignBack(students, "", Utils.toLong(swipeTime), ConstantsYJ.ParamsTag.SWIPE_CODE_STATUS);
    }
  }

  /*-----------------------------------------扫码结束------------------------------------------------*/

  @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(ConstantsYJ.RxTag.RX_TAG_CONFIRM_USER)})
  public void confirmUserCallback(String userId) {
    //点击确定框 回调
    swipeFaceSign(userId);
  }

  /**
   * 同步dialog
   */
  public void showResetDialog() {
    AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setCancelable(true)
            .setMessage(getString(R.string.sync_tip))
            .setPositiveButton(getString(R.string.sure), (dialog, which) -> resetData())
            .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
            .create();
    alertDialog.show();
    Subscription subscription = Observable.timer(10, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r -> alertDialog.dismiss());
    addRxSubscription(subscription);
  }


}
