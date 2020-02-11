package viroyal.com.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.deblocks.FaceManager;
import com.baidu.tts.BaiDuTtsUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import viroyal.com.base.AppConfig;
import viroyal.com.base.activity.main.MainActivity;
import viroyal.com.base.face.aiwinn.common.AttConstants;

/**
 * @author chenjunwei
 * @desc
 * @date 2019/6/21
 */
public class Utils {

  public static void setText(TextView view, String text) {
    if (view == null) {
      return;
    }
    if (!isEmpty(text)) {
      view.setText(text);
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }
  }

  public static void setTextView(TextView view, String text) {
    if (view == null) {
      return;
    }
    if (!isEmpty(text)) {
      view.setText(text);
    } else {
      view.setText("");
    }
  }

  /**
   * 判断字符串是否为空，或则null
   */

  public static boolean isEmpty(String data) {
    if (TextUtils.isEmpty(data) || data.equalsIgnoreCase("null")
            || data.equals("[]")) {
      return true;
    }
    return false;
  }


  public static void setVisibility(View view, int flag) {
    if (view != null && view.getVisibility() != flag) {
      view.setVisibility(flag);
    }
  }

  /**
   * @param mContext
   * @param rid
   * @return
   */
  public static String getString(Context mContext, int rid) {
    if (null == mContext || mContext.getResources() == null)
      return "";
    return mContext.getResources().getString(rid);
  }

  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static long toLong(String time) {
    try {
      return Long.parseLong(time);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * 将byte[]转为各种进制的字符串
   *
   * @param bytes byte[]
   * @param radix 基数可以转换进制的范围(2-36)，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
   * @return 转换后的字符串
   */
  public static String binary(byte[] bytes, int radix) {
    // 这里的1代表正数
    return new BigInteger(1, bytes).toString(radix);
  }

  public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
    if (bitmap != null) {
      Matrix m = new Matrix();
      m.postRotate(degress);
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
      return bitmap;
    }
    return bitmap;

  }

  public static String toString(long time) {
    try {
      return String.valueOf(time);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public static long getTime(String time) {
    if (TextUtils.isEmpty(time)) {
      return 0;
    }
    //设置日期格式
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd HH:mm");
    try {
      return df.parse(time).getTime() / 1000;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static void speak(String msg, Context context) {
    if (AppConfig.isSpeak(context)) {
      BaiDuTtsUtil.stop();
      BaiDuTtsUtil.speak(msg);
    }
  }

  public static Bitmap getBitmap(final byte[] data, int width, int height) {
    Bitmap bitmap = FaceManager.init().Nv21ToBitmap(data, width, height, 0, Bitmap.Config.ARGB_8888);
    bitmap = rotateBitmap(bitmap, AttConstants.CAMERA_FRONT_DEGREE);
    return bitmap;
  }

  public static Bitmap getBitmap(final byte[] data, int width, int height, Camera.Parameters parameters) {
    YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
    byte[] bytes = out.toByteArray();
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
  }

  /**
   * 将bitmap按照最大的file size保存到指定文件
   *
   * @param filePath 待保存的文件路径
   * @param bitmap   图片
   */

  public static boolean saveBitmap2File(Bitmap bitmap, String filePath, Context context) {
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      Toast.makeText(context, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
      return false;
    }
    File photoFile = new File(filePath);
    int maxSize = 200;
    FileOutputStream fileOutputStream = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int scale = 100;
    try {
      if (!photoFile.exists()) {
        photoFile.getParentFile().mkdirs();
        photoFile.createNewFile();
      }
      fileOutputStream = new FileOutputStream(photoFile);
      if (bitmap != null) {
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, scale, baos)) {
          int baosSize = baos.toByteArray().length;
          while (baosSize / 1024 > maxSize && scale > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, scale, baos);
            baosSize = baos.toByteArray().length;
            scale -= 5;
          }
          // 缩放后的数据写入到文件中
          baos.writeTo(fileOutputStream);
          fileOutputStream.flush();
          return true;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      photoFile.delete();
      e.printStackTrace();
    } finally {
      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 删除文件夹中最早产生的一个文件，如csv文件夹永远只保留max=30个最新的文件，超过30则删除一个文件
   * 注意：是根据文件命名来delete的，所以必须要有公共的命名规则
   *
   * @param commonFileSuffix 文件夹下的文件名的公共后缀：如_log.txt
   * @param fileDir          文件夹名称
   * @param max              文件夹下最多的文件数量
   * @param format           时间格式
   */
  public static void deleteFurthestFile(String fileDir, String format, final String commonFileSuffix, final int max) {
    try {
      File dir = new File(fileDir);
      if (!dir.exists()) {
        return;
      }
      int currentNum = 0;
      if (dir.exists()) {
        //得到当前文件夹文件数目
        currentNum = dir.listFiles().length;
      }
      while (currentNum > max) {
        //删除产生时间最早的一个文件
        //得到文件名的一个map
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Map<Long, String> map = new HashMap<>();
        for (File file1 : dir.listFiles()) {
          String time = file1.getName().replace(commonFileSuffix, "");
          Date date = simpleDateFormat.parse(time);
          map.put(date.getTime(), file1.getName()); //<1233282716409,2018-2-1_log.txt>
        }

        //产生时间最早的文件名
        long lt = Long.valueOf(Collections.min(map.keySet()).toString());
        File f1 = new File(fileDir + map.get(lt));
        if (f1.exists()) {
          f1.delete();
        }
        if (dir.exists()) {
          currentNum = dir.listFiles().length;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 删除保存超过2天的文件
   *
   * @param filePath
   */
  public static void deleteTwoTodayFile(String filePath) {
    try {
      long currentTime = System.currentTimeMillis();
      // 里面输入特定目录
      File file = new File(filePath);
      if (file.isDirectory() && file.exists()) {
        File temp;
        String[] tempList = file.list();
        for (int i = 0; i < tempList.length; i++) {
          if (filePath.endsWith(File.separator)) {
            temp = new File(filePath + tempList[i]);
          } else {
            temp = new File(filePath + File.separator + tempList[i]);
          }
          if (!TextUtils.isEmpty(temp.getName())) {
            SimpleDateFormat df = new SimpleDateFormat(MainActivity.DATA_STYLE);
            long fileTime = df.parse(temp.getName()).getTime();
            if (temp.isFile() && currentTime - fileTime > 48 * 60 * 60 * 1000) {
              temp.delete();
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 先根遍历序递归删除文件夹
   *
   * @param dirFile 要被删除的文件或者目录
   * @return 删除成功返回true, 否则返回false
   */
  public static boolean deleteFile(File dirFile) {
    // 如果dir对应的文件不存在，则退出
    if (!dirFile.exists()) {
      return false;
    }

    if (dirFile.isFile()) {
      return dirFile.delete();
    } else {

      for (File file : dirFile.listFiles()) {
        deleteFile(file);
      }
    }

    return dirFile.delete();
  }


  /**
   * 加载图片
   *
   * @param context
   * @param url
   * @param imageView
   */
  public static void loadImage(Context context, String url, ImageView imageView, int placeholder, int errorPlaceholder) {
    Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView);
  }

  /**
   * 加载图片
   *
   * @param context
   * @param filePath
   * @param imageView
   */
  public static void loadImageFile(Context context, String filePath, ImageView imageView, int placeholder, int errorPlaceholder) {
    Glide.with(context)
            .load(new File(filePath))
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView);
  }

  /**
   * 加载图片 优先级123
   * 1.加载本地图片
   * 2.加载网络图片
   * 3.加载默认图
   *
   * @param context
   * @param url
   * @param filePath
   * @param imageView
   * @param placeholder
   */
  public static void loadImage(Context context, String url, String filePath, ImageView imageView, int placeholder) {
    if (TextUtils.isEmpty(filePath) || filePath.endsWith("portrait.png")) {
      if (TextUtils.isEmpty(url)) {
        imageView.setImageResource(placeholder);
      } else {
        loadImage(context, url, imageView, 0, placeholder);
      }
    } else {
      loadImageFile(context, filePath, imageView, 0, 0);
    }
  }

  public static void fullScreen(Window window) {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_FULLSCREEN;
    window.getDecorView().setSystemUiVisibility(uiOptions);
  }
}

