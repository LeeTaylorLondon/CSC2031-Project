import BCrypt.src.org.mindrot.jbcrypt.BCrypt;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.Enumeration;

/**
 * The {@code UserLogin} class provides a method
 * to the user to log into an existing account.
 *
 * As stated in the requirements the user's inputted
 * password is hashed and then compared to the stored
 * hash. Using {@link BCrypt#checkpw}.
 *
 * @author Lee Taylor
 * */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute an SQL statement
     * */
    private Statement stmt;

    /**
     * This method enables the user to log into an existing account.
     *
     * The request object storing client information is where the
     * username and password are acquired from.
     * Username is declared as UNIQUE in the SQL database hence it
     * is okay to acquire the hash corresponding to the username.
     * As the same username may never exist for different accounts.
     * The password the user inputs is compared to the stored hash
     * using {@link BCrypt#checkpw}. If they're a match the user is
     * logged in. Otherwise the user is redirected to the error page.
     *
     * Upon logging in {@link EncryptRSA#checkForKeys()} is called.
     * This searches for an existing keypair (used to encrypt user
     * numbers) within a text file. If the keys exist they're
     * extracted and set as attributes of {@code EncryptRSA} object.
     * Which are later used to encrypt and decrypt data. Otherwise
     * the keys are created and stored as attributes of
     * {@code EncryptRSA} object. As well as stored in a text file
     * for later retrieval if logged out and logged in.
     *
     * Also upon logging in the user's data set is extracted from
     * the database. The admin value is checked, either 1 or 0.
     * If 0 then the user is directed to the account page. Otherwise
     * they're directed to the admin_home page and no {@code EncryptRSA}
     * object is created.
     *
     * @param request
     *        Provides client request information
     *        to a servlet.
     *
     * @param response
     *        Provide client response information
     *        to a servlet.
     *
     * @throws ServletException
     *         Thrown if forwarded Servlet does
     *         not exist.
     *
     * @throws IOException
     *         Thrown if input/output to a directory
     *         that does not exist.
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        // get parameter data that was submitted in HTML form (use form attributes 'name')
        String username = request.getParameter("loginUsername");
        String password = request.getParameter("loginPassword");

        // Removes pre-existing attributes in the session object
        Enumeration<String> attrs = request.getSession().getAttributeNames();
        while (attrs.hasMoreElements()){
            request.getSession().removeAttribute(attrs.nextElement());
        }

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // Select all from table userAccounts where username=inputted-username and password=inputted-password
            String query = "SELECT * FROM userAccounts WHERE Username=" + "'" + username + "';";
            ResultSet accountData = stmt.executeQuery(query);
            RequestDispatcher dispatcher;

            // If the username does not exist the user is forwarded to error page
            if (!accountData.isBeforeFirst()) {
                dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message", "Login unsuccessful!");
                dispatcher.forward(request, response);
                return;
            }

            // Account-data is not null therefore it needs to point to it's data
            accountData.next();

            // Get session
            HttpSession session = request.getSession();

            /* Check extracted hash based on submitted username against submitted password.
            Hash string stored in database is composed of salt and cipher text to
            prevent rainbow table attacks */
            if (BCrypt.checkpw(password, accountData.getString("Pwd"))){

                // Init. Encryption
                EncryptRSA encryption = new EncryptRSA(request);

                // Set session attributes
                session.setAttribute("win", null);
                session.setAttribute("message", "Login success!");
                session.setAttribute("firstname", accountData.getString("Firstname"));
                session.setAttribute("lastname", accountData.getString("Lastname"));
                session.setAttribute("username", accountData.getString("Username"));
                session.setAttribute("email", accountData.getString("Email"));
                session.setAttribute("hash", accountData.getString("Pwd"));

                // Check if user is admin
                if (accountData.getString("Admin").equals("1")){
                    // If user is admin set next page to admin home
                    session.setAttribute("admin", true);
                    dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
                } else {
                    // Also add public & private keys for encrypting and decrypting user numbers
                    encryption.checkForKeys();
                    session.setAttribute("keys", encryption);
                    // If user is not admin set next page to account page
                    session.setAttribute("admin", false);
                    dispatcher = request.getRequestDispatcher("/account.jsp");
                }

            // If BCrypt check of input & stored hash fails then forward to error page
            } else {
                dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message", "Login unsuccessful!");
            }

            // first name, last name, username, email, and hashed password
            dispatcher.forward(request, response);

            // close connection
            conn.close();

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
