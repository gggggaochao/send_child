package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import viroyal.com.base.model.Weather;

public class WeatherResponseS extends BaseResponse {
    @SerializedName("extra")
    public Weather extra;
}
