package viroyal.com.base.util;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


/**
 * @author chenjunwei
 * @desc 高度地图工具类
 * @date 2019/5/28
 */
public class AMapUtil {
  public static final String TAG = "AMapUtil";

  /**
   * 声明AMapLocationClient类对象
   */
  public static AMapLocationClient mLocationClient = null;

  /**
   * 声明AMapLocationClientOption对象
   */
  public static AMapLocationClientOption mLocationOption = null;

  /**
   * 初始化定位
   */
  public static void initAMap(Context context, AMapLocationListener mLocationListener) {
    //初始化定位
    mLocationClient = new AMapLocationClient(context);
    mLocationClient.setLocationListener(mLocationListener);
    //初始化AMapLocationClientOption对象
    mLocationOption = new AMapLocationClientOption();
    AMapLocationClientOption option = new AMapLocationClientOption();
    // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
    option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
    if (null != mLocationClient) {
      mLocationClient.setLocationOption(option);
      //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
      mLocationClient.stopLocation();
      mLocationClient.startLocation();
    }
    //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
    //获取一次定位结果：
    mLocationOption.setOnceLocation(true);
    //设置是否返回地址信息（默认返回地址信息）
    mLocationOption.setNeedAddress(true);
    //给定位客户端对象设置定位参数
    mLocationClient.setLocationOption(mLocationOption);
    //启动定位
    mLocationClient.startLocation();
  }

}
