package viroyal.com.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntiago.a.WeatherUtil;

import viroyal.com.base.R;
import viroyal.com.base.model.Weather;

public class WeatherPopwindow {

    public Context context;
    private PopupWindow popupWindow;

    public WeatherPopwindow(Context context) {
        this.context = context;
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void initCalendar(Weather weatherResponse) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.popup_weather,null,false);
        popupWindow = new PopupWindow(inflate,600,200, false);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.bg_pop_weather));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        TextView tvTodayMin = (TextView) inflate.findViewById(R.id.tv_today_min);
        TextView tvTomorrowMin = (TextView) inflate.findViewById(R.id.tv_tomorrow_min);
        TextView tvThirdDayMin = (TextView) inflate.findViewById(R.id.tv_third_day_min);
        TextView tvTodayMax = (TextView) inflate.findViewById(R.id.tv_today_max);
        TextView tvTomorrowMax = (TextView) inflate.findViewById(R.id.tv_tomorrow_max);
        TextView tvThirdDayMax = (TextView) inflate.findViewById(R.id.tv_third_day_max);
        ImageView ivToday = (ImageView) inflate.findViewById(R.id.iv_weather_today);
        ImageView ivTomorrow = (ImageView) inflate.findViewById(R.id.iv_weather_tomorrow);
        ImageView ivThirdDay = (ImageView) inflate.findViewById(R.id.iv_weather_third_day);
        if(weatherResponse != null) {
            Weather.AreaToWeather.Body body = weatherResponse.area_to_weather.showapi_res_body;
            tvTodayMin.setText(body.f1.night_air_temperature);
            tvTodayMax.setText(body.f1.day_air_temperature);
            tvTomorrowMin.setText(body.f2.night_air_temperature);
            tvTomorrowMax.setText(body.f2.day_air_temperature);
            tvThirdDayMin.setText(body.f3.night_air_temperature);
            tvThirdDayMax.setText(body.f3.day_air_temperature);
            if (WeatherUtil.map.containsKey(body.f1.day_weather)) {
                ivToday.setImageResource(WeatherUtil.get(body.f1.day_weather));
            }
            if (WeatherUtil.map.containsKey(body.f2.day_weather)) {
                ivTomorrow.setImageResource(WeatherUtil.get(body.f2.day_weather));
            }
            if (WeatherUtil.map.containsKey(body.f3.day_weather)) {
                ivThirdDay.setImageResource(WeatherUtil.get(body.f3.day_weather));
            }
        }
    }

    //展示天气
    public void showWeather(View anchor) {
        //调整popwindow的位置
        popupWindow.showAsDropDown(anchor,0,0);
    }
}
