package cn.coselding.myblog.action.manage;

import cn.coselding.myblog.domain.Article;
import cn.coselding.myblog.domain.Category;
import cn.coselding.myblog.domain.Page;
import cn.coselding.myblog.domain.User;
import cn.coselding.myblog.interceptor.LoginInterceptor;
import cn.coselding.myblog.service.impl.ArticleServiceImpl;
import cn.coselding.myblog.utils.TemplateUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletRedirectResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 宇强 on 2016/3/12 0012.
 */
public class ArticleManager extends ActionSupport {

    private String title;
    private int cid;
    private String type;
    private String content;
    private int artid;
    private String method;
    private String time;
    private Date date;
    private String pagenum;
    private int top;
    private String meta ;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getPagenum() {
        return pagenum;
    }

    public void setPagenum(String pagenum) {
        this.pagenum = pagenum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getArtid() {
        return artid;
    }

    public void setArtid(int artid) {
        this.artid = artid;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //表单校验
    @Override
    public void validate() {
        if (method == null)
            return;
        else if (method.equals("add") || method.equals("update")) {
            if (title == null || title.trim().equals("")) {
                this.addFieldError("title", "博文标题不能为空！");
            }
            if (type == null || type.trim().equals("")) {
                this.addFieldError("type", "博文原创/转载不能为空！！！");
            }
            if (content == null || content.trim().equals("")) {
                this.addFieldError("content", "博文内容不能为空！");
            }
            time = time.replace("T", " ");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                date = format.parse(time);
            } catch (ParseException e) {
                this.addFieldError("time", "时间格式不正确");
            }
            //数据回显
            if (this.getFieldErrors().size() > 0) {
                Article article = new Article();
                article.setShowtime(time.replace(" ", "T"));
                article.setTitle(title);
                article.setType(type);
                article.setContent(content);
                article.setCid(cid);
                HttpServletRequest request = ServletActionContext.getRequest();
                request.setAttribute("categories", new ArticleServiceImpl().getAllCategories());
                request.setAttribute("article", article);
                request.setAttribute("artid", artid);

                request.setAttribute("method", method);
                if (method.equals("add"))
                    request.setAttribute("pageTitle", "添加文章");
                else
                    request.setAttribute("pageTitle", "修改文章");
            }
        } else if (method.equals("delete")) {

        }
    }

    //添加文章界面
    public String addui() {
        ArticleServiceImpl service = new ArticleServiceImpl();
        List<Category> categories = service.getAllCategories();
        ServletActionContext.getRequest().setAttribute("categories", categories);

        HttpServletRequest request = ServletActionContext.getRequest();
        request.setAttribute("method", "add");
        request.setAttribute("pageTitle", "添加文章");
        return SUCCESS;
    }

    //添加文章操作
    public String add() throws Exception {
        //封装博文体
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(LoginInterceptor.LOGIN_TAG);

        Article article = initAddArticle();
        article.setUid(user.getUid());
        article.setAuthor(user.getUname());

        //保存博文
        ArticleServiceImpl service = new ArticleServiceImpl();
        service.addArticle(article, ServletActionContext.getRequest().getContextPath(), ServletActionContext.getServletContext().getRealPath("/blog"));

        request.setAttribute("message", "博文录入成功！！！");
        request.setAttribute("url", request.getContextPath() + "/manage/article.action");
        return "message";
    }

    //删除操作
    public String delete() {
        ArticleServiceImpl service = new ArticleServiceImpl();
        service.deleteArticle(artid, ServletActionContext.getServletContext().getRealPath("/"));
        return "delete";
    }

    //修改文章界面
    public String updateui() {
        ArticleServiceImpl service = new ArticleServiceImpl();
        //查询要修改的文章
        Article article = service.queryArticle(artid);

        System.out.println("update Article ------------------------------");
        System.out.println(article.getContent());

        HttpServletRequest request = ServletActionContext.getRequest();
        request.setAttribute("article", article);
        //查询所有类别
        List<Category> categories = service.getAllCategories();
        request.setAttribute("categories", categories);
        request.setAttribute("artid", artid);

        request.setAttribute("method", "update");
        request.setAttribute("pageTitle", "修改文章");
        return SUCCESS;
    }

    //修改文章操作
    public String update() {
        HttpServletRequest request = ServletActionContext.getRequest();

        //修改博文
        ArticleServiceImpl service = new ArticleServiceImpl();
        Article article = initUpdateArticle();
        article.setArtid(artid);
        service.updateArticle(article, ServletActionContext.getRequest().getContextPath(), ServletActionContext.getServletContext().getRealPath("/blog"));

        request.setAttribute("message", "博文修改成功！！！");
        request.setAttribute("url", request.getContextPath() + "/manage/article.action");
        return "message";
    }

    //初始化封装文章实体
    private Article initAddArticle() {
        Article article = new Article();
        article.setCid(cid);
        article.setTime(date == null ? new Date() : date);
        article.setTitle(title);
        article.setType(type);
        article.setTop(top);

        //截取正文部分
        content = content.substring(content.indexOf("<body>") + 6);
        content = content.substring(0, content.indexOf("</body>"));
        article.setContent(content);
        //提取文章摘要
        if (content.length() > 250) {
            int start = content.indexOf("<span style=\"font-family:") + 25;
            String meta = content.substring(start, start + 220);
            meta = meta.substring(meta.indexOf(">") + 1);
            meta = "<span style=\"font-family:微软雅黑;\">" + meta + "</span>";
            article.setMeta(meta);
        } else article.setMeta(content);
        return article;
    }

    //初始化封装文章实体
    private Article initUpdateArticle() {
        Article article = new Article();
        article.setCid(cid);
        article.setTime(date == null ? new Date() : date);
        article.setTitle(title);
        article.setType(type);
        article.setTop(top);

        //截取正文部分
        content = content.substring(content.indexOf("<body>") + 6);
        content = content.substring(0, content.indexOf("</body>"));
        article.setContent(content);
        //提取文章摘要
        article.setMeta(meta);
        return article;
    }

    //分页查询文章
    public String query() throws Exception {
        //分页查询所有博文
        ArticleServiceImpl service = new ArticleServiceImpl();
        String url = ServletActionContext.getRequest().getContextPath() + "/manage/article.action";
        Page<Article> page = service.getPageArticles(pagenum, url);
        ServletActionContext.getRequest().setAttribute("page", page);
        return "query";
    }

    //查看单篇文章
    public String queryArticle() throws Exception {
        //获取模版填充所需信息
        ArticleServiceImpl service = new ArticleServiceImpl();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        //填充模版信息
        Map<String, Object> params = service.getTemplateParams(artid, request.getContextPath());
        if (params == null) {
            request.setAttribute("message", "文章不存在");
            request.setAttribute("url", request.getContextPath() + "/manage/comment.action");
            return "message";
        }
        //找得到文章，显示给浏览器
        String path = params.get("staticURL").toString().substring(5);
        boolean result = TemplateUtils.parserTemplate(request.getServletContext().getRealPath("/") + File.separator + "blog", path + ".ftl", params, response.getOutputStream());
        if (!result) {
            //服务器异常
            response.sendError(500, "服务器未知异常！");
        }
        response.getOutputStream().close();
        return null;
    }

    public String reload() {
        HttpServletRequest request = ServletActionContext.getRequest();

        ArticleServiceImpl service = new ArticleServiceImpl();
        service.reloadArticle(artid, request.getContextPath(), request.getServletContext().getRealPath("/blog"));

        request.setAttribute("message", "博文重新静态化成功！！！");
        request.setAttribute("url", request.getContextPath() + "/manage/article.action");
        return "message";
    }

    public String reloadAll() {
        HttpServletRequest request = ServletActionContext.getRequest();

        ArticleServiceImpl service = new ArticleServiceImpl();
        service.reloadAllArticles(request.getContextPath(), request.getServletContext().getRealPath("/blog"));

        request.setAttribute("message", "所有博文重新静态化成功！！！");
        request.setAttribute("url", request.getContextPath() + "/manage/article.action");
        return "message";
    }

    //默认情况下，查询文章列表
    @Override
    public String execute() throws Exception {
        return query();
    }
}
