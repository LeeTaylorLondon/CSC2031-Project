import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * The {@code GetWinningNumbers} class is a servlet providing
 * functionality to admin users who wish to see all data
 * relating to the stored sets of winning numbers.
 *
 * @author Lee Taylor
 * */
@WebServlet("/GetWinningNumbers")
public class GetWinningNumbers extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute an SQL statement
     * */
    private Statement stmt;

    /**
     * Presents an admin user a table each set of winning numbers.
     * An SQL statement is executed to acquire the numbers stored
     * in the winning numbers table. This data is inserted into a
     * html table format for table display.
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM winningNumbers");

            // Init. request object
            RequestDispatcher dispatcher;

            // If the winning table of numbers is empty then do not execute SELECT query
            if (!rs.isBeforeFirst()) {
                dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // create HTML table text
            String content = "<table border='1' cellspacing='2' cellpadding='2' width='100%' align='left'>" +
                    "<tr><th>PickID</th><th>Pick One</th><th>Pick Two</th><th>Pick Three</th><th>Pick Four</th>" +
                    "<th>Pick Five</th><th>Pick Six</th></tr>";

            // add HTML table data using data from database
            while (rs.next()) {
                content += "<tr><td>"+ rs.getString("Pickid") + "</td>" +
                        "<td>" + rs.getString("Pickone") + "</td>" +
                        "<td>" + rs.getString("Picktwo") + "</td>" +
                        "<td>" + rs.getString("Pickthree") + "</td>" +
                        "<td>" + rs.getString("Pickfour") + "</td>" +
                        "<td>" + rs.getString("Pickfive") + "</td>" +
                        "<td>" + rs.getString("Picksix") + "</td></tr>";
            }
            // finish HTML table text
            content += "</table>";

            // close connection
            conn.close();

            // display output.jsp page with given content above if successful
            dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
            request.setAttribute("winningNumbersTable", content);
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
