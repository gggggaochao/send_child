package viroyal.com.base.net.rsp;

import com.google.gson.annotations.SerializedName;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.List;

import viroyal.com.base.model.Food;

/**
 * Created by viroyal on 2017/11/28.
 */

public class FoodResponseS extends BaseResponse {
    @SerializedName("extra")
    public List<Food> foods;
}
