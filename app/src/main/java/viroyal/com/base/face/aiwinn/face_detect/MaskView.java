package viroyal.com.base.face.aiwinn.face_detect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.suntiago.baseui.utils.log.Slog;

import java.io.InputStream;
import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.face.aiwinn.common.AttConstants;

import static viroyal.com.base.activity.main.MainDelegate.TAG_DEBUG_FACE;


/**
 * com.aiwinn.facelock.widget.camera
 * 1217/08/05
 * Created by LeoLiu on User.
 */

@SuppressLint("AppCompatCustomView")
public class MaskView extends ImageView {

  private final String TAG = getClass().getSimpleName();

  List<FaceBean> mFaceBeans;
  boolean mDraw = false;
  boolean mLandScape = true;
  int hBar = 0;
  int widthScreen, heightScreen;
  int widthPreview, heightPreview;
  Bitmap mRedRectBitmap;
  Bitmap mBlueRectBitmap;
  Bitmap mGreenRectBitmap;
  Rect mRedSrcRec;
  Rect mBlueSrcRec;
  Rect mGreenSrcRec;
  Paint mPaint;
  TextPaint mTextPaint;
  Rect rect = new Rect();
  Paint paint = new Paint();
  Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

  public MaskView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initPaint();
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      //根据资源ID获取响应的尺寸值
      hBar = getResources().getDimensionPixelSize(resourceId);
    }
    mDraw = false;
    mLandScape = ScreenUtils.isLandscape();
    widthScreen = ScreenUtils.getScreenWidth();
    heightScreen = ScreenUtils.getScreenHeight();
    mRedRectBitmap = decodeBitmapResource(getResources(), R.drawable.ic_red_rect);
    mBlueRectBitmap = decodeBitmapResource(getResources(), R.drawable.ic_blue_rect);
    mGreenRectBitmap = decodeBitmapResource(getResources(), R.drawable.ic_green_rect);
    mRedSrcRec = new Rect(0, 0, mRedRectBitmap.getWidth(), mRedRectBitmap.getHeight());
    mBlueSrcRec = new Rect(0, 0, mBlueRectBitmap.getWidth(), mBlueRectBitmap.getHeight());
    mGreenSrcRec = new Rect(0, 0, mGreenRectBitmap.getWidth(), mGreenRectBitmap.getHeight());
  }

  private void initPaint() {

    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setColor(Color.WHITE);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(6f);
    mPaint.setAlpha(180);

    mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setColor(getResources().getColor(R.color.white));
    mTextPaint.setStyle(Paint.Style.FILL);
    mTextPaint.setTextSize(80);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

  }

  public void drawRect(List<FaceBean> faceInfoExes, int width, int height) {
    Slog.d(TAG_DEBUG_FACE, "start drawRect...");
//    widthPreview = width;
//    heightPreview = height;
    mFaceBeans = faceInfoExes;
    mDraw = true;
    postInvalidate();
  }

  public void clearRect() {
    if (mDraw) {
      mDraw = false;
    }
    this.invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    widthScreen = MeasureSpec.getSize(widthMeasureSpec);
    heightScreen = MeasureSpec.getSize(heightMeasureSpec);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
//    Slog.d(TAG_DEBUG_FACE, "start onDraw mDraw:" + mDraw);
    if (!mDraw) {
      paint.setXfermode(xfermode);
      canvas.drawPaint(paint);
    } else {
      for (FaceBean faceBean : mFaceBeans) {
        if (faceBean != null) {
          LogUtils.d(TAG, "RecognitionFace face [ maskview ] name = " + faceBean.mUserBean.name + ", flag = " + faceBean.mDetectBean.flag + ", live tag = " + faceBean.mLiveBean.livenessTag);

//          LogUtils.d(TAG, "RecognitionFace face [ maskview ] widthScreen = " + widthScreen);
//          LogUtils.d(TAG, "RecognitionFace face [ maskview ] heightScreen = " + heightScreen);
//
//          LogUtils.d(TAG, "RecognitionFace face [ maskview ] widthPreview = " + widthPreview);
//          LogUtils.d(TAG, "RecognitionFace face [ maskview ] heightPreview = " + heightPreview);

          float _x0 = faceBean.mDetectBean.x0;
          float _y0 = faceBean.mDetectBean.y0;
          float _x1 = faceBean.mDetectBean.x1;
          float _y1 = faceBean.mDetectBean.y1;

//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] _x0 = " + _x0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] _y0 = " + _y0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] _x1 = " + _x1);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] _y1 = " + _y1);

          if (mLandScape) {

            double scale_x = ((double) widthScreen / widthPreview);
            double scale_y = ((double) heightScreen / heightPreview);

//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] scale_x = " + scale_x);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] scale_y = " + scale_y);

            int x0 = (int) (_x0 * scale_x);
            int y0 = (int) (_y0 * scale_y);
            int x1 = (int) (_x1 * scale_x);
            int y1 = (int) (_y1 * scale_y);

//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] x0 = " + x0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] y0 = " + y0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] x1 = " + x1);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] y1 = " + y1);

            if (AttConstants.LEFT_RIGHT) {
              int x0b = widthScreen - x1;
              int x1b = widthScreen - x0;
              x0 = x0b;
              x1 = x1b;
            }

            if (AttConstants.TOP_BOTTOM) {
              int y0b = heightScreen - y1;
              int y1b = heightScreen - y0;
              y0 = y0b;
              y1 = y1b;
            }

            rect.set(x0, y0, x1, y1);

          } else {

            double scale_x = ((double) widthScreen / heightPreview);
            double scale_y = ((double) heightScreen / widthPreview);

//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] scale_x = " + scale_x);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] scale_y = " + scale_y);

            int x0 = (int) (_x0 * scale_x);
            int y0 = (int) (_y0 * scale_y);
            int x1 = (int) (_x1 * scale_x);
            int y1 = (int) (_y1 * scale_y);

//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] x0 = " + x0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] y0 = " + y0);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] x1 = " + x1);
//                    LogUtils.d(TAG, "RecognitionFace face [ maskview ] y1 = " + y1);

            if (AttConstants.LEFT_RIGHT) {
              int x0b = widthScreen - x1;
              int x1b = widthScreen - x0;
              x0 = x0b;
              x1 = x1b;
            }

            if (AttConstants.TOP_BOTTOM) {
              int y0b = heightScreen - y1;
              int y1b = heightScreen - y0;
              y0 = y0b;
              y1 = y1b;
            }

            rect.set(x0, y0, x1, y1);

          }

//                    if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.FAKE) {
//                        canvas.drawBitmap(mRedRectBitmap, mRedSrcRec, rect, mPaint);
//                        mTextPaint.setColor(getResources().getColor(R.color.rect_red));
//                        faceBean.mUserBean.name = "FAKE";
//                        LogUtils.d(TAG, "draw [ maskview ] RED");
//                    }else if(faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.LIVE){
//                        canvas.drawBitmap(mGreenRectBitmap, mGreenSrcRec, rect, mPaint);
//                        mTextPaint.setColor(getResources().getColor(R.color.rect_green));
//                        LogUtils.d(TAG, "draw [ maskview ] GREEN");
//                    }else {
//                        mTextPaint.setColor(getResources().getColor(R.color.rect_blue));
//                        canvas.drawBitmap(mBlueRectBitmap, mBlueSrcRec, rect, mPaint);
//                        LogUtils.d(TAG, "draw [ maskview ] BLUE");
//                    }

          if ((ConfigLib.detectWithLiveness || ConfigLib.detectWithInfraredLiveness) && AttConstants.DEBUG) {
            if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.FAKE) {
              if (mRedRectBitmap != null) {
                canvas.drawBitmap(mRedRectBitmap, mBlueSrcRec, rect, mPaint);
              }
              mTextPaint.setColor(getResources().getColor(R.color.rect_red));
              LogUtils.d(TAG, "draw [ maskview ] RED");
            } else if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.LIVE) {
              if (mRedRectBitmap != null) {
                canvas.drawBitmap(mGreenRectBitmap, mBlueSrcRec, rect, mPaint);
              }
              mTextPaint.setColor(getResources().getColor(R.color.rect_green));
              LogUtils.d(TAG, "draw [ maskview ] GREEN");
            } else {
              mTextPaint.setColor(getResources().getColor(R.color.rect_blue));
              if (mRedRectBitmap != null) {
                canvas.drawBitmap(mBlueRectBitmap, mBlueSrcRec, rect, mPaint);
              }
              LogUtils.d(TAG, "draw [ maskview ] UNKNOWN");
            }
          } else {
            mTextPaint.setColor(getResources().getColor(R.color.rect_blue));
            if (mRedRectBitmap != null) {
              canvas.drawBitmap(mBlueRectBitmap, mBlueSrcRec, rect, mPaint);
            }
            LogUtils.d(TAG, "draw [ maskview ] BLUE");
          }

          Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
          mTextPaint.setTextSize(80);
//                    int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
          canvas.drawText(faceBean.mUserBean.name == null ? "" : faceBean.mUserBean.name, rect.centerX(), rect.top - 20, mTextPaint);
          mTextPaint.setTextSize(30);
          String f = "";
          if (faceBean.mDetectBean != null && AttConstants.DEBUG) {
            f += "ID : " + faceBean.mDetectBean.id;
            f += getResources().getString(R.string.rect) + faceBean.mDetectBean.getFaceWidth() + "~" + faceBean.mDetectBean.getFaceHeight();
            f += "\r\n" + getResources().getString(R.string.blur) + faceBean.mDetectBean.blur + " NetBlur:" + faceBean.mDetectBean.netBlur;
            f += "  Cs : " + faceBean.mDetectBean.coverStatus;
            f += " ";
            f += getResources().getString(R.string.light) + faceBean.mDetectBean.light;
            f += "fs : " + faceBean.mDetectBean.fdScore;
          }
          if (faceBean.mUserBean != null && AttConstants.DEBUG) {
            f += "\r\n" + getResources().getString(R.string.score) + faceBean.mUserBean.compareScore;
          }
          if (faceBean.mLiveBean != null && (ConfigLib.detectWithLiveness || ConfigLib.detectWithInfraredLiveness) && AttConstants.DEBUG) {
            String tag = "UNKNOWN";
            if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.FAKE) {
              tag = "FAKE";
            }
            if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.LIVE) {
              tag = "LIVE";
            }
            if (faceBean.mLiveBean.livenessTag == faceBean.mLiveBean.UNKNOWN) {
              tag = "UNKNOWN";
            }
            f += "\r\n" + "TAG : " + tag;
            f += " -> FC : " + faceBean.mLiveBean.fakeCount + " | " + "LC : " + faceBean.mLiveBean.liveCount;
            f += "\r\n" + "LS : " + faceBean.mLiveBean.livenessScore;
            f += "\r\n" + "LS1 : " + faceBean.mLiveBean.livenessScore1;
            if ((ConfigLib.enhanceMode) && (ConfigLib.isAttaMode)) {
              f += "\r\n" + "LT : " + ConfigLib.attackModeLiveThreshold;
            } else {
              f += "\r\n" + "LT : " + ConfigLib.livenessThreshold;
            }

          }
          StaticLayout layout = new StaticLayout(f, mTextPaint, canvas.getWidth(),
                  Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
          canvas.save();
          canvas.translate(rect.centerX(), rect.bottom + 10);
          layout.draw(canvas);
          canvas.restore();
//                    canvas.drawText(wxh, rect.centerX(), rect.bottom+50, mTextPaint);
        }
      }
    }
  }

  /**
   * 加载图片
   *
   * @return Bitmap
   */
  private Bitmap decodeBitmapResource(Resources resources, int id) {
    Bitmap bitmap;
    InputStream is = resources.openRawResource(id);
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inPurgeable = true;
    opts.inInputShareable = true;
    opts.inPreferredConfig = Bitmap.Config.RGB_565;
    bitmap = BitmapFactory.decodeStream(is, null, opts);
    return bitmap;
  }

  public void unInit() {
    if (mRedRectBitmap != null && !mRedRectBitmap.isRecycled()) {
      mRedRectBitmap.recycle();
      mRedRectBitmap = null;
    }
    if (mBlueRectBitmap != null && !mBlueRectBitmap.isRecycled()) {
      mBlueRectBitmap.recycle();
      mBlueRectBitmap = null;
    }
    if (mGreenRectBitmap != null && !mGreenRectBitmap.isRecycled()) {
      mGreenRectBitmap.recycle();
      mGreenRectBitmap = null;
    }
  }
}
