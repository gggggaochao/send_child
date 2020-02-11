package viroyal.com.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.suntiago.baseui.utils.ScreenUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.model.Food;


/**
 * @author chenjunwei
 * @desc 菜谱列表
 * @date 2019/5/22
 */
public class ItemFoodAdapter extends BaseItemDraggableAdapter<Food> {
  private String[] week;
  private int weekDay;
  private int text_color_food_item;
  private int foodHeight;

  public ItemFoodAdapter(Context context, List<Food> data) {
    super(R.layout.item_food_sub_layout, data);
    week = context.getResources().getStringArray(R.array.week_array);
    text_color_food_item = context.getResources().getColor(R.color.text_color_food_item);
    weekDay = getWeek();
    int height = ScreenUtils.getScreenHeight(context) + ScreenUtils.getDaoHangHeight(context);
    foodHeight = (int) (height * 0.24);
  }

  @Override
  protected void convert(BaseViewHolder helper, Food item) {
    helper.getView(R.id.ll_item_food).setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, (int) (foodHeight * 0.173)));
    helper.setText(R.id.tv_item_food_name, item.name);
    int position = helper.getAdapterPosition();
    helper.setText(R.id.tv_item_week, week[position % getItemCount()]);
    if (item.week == weekDay) {
      helper.setTextColor(R.id.tv_item_food_name, Color.RED);
      helper.setTextColor(R.id.tv_item_week, Color.RED);
    } else {
      helper.setTextColor(R.id.tv_item_food_name, text_color_food_item);
      helper.setTextColor(R.id.tv_item_week, text_color_food_item);
    }
    if (1 == position % 2) {
      helper.setBackgroundRes(R.id.ll_item_food, R.color.color_food_item_even);
    } else {
      helper.setBackgroundRes(R.id.ll_item_food, R.color.color_food_item_odd);
    }
  }

  public int getWeek() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(System.currentTimeMillis()));
    int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (week == 0) {
      week = 7;
    }
    return week;
  }
}
