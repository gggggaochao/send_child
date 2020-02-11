package viroyal.com.base.face.aiwinn.face_detect;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.LivenessBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.RecognizeListener;
import com.suntiago.baseui.utils.log.Slog;

import java.util.List;

import viroyal.com.base.face.aiwinn.common.AttConstants;

import static viroyal.com.base.activity.main.MainDelegate.TAG_DEBUG_FACE;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class DetectPresenter {

  private final String TAG = getClass().getSimpleName();

  private static final String HEAD = "ATT_DETECT";

  private DetectView mDetectView;

  public DetectPresenter(DetectView detectView) {
    mDetectView = detectView;
  }

  public void detectFaceData(final byte[] data, final int w, final int h) {
    FaceDetectManager.recognizeFace(AttConstants.EXDB, data, w, h, new RecognizeListener() {
      @Override
      public void onDetectFace(List<FaceBean> faceBeanList) {
        if (faceBeanList.size() > 0) {
          mDetectView.detectFace(faceBeanList);
          LogUtils.d(DetectPresenter.HEAD, "faceBeanList_mDetectBean_id=" + faceBeanList.get(0).mDetectBean.id);
        } else {
          mDetectView.detectNoFace();
        }
      }

      @Override
      public void onLiveness(LivenessBean livenessBean) {

      }

      @Override
      public void onRecognize(UserBean userBean, DetectBean detectBean) {
        if (userBean == null) {
          return;
        }
        Slog.d(TAG_DEBUG_FACE, "onRecognize, name: " + userBean.name);
        if (StringUtils.isEmpty(userBean.name)) {
          mDetectView.recognizeFaceNotMatch(userBean);
        } else {
          mDetectView.recognizeFace(userBean);
        }
      }

      @Override
      public void onError(Status status) {
        mDetectView.detectFail(status);
      }
    }, faceBean -> mDetectView.debug(faceBean));
  }
}
