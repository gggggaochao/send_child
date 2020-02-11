package viroyal.com.base.activity.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.aiwinn.base.util.AppUtils;
import com.amap.api.location.AMapLocationListener;
import com.baidu.tts.BaiDuTtsUtil;
import com.suntiago.baseui.activity.base.theMvp.databind.DataBinder;
import com.suntiago.baseui.utils.SPUtils;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.getpermission.rxpermissions.RxPermissions;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import viroyal.com.base.AppConfig;
import viroyal.com.base.MyApp;
import viroyal.com.base.activity.main.MainActivity;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.face.aiwinn.AiwinnManager;
import viroyal.com.base.util.Utils;
import viroyal.com.dev.splash.ConfigDevice;
import viroyal.com.dev.splash.SplashIpActivity;

/**
 * Created by zy on 2018/12/6.
 */

public class SplashIp extends SplashIpActivity<SplashAppDelegate, SplashModel> {
  /**
   * 声明定位回调监听器
   */
  public AMapLocationListener mLocationListener = aMapLocation -> {
    if (aMapLocation != null) {
      if (aMapLocation.getErrorCode() == 0) {
        //获取三天的天气
        ConstantsYJ.LOCATION_ADDRESS = aMapLocation.getCity();
        Slog.e(TAG, "aMapLocation.getCity(): " + aMapLocation.getCity());
      } else {
        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
        Slog.e(TAG, "[AMapLocation]" + aMapLocation + "----> location Error, ErrCode:"
                + aMapLocation.getErrorCode() + ", errInfo:"
                + aMapLocation.getErrorInfo());
      }
    }
  };


  private void initPermission() {
    RxPermissions.getInstance(this).request(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_PHONE_STATE)
            .subscribe(aBoolean -> {
              Slog.d(TAG, "requestPermissionAndEnterApp  []:" + aBoolean);
              if (!aBoolean) {
                viewDelegate.showToast("请手动打开权限！");
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
              } else {
                MyApp.initBaiDuTts();
                MyApp.initAMap(mLocationListener);
                MyApp.initAiwinnFace(() -> {
                  dismissDialog();
                  finish();
                  //有时候会出现school_id为空的情况 这里重新判断
                  ConfigDevice.school_id = TextUtils.isEmpty(ConfigDevice.school_id) ? SPUtils.getInstance(SplashIp.this).get("school_id")
                          : ConfigDevice.school_id;
                  startActivity(new Intent(SplashIp.this, MainActivity.class));
                });
              }
            });
  }

  private AlertDialog sdkAlertDialog;

  private void showAlertDialog() {
    sdkAlertDialog = new AlertDialog.Builder(this)
            .setTitle("SDK配置初始化")
            .setCancelable(false)
            .setMessage("正在初始化SDK配置...")
            .create();
    sdkAlertDialog.setOnKeyListener((dialog, keyCode, event) -> {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        onBackPressed();
        AiwinnManager.get().release();
        BaiDuTtsUtil.release();
        AppUtils.exitApp();
      }
      return false;
    });
    sdkAlertDialog.show();
  }

  private void dismissDialog() {
    if (null != sdkAlertDialog) {
      sdkAlertDialog.dismiss();
    }
  }

  @Override
  protected void splashFinish() {
    showAlertDialog();
    //配置加载完成初始化百度语音、高德地图、人脸sdk
    initPermission();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    Utils.fullScreen(getWindow());
  }

  @Override
  protected String getHostApi() {
    return AppConfig.DEV_HOST;
  }

  @Override
  protected NFCSwitch NFCSwitch() {
    return NFCSwitch.DEFAULT;
  }

  @Override
  protected String getDeviceType() {
    return AppConfig.DEV_APP_TYPE;
  }

  @Override
  public DataBinder getDataBinder() {
    return super.getDataBinder();
  }

  @Override
  protected Class<SplashAppDelegate> getDelegateClass() {
    return SplashAppDelegate.class;
  }

  @Override
  protected Class<SplashModel> getModelClass() {
    return SplashModel.class;
  }
}
