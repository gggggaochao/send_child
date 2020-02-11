package viroyal.com.base.listener;

import com.aiwinn.facedetectsdk.bean.UserBean;

import java.util.List;

/**
 * @author chenjunwei
 * @desc 同步成功回调
 * @date 2019-09-05
 */
public interface OnSyncToLocalSuccessListener {
  void setOnSyncToLocalSuccessListener(List<UserBean> saveReadySyncUsers);
}
