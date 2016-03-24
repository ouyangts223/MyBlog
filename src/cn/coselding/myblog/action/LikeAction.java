package cn.coselding.myblog.action;

import cn.coselding.myblog.service.impl.VisitorServiceImpl;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

/**
 * Created by 宇强 on 2016/3/13 0013.
 */
public class LikeAction extends ActionSupport{

    private int artid;

    public int getArtid() {
        return artid;
    }

    public void setArtid(int artid) {
        this.artid = artid;
    }

    @Override
    public String execute() throws Exception {
        //刷新数据库
        VisitorServiceImpl service = new VisitorServiceImpl();
        int likes =  service.likeArticle(artid);

        //获取请求前的页面
        String referer = ServletActionContext.getRequest().getHeader("referer");
        //跳转回原页面
        ServletActionContext.getResponse().sendRedirect(referer);
        return null;
    }
}
