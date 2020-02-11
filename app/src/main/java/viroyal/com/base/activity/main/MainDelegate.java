package viroyal.com.base.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.SizeUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.widget.CameraInterfaceBak;
import com.aiwinn.base.widget.CameraSurfaceView;
import com.aiwinn.deblocks.utils.FeatureUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Status;
import com.baidu.tts.BaiDuTtsUtil;
import com.baidu.tts.listener.OnSpeechListener;
import com.github.demono.AutoScrollViewPager;
import com.hwangjr.rxbus.RxBus;
import com.suntiago.a.ChinaDateUtil;
import com.suntiago.a.HolidayUtil;
import com.suntiago.a.WeatherUtil;
import com.suntiago.baseui.activity.base.ActivityBase;
import com.suntiago.baseui.activity.base.AppDelegateBase;
import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.ScreenUtils;
import com.suntiago.baseui.utils.date.DateUtils;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.network.network.ErrorCode;

import org.kymjs.kjframe.KJDB;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import viroyal.com.base.AppConfig;
import viroyal.com.base.MyApp;
import viroyal.com.base.R;
import viroyal.com.base.adapter.ItemFoodPagerAdapter;
import viroyal.com.base.adapter.ItemSortAdapter;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.aiwinn.common.AttConstants;
import viroyal.com.base.face.aiwinn.face_detect.DetectPresenter;
import viroyal.com.base.face.aiwinn.face_detect.DetectView;
import viroyal.com.base.face.bean.DeviceInfo;
import viroyal.com.base.face.rsp.DeviceInfoRsp;
import viroyal.com.base.listener.OnClickEffectiveListener;
import viroyal.com.base.listener.OnSelectStudentDialogListener;
import viroyal.com.base.model.Announced;
import viroyal.com.base.model.Food;
import viroyal.com.base.model.OwnStyle;
import viroyal.com.base.model.QrCode;
import viroyal.com.base.model.Student;
import viroyal.com.base.model.Weather;
import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;
import viroyal.com.base.util.HtmlToStringUtil;
import viroyal.com.base.util.QrCodeUtil;
import viroyal.com.base.util.Utils;
import viroyal.com.base.widget.CustomCalendarPopwindow.CalendarPopwindow;
import viroyal.com.base.widget.InputDialog;
import viroyal.com.base.widget.NoticeDialogFragment;
import viroyal.com.base.widget.ParentStudentDialogFragment;
import viroyal.com.base.widget.SelectStudentDialogFragment;
import viroyal.com.base.widget.SignSearchDialogFragment;
import viroyal.com.base.widget.StudentDialogFragment;
import viroyal.com.base.widget.WeatherPopwindow;
import viroyal.com.base.widget.dialog.OnDialogKeyListener;
import viroyal.com.dev.broadcast.BroadcastData;
import viroyal.com.dev.broadcast.BroadcastView;
import viroyal.com.dev.splash.ConfigDevice;

import static viroyal.com.base.AppConfig.TIME_SYNC_FOOD_DELAY_MS;


/**
 * @author chenjunwei
 * @desc 主页面UI处理
 * @date 2019/5/28
 */
public class MainDelegate extends AppDelegateBase<MainModel> implements CameraInterfaceBak.CameraStateCallBack, DetectView {
  private final String TAG = getClass().getSimpleName();
  public static final String TAG_DEBUG_FACE = "tag_debug_face";

  private List<Announced> mNoticesList = new ArrayList<>();

  private ItemSortAdapter itemSortAdapter;
  private List<DeviceInfoRsp.Apps> mDeviceApps = new ArrayList<>();

  private boolean isSpeech = false;
  private View lastView;
  private ViewFlipper noticeViewFlipper;
  private AutoScrollViewPager viewPager;

  private int mMaxClickNum = 5;
  /**
   * 记录点击次数
   */
  private long[] mHits = new long[mMaxClickNum];
  /**
   * 记录点击次数
   */
  private long[] mSignRecordHits = new long[mMaxClickNum];
  /**
   * 记录点击次数
   */
  private long[] mSyncDataHits = new long[mMaxClickNum];


  private String mPassword = "";

  @Override
  public int getRootLayoutId() {
    return R.layout.activity_main;
  }

  @Override
  public void initWidget() {
    noticeViewFlipper = get(R.id.notice_flipper);
    viewPager = get(R.id.viewPager);

    itemSortAdapter = new ItemSortAdapter(getActivity(), mDeviceApps, this);
    ((RecyclerView) get(R.id.rv_sort_list)).setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    ((RecyclerView) get(R.id.rv_sort_list)).setAdapter(itemSortAdapter);

    int height = ScreenUtils.getScreenHeight(getActivity()) + ScreenUtils.getDaoHangHeight(getActivity());
    int faceHeight = (int) (height * 0.2) - SizeUtils.dp2px(10);

    LinearLayout.LayoutParams cameraLp = (LinearLayout.LayoutParams) get(R.id.rl_camera).getLayoutParams();
    cameraLp.width = (int) (faceHeight * 0.7337);
    cameraLp.height = (int) (faceHeight * 0.7337);
    get(R.id.rl_camera).setLayoutParams(cameraLp);

    LinearLayout.LayoutParams qrCodeLp = (LinearLayout.LayoutParams) get(R.id.iv_qr_code).getLayoutParams();
    qrCodeLp.width = (int) (faceHeight * 0.6413);
    qrCodeLp.height = (int) (faceHeight * 0.6413);
    get(R.id.iv_qr_code).setLayoutParams(qrCodeLp);

    int foodHeight = (int) (height * 0.24);
    //food
    LinearLayout ll_food_title = get(R.id.ll_food_title);
    LinearLayout.LayoutParams foodLp = (LinearLayout.LayoutParams) ll_food_title.getLayoutParams();
    foodLp.width = LinearLayout.LayoutParams.MATCH_PARENT;
    foodLp.height = (int) (foodHeight * 0.135);
    ll_food_title.setLayoutParams(foodLp);
  }

  void setListener() {
    get(R.id.iv_horn).setOnClickListener(view -> {
      if (null == noticeViewFlipper) {
        return;
      }
      View currentView = noticeViewFlipper.getCurrentView();
      if (null == currentView) {
        return;
      }
      if (isSpeech && lastView == currentView) {
        BaiDuTtsUtil.stop();
        stopSpeak();
        return;
      }
      startSpeak(currentView);
      TextView textView = currentView.findViewById(R.id.tv_notice);
      if (null == textView || TextUtils.isEmpty(textView.getText())) {
        return;
      }
      BaiDuTtsUtil.speak(textView.getText().toString(), new OnSpeechListener() {
        @Override
        public void onSpeechStart() {

        }

        @Override
        public void onSpeechFinish() {
          stopSpeak();
        }

        @Override
        public void onError() {
          stopSpeak();
        }
      });
    });
    get(R.id.iv_school_logo).setOnClickListener(v -> {
      //所有元素左移一个位置
      System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
      mHits[mHits.length - 1] = SystemClock.uptimeMillis();
      if (mHits[0] >= (SystemClock.uptimeMillis() - 3000)) {
        //重置点击次数
        mHits = new long[mMaxClickNum];
        InputDialog inputDialog = InputDialog.newInstance(new InputDialog.Listener() {
          @Override
          public void onNumberSelected(String item) {

          }

          @Override
          public void ok(String item) {
            if (TextUtils.equals(mPassword, item)) {
              Slog.i(TAG, "onClick:weather_img:password currect");
              getActivity().finish();
            } else {
              showToast(getActivity().getString(R.string.password_error));
            }
          }
        }, "", 0);
        inputDialog.setShowBottom(true);
        inputDialog.show(getActivity().getFragmentManager(), "");
      }
    });

    get(R.id.iv_qr_code).setOnClickListener(v -> {
      //所有元素左移一个位置
      System.arraycopy(mSignRecordHits, 1, mSignRecordHits, 0, mSignRecordHits.length - 1);
      mSignRecordHits[mHits.length - 1] = SystemClock.uptimeMillis();
      if (mSignRecordHits[0] >= (SystemClock.uptimeMillis() - 3000)) {
        //重置点击次数
        mSignRecordHits = new long[mMaxClickNum];
        InputDialog inputDialog = InputDialog.newInstance(new InputDialog.Listener() {
          @Override
          public void onNumberSelected(String item) {

          }

          @Override
          public void ok(String item) {
            if (TextUtils.equals(mPassword, item)) {
              Slog.i(TAG, "onClick:weather_img:password currect");
              showAllSignRecordDialogFragment();
            } else {
              showToast(getActivity().getString(R.string.password_error));
            }
          }
        }, "", 0);
        inputDialog.setShowBottom(true);
        inputDialog.show(getActivity().getFragmentManager(), "");
      }
    });

    get(R.id.ll_food_title).setOnClickListener(v -> {
      //所有元素左移一个位置
      System.arraycopy(mSyncDataHits, 1, mSyncDataHits, 0, mSyncDataHits.length - 1);
      mSyncDataHits[mSyncDataHits.length - 1] = SystemClock.uptimeMillis();
      if (mSyncDataHits[0] >= (SystemClock.uptimeMillis() - 3000)) {
        //重置点击次数
        mSyncDataHits = new long[mMaxClickNum];
        InputDialog inputDialog = InputDialog.newInstance(new InputDialog.Listener() {
          @Override
          public void onNumberSelected(String item) {

          }

          @Override
          public void ok(String item) {
            if (TextUtils.equals(mPassword, item)) {
              Slog.i(TAG, "onClick:weather_img:password currect");
              ((MainActivity) getActivity()).showResetDialog();
            } else {
              showToast(getActivity().getString(R.string.password_error));
            }
          }
        }, "", 0);
        inputDialog.setShowBottom(true);
        inputDialog.show(getActivity().getFragmentManager(), "");
      }
    });

  }

  private void startSpeak(View currentView) {
    getActivity().runOnUiThread(() -> {
      lastView = currentView;
      isSpeech = true;
      Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_horn);
      ((ImageView) get(R.id.iv_horn)).setImageDrawable(drawable);
      Animatable animatable = (Animatable) ((ImageView) get(R.id.iv_horn)).getDrawable();
      if (null != animatable) {
        animatable.start();
      }
    });
  }

  private void stopSpeak() {
    getActivity().runOnUiThread(() -> {
      ((ImageView) get(R.id.iv_horn)).setImageResource(R.mipmap.ic_horn4);
      isSpeech = false;
    });
  }

  @Override
  public void viewBindModel(MainModel data) {
  }

  void initDateShow() {
    TextView tvLunarCalendar = get(R.id.tv_lunar_calendar);
    String oDate = ChinaDateUtil.today();
    tvLunarCalendar.setText(oDate);

    TextView tvDateShow = get(R.id.tv_date_show);
    TextView tvHoliday = get(R.id.tv_holiday);
    String nDate = DateUtils.format(new Date(), "yyyy年MM月dd    EEEE");
    tvDateShow.setText(nDate);

    String nh = HolidayUtil.getHoliday(nDate.substring(0, nDate.indexOf(" ")));
    String oh = HolidayUtil.getHoliday(oDate.substring(oDate.lastIndexOf("年") + 2).replaceAll(" ", ""));

    if (null != nh) {
      tvHoliday.setText(nh);
    }
    if (null != oh) {
      if (null != nh) {
        tvHoliday.append(" " + oh);
      } else {
        tvHoliday.append(oh);
      }
    }
  }

  void showShortToast(int code, String msg) {
    switch (code) {
      case ErrorCode.CONNECTION_EXCEPTION:
        msg = getActivity().getString(R.string.error_connection);
        break;
      case ErrorCode.SERVER_Exception:
      case ErrorCode.SERVER_BadGateway:
        msg = getActivity().getString(R.string.error_server);
        break;
      case ErrorCode.SOCKET_TIMEOUT_Exception:
        msg = getActivity().getString(R.string.error_timeout);
        break;
      case ErrorCode.SSLHandshakeException:
        msg = getActivity().getString(R.string.error_ssl);
        break;
      case ErrorCode.JSON_Exception:
        msg = getActivity().getString(R.string.error_json);
        break;
      default:
        if (TextUtils.isEmpty(msg)) {
          msg = getActivity().getString(R.string.error_unknown_server) + "(" + code + ")";
        }
        break;
    }
    String finalMsg = msg;
    getActivity().runOnUiThread(() -> showToast(finalMsg));

  }

  @Override
  public void showToast(String msg) {
    getActivity().runOnUiThread(() -> super.showToast(msg));
  }

  void updateBroadcastView(List<BroadcastData> dataList) {
    BroadcastView b = get(R.id.bcv_broadcast);
    b.setbroadcastIndex(1);
    b.refreshData(dataList);
    b.startPlay();
  }

  void updateBroadcastView() {
    BroadcastView b = get(R.id.bcv_broadcast);
    b.setbroadcastIndex(1);
    b.startPlay();
  }

  /**
   * 更新界面Style
   */
  void updateStyle() {
    List<OwnStyle> list = KJDB.getDefaultInstance().findAll(OwnStyle.class);
    if (list == null || list.isEmpty()) {
      return;
    }
    ImageView ivSchoolLogo = get(R.id.iv_school_logo);
    ImageView ivHeadLayoutBg = get(R.id.iv_head_layout_img);
    ImageView ivMidBg = get(R.id.iv_mid_layout_img);

    OwnStyle style = list.get(0);
    try {
      Utils.loadImage(getActivity(), style.head_bg, ivHeadLayoutBg, R.mipmap.bg_default_header, R.mipmap.bg_default_header);
      Utils.loadImage(getActivity(), style.head_url, ivSchoolLogo, 0, 0);
      Utils.loadImage(getActivity(), style.mid_bg, ivMidBg, R.mipmap.bg_default_mid, R.mipmap.bg_default_mid);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 更新菜谱
   */
  void updateFoods() {
    List<Food> foods = KJDB.getDefaultInstance().findAll(Food.class);
    if (foods == null || foods.isEmpty()) {
      get(R.id.tv_empty_food).setVisibility(View.VISIBLE);
      viewPager.setVisibility(View.GONE);
    } else {
      get(R.id.tv_empty_food).setVisibility(View.GONE);
      viewPager.setVisibility(View.VISIBLE);
      List<List<Food>> sortAllFood = sortFoods(foods);
      ItemFoodPagerAdapter itemFoodPagerAdapter = new ItemFoodPagerAdapter(sortAllFood, getActivity());
      viewPager.setAdapter(itemFoodPagerAdapter);
      viewPager.startAutoScroll();
      viewPager.setSlideInterval(TIME_SYNC_FOOD_DELAY_MS);
    }
  }

  private List<List<Food>> sortFoods(List<Food> list) {
    Collections.sort(list, (data1, data2) -> {
      if (data1.week > data2.week) {
        return 1;
      } else if (data1.week == data2.week) {
        return 0;
      } else {
        return -1;
      }
    });

    List<List<Food>> sortAllFood = new ArrayList<>();
    List<List<Food>> sortPageFoods = new ArrayList<>();
    try {
      //拆分
      for (Food food : list) {
        List<Food> sortSubFood = new ArrayList<>();
        if (!TextUtils.isEmpty(food.name) && food.name.contains(";")) {
          String[] strings = food.name.split(";");
          if (null != strings && strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
              //;拆分菜谱
              Food subFood = new Food();
              subFood.week = food.week;
              subFood.name = strings[i];
              sortSubFood.add(subFood);
            }
          }
        } else {
          sortSubFood.add(food);
        }
        sortAllFood.add(sortSubFood);
      }
      //获取page页数
      int maxSize = 0;
      for (List<Food> sortSubFood : sortAllFood) {
        maxSize = Math.max(maxSize, sortSubFood.size());
      }
      //分页处理
      for (int i = 0; i < maxSize; i++) {
        List<Food> sortSubPageFoods = new ArrayList<>();
        for (int j = 0; j < sortAllFood.size(); j++) {
          List<Food> sortSubFoods = sortAllFood.get(j);
          if (i < sortSubFoods.size()) {
            sortSubPageFoods.add(null == sortSubFoods.get(i) ? new Food() : sortSubFoods.get(i));
          } else {
            sortSubPageFoods.add(new Food());
          }
        }
        sortPageFoods.add(sortSubPageFoods);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return sortPageFoods;
  }

  /**
   * 更新通告
   */
  void updateNotices() {
    List<Announced> announcedList = KJDB.getDefaultInstance().findAll(Announced.class);
    mNoticesList.clear();
    mNoticesList.addAll(announcedList);
    noticeViewFlipper.removeAllViews();
    if (null != mNoticesList && mNoticesList.size() > 0) {
      for (int i = 0; i < mNoticesList.size(); i++) {
        Announced announced = mNoticesList.get(i);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_notice_layout, null);
        TextView textView = view.findViewById(R.id.tv_notice);
        textView.setText(announced.title + HtmlToStringUtil.replaceBlank(HtmlToStringUtil.getHtmlContent(announced.value)));
        textView.setSelected(true);
        textView.setOnClickListener(v -> showNoticeDetailsDialogFragment(announced));
        noticeViewFlipper.addView(view);
      }
      //是否自动开始滚动
      noticeViewFlipper.setAutoStart(true);
      //滚动时间
      noticeViewFlipper.setFlipInterval(20000);
      //开始滚动
      noticeViewFlipper.startFlipping();
      //出入动画
      noticeViewFlipper.setOutAnimation(getActivity(), R.anim.slider_out_top);
      noticeViewFlipper.setInAnimation(getActivity(), R.anim.slider_in_bottom);
    }
  }

  /**
   * 更新密码
   */
  public void updatePassword() {
    this.mPassword = SPUtils.getInstance(getActivity()).get(ConstantsYJ.SpTag.PASS_WORD, "");
  }

  /**
   * 更新天气
   */
  @SuppressLint("SetTextI18n")
  void updateWeather(Context context, Weather weather) {
    TextView tvTemperature = get(R.id.tv_temperature);
    TextView tvWeather = get(R.id.tv_weather);
    TextView tvAirDetail = get(R.id.tv_air_detail);
    ImageView imgWeather = get(R.id.img_weather);
    Weather.AreaToWeather.Body body = weather.area_to_weather.showapi_res_body;
    tvTemperature.setText(body.f1.day_air_temperature + context.getResources().getString(R.string.max_oc));
    tvWeather.setText(body.f1.day_weather);
    tvAirDetail.setText(context.getResources().getString(R.string.air) +
            body.now.aqiDetail.quality + context.getResources().getString(R.string.eg) +
            body.now.wind_direction +
            body.now.wind_power);
    if (WeatherUtil.map.containsKey(body.f1.day_weather)) {
      imgWeather.setImageResource(WeatherUtil.get(body.f1.day_weather));
    }
  }

  /**
   * 小应用
   */
  void updateApps() {
    try {
      DeviceInfoRsp deviceInfoRsp = SPUtils.getInstance(getActivity()).get(ConstantsYJ.SpTag.DEVICE_APP_INFO, DeviceInfoRsp.class);
      if (null == deviceInfoRsp) {
        return;
      }
      List<DeviceInfoRsp.Apps> apps = deviceInfoRsp.extra.device_apps;
      this.mDeviceApps.clear();
      if (null != apps && apps.size() > 0) {
        this.mDeviceApps.addAll(apps);
      }
      itemSortAdapter.adapterNotifyDataSetChanged(mDeviceApps);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 二维码
   */
  void updateQrCode() {
    Slog.d(TAG, "start updateQrCode");
    getActivity().runOnUiThread(() -> {
      ImageView imgQrCode = get(R.id.iv_qr_code);
      if (!((MainActivity) getActivity()).isNetConnected()) {
        // 断网屏蔽扫码功能
        Slog.d(TAG, "updateQrCode abort: network disconnected!");
        imgQrCode.setImageResource(R.mipmap.load_fail_qr_code);
        return;
      }
      if (!AppConfig.isCode(getActivity())) {
        // 不支持扫码功能
        Slog.d(TAG, "updateQrCode abort: nonsupport code!");
        imgQrCode.setImageResource(R.mipmap.load_fail_qr_code);
        return;
      }
      List<QrCode> qrCodeList = KJDB.getDefaultInstance().findAll(QrCode.class);
      if (qrCodeList != null && !qrCodeList.isEmpty()) {
        QrCode qrCode = qrCodeList.get(0);
        if (qrCode != null) {
          String url = qrCode.url;
          if (!TextUtils.isEmpty(url)) {
            Bitmap bitmap = QrCodeUtil.createQRCodeBitmap(
                    url
                            + "?device_id=" + toURLEncoded(ConfigDevice.getDeviceId())
                            + "&school_id=" + ConfigDevice.school_id,
                    400,
                    400
            );
            imgQrCode.setImageBitmap(bitmap);
            return;
          }
        }
      }
      imgQrCode.setImageResource(R.mipmap.load_fail_qr_code);
    });
  }

  private Subscription mCameraTimerSubscription;

  /**
   * 定时更新相机开关状态
   */
  private void regularlySetCameraStatus() {
    Slog.d(TAG, "start regularlySetCameraStatus");
    updateCameraStatusBySignTime();
    updateQrCode();

    if (mCameraTimerSubscription != null && !mCameraTimerSubscription.isUnsubscribed()) {
      mCameraTimerSubscription.unsubscribe();
    }
    mCameraTimerSubscription = Observable.timer(30, TimeUnit.SECONDS).subscribe(r ->
            regularlySetCameraStatus());
    ((ActivityBase) getActivity()).addRxSubscription(mCameraTimerSubscription);
  }

  /**
   * 签到时间内 && 已经配置硬件信息：开启相机； 否则关闭相机
   */
  void updateCameraStatusBySignTime() {
    Slog.d(TAG, "start updateCameraStatusBySignTime");
    if (!AppConfig.isFace(getActivity())) {
      // 不支持人脸识别
      Slog.d(TAG, "updateCameraStatusBySignTime abort: nonsupport face!");
      stopCamera();
      return;
    }
    DeviceInfo deviceInfo = SPUtils.getInstance(getActivity()).get(ConstantsYJ.SpTag.DEVICE_INFO, DeviceInfo.class);
    if (deviceInfo == null || deviceInfo.cameras == null || deviceInfo.cameras.isEmpty()) {
      // 没有配置硬件信息
      Slog.d(TAG, "updateCameraStatusBySignTime abort: no deviceInfo!");
      stopCamera();
      return;
    }
    // 默认打开id为0的摄像头，如果打开失败，则继续尝试打开后面的
    for (DeviceInfo.Camera camera : deviceInfo.cameras) {
      if (camera != null && openCamera(camera.position, getCameraDegree(camera.orientation))) {
        return;
      }
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

  private String toURLEncoded(String paramString) {
    if (TextUtils.isDigitsOnly(paramString)) {
      return "";
    }
    try {
      String str = new String(paramString.getBytes(), "UTF-8");
      str = URLEncoder.encode(str, "UTF-8");
      return str;
    } catch (Exception localException) {
      Slog.e(TAG + " toURLEncoded error:" + paramString, localException.getMessage());
    }
    return "";
  }

  private NoticeDialogFragment noticeDialogFragment;
  private Subscription noticeSubscription;
  private ParentStudentDialogFragment parentStudentDialogFragment;
  private Subscription parentStudentSubscription;
  private StudentDialogFragment studentDialogFragment;
  private Subscription studentSubscription;
  //  private ConfirmStudentDialogFragment confirmStudentDialogFragment;
//  private Subscription confirmStudentSubscription;
  private SignSearchDialogFragment allSignRecordDialogFragment;

  /**
   * 通告弹框
   *
   * @param announced
   */
  public void showNoticeDetailsDialogFragment(Announced announced) {
    if (null != noticeDialogFragment) {
      noticeDialogFragment.dismiss();
    }
    noticeDialogFragment = new NoticeDialogFragment();
    noticeDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
      @Override
      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyDown(keyCode, event);
      }

      @Override
      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyUp(keyCode, event);
      }
    });
    noticeDialogFragment.setDialogContent(announced);
    noticeDialogFragment.show(getActivity().getFragmentManager(), "noticeDialogFragment");
    if (null != noticeSubscription && !noticeSubscription.isUnsubscribed()) {
      noticeSubscription.unsubscribe();
    }
    noticeSubscription = Observable.timer(120, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r ->
                    noticeDialogFragment.dismiss());
    ((ActivityBase) getActivity()).addRxSubscription(noticeSubscription);
  }

  /**
   * 学生家长信息框
   *
   * @param student
   */
  public void showParentStudentInfo(Student student) {
    if (AppConfig.isShowPhoto(getActivity())) {
      showParentStudentDialogFragment(student);
    } else {
      showStudentDialogFragment(student);
    }
  }

  /**
   * 学生家长信息框
   *
   * @param student
   */
  public void showParentStudentDialogFragment(Student student) {
    if (null != parentStudentDialogFragment) {
      parentStudentDialogFragment.dismiss();
    }
    parentStudentDialogFragment = new ParentStudentDialogFragment();
    parentStudentDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
      @Override
      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyDown(keyCode, event);
      }

      @Override
      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyUp(keyCode, event);
      }
    });
    parentStudentDialogFragment.setDialogContent(student);
    parentStudentDialogFragment.show(getActivity().getFragmentManager(), "");
    if (null != parentStudentSubscription && !parentStudentSubscription.isUnsubscribed()) {
      parentStudentSubscription.unsubscribe();
    }
    parentStudentSubscription = Observable.timer(AppConfig.TIME_SYNC_STUDENT_DIALOG_DELAY_MS, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r ->
                    parentStudentDialogFragment.dismiss());
    ((ActivityBase) getActivity()).addRxSubscription(parentStudentSubscription);
  }

  /**
   * 学生信息框
   *
   * @param student
   */
  public void showStudentDialogFragment(Student student) {
    if (null != studentDialogFragment) {
      studentDialogFragment.dismiss();
    }
    studentDialogFragment = new StudentDialogFragment();
    studentDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
      @Override
      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyDown(keyCode, event);
      }

      @Override
      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyUp(keyCode, event);
      }
    });
    studentDialogFragment.setDialogContent(student);
    studentDialogFragment.show(getActivity().getFragmentManager(), "");
    if (null != studentSubscription && !studentSubscription.isUnsubscribed()) {
      studentSubscription.unsubscribe();
    }
    studentSubscription = Observable.timer(AppConfig.TIME_SYNC_STUDENT_DIALOG_DELAY_MS, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(r ->
                    studentDialogFragment.dismiss());
    ((ActivityBase) getActivity()).addRxSubscription(studentSubscription);
  }

  /**
   * 选择学生信息框 弹框不做覆盖操作
   *
   * @param responseS
   * @param mCurrentPhone
   * @param mCurrentTime
   * @param swipeStatus
   * @param onSelectStudentDialogListener
   */
  public void showSelectStudentDialogFragment(ParentLinkStudentsResponseS responseS, String mCurrentPhone, long mCurrentTime,
                                              int swipeStatus, OnSelectStudentDialogListener onSelectStudentDialogListener) {
    SelectStudentDialogFragment selectStudentDialogFragment = new SelectStudentDialogFragment();
    selectStudentDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
      @Override
      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyDown(keyCode, event);
      }

      @Override
      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyUp(keyCode, event);
      }
    });
    Subscription selectStudentSubscription = Observable.timer(AppConfig.TIME_SYNC_SELECT_STUDENT_DIALOG_DELAY_MS, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> {
              selectStudentDialogFragment.dismiss();
              if (null != selectStudentDialogFragment.getItemStudentAdapter() && null != onSelectStudentDialogListener) {
                ParentLinkStudentsResponseS responseS1 = new ParentLinkStudentsResponseS();
                responseS1.students = selectStudentDialogFragment.getItemStudentAdapter().getStudents();
                onSelectStudentDialogListener.setOnSelectStudentDialogListener(responseS1, mCurrentPhone, mCurrentTime, swipeStatus);
              }
            });
    ((ActivityBase) getActivity()).addRxSubscription(selectStudentSubscription);
    selectStudentDialogFragment.setDialogContent(responseS, students -> {
      if (null != selectStudentSubscription && !selectStudentSubscription.isUnsubscribed()) {
        selectStudentSubscription.unsubscribe();
      }
      ParentLinkStudentsResponseS responseS1 = new ParentLinkStudentsResponseS();
      responseS1.students = students;
      responseS1.pick_up_status = responseS.pick_up_status;
      if (null != onSelectStudentDialogListener) {
        onSelectStudentDialogListener.setOnSelectStudentDialogListener(responseS1, mCurrentPhone, mCurrentTime, swipeStatus);
      }
    });
    selectStudentDialogFragment.show(getActivity().getFragmentManager(), "");
  }

//  /**
//   * 确认学生信息框
//   *
//   * @param responseS
//   */
//  public void showConfirmStudentDialogFragment(ParentLinkStudentsResponseS responseS, String mCurrentPhone, long mCurrentTime,
//                                               int swipeStatus, OnSelectStudentDialogListener onSelectStudentDialogListener) {
//    if (null != confirmStudentDialogFragment) {
//      confirmStudentDialogFragment.dismiss();
//    }
//    confirmStudentDialogFragment = new ConfirmStudentDialogFragment();
//    confirmStudentDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
//      @Override
//      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        (getActivity()).onKeyDown(keyCode, event);
//      }
//
//      @Override
//      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
//        (getActivity()).onKeyUp(keyCode, event);
//      }
//    });
//    confirmStudentDialogFragment.setDialogContent(responseS, responseS1 -> {
//      if (null != onSelectStudentDialogListener) {
//        onSelectStudentDialogListener.setOnSelectStudentDialogListener(responseS1, mCurrentPhone, mCurrentTime, swipeStatus);
//      }
//    });
//    confirmStudentDialogFragment.show(getActivity().getFragmentManager(), "");
//
//    if (null != confirmStudentSubscription && !confirmStudentSubscription.isUnsubscribed()) {
//      confirmStudentSubscription.unsubscribe();
//    }
//    confirmStudentSubscription = Observable.timer(AppConfig.TIME_SYNC_SELECT_STUDENT_DIALOG_DELAY_MS, TimeUnit.SECONDS).subscribe(r ->
//            confirmStudentDialogFragment.dismiss());
//    ((ActivityBase) getActivity()).addRxSubscription(confirmStudentSubscription);
//  }

  /**
   * 学生所有签到记录信息框
   */
  public void showAllSignRecordDialogFragment() {
    if (null != allSignRecordDialogFragment) {
      allSignRecordDialogFragment.dismiss();
    }
    allSignRecordDialogFragment = new SignSearchDialogFragment();
    allSignRecordDialogFragment.setOnDialogKeyListener(new OnDialogKeyListener() {
      @Override
      public void onKeyDown(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyDown(keyCode, event);
      }

      @Override
      public void onKeyUp(int keyCode, @NonNull KeyEvent event) {
        (getActivity()).onKeyUp(keyCode, event);
      }
    });
    allSignRecordDialogFragment.show(getActivity().getFragmentManager(), "noticeDialogFragment");
  }

  @Override
  protected void activityResume(boolean resume) {
    super.activityResume(resume);
    BroadcastView b = get(R.id.bcv_broadcast);
    if (b != null) {
      b.activityResume(resume);
    }
  }

  private Subscription mUserInfoTimerSubscription;

  /**
   * 刷卡、扫码、人脸识别成功或失败时的界面显示
   *
   * @param statusText      如：刷卡成功
   * @param statusTextColor 消费状态的字体颜色
   * @param students        学生信息
   */
  void setUserInfoTextOnLine(String statusText, int statusTextColor, List<Student> students) {
    setUserInfoTextOnLine(statusText, statusTextColor, students, AppConfig.TIME_SYNC_STUDENT_DIALOG_DELAY_MS);
  }

  /**
   * 刷卡、扫码、人脸识别成功或失败时的界面显示
   *
   * @param statusText      如：刷卡成功
   * @param statusTextColor 消费状态的字体颜色
   * @param students        学生信息
   * @param duration        该状态的持续时间，到时会自动清空
   */
  void setUserInfoTextOnLine(String statusText, int statusTextColor, List<Student> students, int duration) {

    if (mUserInfoTimerSubscription != null && !mUserInfoTimerSubscription.isUnsubscribed()) {
      mUserInfoTimerSubscription.unsubscribe();
    }
    mUserInfoTimerSubscription = Observable.timer(duration, TimeUnit.SECONDS).subscribe(aLong ->
            ((Activity) rootView.getContext()).runOnUiThread(this::clearStatusText));
    ((ActivityBase) rootView.getContext()).addRxSubscription(mUserInfoTimerSubscription);
    clearStatusText();
    ((TextView) get(R.id.tv_swipe_status)).setText(TextUtils.isEmpty(statusText) ? "" : statusText);
    ((TextView) get(R.id.tv_swipe_status)).setTextColor(statusTextColor);
    if (null != students && students.size() > 0) {
      StringBuilder cardNoSb = new StringBuilder();
      StringBuilder unionIdSb = new StringBuilder();
      StringBuilder nameSb = new StringBuilder();
      for (int i = 0; i < students.size(); i++) {
        Student student = students.get(i);
        if (i == students.size() - 1) {
          cardNoSb.append(TextUtils.isEmpty(student.card_no) ? "" : student.card_no);
          unionIdSb.append(TextUtils.isEmpty(student.union_id) ? "" : student.union_id);
          nameSb.append(TextUtils.isEmpty(student.name) ? "" : student.name);
        } else {
          cardNoSb.append(TextUtils.isEmpty(student.card_no) ? "" : (student.card_no + "、"));
          unionIdSb.append(TextUtils.isEmpty(student.union_id) ? "" : (student.union_id + "、"));
          nameSb.append(TextUtils.isEmpty(student.name) ? "" : (student.name + "、"));
        }
      }
      ((TextView) get(R.id.tv_card_no)).setText(cardNoSb.toString());
      ((TextView) get(R.id.tv_union_id)).setText(unionIdSb.toString());
      ((TextView) get(R.id.tv_username)).setText(nameSb.toString());
    }
  }

  private void clearStatusText() {
    ((TextView) get(R.id.tv_swipe_status)).setText("");
    ((TextView) get(R.id.tv_card_no)).setText("");
    ((TextView) get(R.id.tv_union_id)).setText("");
    ((TextView) get(R.id.tv_username)).setText("");
  }


  // 以下是人脸识别相关//////////////////////////////////////////////////////////////////////////////

  /**
   * 预览宽度
   */
  private int mPreviewWidth;
  /**
   * 预览高度
   */
  private int mPreviewHeight;
  private DetectPresenter mPresenter;
  private long mLastDebugText = 0;

  private ArrayList<Float> features = new ArrayList<>();

  void initCameraData() {
    mPresenter = new DetectPresenter(this);
    CameraInterfaceBak.getInstance().setCameraStateCallBack(this);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void recognizeFace(UserBean userBean) {
    Slog.d(TAG_DEBUG_FACE, "recognizeFace userId:" + userBean.userId + userBean.name + "\nimageUrl:" + userBean.urlImagePath);
    getActivity().runOnUiThread(() -> {
      synchronized (userBean) {
        //通知关闭全屏广播界面
        RxBus.get().post(ConstantsYJ.RxTag.RX_TAG_CONFIRM_USER, userBean.userId);
        ((TextView) get(R.id.tv_debug_msg)).setText(
                getActivity().getString(R.string.success_recognized) + ":" + userBean.name);
      }
    });
  }

  @Override
  public void recognizeFaceNotMatch(UserBean userBean) {
    if (features.size() > 0) {
      Slog.d(TAG_DEBUG_FACE, "extractFeatureASync find recognizeFaceNotMatch");
      ArrayList<Float> floatArrayList = new ArrayList<>();
      floatArrayList.clear();
      floatArrayList.addAll(features);
      final float compare = FaceDetectManager.compareFeature(FeatureUtils.arrayListToFloat(floatArrayList), FeatureUtils.arrayListToFloat(userBean.features));
      LogUtils.d(TAG_DEBUG_FACE, "extractFeatureASync find recognizeFaceNotMatch : " + compare);
      if (compare > ConfigLib.featureThreshold) {
        // 识别成功
        Message message = Message.obtain();
        message.obj = userBean.headImage;
        features.clear();
        showSlotCardState(getActivity().getResources().getString(R.string.match_success) + " : " + compare);
      } else {
        showSlotCardState(getActivity().getResources().getString(R.string.match_fail) + " : " + compare);
      }
    }
  }

  private void showSlotCardState(final String state) {
    getActivity().runOnUiThread(() -> ((TextView) get(R.id.tv_debug_msg)).setText(state));
  }

  @Override
  public void detectNoFace() {
//    Slog.d(TAG_DEBUG_FACE, "detectFace: detectNoFace");
    getActivity().runOnUiThread(() -> {
      mLastDebugText = System.currentTimeMillis();
      ((TextView) get(R.id.tv_debug_msg)).setText(getActivity().getString(R.string.no_face));
      ((TextView) get(R.id.tv_camera_debug_message)).setText("");
      updatePreviewMessage("");
    });
  }

  @Override
  public void detectFail(Status status) {
    Slog.d(TAG_DEBUG_FACE, "detectFace: detectFail");
    getActivity().runOnUiThread(() -> {
      ((TextView) get(R.id.tv_debug_msg)).setText(getActivity().getString(R.string.detect_face));
      updatePreviewMessage(status.toString());
    });
  }

  private void updatePreviewMessage(String s) {
    String stringBuilder = ("w = " + mPreviewWidth + " h = " + mPreviewHeight) + s;
    ((TextView) get(R.id.tv_camera_message)).setText(stringBuilder);
  }

  @Override
  public void detectFace(List<FaceBean> faceBeans) {
    Slog.d(TAG_DEBUG_FACE, "detectFace: detectFace");
    getActivity().runOnUiThread(() -> {
      synchronized (faceBeans) {
        List<FaceBean> faceBeanList = new ArrayList<>();
        faceBeanList.clear();
        faceBeanList.addAll(faceBeans);
        StringBuilder stringBuilder = new StringBuilder();
        try {
          for (int i = 0; i < faceBeanList.size(); i++) {
            FaceBean bean = faceBeanList.get(i);
            if (bean.mUserBean != null && !StringUtils.isEmpty(bean.mUserBean.name)) {
              // 显示人名
              String name = bean.mUserBean.name;
              stringBuilder.append("Find name:").append(name).append("");
            }
            String score = "";
            if (ConfigLib.detectWithLiveness || ConfigLib.detectWithInfraredLiveness) {
              // 显示活体识别分数
              if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.UNKNOWN) {
                score = getActivity().getString(R.string.unknown_status);
              } else if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.FAKE) {
                score = getActivity().getString(R.string.unknown_status);
              } else {
                if (bean.mUserBean != null) {
                  score = new DecimalFormat(".00").format(bean.mUserBean.compareScore);
                }
              }
            } else {// 显示非活体识别分数
              if (bean.mUserBean != null) {
                score = bean.mUserBean.compareScore + "";
              }
            }
            stringBuilder.append(" score:").append(score).append("");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        ((TextView) get(R.id.tv_debug_msg)).setText(stringBuilder.toString());
        if (System.currentTimeMillis() - mLastDebugText > 1000) {
          ((TextView) get(R.id.tv_camera_debug_message)).setText("");
        }
      }
    });
  }

  @Override
  public void debug(FaceBean faceBean) {

  }

  private boolean openCamera(final int id, final int degree) {
    Slog.d(TAG, "start openCamera id:" + id + ",degree:" + degree);
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
      Slog.d(TAG, "openCamera abort: permission not granted!");
      return false;
    }
    if (mCameraIsOpened) {
      // 相机已打开
      Slog.d(TAG, "openCamera abort: mCameraIsOpened!");
      return false;
    }
    if (!AttConstants.INIT_STATE) {
      // 人脸sdk未初始化
      Slog.d(TAG, "openCamera abort: sdk not init!");
      getActivity().runOnUiThread(()
              -> showToast(getActivity().getString(R.string.being_init_face_sdk)));
      MyApp.initAiwinnFace(null);
      return false;
    }
    int supportedMaxId = Camera.getNumberOfCameras();
    if (id < 0 || id >= supportedMaxId) {
      // 拦截设备不支持的CameraId
      Slog.d(TAG, "openCamera abort:" + getActivity().getString(R.string.fail_open_unsupported_camera_id)
              + id + "(supported" + (supportedMaxId - 1) + ")");
      getActivity().runOnUiThread(() ->
              showToast(getActivity().getString(R.string.fail_open_unsupported_camera_id)
                      + id + "(supported" + (supportedMaxId - 1) + ")"));
      return false;
    }
    AttConstants.Detect_Exception = true;
    getActivity().runOnUiThread(() -> {
      get(R.id.sv_camera).setVisibility(View.VISIBLE);
      get(R.id.tv_camera_message).setVisibility(View.VISIBLE);
      get(R.id.tv_camera_debug_message).setVisibility(View.VISIBLE);
      get(R.id.tv_debug_msg).setVisibility(View.VISIBLE);
      get(R.id.iv_avatar).setVisibility(View.GONE);
    });
    CameraInterfaceBak.getInstance().setPreViewSize(AttConstants.CAMERA_PREVIEW_WIDTH, AttConstants.CAMERA_PREVIEW_HEIGHT);

    Thread openThread = new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (AttConstants.INIT_STATE) {
          // 如果没有初始化，此处setDegree会报异常
          FaceDetectManager.setDegree(degree);
          CameraInterfaceBak.getInstance().doOpenCamera(id, degree);
        }
      }
    };
    openThread.start();
    mCameraIsOpened = true;
    Slog.d(TAG_DEBUG_FACE, "openCamera success [id]:" + id + ",[degree]:" + degree);
    return true;
  }

  private boolean mCameraIsOpened = false;

  @Override
  public void cameraHasOpened() {
    Slog.d(TAG_DEBUG_FACE, "cameraHasOpened");
    Camera camera = CameraInterfaceBak.getInstance().mCamera;
    if (camera == null) {
      Slog.e(TAG_DEBUG_FACE, "cameraHasOpened error: camera is null!");
      return;
    }
    camera.setErrorCallback(mCameraErrorCallback);
    CameraSurfaceView svCamera = get(R.id.sv_camera);
    while (true) {
      if (!svCamera.hasCreated) {
//        Slog.d(TAG_DEBUG_FACE, "wait CameraSurfaceView created !");
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        break;
      }
    }
    Slog.d(TAG_DEBUG_FACE, "openCamera mCameraHasError:" + mCameraHasError);
    if (mCameraHasError) {
      mCameraHasError = false;
      return;
    }
    CameraInterfaceBak.getInstance().doStartPreview(svCamera.getSurfaceHolder());
  }

  private boolean mCameraHasError = false;

  private Camera.ErrorCallback mCameraErrorCallback = (error, camera) -> {
    Slog.d(TAG_DEBUG_FACE, "openCamera ErrorCallback:" + error);
    switch (error) {
      case Camera.CAMERA_ERROR_SERVER_DIED:
      case Camera.CAMERA_ERROR_UNKNOWN:
        Log.e(TAG_DEBUG_FACE, "openCamera ErrorCallback:" + error);
        mCameraHasError = true;
        stopCamera();
        //这里重新初始化Camera即可
        updateCameraStatusBySignTime();
        break;
    }
  };

  @Override
  public void cameraHasParameters() {
    mPreviewWidth = CameraInterfaceBak.getInstance().getPreviewWidth();
    mPreviewHeight = CameraInterfaceBak.getInstance().getPreviewHeight();
  }

  @Override
  public void cameraHasPreview(byte[] data, Camera camera) {
    this.data = data;
    if (controlFrame()) {
      Slog.d(TAG, "Begin -> ( w = " + mPreviewWidth + " h = " + mPreviewHeight + " size = " + data.length + " )");
      mPresenter.detectFaceData(data, mPreviewWidth, mPreviewHeight);
    }
  }

  private byte[] data;

  public Bitmap getFaceBitmap() {
    if (null == data) {
      return null;
    }
    return Utils.getBitmap(data, mPreviewWidth, mPreviewHeight);
  }

  private int nowFrame = 1;

  /**
   * 控制一定帧数才检测一次人脸
   */
  private boolean controlFrame() {
    Slog.d(TAG, "start controlFrame, nowFrame: " + nowFrame);
    nowFrame++;
    dismissProgress();
    if (nowFrame >= 8) {
      nowFrame = 1;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void onAStart() {
    super.onAStart();
    regularlySetCameraStatus();
  }

  @Override
  public void onASTop() {
    stopCamera();
    if (mCameraTimerSubscription != null && !mCameraTimerSubscription.isUnsubscribed()) {
      mCameraTimerSubscription.unsubscribe();
    }
    super.onASTop();
  }

  @Override
  public void onADestory() {
    super.onADestory();
    BroadcastView b = get(R.id.bcv_broadcast);
    if (b != null) {
      b.destory();
    }
  }

  private void stopCamera() {
    AttConstants.Detect_Exception = false;
    mCameraIsOpened = false;
    getActivity().runOnUiThread(() -> {
      get(R.id.sv_camera).setVisibility(View.GONE);
      get(R.id.tv_camera_message).setVisibility(View.GONE);
      get(R.id.tv_camera_debug_message).setVisibility(View.GONE);
      get(R.id.tv_debug_msg).setVisibility(View.GONE);
      get(R.id.iv_avatar).setVisibility(View.VISIBLE);
      CameraInterfaceBak.getInstance().doStopCamera();
    });
  }

  /**
   * 展示天气popwindow
   */
  void showWeatherPopwindow(Context context, Weather weatherResponse) {
    WeatherPopwindow weatherPopwindow = new WeatherPopwindow(context);
    weatherPopwindow.initCalendar(weatherResponse);
    get(R.id.ll_weather).setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        weatherPopwindow.showWeather(get(R.id.tv_air_detail));
      }
    });
  }

  void showCalendarPopwindow(Context context) {
    //初始化日历popwindow
    CalendarPopwindow calendarPopwindow = new CalendarPopwindow(context);
    calendarPopwindow.initCalendar();
    get(R.id.cl_calendar).setOnClickListener(new OnClickEffectiveListener() {
      @Override
      public void onClickEffective(View v) {
        calendarPopwindow.showCalendar(get(R.id.cl_calendar));
      }
    });
  }
}
