package viroyal.com.base.face.aiwinn.common;

/**
 * com.aiwinn.faceattendance.service
 * 2017/11/15
 * Created by leilei.deng on User.
 */

import android.os.Environment;

/**
 * Created by Administrator on 2016/12/5.
 */
public class Config {
  //有些摄像头， -1 时 默认是后置  摄像头，画面会左右反掉
  public static int facing = 0;
  // 采集视频数据的宽
  public static int WIDTH = 1280;   // 1920;  //640; 		//1280;
  // 采集视频数据的高
  public static int HEIGHT = 960;    // 1080;  //480; 		//720;
  //显示摄像头视屏预览画面的宽，有区别与摄像头输出视频数据的宽
  public static int ShowWidth = 1920;  // 1280;  1280
  //显示摄像头视屏预览画面的高，有区别与摄像头输出视频数据的高
  public static int ShowHeight = 1080;    //960;
  //窗口缩放大小
  public static float WidthpsScale = 1f;
  //窗口缩放大小
  public static float HeightpsScale = 1f;

  public static int RecogniNumOKflag = 100;
  //人脸检测一张画面最多识别人脸数
  public static final int MaxfaceNum = 50;
  //    //两帧之间 两张人脸之间距离小于这个就为同一张人脸，
//    public static float             newFaceDistance        = 60.0f;
  //注册名字允许的最大的长度
  public static int RegisterNameNum = 15;
  //开始注册标志
  public static boolean beginRegisterfalg = false;

  public static boolean mFirst_pic_flag = false;

  public static boolean mSecond_pic_flag = false;

  public static int AbsolutePathflag = 0;
  // 大于这个时间 ，加载完后，进行 UI提示
//    public static long              UpdateDbtrainMaxtime   = 8000;  // 8S

//    public static boolean           UpdateDbtrainfalg      = false;

  public static boolean FaceDetecInitFlag = false;

  public static final String SD_DIR = Environment.getExternalStorageDirectory().getPath();

  public static final String SD_AIWINN_DIR = SD_DIR + "/" + "aiwinn";

  public static final String SD_ATT_DIR = SD_AIWINN_DIR + "/" + "attendance";

  public static final String SD_LOG_DIR = SD_AIWINN_DIR + "/" + "logdir";  //创建保存 log的文件夹

  public static final String SD_ATT_PIC_DIR = SD_ATT_DIR + "/" + "pic";

  public static final String SD_LOCK_DIR = SD_AIWINN_DIR + "/" + "facelock";

  public static final String SD_LOCK_PD_DIR = SD_ATT_DIR + "/" + "pb";

  public static final String SD_LOCK_ATT_PIC = SD_DIR + "/" + "att_pic";

}

