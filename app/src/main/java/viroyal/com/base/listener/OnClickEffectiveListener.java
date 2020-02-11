package viroyal.com.base.listener;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/5/28
 */
public abstract class OnClickEffectiveListener implements OnClickListener {

  private static final long TIME_INTERVAL = 1000L;
  private static long lastClickTime;

  public static boolean isFastDoubleClick() {
    long time = System.currentTimeMillis();
    if (time - lastClickTime < TIME_INTERVAL) {
      return true;
    }
    lastClickTime = time;
    return false;
  }

  @Override
  public void onClick(View v) {
    if (isFastDoubleClick()) {
      return;
    } else {
      onClickEffective(v);
    }
  }

  public abstract void onClickEffective(View v);
}
