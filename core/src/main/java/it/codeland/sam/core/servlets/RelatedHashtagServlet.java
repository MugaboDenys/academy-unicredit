package it.codeland.sam.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletResponse;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = { Servlet.class })
@SlingServletPaths(
            value={"/bin/articles"})
public class RelatedHashtagServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RelatedHashtagServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
                List<Article> articles = new ArrayList<>();
                List<Node> nodes = new ArrayList<>();

                String tag = "";
                String order = "";
                Integer maximum = 20;
                Session session = req.getResourceResolver().adaptTo(Session.class);
                try {
                    tag = req.getParameter("tag");
                    maximum = Integer.parseInt(req.getParameter("max"));
                    order = req.getParameter("order");

                    QueryManager queryHandler = session.getWorkspace().getQueryManager();

                    String sql = "SELECT * FROM [cq:PageContent] AS nodes WHERE isdescendantnode ([/content/homepage/magazine]) AND nodes.[cq:tags] LIKE '%"+tag+"' ORDER BY [jcr:created]" +order;
                    Query query = queryHandler.createQuery(sql, "JCR-SQL2");
                    query.setLimit(maximum);
                    QueryResult queryResult = query.execute();

                    Iterator<Node> nodeIterator = queryResult.getNodes();

                    while (nodeIterator.hasNext()) {
                        Node currentNode = nodeIterator.next();
                        nodes.add(currentNode);
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy");

                    for(Node node : nodes) {
                        Article article = new Article();

                        String[] tags = { tag };
                        article.setTags(tags);

                        article.setTitle(node.getProperty("jcr:title").getString());
                        article.setImage(node.getProperty("image").getString());
                        article.setArticleAbstract(node.getProperty("text").getString());
                        String datestr = node.getProperty("date").getString();

                        Date date =new SimpleDateFormat( "yyyy-MM-dd").parse(datestr);
                       // article.setDate(dateFormat.format(node.getProperty("date").getDate().getTime()));

                        article.setDate(datestr);
                        String pathLink = node.getParent().getPath();
                        String linkUrl = (pathLink.substring(0,pathLink.length()-1))+".html";
                        article.setLink(linkUrl);

                        articles.add(article);
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(articles);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonString);

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                finally{
                    session.logout();
                }
    }
}
