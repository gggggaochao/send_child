package viroyal.com.base.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;



/**
 * @author chenjunwei
 * @desc 处理刷卡问题
 * @date 2019-09-03
 */
public class OnProgressDialog extends ProgressDialog {

  private OnDialogKeyListener onDialogKeyListener;

  public OnProgressDialog(Context context) {
    super(context);
  }

  public OnProgressDialog(Context context, int theme) {
    super(context, theme);
  }


  public void setOnKeyListener(OnDialogKeyListener onDialogKeyListener) {
    this.onDialogKeyListener = onDialogKeyListener;
  }

  @Override
  public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
    if (null != onDialogKeyListener) {
      onDialogKeyListener.onKeyDown(keyCode, event);
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
    if (null != onDialogKeyListener) {
      onDialogKeyListener.onKeyUp(keyCode, event);
    }
    return super.onKeyUp(keyCode, event);
  }
}
