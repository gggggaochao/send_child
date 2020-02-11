package viroyal.com.base.listener;

import viroyal.com.base.net.rsp.ParentLinkStudentsResponseS;

/**
*@author chenjunwei
*@desc 选择学生回调
*@date 2019-09-04
*/
public interface OnSelectStudentDialogListener {
  void setOnSelectStudentDialogListener(ParentLinkStudentsResponseS responseS, String mCurrentPhone, long mCurrentTime, int swipeStatus);
}
