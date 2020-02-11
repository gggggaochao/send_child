package viroyal.com.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.suntiago.baseui.utils.log.Slog;

import viroyal.com.base.activity.splash.SplashIp;

/**
 * Created by chuxiao on 2019/2/21.
 */

public class BootReceiver extends BroadcastReceiver {

  private final String TAG = getClass().getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
      Slog.d(TAG, "收到开机广播");
      // 要启动的Activity
      Intent bootIntent = new Intent(context, SplashIp.class);
      //1.如果自启动APP，参数为需要自动启动的应用包名
//      bootIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
      //下面这句话必须加上才能开机自动运行app的界面
      bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //2.如果自启动Activity
      context.startActivity(bootIntent);
      //3.如果自启动服务
//      context.startService(bootIntent);
    }
  }
}
