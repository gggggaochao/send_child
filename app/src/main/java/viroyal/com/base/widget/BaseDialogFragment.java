package viroyal.com.base.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import viroyal.com.base.util.Utils;
import viroyal.com.base.widget.dialog.OnDialogKeyListener;
import viroyal.com.base.widget.dialog.OnKeyDialog;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/8/9
 */
public abstract class BaseDialogFragment extends DialogFragment {

  protected OnDialogKeyListener onDialogKeyListener;

  protected Context mContext;

  public void setOnDialogKeyListener(OnDialogKeyListener onDialogKeyListener) {
    this.onDialogKeyListener = onDialogKeyListener;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    if (getDialog() != null) {
      Window window = getDialog().getWindow();
      if(null != window){
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().setOnShowListener(dialog -> {
          window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
          Utils.fullScreen(window);
        });
      }
    }
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mContext = getActivity();
    OnKeyDialog dialog = new OnKeyDialog(mContext);
    dialog.setOnKeyListener(onDialogKeyListener);
    dialog.setContentView(getContentView());
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(true);
    return dialog;
  }

  protected abstract View getContentView();

}
