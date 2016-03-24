package cn.coselding.myblog.service.impl;

import cn.coselding.myblog.dao.ArticleDao;
import cn.coselding.myblog.dao.CategoryDao;
import cn.coselding.myblog.dao.impl.ArticleDaoImpl;
import cn.coselding.myblog.dao.impl.CategoryDaoImpl;
import cn.coselding.myblog.dao.impl.GuestDaoImpl;
import cn.coselding.myblog.domain.Article;
import cn.coselding.myblog.domain.Category;
import cn.coselding.myblog.domain.Guest;
import cn.coselding.myblog.domain.Page;
import cn.coselding.myblog.email.JavaMailWithAttachment;
import cn.coselding.myblog.exception.ForeignKeyException;
import cn.coselding.myblog.utils.Global;
import cn.coselding.myblog.utils.JdbcUtils;
import cn.coselding.myblog.utils.ServiceUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 宇强 on 2016/3/12 0012.
 */
public class ArticleServiceImpl {

    private CategoryDao categoryDao = new CategoryDaoImpl();
    private ArticleDao articleDao = new ArticleDaoImpl();
    private GuestDaoImpl guestDao = new GuestDaoImpl();

    //得到所有类别
    public List<Category> getAllCategories() {
        //缓存中有，直接查询缓存
        if (Global.isCategories_cached())
            return Global.getCategories();
        //还没缓存  查询数据库
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            List<Category> list = categoryDao.queryAll();
            Global.setCategories(list);
            Global.setCategories_cached(false);

            JdbcUtils.commit();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //添加类别
    public boolean addCategory(Category category) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            boolean result = false;
            //如果已经有这个类别的，报错
            Category temp = categoryDao.queryCategoryByName(category.getCname());
            if (temp != null)
                result = false;
            else {
                categoryDao.saveCategory(category);
                List<Category> list = categoryDao.queryAll();
                Global.setCategories(list);
                Global.setCategories_cached(false);
                result = true;
            }
            JdbcUtils.commit();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //删除类别
    public boolean deleteCategory(int cid) throws ForeignKeyException {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            categoryDao.deleteCategory(cid);
            Global.setCategories(categoryDao.queryAll());
            JdbcUtils.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            if(e.getMessage().contains("a foreign key constraint fails"))
                throw new ForeignKeyException(e);
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //查询指定类别
    public Category queryCategory(int cid) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            Category category = categoryDao.queryCategory(cid);

            JdbcUtils.commit();
            return category;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //更新类别
    public void updateCategory(Category category){
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();

            Category temp = categoryDao.queryCategory(category.getCid());
            temp.setCname(category.getCname());
            categoryDao.updateCategory(temp);
            Global.setCategories(categoryDao.queryAll());

            JdbcUtils.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //分页查询类别
    public Page<Category> queryPageCategory(String pagenum,String url){
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            // 总记录数
            int totalrecord = (int) categoryDao.getCount();
            Page<Category> page = null;
            if (pagenum == null)
                // 没传递页号，回传第一页数据
                page = new Page<Category>(totalrecord, 1);
            else
                // 根据传递的页号查找所需显示数据
                page = new Page<Category>(totalrecord, Integer.parseInt(pagenum));
            List<Category> list = categoryDao.getPageData(page.getStartindex(),
                    page.getPagesize());
            page.setList(list);
            page.setUrl(url);

            JdbcUtils.commit();
            return  page;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //添加文章，半静态化，邮件通知订阅用户
    public boolean addArticle(Article article, String contextPath, String realPath) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            boolean result = true;

            //保存数据库
            long artid = articleDao.saveArticle(article);
            article.setArtid((int) artid);

            //建立solr索引
            // TODO 先注释
            //SolrjUtils.createIndex(article);

            //静态化页面
            List<Category> list = categoryDao.queryAll();
            Global.setCategories(list);
            Global.setCategories_cached(false);

            //静态化路径
            article.setStaticURL("/blog/" + article.getCid() + "/" + article.getCid() + "-" + artid);

            Category category = categoryDao.queryCategory(article.getCid());
            ServiceUtils.staticPage(article, contextPath, category, realPath);

            //储存静态化页面路径
            articleDao.updateArticleInfo(article);

            //查询已订阅的用户
            List<Guest> guests = guestDao.queryRssGuests();

            //提交事务
            JdbcUtils.commit();

            //发送邮件
            JavaMailWithAttachment.getInstance().sendRSS(article,guests,contextPath,true);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //更新文章信息，喜爱，访问量
    public void updateArticleInfo(Article article) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();

            articleDao.updateArticleInfo(article);

            JdbcUtils.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //得到某个类别的文章，分页
    public Page<Article> getCategoryPageArticles(int cid, String pagenum, String url) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            // 总记录数
            int totalrecord = (int) articleDao.queryCount("cid=?", new Object[]{cid});
            Page<Article> page = null;
            if (pagenum == null)
                // 没传递页号，回传第一页数据
                page = new Page<Article>(totalrecord, 1);
            else
                // 根据传递的页号查找所需显示数据
                page = new Page<Article>(totalrecord, Integer.parseInt(pagenum));
            List<Article> list = articleDao.getPageData("cid=?", new Object[]{cid}, page.getStartindex(),
                    page.getPagesize());
            page.setList(list);
            page.setUrl(url);

            JdbcUtils.commit();
            return page;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //分页查询文章
    public Page<Article> getPageArticles(String pagenum, String url) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            // 总记录数
            int totalrecord = (int) articleDao.queryCount(null, null);
            Page<Article> page = null;
            if (pagenum == null)
                // 没传递页号，回传第一页数据
                page = new Page<Article>(totalrecord, 1);
            else
                // 根据传递的页号查找所需显示数据
                page = new Page<Article>(totalrecord, Integer.parseInt(pagenum));
            List<Article> list = articleDao.getPageData(null, null, page.getStartindex(),
                    page.getPagesize());
            page.setList(list);
            page.setUrl(url);

            JdbcUtils.commit();
            return page;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //搜索文章，分页
    public Page<Article> searchArticle(String key, String pagenum, String url) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            // 总记录数
            int totalrecord = (int) articleDao.queryCountSQL("select count(*) from article where title like '%" + key + "%';", new Object[]{});
            Page<Article> page = null;
            if (pagenum == null)
                // 没传递页号，回传第一页数据
                page = new Page<Article>(totalrecord, 1);
            else
                // 根据传递的页号查找所需显示数据
                page = new Page<Article>(totalrecord, Integer.parseInt(pagenum));
            List<Article> list = articleDao.queryArticleBySQL("select artid,time,author,cid,title,staticURL,top from article where title like '%" + key + "%' order by top desc,time desc limit ?,?;", new Object[]{page.getStartindex(), page.getPagesize()});
            page.setList(list);
            page.setUrl(url);

            JdbcUtils.commit();
            return page;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //删除文章
    public void deleteArticle(int artid) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();

            articleDao.deleteArticle(artid);

            JdbcUtils.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //查询指定文章
    public Article queryArticle(int artid) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            Article article = articleDao.queryArticle(artid);

            JdbcUtils.commit();
            return article;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //修改文章内容，半静态化，通知订阅用户
    public boolean updateArticle(Article temp, String contextPath, String realPath) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            boolean result = true;

            Article article = articleDao.queryArticle(temp.getArtid());

            article.setType(temp.getType());
            article.setContent(temp.getContent());
            article.setCid(temp.getCid());
            article.setTitle(temp.getTitle());
            article.setTime(temp.getTime());
            article.setMeta(temp.getMeta());
            article.setTop(temp.getTop());
            //保存数据库
            articleDao.updateArticle(article);

            //建立solr索引
            // TODO 先注释
            //SolrjUtils.createIndex(article);

            //静态化页面

            List<Category> list = categoryDao.queryAll();
            Global.setCategories(list);
            Global.setCategories_cached(false);

            Category category = categoryDao.queryCategory(article.getCid());
            ServiceUtils.staticPage(article, contextPath, category, realPath);

            //储存静态化页面路径
            article.setStaticURL("/blog/" + article.getCid() + "/" + article.getCid() + "-" + article.getArtid());
            articleDao.updateArticleInfo(article);

            //查询已订阅的用户
            List<Guest> guests = guestDao.queryRssGuests();

            //提交事务
            JdbcUtils.commit();

            //发送邮件
            JavaMailWithAttachment.getInstance().sendRSS(article,guests,contextPath,false);

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //得到freemarker模版文件所需参数
    public Map<String, Object> getTemplateParams(int artid, String contextPath) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();

            //要看的文章
            List<Article> articles = articleDao.queryArticleBySQL("select time,looked,likes,artid,title,cid,staticURL from article where artid=?", new Object[]{artid});
            if (articles.size() <= 0)
                return null;
            //增加访问量
            articles.get(0).setLooked(articles.get(0).getLooked() + 1);
            articleDao.updateArticleInfo(articles.get(0));

            //最新三篇文章
            List<Article> lastArticles = null;
            //先查缓存
            if (Global.isIsLast())
                lastArticles = Global.getLastArticles();
            else {
                lastArticles = articleDao.queryArticleBySQL("select title,time,artid,cid,staticURL from article order by time desc limit 0,3", null);
                Global.setLastArticles(lastArticles);
                Global.setIsLast(false);
            }

            //所有类别
            List<Category> categories = null;
            //先查缓存
            if (Global.isCategories_cached())
                categories = Global.getCategories();
            else {
                categories = categoryDao.queryAll();
                Global.setCategories(categories);
                Global.setCategories_cached(false);
            }

            //下一篇
            Article next = null;
            List<Article> nextArticles = articleDao.queryArticleBySQL("select title,time,artid,cid,staticURL from article where time>? order by time asc limit 0,3", new Object[]{articles.get(0).getTime()});
            if (nextArticles == null || nextArticles.size() <= 0) {
                next = new Article();
                next.setStaticURL("#");
                next.setTitle("这是最后一篇了哦！");
            } else {
                next = nextArticles.get(0);
                next.setStaticURL(contextPath + next.getStaticURL() + ".html");
            }

            //上一篇文章
            Article last = null;
            List<Article> lastAs = articleDao.queryArticleBySQL("select title,time,artid,cid,staticURL from article where time<? order by time desc limit 0,3", new Object[]{articles.get(0).getTime()});
            if (lastAs == null || lastAs.size() <= 0) {
                last = new Article();
                last.setStaticURL("#");
                last.setTitle("这是第一篇哦！");
            } else {
                last = lastAs.get(0);
                last.setStaticURL(contextPath + last.getStaticURL() + ".html");
            }

            //封装模版所需参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("looked", articles.get(0).getLooked());
            params.put("likes", articles.get(0).getLikes());
            params.put("lastArticlesList", lastArticles);
            params.put("categoryList", categories);
            params.put("likesURL", contextPath + "/likeAction.action?artid=" + artid);
            params.put("nextArticle", next);
            params.put("lastArticle", last);
            params.put("staticURL", articles.get(0).getStaticURL());

            //提交事务
            JdbcUtils.commit();
            return params;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    //得到模板引擎参数
    public Map<String, Object> getArticleListParams(String contextPath) {
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            //最新三篇文章
            List<Article> lastArticles = null;
            //先查缓存
            if (Global.isIsLast())
                lastArticles = Global.getLastArticles();
            else {
                lastArticles = articleDao.queryArticleBySQL("select title,time,artid,cid,staticURL from article order by time desc limit 0,3", null);
                Global.setLastArticles(lastArticles);
                Global.setIsLast(false);
            }

            //列表顶置四篇文章
            List<Article> topArticles = articleDao.queryArticleBySQL("select title,time,looked,likes,meta,type,artid,cname,author,c.cid,staticURL from article a,category c where a.cid=c.cid order by top desc,time desc limit 0,4", null);

            //所有类别
            List<Category> categories = null;
            //先查缓存
            if (Global.isCategories_cached())
                categories = Global.getCategories();
            else {
                categories = categoryDao.queryAll();
                Global.setCategories(categories);
                Global.setCategories_cached(false);
            }

            //封装模版所需参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("lastArticlesList", lastArticles);
            params.put("categories", categories);
            params.put("topArticles", topArticles);

            //提交事务
            JdbcUtils.commit();
            return params;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }

    public boolean reloadAllArticles(){
        try {
            // 设置事务隔离级别
            JdbcUtils
                    .setTransactionIsolation(JdbcUtils.TRANSACTION_READ_COMMITTED);
            // 开启事务
            JdbcUtils.startTransaction();
            //只读
            JdbcUtils.setReadOnly();

            articleDao.queryCount("",new Object[]{1,1,1});

            

            //提交事务
            JdbcUtils.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JdbcUtils.rollback();
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release();
        }
    }
}
