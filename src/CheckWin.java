import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The {@code CheckWin} class is a servlet providing
 * functionality to users checking if the numbers they've
 * chosen, have won. The {@code CheckWin} class provides
 * the doPost method containing the functionality for checking
 * the user's numbers. See {@link #doPost} for more details.
 * The {@code EncryptRSA} class is used for decryption in
 * This class. Please see {@link EncryptRSA} for more details.
 *
 * @author Lee Taylor
 * */
@WebServlet("/CheckWin")
public class CheckWin extends HttpServlet {

    /**
     * Object used for connection to SQL database
     * */
    private Connection conn;

    /**
     * Object used to store and execute an SQL statement
     * */
    private Statement stmt;

    /**
     * The user's encrypted numbers are extracted as decrypted
     * strings. Each string is converted into an array list
     * of integers. Each list stored in a single array list
     * (2D-ArrayList). The last set of winning numbers
     * is extracted from the database. Each list of the user's
     * numbers are compared to the winning numbers. However
     * as both the winning and user's numbers are stored in
     * array lists. Both data structures are sorted
     * before hand as without doing so leads to incorrect
     * comparison results. For more details on decryption
     * please see {@link EncryptRSA}
     *
     * Note: the session attribute, win, is used to
     * determine whether or not a winning message
     * should be displayed. This is set null when
     * no message should be displayed. Otherwise
     * it is set to true when the user has won
     * hence a message should be displayed.
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
     *
     * */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        // MySql database connection info
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        // Init. session object, dispatcher, and Encryption object
        HttpSession session = request.getSession();
        RequestDispatcher dispatcher;
        EncryptRSA encryptRSA = (EncryptRSA) session.getAttribute("keys");

        // Init. store for text file content containing user's numbers
        ArrayList<ArrayList<Integer>> intLines = new ArrayList<>();
        ArrayList<String> lines;

        // Store name of file containing user's numbers
        String hashCopy = AddUserNumbers.preventFileNotFound(session.getAttribute("hash")).substring(0, 20);

        // Store file path to above located file containing user's numbers
        String fileName = getServletContext().getRealPath("/") + hashCopy + ".txt";

        // Acquire decrypted text content from file
        lines = encryptRSA.decryptFile(fileName);

        // Checks if any decrypted text was acquired
        if (lines.size() == 0){
            // If none (user did not add numbers) then stay on the same page
            dispatcher = request.getRequestDispatcher("/account.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Delete user numbers from the text file as stated in requirements
        FileWriter fw = new FileWriter(fileName);
        fw.write("");
        fw.close();

        // Update intLines -> Storing the numbers as an array list of integers
        for (int i = 0; i < lines.size(); i++){
            // Store the current line of text
            String line = lines.get(i);
            // Init. a new array list ready to store the integer values
            intLines.add(new ArrayList<>());
            // For each integer in the string ...
            for (String n : line.split(",")){
                // Parse the chars as integer and store in the array list
                intLines.get(i).add(Integer.parseInt(n));
            }
        }

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // Select last set of winning numbers
            String query = "SELECT * FROM winningNumbers ORDER BY Pickid DESC LIMIT 1;";
            ResultSet rs = stmt.executeQuery(query);

            // Check stored winning numbers (result from query)
            if (!rs.isBeforeFirst()) {
                // If no results the user simply stays on the account page
                dispatcher = request.getRequestDispatcher("/account.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Result set is not null therefore it needs to point to it's data
            rs.next();

            // Extract winning numbers
            ArrayList<Integer> winningNumbers = new ArrayList<>();
            winningNumbers.add(rs.getInt("Pickone"));
            winningNumbers.add(rs.getInt("Picktwo"));
            winningNumbers.add(rs.getInt("Pickthree"));
            winningNumbers.add(rs.getInt("Pickfour"));
            winningNumbers.add(rs.getInt("Pickfive"));
            winningNumbers.add(rs.getInt("Picksix"));

            // Sort winning numbers to perform correct equality comparison
            Collections.sort(winningNumbers);

            // Iterate through the user's sets of picks one by one
            for (ArrayList<Integer> userPicks : intLines){
                // Sort each set of user numbers to perform correct equality comparison
                Collections.sort(userPicks);
                // Compare every line of user picks to the last set of winning numbers
                if (userPicks.equals(winningNumbers)){
                    // User is a winner
                    session.setAttribute("win", true);
                    session.setAttribute("winMessage", "You've Won The Lottery!");
                    // Stay on the same page
                    dispatcher = request.getRequestDispatcher("/account.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            }

        // User is a loser
        session.setAttribute("win", null);

        // Stay on the same page
        dispatcher = request.getRequestDispatcher("/account.jsp");
        dispatcher.forward(request, response);

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "Database Error, Please try again");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
