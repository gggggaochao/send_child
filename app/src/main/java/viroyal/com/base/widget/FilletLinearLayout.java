package viroyal.com.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.aiwinn.base.util.SizeUtils;

/**
 * @author chenjunwei
 * @desc 圆角FilletFrameLayout
 * @date 2019/5/22
 */
public class FilletLinearLayout extends LinearLayout {
  private int radius = SizeUtils.dp2px(10);

  public FilletLinearLayout(@NonNull Context context) {
    super(context);
  }

  public FilletLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FilletLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    Path path = new Path();
    path.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), radius, radius, Path.Direction.CW);
    canvas.clipPath(path, Region.Op.REPLACE);
    super.dispatchDraw(canvas);
  }
}
