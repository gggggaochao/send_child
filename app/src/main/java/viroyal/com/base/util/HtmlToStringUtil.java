package viroyal.com.base.util;

import android.text.Html;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html转string
 */
public class HtmlToStringUtil {

  public static String getHtmlContent(String content) {
    if(TextUtils.isEmpty(content)){
      return "";
    }
    CharSequence charSequence;
    //匹配img标签的正则表达式并且过滤
    String regxpForImgTag = "<img[^>]*>";
    Pattern pattern = Pattern.compile(regxpForImgTag);
    Matcher matcher = pattern.matcher(content);
    while (matcher.find()) {
      String temp = matcher.group();
      String urlResult = "";
      content = content.replace(temp, urlResult);
    }
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

      charSequence = Html.fromHtml(content,Html.FROM_HTML_MODE_COMPACT, null, null);
    } else {
      charSequence = Html.fromHtml(content);
    }

    return charSequence.toString().trim();
  }

  /**
   * 去除换行空格
   * @param src
   * @return
   */
  public static String replaceBlank(String src) {
    String dest = "";
    if (src != null) {
      Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
      Matcher matcher = pattern.matcher(src);
      dest = matcher.replaceAll("");
    }
    return dest;
  }
}
