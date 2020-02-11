package viroyal.com.base.model;

/**
 * Created by chuxiao on 2019/5/7.
 */

public class Weather {
  public AreaToWeather area_to_weather;

  public class AreaToWeather {
    public Body showapi_res_body;

    public class Body {
      public Today f1;
      public Tomorrow f2;
      public Thirdday f3;
      public Newest now;

      public class Newest {
        public String wind_direction;
        public String wind_power;
        public Detail aqiDetail;
        public class Detail {
          public String quality;
        }

      }

      public class Today {
        public String day_weather;
        public String night_weather;
        public String night_air_temperature;
        public String day_air_temperature;

      }

      public class Tomorrow {
        public String day_weather;
        public String night_weather;
        public String night_air_temperature;
        public String day_air_temperature;

      }

      public class Thirdday {
        public String day_weather;
        public String night_weather;
        public String night_air_temperature;
        public String day_air_temperature;

      }
    }
  }
}
