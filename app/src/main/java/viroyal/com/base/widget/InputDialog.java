package viroyal.com.base.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import viroyal.com.base.R;


public class InputDialog extends BaseDialogFragment {
  private final static String EXTRA_SHOW_BOTTOM = "show_bottom";
  TextView number_show;
  LinearLayout zero_d_layout;
  StringBuffer mNumber = new StringBuffer();
  int mLenght = 8;
  int mType = 0;
  private Listener mListener;

  public static InputDialog newInstance(Listener listener, String number, int type) {
    InputDialog f = new InputDialog();
    Bundle b = new Bundle();
    f.setArguments(b);
    f.mListener = listener;
    f.mType = type;
    f.mNumber.append(number);
    return f;
  }

  /**
   * 设置dialog的显示位置
   *
   * @param showBottom true 在底部显示 false 在中间显示
   */
  public InputDialog setShowBottom(boolean showBottom) {
    getArguments().putBoolean(EXTRA_SHOW_BOTTOM, showBottom);
    return this;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    boolean showBottom = getArguments().getBoolean(EXTRA_SHOW_BOTTOM, false);
    Window window = getDialog().getWindow();
    if (showBottom && null != window) {
      WindowManager.LayoutParams layoutParams = window.getAttributes();
      layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
      layoutParams.dimAmount = 0.5f;
      window.setGravity(Gravity.BOTTOM);
      window.setAttributes(layoutParams);
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  protected View getContentView() {
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_layout, null);
    MyOnClickListener listener = new MyOnClickListener();
    Button one_num = view.findViewById(R.id.one_num);
    Button two_num = view.findViewById(R.id.two_num);
    Button three_num = view.findViewById(R.id.three_num);
    Button four_num = view.findViewById(R.id.four_num);
    Button five_num = view.findViewById(R.id.five_num);
    Button six_num = view.findViewById(R.id.six_num);
    Button seven_num = view.findViewById(R.id.seven_num);
    Button eight_num = view.findViewById(R.id.eight_num);
    Button nine_num = view.findViewById(R.id.nine_num);
    Button zero_num = view.findViewById(R.id.zero_num);
    Button del_num = view.findViewById(R.id.del_num);
    Button ok_num = view.findViewById(R.id.ok_num);
    Button zero_d = view.findViewById(R.id.zero_d);
    zero_d_layout =  view.findViewById(R.id.zero_d_layout);
    number_show = view.findViewById(R.id.number_show);
    if (mType == 5) {
      zero_d_layout.setVisibility(View.VISIBLE);
      zero_d.setText("-");
      mLenght = 32;
    }
    if (mType == 4) {
      zero_d_layout.setVisibility(View.GONE);
      mLenght = 32;
    } else if (mType == 1) {
      zero_d_layout.setVisibility(View.GONE);
      mLenght = 8;
    } else {
      zero_d_layout.setVisibility(View.VISIBLE);
      mLenght = 32;
    }
    number_show.setText(mNumber.toString());
    one_num.setOnClickListener(listener);
    two_num.setOnClickListener(listener);
    three_num.setOnClickListener(listener);
    four_num.setOnClickListener(listener);
    five_num.setOnClickListener(listener);
    six_num.setOnClickListener(listener);
    seven_num.setOnClickListener(listener);
    eight_num.setOnClickListener(listener);
    nine_num.setOnClickListener(listener);
    zero_num.setOnClickListener(listener);
    del_num.setOnClickListener(listener);
    ok_num.setOnClickListener(listener);
    zero_d.setOnClickListener(listener);
    return view;
  }


  public interface Listener {
    void onNumberSelected(String item);

    void ok(String item);
  }

  class MyOnClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      {
        if (v.getId() == R.id.ok_num) {
          mListener.ok(mNumber.toString());
          dismiss();
        } else {
          if (mNumber.length() < mLenght) {
            if (v.getId() == R.id.one_num) {
              mNumber.append(1);
            }
            if (v.getId() == R.id.two_num) {
              mNumber.append(2);
            }
            if (v.getId() == R.id.three_num) {
              mNumber.append(3);
            }
            if (v.getId() == R.id.four_num) {
              mNumber.append(4);
            }
            if (v.getId() == R.id.five_num) {
              mNumber.append(5);
            }
            if (v.getId() == R.id.six_num) {
              mNumber.append(6);
            }
            if (v.getId() == R.id.seven_num) {
              mNumber.append(7);
            }
            if (v.getId() == R.id.eight_num) {
              mNumber.append(8);
            }
            if (v.getId() == R.id.nine_num) {
              mNumber.append(9);
            }
            if (v.getId() == R.id.zero_num) {
              mNumber.append(0);
            }
            if (mType != 1) {
              if (v.getId() == R.id.zero_d) {
                if (mType == 2) {
                  if (mNumber.indexOf(".") > -1) {

                  } else {
                    if (mNumber.length() > 0) {
                      mNumber.append(".");
                    }
                  }
                } else if (mType == 5) {
                  mNumber.append("-");
                } else {
                  if (mNumber.length() > 0) {
                    mNumber.append(".");
                  }
                }
              }
            }

          }
          if (v.getId() == R.id.del_num) {
            if (mNumber.length() > 0) {
              mNumber.deleteCharAt(mNumber.length() - 1);
            }
          }

          mListener.onNumberSelected(mNumber.toString());
          number_show.setText(mNumber.toString());
        }
      }
    }
  }

}
