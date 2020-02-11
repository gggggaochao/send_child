package viroyal.com.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author chenjunwei
 * @desc 网络状态
 * @date 2019/4/29
 */
public class WifiReceiver extends BroadcastReceiver {
  /**
   * 无网络
   */
  public static final int NET_NO_CONNECT = 0;
  /**
   * 有线
   */
  public static final int NET_ETHERNET = 1;
  /**
   * 无线
   */
  public static final int NET_WIFI = 2;

  private OnWifiConnectedListener onWifiConnectedListener;

  public void setOnWifiConnectedListener(OnWifiConnectedListener onWifiConnectedListener) {
    this.onWifiConnectedListener = onWifiConnectedListener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();

    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
            || action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

      switch (isNetworkAvailable(context)) {
        case 1:
        case 2:
          if (null != onWifiConnectedListener) {
            onWifiConnectedListener.setOnWifiConnectedListener(true);
          }
          break;
        case 0:
          if (null != onWifiConnectedListener) {
            onWifiConnectedListener.setOnWifiConnectedListener(false);
          }
          break;
        default:
          break;
      }
    }
  }

  private int isNetworkAvailable(Context context) {
    ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


    if (ethNetInfo != null && ethNetInfo.isConnected()) {
      return NET_ETHERNET;
    } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
      return NET_WIFI;
    } else {
      return NET_NO_CONNECT;
    }
  }

  public interface OnWifiConnectedListener {
    /**
     * wifi变化监听
     *
     * @param isWifiConnected
     */
    void setOnWifiConnectedListener(boolean isWifiConnected);
  }
}
