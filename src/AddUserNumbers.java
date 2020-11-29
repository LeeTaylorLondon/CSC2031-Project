import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * The {@code AddUserNumbers} class is a servlet providing
 * functionality for users to add their numbers. The
 * {@code AddUserNumbers} class provides methods for outputting
 * to a file, preventing file not found, and doPost.
 *
 * When out putting to a file it is important to note: That
 * password hashes contain special characters that cause
 * FileNotFound errors, hence the creation and use of
 * {@link #preventFileNotFound} as a substring of the hash is
 * used as the file name.
 *
 * The doPost method contains the functionality for storing
 * the user's numbers. In a text file in encrypted form for
 * more details on encryption see {@link EncryptRSA} also
 * See {@link #doPost} for more details about the doPost method.
 *
 * @author Lee Taylor
 * */
@WebServlet("/AddUserNumbers")
public class AddUserNumbers extends HttpServlet {

    /**
     * As BCrypt is used for hashing user passwords
     * {@link BCrypt.src.org.mindrot.jbcrypt.BCrypt}
     * sometimes a hash may include the character '/'
     * which causes this and other servlets FileNotFound
     * errors. As it attempts to look for a folder that does
     * not exist.
     *
     * This is prevented by replacing those characters with an
     * arbitrary character, 'r'.
     *
     * @param hash
     *        Contains hash characters which are modified
     *
     * @return String with all characters '/' replaced with 'r'.
     * */
    public static String preventFileNotFound(Object hash){
        StringBuilder hashCopy = new StringBuilder();
        hashCopy.append(hash);
        // Only 20 characters are replaced as only those are used
        for (int i = 0; i < 20; i++){
            // If current character is '/' then replace with 'r'
            if (hashCopy.charAt(i) == '/'){
                hashCopy.setCharAt(i, 'r');  // "r" for replaced
            }
        }
        return hashCopy.toString();
    }

    /**
     * This method is used to write encrypted user numbers to a text file.
     * For every string of text, content, is written on a new line.
     *
     * @param fileName
     *        Name of file to write to
     *
     * @param content
     *        Data to write to file
     *
     * @throws IOException
     *         If directory does not exist raise IOException
     * */
    private void outputToFile(String fileName, String content) throws IOException {
        // Append newline character to write all text on different lines
        content = content + "\n";
        File file = new File(fileName);
        // Create output stream to file, append is true so that it adds lines
        FileOutputStream fos = new FileOutputStream(file, true);
        // Write the bytes to the file
        byte[] bytesArray = content.getBytes();
        fos.write(bytesArray);
        // Force any buffered output bytes to be written out
        fos.flush();
    }

    /**
     * Acquires user's inputted numbers from http-session, performs
     * encryption and stores them in a text file.
     *
     * The Http Servlet Request object containing client information
     * contains a http-session. This is extracted and stored in memory
     * as a http session object. From this object the user's hash is
     * acquired. This hash is used as the filename of the user's encrypted
     * numbers. The user's numbers are the six numbers a user chooses
     * for the lottery. The user's numbers are encrypted and stored
     * as previously described. For more details on encryption please see
     * {@link EncryptRSA}. The path of the file itself is determined
     * by the real path of the servlet context.
     *
     * @see EncryptRSA
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

        // Continue user session. Acquire object containing client information.
        HttpSession session = request.getSession();

        // Hashes containing '/' causes FileNotFound exception. Error prevented by replacing that char.
        String hashCopy = preventFileNotFound(session.getAttribute("hash"));

        // Create string representing directory path to file
        String filePath = getServletContext().getRealPath("/") + hashCopy.substring(0, 20) + ".txt";

        // Acquire each user number as a string
        String s1, s2, s3, s4, s5, s6;
        s1 = request.getParameter("slot1");
        s2 = request.getParameter("slot2");
        s3 = request.getParameter("slot3");
        s4 = request.getParameter("slot4");
        s5 = request.getParameter("slot5");
        s6 = request.getParameter("slot6");

        // Creates a single String from the received 6 integers
        String userNumbers = s1 + "," + s2 + "," + s3 + ","  + s4 + ","  + s5 + ","  + s6;

        // Acquire encryption object containing encryption objects & methods
        EncryptRSA encryption = (EncryptRSA) session.getAttribute("keys");

        // Encrypt the users numbers
        try {
            userNumbers = encryption.encrypt(userNumbers);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        // Amend encrypted String to txt file
        outputToFile(filePath, userNumbers);

        // Stay on the same page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
        dispatcher.forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
