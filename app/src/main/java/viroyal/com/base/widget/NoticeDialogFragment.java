package viroyal.com.base.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suntiago.baseui.utils.ScreenUtils;

import viroyal.com.base.R;
import viroyal.com.base.model.Announced;


/**
 * @author chenjunwei
 * @desc
 * @date 2019/8/13
 */
public class NoticeDialogFragment extends BaseDialogFragment {

  private Announced mAnnounced;

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_notice_layout, null);
    LinearLayout ll_dialog_notice = view.findViewById(R.id.ll_dialog_notice);
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_dialog_notice.getLayoutParams();
    layoutParams.width = (int) (ScreenUtils.getScreenWidth(mContext) * 0.87);
    layoutParams.height = (int) (ScreenUtils.getScreenHeight(mContext) * 0.71);
    ll_dialog_notice.setLayoutParams(layoutParams);

    if (mAnnounced != null) {
      TextView tvTitle = view.findViewById(R.id.tv_title);
      WebView webView = view.findViewById(R.id.web_view_notice_value);
      tvTitle.setText(mAnnounced.title);
      webView.getSettings().setJavaScriptEnabled(true);
      //设置网页在WebView中显示而不是调用浏览器
      webView.setWebViewClient(new WebViewClient());
      webView.getSettings().setJavaScriptEnabled(true);
      webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
      if (!TextUtils.isEmpty(mAnnounced.url)) {
        webView.loadUrl(mAnnounced.url);
      } else {
        webView.loadData(mAnnounced.value, "text/html; charset=UTF-8", "UTF-8");
      }
    }
    ImageView iv_close_notice = view.findViewById(R.id.iv_close_notice);
    iv_close_notice.setOnClickListener(v -> getDialog().dismiss());
    return view;
  }

  public void setDialogContent(Announced announced) {
    mAnnounced = announced;
  }
}
