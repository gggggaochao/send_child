package viroyal.com.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.dblib.DBUpdateHelper;

/**
 * Created by chuxiao on 2019/5/28.
 */

public class KJDB_UpdateHelper extends DBUpdateHelper {

  private final String TAG = getClass().getSimpleName();
  static final int DATABASE_VERSION = 2;

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    super.onUpgrade(db, oldVersion, newVersion);
    Slog.d(TAG, "onUpgrade, old=" + oldVersion + ", new=" + newVersion);
    if (oldVersion < 2) {
      if (tableIsExist(db, "mealtime")) {
        db.execSQL("ALTER TABLE mealtime ADD COLUMN max_meal_times int DEFAULT 0;");
      }
    }
  }

  private boolean tableIsExist(SQLiteDatabase db, String tableName) {
    boolean result = false;
    if (TextUtils.isEmpty(tableName)) {
      return false;
    }
    Cursor cursor;
    try {
      //这里表名可以是Sqlite_master
      String sql = "select count(*) as c from mealtime where type ='table' and name ='" + tableName + "'";
      cursor = db.rawQuery(sql, null);
      if (cursor.moveToNext()) {
        int count = cursor.getInt(0);
        if (count > 0) {
          result = true;
        }
      }

    } catch (Exception e) {
      // TODO: handle exception
    }
    return result;
  }
}
