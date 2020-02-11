package viroyal.com.base.widget.smart.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import viroyal.com.base.util.Utils;


/**
 * @author chenjunwei
 * @date 2019/1/29
 */
public class RVBaseViewHolder extends RecyclerView.ViewHolder {
  public View itemView;
  public SparseArray<View> mView;

  public RVBaseViewHolder(View itemView) {
    super(itemView);
    this.itemView = itemView;
    mView = new SparseArray<>();
  }

  public <T extends View> T retrieveView(int id) {
    View view = mView.get(id);
    if (view == null) {
      view = itemView.findViewById(id);
      mView.put(id, view);
    }
    return (T) view;
  }

  public RVBaseViewHolder setTextView(int id, String text) {
    TextView textView = retrieveView(id);
    if (null != textView) {
      if (Utils.isEmpty(text)) {
        textView.setText("");
      } else {
        textView.setText(text);
      }
    }
    return this;
  }

  public RVBaseViewHolder setTextViewVisible(int id, String text) {
    TextView textView = retrieveView(id);
    Utils.setText(textView, text);
    return this;
  }

  public RVBaseViewHolder setTextView(int id, String text, int color) {
    TextView textView = retrieveView(id);
    textView.setText(text);
    textView.setTextColor(color);
    return this;
  }

  public RVBaseViewHolder setTexColor(int id, int color) {
    TextView textView = retrieveView(id);
    if (null != textView) {
      textView.setTextColor(color);
    }
    return this;
  }

//    /**
//     * @param id
//     * @param url
//     * @param width
//     * @param height
//     * @return 默认图为 loading_50
//     */
//    public RVBaseViewHolder setImageView(int id, String url, int width, int height) {
//        return setImageView(id, url, width, height, 0);
//    }
//
//    /**
//     * @param id
//     * @param url
//     * @param width
//     * @param height
//     * @param default_loading_pic
//     * @return
//     */
//    public RVBaseViewHolder setImageView(int id, String url, int width, int height, int default_loading_pic) {
//        ImageView imageView = retrieveView(id);
//        if (null != imageView && null != itemView) {
//            Glide.with(itemView.getContext()).asBitmap().load(url).
//            ImageLoaderUtils.loadingImg(itemView.getContext(), url, imageView, default_loading_pic,
//                    width, height);
//        }
//        return this;
//    }

  /**
   * 设置ImageResource
   *
   * @param id
   * @param resource
   * @return
   */
  public RVBaseViewHolder setImageResource(int id, int resource) {
    ImageView imageView = retrieveView(id);
    if (null != imageView && null != itemView) {
      imageView.setImageResource(resource);
    }
    return this;
  }

  /**
   * 设置布局的显示状态
   *
   * @param id       R.id.
   * @param visiable View.VISIBLE | View.GONE | View.INVISIBLE
   * @return
   */
  public RVBaseViewHolder setVisibility(int id, int visiable) {
    View view = retrieveView(id);
    Utils.setVisibility(view, visiable);
    return this;
  }

  public RVBaseViewHolder setVisibiity(int id, boolean visiable) {
    View view = retrieveView(id);
    if (view != null)
      view.setVisibility(visiable ? View.VISIBLE : View.GONE);
    return this;
  }

  public RVBaseViewHolder setInVisibiity(int id, boolean visiable) {
    View view = retrieveView(id);
    if (view != null)
      view.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
    return this;
  }

  /**
   * 设置布局的显示状态
   *
   * @param id   R.id.
   * @param show true  |  false
   * @return
   */
  public RVBaseViewHolder setVisibility(int id, boolean show) {
    View view = retrieveView(id);
    Utils.setVisibility(view, show ? View.VISIBLE : View.GONE);
    return this;
  }

  public RVBaseViewHolder setVisibility(boolean visiable, Integer... ids) {
    for (int i = 0; i < ids.length; i++) {
      View view = retrieveView(ids[i]);
      if (view != null) {
        view.setVisibility(visiable ? View.VISIBLE : View.GONE);
      }
    }
    return this;
  }

  /**
   * checkBox 选中状态
   *
   * @param id
   * @param check
   * @return
   */
  public RVBaseViewHolder checkBox(int id, boolean check) {
    CheckBox checkBox = retrieveView(id);
    if (null != checkBox) {
      checkBox.setChecked(check);
    }
    return this;
  }

}
