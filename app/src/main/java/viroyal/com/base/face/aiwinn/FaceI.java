package viroyal.com.base.face.aiwinn;

import android.content.Context;

import com.aiwinn.facedetectsdk.bean.UserBean;

import java.util.List;

import viroyal.com.base.face.bean.FaceInfoBean;
import viroyal.com.base.face.rsp.FeatureUser;
import viroyal.com.base.face.rsp.FeaturesSyncRsp;
import viroyal.com.base.listener.OnAiwinnSuccessListener;
import viroyal.com.base.listener.OnSyncToLocalSuccessListener;

/**
 * Created by zy on 2019/3/8.
 */

public interface FaceI {
  //设备授权
  void authorization(Context context, OnAiwinnSuccessListener onAiwinnSuccessListener);

  //人脸信息采集
  void collectFaceData();

  //人脸信息同步上行
  List<FaceInfoBean> syncToServer();

  //人脸信息同步下行
  List<UserBean> syncToLocal(List<FeatureUser> list, Context mContext);
}
