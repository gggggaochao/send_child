package viroyal.com.base.activity.broadcast;

import android.view.ViewGroup;

import com.suntiago.baseui.activity.base.AppDelegateBase;

import viroyal.com.base.R;
import viroyal.com.dev.broadcast.BroadcastView;


/**
 * @author chenjunwei
 * @desc 广告界面更新
 * @date 2019/4/23
 */
public class BroadcastDelegate extends AppDelegateBase<BroadcastModel> {
  @Override
  public int getRootLayoutId() {
    return R.layout.activity_broadcast;
  }

  @Override
  public void initWidget() {
  }

  @Override
  public void viewBindModel(BroadcastModel data) {

  }

  @Override
  protected void activityResume(boolean resume) {
    super.activityResume(resume);
    ((BroadcastView) this.get(R.id.bv_broadcast)).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    this.get(R.id.bv_broadcast).requestFocus();
    ((BroadcastView) this.get(R.id.bv_broadcast)).activityResume(resume);
  }

  @Override
  public void onADestory() {
    ((BroadcastView) this.get(R.id.bv_broadcast)).destory();
    super.onADestory();
  }

  public final void updateBroadcastView() {
    ((BroadcastView) this.get(R.id.bv_broadcast)).setbroadcastIndex(2);
    ((BroadcastView) this.get(R.id.bv_broadcast)).startPlay();
    ((BroadcastView) this.get(R.id.bv_broadcast)).setOnNoMediaListener(() -> {
      BroadcastDelegate.this.getActivity().finish();
    });
  }
}
