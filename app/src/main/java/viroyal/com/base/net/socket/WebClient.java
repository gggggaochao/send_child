package viroyal.com.base.net.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;
import com.suntiago.baseui.utils.log.Slog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import viroyal.com.base.BuildConfig;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.dev.splash.ConfigDevice;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/7/23
 */
public class WebClient extends WebSocketClient {

  private static final String TAG = "WebClient";

  /**
   * 路径为ws+服务器地址+服务器端设置的子路径+参数（这里对应服务器端机器编号为参数）
   * 如果服务器端为https的，则前缀的ws则变为wss
   */
  private static final String mAddress =
          BuildConfig.SOCKET_HOST +
                  "websocket/scan/" +
                  ConfigDevice.getDeviceId();

  private Context mContext;

  private WebClient(URI serverUri, Context context) {
    super(serverUri, new Draft_6455());
    mContext = context;
    Slog.d(TAG, "WebClient  [serverUri, context]:");
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Slog.d(TAG, "onOpen  [handshakedata]:" + handshakedata.getHttpStatus()
            + handshakedata.getHttpStatusMessage());
  }

  @Override
  public void onMessage(String data) {

    Slog.d(TAG, "onMessage  [data]:" + data);
    if (!TextUtils.isEmpty(data)) {
      RxBus.get().post(ConstantsYJ.RxTag.RX_TAG_SOCKET_ON_MESSAGE, data);
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    Slog.d(TAG, "onClose  [code, reason, remote]:" + code + "  " + reason);
    retryConnect();
  }

  @Override
  public void onError(Exception ex) {
    Slog.e(TAG, "onError  [ex]:" + ex.toString());
    retryConnect();
  }

  private Subscription mTimerSubscription;

  private void retryConnect() {
    if (mTimerSubscription != null && !mTimerSubscription.isUnsubscribed()) {
      mTimerSubscription.unsubscribe();
    }
    if (!isNetworkConnected(mContext)) {
      // 网络恢复后才去重连
      mTimerSubscription = Observable.timer(5, TimeUnit.SECONDS).subscribe(aLong ->
              retryConnect());
      return;
    }
    // 失败了重连
    mTimerSubscription = Observable.timer(5, TimeUnit.SECONDS).subscribe(aLong ->
            connectWebSocket(mContext));
  }

  private static WebClient mWebClient;

  /**
   * 初始化
   */
  public static void connectWebSocket(Context context) {
    Slog.d(TAG, "call connectWebSocket");
    new Thread(() -> {
      try {
        Slog.d(TAG, "connectWebSocket address:" + mAddress);
        mWebClient = new WebClient(new URI(mAddress), context);
        if (!mWebClient.isOpen()) {
          Slog.d(TAG, "start connectWebSocket");
          mWebClient.connectBlocking();
        }
      } catch (Exception e) {
        if (mWebClient != null) {
          mWebClient.onError(e);
        }
        e.printStackTrace();
      }
    }).start();
  }

  /**
   * 关闭
   */
  public static void closeWebSocket() {
    new Thread(() -> {
      try {
        if (mWebClient != null && mWebClient.isOpen()) {
          Slog.d(TAG, "start closeWebSocket");
          mWebClient.closeBlocking();
        }
        mWebClient = null;
      } catch (Exception e) {
        Slog.e(TAG, e.getMessage());
        e.printStackTrace();
      }
    }).start();
  }

  private static boolean isNetworkConnected(Context context) {
    ConnectivityManager connectivity = (ConnectivityManager) context
            .getSystemService(CONNECTIVITY_SERVICE);
    if (null != connectivity) {
      NetworkInfo info = connectivity.getActiveNetworkInfo();
      if (null != info && info.isConnected()) {
        if (info.getState() == NetworkInfo.State.CONNECTED) {
          return true;
        }
      }
    }
    return false;
  }

}
