package viroyal.com.base.activity.splash;

import android.os.Parcel;

import com.suntiago.baseui.activity.base.theMvp.model.IModel;

/**
 * Created by zy on 2019/1/25.
 */


public class SplashModel implements IModel {
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

  }

  private SplashModel(Parcel in) {
  }

  public SplashModel() {
  }

  public static final Creator<SplashModel> CREATOR = new Creator<SplashModel>() {
    @Override
    public SplashModel createFromParcel(Parcel in) {
      return new SplashModel(in);
    }

    @Override
    public SplashModel[] newArray(int size) {
      return new SplashModel[size];
    }
  };
}
