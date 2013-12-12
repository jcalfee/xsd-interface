package info.jcalfee.gae.ds;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class XsiFilter implements Filter {

//    private static final Logger log = Logger.getLogger(XsiFilter.class.getName());

    public void init(FilterConfig config) throws ServletException {
        //config.getServletContext().getServlet("");
    }

    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain filter) throws IOException, ServletException {
        
        final HttpServletRequest hsRequest = (HttpServletRequest) request;
        //final HttpServletResponse hsResponse = (HttpServletResponse) response;
        
        String reqUri = hsRequest.getRequestURI();
        
        //new uri
        
        RequestDispatcher rq = request.getRequestDispatcher(reqUri);
        rq.forward(request, response);
    }
    

    public void destroy() {

    }
}
