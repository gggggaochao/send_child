package viroyal.com.base.activity.broadcast;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import viroyal.com.base.common.ConstantsYJ;
import viroyal.com.base.util.Utils;
import viroyal.com.dev.NFCMonitorBaseActivity;


/**
 * @author chenjunwei
 * @desc 广告页面
 * @date 2019/4/30
 */
public class BroadcastActivity extends NFCMonitorBaseActivity<BroadcastDelegate, BroadcastModel> {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewDelegate.initWidget();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    Utils.fullScreen(getWindow());
  }

  @Override
  protected Class<BroadcastDelegate> getDelegateClass() {
    return BroadcastDelegate.class;
  }

  @Override
  protected Class<BroadcastModel> getModelClass() {
    return BroadcastModel.class;
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    super.initView(savedInstanceState);
    viewDelegate.updateBroadcastView();
  }

  @Subscribe(tags = @Tag(ConstantsYJ.RxTag.RX_TAG_CLOSE_BROADCAST), thread = EventThread.MAIN_THREAD)
  public void finishThis(String s) {
    this.finish();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    this.finish();
    return super.dispatchTouchEvent(ev);
  }

}
