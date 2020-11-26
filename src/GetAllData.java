import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * The {@code GetAllData} class is a servlet providing
 * functionality to admin users who wish to see all data
 * relating to stored user information.
 * */
@WebServlet("/GetAllData")
public class GetAllData extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute a SQL statement
     * */
    private Statement stmt;

    /**
     * This method acquires all data from the userAccounts
     * table stored in the database. This data contains
     * user information such as firstname, lastname, phone,
     * email, username, & email.
     *
     * Note: the hash is not acquired as stated in
     * requirements.
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

        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // query database and get results
            ResultSet rs = stmt.executeQuery("SELECT Firstname, Lastname, Email, Phone," +
                    "Username, Admin FROM userAccounts");

            // create HTML table text
            String content = "<table border='1' cellspacing='2' cellpadding='2' width='100%' align='left'>" +
                    "<tr><th>First name</th><th>Last name</th><th>Email</th><th>Phone number</th><th>Username</th>" +
                    "<th>Admin</th></tr>";

            // add HTML table data using data from database
            while (rs.next()) {
                content += "<tr><td>"+ rs.getString("Firstname") + "</td>" +
                        "<td>" + rs.getString("Lastname") + "</td>" +
                        "<td>" + rs.getString("Email") + "</td>" +
                        "<td>" + rs.getString("Phone") + "</td>" +
                        "<td>" + rs.getString("Username") + "</td>" +
                        "<td>" + rs.getString("Admin") + "</td></tr>";
            }
            // finish HTML table text
            content += "</table>";

            // close connection
            conn.close();

            // display output.jsp page with given content above if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
            request.setAttribute("data", content);
            dispatcher.forward(request, response);

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if successful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
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
