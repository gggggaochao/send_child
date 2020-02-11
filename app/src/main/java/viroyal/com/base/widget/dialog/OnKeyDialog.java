package viroyal.com.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;


/**
 * @author chenjunwei
 * @desc 处理刷卡问题
 * @date 2019-09-03
 */
public class OnKeyDialog extends Dialog {

  private OnDialogKeyListener onDialogKeyListener;

  public OnKeyDialog(@NonNull Context context) {
    super(context);
  }

  public OnKeyDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
  }

  protected OnKeyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
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
