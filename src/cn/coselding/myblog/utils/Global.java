package cn.coselding.myblog.utils;

import cn.coselding.myblog.domain.Article;
import cn.coselding.myblog.domain.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 宇强 on 2016/3/12 0012.
 */
public class Global {
    //订阅缓存
    public static final int RSS_TRUE = 1;
    public static final int RSS_FALSE = 0;

    //类别
    private static boolean categories_cached = false;
    public static boolean isCategories_cached() {
        return categories_cached;
    }
    public static void setCategories_cached(boolean categories_cached) {
        Global.categories_cached = categories_cached;
    }
    private static List<Category> categories = new ArrayList<Category>();
    public static List<Category> getCategories() {
        return categories;
    }
    public static void setCategories(List<Category> categories) {
        Global.categories = categories;
    }

    //最新文件
    private static boolean isLast = false;
    public static boolean isIsLast() {
        return isLast;
    }
    public static void setIsLast(boolean isLast) {
        Global.isLast = isLast;
    }
    private static List<Article> lastArticles = new ArrayList<Article>();
    public static List<Article> getLastArticles() {
        return lastArticles;
    }
    public static void setLastArticles(List<Article> lastArticles) {
        lastArticles = lastArticles;
    }

    //网页标题
    public static final Map<String,String> pageTitles = new HashMap<String, String>();
    static {

    }
}
