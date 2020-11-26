import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The {@code GetUserNumbers} class provides a method
 * to display the current user's numbers in the web page.
 *
 * @author Lee Taylor
 * */
@WebServlet("/GetUserNumbers")
public class GetUserNumbers extends HttpServlet {

    /**
     * This method acquires all sets of the current user's
     * numbers from a text file.
     *
     * The session object containing client information is acquired
     * from the request object, also containing client information.
     * The EncryptRSA object is acquired from the session object. The
     * path as a string is passed as a parameter to a method within
     * EncryptRSA which returns the user's numbers. These numbers are
     * displayed
     * See {@link EncryptRSA} and see {@link EncryptRSA#decryptFile}
     * for more details.
     *
     * Note: the path to the file uses the user's hash as a file name.
     * The hash may contain a char that cause errors hence the use of
     * {@link AddUserNumbers#preventFileNotFound}.
     *
     * @param request
     *        Provides client request information to a servlet.
     *
     * @param response
     *        Provide client response information to a servlet.
     *
     * @throws ServletException
     *         Thrown if forwarded Servlet does not exist.
     *
     * @throws IOException
     *         Thrown if input to a directory that does not exist.
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Init Session & encryption object
        HttpSession session = request.getSession();
        EncryptRSA encryptRSA = (EncryptRSA) session.getAttribute("keys");

        // Hash containing '/' causes FileNotFound exception. Error prevented by replacing that char.
        String hashCopy = AddUserNumbers.preventFileNotFound(session.getAttribute("hash"));

        // Init. objects for reading in data
        String filePath = getServletContext().getRealPath("/") + hashCopy.substring(0, 20) + ".txt";

        // Init. storage for users draws
        ArrayList<String> usersDraws;

        // Check if file for user's numbers exists
        if (!(new File(filePath).exists())){
            // If file does not exist do not execute and user stays on the same web page.
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            dispatcher.forward(request, response);
            return;
        } else {
            usersDraws = encryptRSA.decryptFile(filePath);
        }

        // Stay on the same page
        request.setAttribute("draws", usersDraws);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
