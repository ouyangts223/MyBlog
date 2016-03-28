package cn.coselding.myblog;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 宇强 on 2016/3/19 0019.
 */
public class CommentNoticeListener implements ServletContextListener {

    private Timer timer = new Timer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.print("Test");
            }
        },0,1000*3600*12);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
