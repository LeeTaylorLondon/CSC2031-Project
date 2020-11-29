import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Simple servlet for logging out.
 *
 * @author Lee Taylor
 * */
@WebServlet("/Logout")
public class Logout extends HttpServlet {

    /**
     * This method removes all attributes in the session object.
     * Then sends the user to the home page. Which is index.jsp.
     *
     * @param request
     *        Provides client request information
     *        to a servlet.
     *
     * @param response
     *        Provide client response information
     *        to a servlet.
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Removes pre-existing attributes in the session object
        Enumeration<String> attrs = request.getSession().getAttributeNames();
        while (attrs.hasMoreElements()){
            request.getSession().removeAttribute(attrs.nextElement());
        }

        // Send user to home page (index.jsp)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
