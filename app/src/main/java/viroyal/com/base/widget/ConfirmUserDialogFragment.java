package viroyal.com.base.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiwinn.facedetectsdk.bean.UserBean;
import com.hwangjr.rxbus.RxBus;

import viroyal.com.base.R;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.util.Utils;

/**
 * Created by chuxiao on 2019/2/14.
 */

public class ConfirmUserDialogFragment extends BaseDialogFragment {

  private UserBean mUserBean;

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm_user_layout, null);
    if (mContext == null || mUserBean == null) {
      return null;
    }
    TextView tvTitle = view.findViewById(R.id.tv_title);
    ImageView ivPortrait = view.findViewById(R.id.iv_portrait);
    tvTitle.setText(mContext.getString(R.string.dialog_title_confirm_user));
    Utils.loadImage(mContext, mUserBean.urlImagePath, mUserBean.localImagePath, ivPortrait, R.mipmap.bg_default_avatar);
    Button btnYes = view.findViewById(R.id.btn_yes);
    Button btnNO = view.findViewById(R.id.btn_no);
    btnYes.setOnClickListener(v -> {
      getDialog().dismiss();
    });
    btnNO.setOnClickListener(v -> getDialog().dismiss());
    return view;
  }

  public void setDialogContent(Context context, UserBean userBean) {
    mContext = context;
    mUserBean = userBean;
  }
}
