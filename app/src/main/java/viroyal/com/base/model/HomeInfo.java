package viroyal.com.base.model;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

import java.util.List;

/**
 * Created by qjj on 2017/9/12.
 */

@Table(name = "home")
public class HomeInfo {
    @Id(column = "deviceid")
    public String id;
    @SerializedName("school_name")
    @Property(column = "schoolname")
    public String schoolname;       //学校名称
    @SerializedName("class_name")
    @Property(column = "classname")
    public String classname;        //班级、教室名称
    @SerializedName("lunarcalendar")
    @Property(column = "lunarcalendar")
    public String lunarcalendar;    //农历
    @SerializedName("weather")
    @Property(column = "weather")
    public String weather;          //天气
    @SerializedName("temperature")
    @Property(column = "temperature")
    public String temperature;      //温度
    @SerializedName("holiday")
    @Property(column = "holiday")
    public String holiday;          //节日
    @SerializedName("class_motto")
    @Property(column = "classmotto")
    public String classmotto;       //班训
    @SerializedName("school_badge")
    @Property(column = "schoolimg")
    public String schoolimg;        //校徽
    @SerializedName("class_badge")
    @Property(column = "classimg")
    public String classimg;        //班徽
    @SerializedName("password")
    @Property(column = "password")
    public String password;        //密码

    @SerializedName("class_star")
    @Property(column = "class_star")
    public String class_star;        //班级之星

    @SerializedName("class_object")
    @Property(column = "class_object")
    public String class_object;        //班级目标

    @SerializedName("volumes")
    public List<Volume> volumes;

    public void update(HomeInfo item){
        this.schoolname = item.schoolname;
        this.classname = item.classname;
        this.lunarcalendar = item.lunarcalendar;
        this.weather = item.weather;
        this.temperature = item.temperature;
        this.holiday = item.holiday;
        this.classmotto = item.classmotto;
        this.schoolimg = item.schoolimg;
        this.classimg = item.classimg;
        this.password = item.password;
    }
}
