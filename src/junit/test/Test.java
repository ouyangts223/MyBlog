package junit.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 宇强 on 2016/3/14 0014.
 */
public class Test {

    public static void main(String[] args){
        /*
        Pattern pattern = Pattern.compile("/blog/([0-9]+)/([0-9]+)-([0-9]+)");
        Matcher matcher = pattern.matcher("http://coselding.cn/MyBlog/blog/1/3-124.html");
        System.out.println(matcher.matches());
        System.out.println(matcher.find());
        System.out.println(matcher.group(0));
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
        */
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        System.out.print(format.format(date));
    }
}
