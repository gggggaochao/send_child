package viroyal.com.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.demono.adapter.InfinitePagerAdapter;

import java.util.List;

import viroyal.com.base.R;
import viroyal.com.base.model.Food;


/**
 * @author chenjunwei
 * @desc 通知
 * @date 2019/5/22
 */
public class ItemFoodPagerAdapter extends InfinitePagerAdapter {
  private Context context;
  private List<List<Food>> sortAllFood;

  public ItemFoodPagerAdapter(List<List<Food>> sortAllFood, Context context) {
    this.context = context;
    this.sortAllFood = sortAllFood;
  }

  @Override
  public int getItemCount() {
    //无限轮播
    return null == sortAllFood ? 0 : sortAllFood.size();
  }

  @Override
  public View getItemView(int position, View convertView, ViewGroup container) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_food_layout, container, false);
    RecyclerView recyclerView = view.findViewById(R.id.rv_food_list);
    if (null != sortAllFood && sortAllFood.size() > 0) {
      List<Food> foodList = sortAllFood.get(position);
      ItemFoodAdapter mFoodAdapter = new ItemFoodAdapter(context, foodList);
      recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
      recyclerView.setAdapter(mFoodAdapter);
    }
    return view;
  }

  @Override
  public int getItemPosition(@NonNull Object object) {
    return POSITION_NONE;
  }
}
