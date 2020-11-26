import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * The {@code SqlInjectionFilter} class is a
 * servlet filter used to prevent SQL injections.
 *
 * @author Lee Taylor
 * */
@WebFilter(filterName = "PreventSQLInjection")
public class SqlInjectionFilter implements Filter {
    public void destroy() {
    }

    /**
     * Filter prevents user from submitting SQL
     * commands via form-input. Preventing SQL injections.
     *
     * Disallowed inputs are stored as strings in array
     * 'disallowedList'. Web pages link to several
     * servlet paths. For example index page currently
     * links to UserLogin & CreateAccount. Whichever
     * path is taken by the user determines which
     * input fields are acquired and compared against
     * illegal input.
     *
     * @param request
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
     *
     * @throws IOException
     *         Thrown if input to a directory that does not exist.
     * */
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws ServletException,
            IOException {

        // Init request, form data, & disallowed list
        HttpServletRequest req = (HttpServletRequest) request;
        String[] formData = new String[0];
        String[] disallowedList = new String[]{"<", ">", "!", "{", "}",
                "'", "=", "''", "insert", "into", "where",
                "script", "delete", "input", "drop", "table"};

        // If user is creating an account. All fields under 'Register' are scraped.
        if (req.getServletPath().equals("/CreateAccount")) {
            formData = new String[]{
                    req.getParameter("firstname"),
                    req.getParameter("lastname"),
                    req.getParameter("email"),
                    req.getParameter("phone"),
                    req.getParameter("username"),
                    req.getParameter("password")};

        // If user is logging into an account. All fields only under 'Login' are scraped.
        } else if (req.getServletPath().equals("/UserLogin")) {
            formData = new String[]{req.getParameter("loginUsername"),
                    req.getParameter("loginPassword")};

        }
        // Check form inputs for illegal characters / words by comparison of each input & illegal string
        for (String s : formData){
            for (String s2 : disallowedList){
                if (s.contains(s2)){
                    // Error because illegal character / words
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                    request.setAttribute("message", "Illegal input detected!");
                    dispatcher.include(request, resp);
                    return;
                }
            }
        }

        // Register -> success no illegal chars/words
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {
    }

}
