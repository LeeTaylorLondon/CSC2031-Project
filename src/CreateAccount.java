import BCrypt.src.org.mindrot.jbcrypt.BCrypt;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The {@code CreateWin} class is a servlet providing
 * functionality to users who wish to create an account.
 *
 * Note: the user inputs a password which is hashed. The
 * next time the user logs in the password they input is
 * hashed and checked against the stored hash. See
 * {@link UserLogin} for more details.
 *
 * @author Lee Taylor
 * */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute a prepared SQL statement
     * */
    private PreparedStatement stmt;

    /**
     * This method allows users to register for an account.
     * The data inputted into the HTML form is acquired through
     * the request object. Which contains the clients information.
     * Specific user information is then inserted into the
     * database.
     *
     * The user inputs firstname, lastname, email, phone,
     * password, & admin (if they're an admin or not). The
     * password is hashed using {@link BCrypt#hashpw(String, String)}
     * which returns cipher text containing the salt and hashed
     * password. This cipher text or rather, hash, is inserted
     * into the database as the password alongside all other
     * data the user inputted.
     *
     * @see BCrypt
     * @see UserLogin
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
     *
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // MySql database connection info
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        // Get client data that was submitted in HTML form using request object
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String adminStr = request.getParameter("admin");
        String adminBit;

        // Two to the power of, logRounds, is the number of iterations
        final int logRounds = 10;
        // BCrypt.hashpw returns hash based on password and generated salt
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));

        // Convert admin from 'on' or 'null' to database ready value
        if (!(adminStr == null)) {
            adminBit = "1";
        } else {
            adminBit = "0";
        }

        try{
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Create sql query - b? Casts that value to type bit used for storing admin
            String query = "INSERT INTO userAccounts (Firstname, Lastname, Email, Phone, Username, Pwd, Admin)"
                    + " VALUES (?, ?, ?, ?, ?, ?, b?)";

            // set values into SQL query statement
            stmt = conn.prepareStatement(query);
            stmt.setString(1,firstname);
            stmt.setString(2,lastname);
            stmt.setString(3,email);
            stmt.setString(4,phone);
            stmt.setString(5,username);
            stmt.setString(6,hash);
            stmt.setString(7,adminBit);

            // execute query and close connection
            stmt.execute();
            conn.close();

            // Init RequestDispatcher object
            RequestDispatcher dispatcher;

            // display account.jsp page with given message if successful
            HttpSession session = request.getSession();
            session.setAttribute("message", firstname+", you have successfully created an account");
            session.setAttribute("firstname", firstname);
            session.setAttribute("lastname", lastname);
            session.setAttribute("username", username);
            session.setAttribute("email", email);
            session.setAttribute("hash", hash);

            // Keep the user on the same page
            dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);

        } catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            request.setAttribute("attemptedLogin", false);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", firstname+", this username/password combination already exists. Please try again");
            dispatcher.forward(request, response);
        }
        finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException ignored){}
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
