package viroyal.com.base.activity.broadcast;

import android.os.Parcel;
import android.os.Parcelable;

import com.suntiago.baseui.activity.base.theMvp.model.IModel;

/**
 * @author chenjunwei
 * @desc 广告model
 * @date 2019/4/23
 */
public class BroadcastModel implements IModel {

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }

  public BroadcastModel() {
  }

  protected BroadcastModel(Parcel in) {
  }

  public static final Parcelable.Creator<BroadcastModel> CREATOR = new Parcelable.Creator<BroadcastModel>() {
    @Override
    public BroadcastModel createFromParcel(Parcel source) {
      return new BroadcastModel(source);
    }

    @Override
    public BroadcastModel[] newArray(int size) {
      return new BroadcastModel[size];
    }
  };
}
