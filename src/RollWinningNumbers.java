import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The {@code RollWinningNumbers} class provides a
 * method to the admin user that adds a new set of 6
 * random secure numbers. This set is added to the
 * database table winning numbers.
 *
 * Note: the last set of numbers in the database table,
 * which is affected by the use of this method, are
 * extracted and compared during {@link CheckWin#doPost}.
 * See link for more details.
 *
 * @author Lee Taylor
 * */
@WebServlet("/RollWinningNumbers")
public class RollWinningNumbers extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute an SQL statement
     * */
    private Statement stmt;

    /**
     * Object used to store and execute a prepared SQL statement
     * */
    private PreparedStatement preparedStatement;

    /**
     * This method generates 6 random secure numbers. Using
     * Java's built-in {@link SecureRandom} class. A database
     * connection is established. The six secure random numbers
     * are inserted into the database.
     *
     * With each set of numbers a pickid is associated with the
     * set. This pickid is determined by selecting the maximum
     * current pick id then adding one. If no such id exists, i.e
     * no winning numbers have ever been generated. Then the pickid
     * is simply set to 1.
     *
     * The pickid is later used in {@link CheckWin#doPost} to
     * select and compare to the last draw. As stated in the
     * requirements. See above link for more details.
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";
         String DB_URL = "jdbc:mysql://db:3306/lottery";

        // Init. SecureRandom object used to generate secure ints
        SecureRandom secureRandom = new SecureRandom();

        // Generate 6 unique random secure ints
        ArrayList<Integer> randomInts = new ArrayList<>();
        while (randomInts.size() != 6){
            int randomInt = secureRandom.nextInt(61);
            // Checks if number has already been generated and stored
            if (!randomInts.contains(randomInt)) {
                // If not then store the new number
                randomInts.add(randomInt);
            }
        }

        // Store winning numbers in sorted order
        Collections.sort(randomInts);

        // Create connection with SQL database
        try {

            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // Determine 'pickid' by selecting the max 'pickid' then adding 1
            String query = "SELECT MAX(Pickid) AS Pickid FROM winningNumbers;";
            ResultSet resultSet = stmt.executeQuery(query);
            int pickId;

            // Set newPickId based on result from SELECT statement
            if (!resultSet.isBeforeFirst()) {
                pickId = 1;
            } else {
                resultSet.next();
                pickId = resultSet.getInt("Pickid") + 1;
            }

            // Insert statement for winning numbers
            String insertQuery = "INSERT INTO winningNumbers (Pickid, Pickone, Picktwo, " +
                    "Pickthree, Pickfour, Pickfive, Picksix) VALUES (?, ?, ?, ?, ?, ?, ?)";

            // Set values into SQL insertQuery statement
            preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setInt(1,pickId);
            preparedStatement.setInt(2,randomInts.get(0));
            preparedStatement.setInt(3,randomInts.get(1));
            preparedStatement.setInt(4,randomInts.get(2));
            preparedStatement.setInt(5,randomInts.get(3));
            preparedStatement.setInt(6,randomInts.get(4));
            preparedStatement.setInt(7,randomInts.get(5));

            // close connection
            preparedStatement.execute();
            conn.close();

            // Admin stays on same page
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
            dispatcher.forward(request, response);

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                    preparedStatement.close();
                }
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

    }
}
