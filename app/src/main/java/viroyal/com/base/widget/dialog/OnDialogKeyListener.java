package viroyal.com.base.widget.dialog;

import android.support.annotation.NonNull;
import android.view.KeyEvent;

/**
*@author chenjunwei
*@desc 解决刷卡问题
*@date 2019-09-03
*/
public interface OnDialogKeyListener {
  void onKeyDown(int keyCode, @NonNull KeyEvent event);

  void onKeyUp(int keyCode, @NonNull KeyEvent event);
}
