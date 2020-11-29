import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "RoleBaseAccessControl")
public class RoleBaseAccessControl implements Filter {
    public void destroy() {
    }

    /**
     * This filter implements RBAC. All features on the admin page
     * have a check performed to see if the current user is an admin.
     * If not they are forwarded to the error page. Otherwise the
     * chain carries on as normal.
     *
     * @param req
     *        Provides client request information to a servlet.
     *
     * @param resp
     *        Provide client response information to a servlet.
     *
     * @param chain
     *        Stores the next web page
     *
     * @throws ServletException
     *         Thrown if forwarded Servlet does not exist.
     * */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        // Create request object & acquire session object
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();

        // Check if current user is admin
        if (session.getAttribute("admin") == null || !(boolean)session.getAttribute("admin")){
            // Check failed therefore current user is not an admin
            RequestDispatcher dispatcher = req.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "You do not have permission to access that feature.");
            dispatcher.include(request, resp);
        } else {
            chain.doFilter(req, resp);
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
