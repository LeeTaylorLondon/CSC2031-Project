import org.apache.commons.codec.binary.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

/**
 * The {@code EncryptRSA} class represents encryption for
 * generating, storing, and extracting public & private
 * keys. Which are used for encrypting and decrypting data.
 * The primary use of this class involves the user's numbers.
 *
 * @author Lee Taylor
 * @see AddUserNumbers
 * @see GetUserNumbers
 * @see CheckWin
 * */
public class EncryptRSA {

    /**
     * Used to acquire file path.
     * */
    private final HttpServletRequest request;

    /**
     * Used to acquire client information.
     * */
    private final HttpSession session;

    /**
     * Used for private key storage.
     * */
    private PrivateKey privateKey;

    /**
     * Used for public key storage.
     * */
    private PublicKey publicKey;

    /**
     * Used to generate keys - initialized with RSA encryption technique.
     * */
    private final KeyFactory factory = KeyFactory.getInstance("RSA");

    /**
     * Used to decrypt and encrypt text - initialized with RSA encryption technique.
     * */
    private final Cipher cipher = Cipher.getInstance("RSA");

    /**
     * Initializes a newly created {@code EncryptRSA}
     * object. User information is acquired through
     * the request's session. Which is used later on
     * to determine storage location of a user's key
     * pair. {@link #createStoreKeys()}
     *
     * @param request
     *        Http servlet request containing
     *        useful attributes
     * */
    public EncryptRSA(HttpServletRequest request) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.request = request;
        this.session = request.getSession();
    }

    /**
     * This method is called upon user login
     * {@link UserLogin#doPost}. A user has keys
     * if there exists a text file with their
     * username apart of the file name. The keys
     * from this file are extracted. If no such
     * file exists then that file and a public
     * and private key are created.
     * */
    public void checkForKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            ClassNotFoundException {
        // Init. Servlet context & path to directory where keys are stored
        ServletContext context = this.request.getServletContext();
        final File fileDir = new File(context.getRealPath("/"));

        // Init the target file
        String targetPath = "keys" + session.getAttribute("username").toString() + ".txt";

        // Check if file with keys already exists
        if (new File(fileDir, targetPath).exists()){
            // If file already exists extract the keys
            extractStoredKeys();
        } else {
            // If file does not exist create the keys
            createStoreKeys();
        }
    }

    /**
     * This method creates a public and private key.
     * The keys are written to a text file as objects.
     * The naming format of the file is
     * "keys" + username + ".txt". Where, username,
     * is extracted from the session object.
     *
     * The naming format is in place to create and
     * extract the keys corresponding only to the
     * current user. No two users can ever have the
     * same username as  usernames are declared UNIQUE
     * in the SQL database config.
     *
     * Note:
     *  - The order of writing the keys and key data
     *    matters. The order is public modulus, public
     *    exponent, private modulus, private exponent.
     *    As the data is extracted and used in the order
     *    inputted {@link #extractStoredKeys()}. Which
     *    is used to build the same key from data.
     * */
    private void createStoreKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        // Init. file path & object-output
        String fileName = this.session.getServletContext().getRealPath("/") +
                "keys" + this.session.getAttribute("username") + ".txt";
        ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)));

        // Init. KeyGen
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        // Create keys
        KeyPair pair = keyGen.generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();

        // Cast private key from SunSignRSA CRT Private to RSA Private
        RSAPrivateKeySpec privateSpec = this.factory.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
        this.privateKey = factory.generatePrivate(new RSAPrivateKeySpec(
                privateSpec.getModulus(),
                privateSpec.getPrivateExponent()
        ));

        // Write public key to file
        RSAPublicKeySpec publicSpec = this.factory.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
        out.writeObject(publicSpec.getModulus());
        out.writeObject(publicSpec.getPublicExponent());

        // Write private key to file
        out.writeObject(privateSpec.getModulus());
        out.writeObject(privateSpec.getPrivateExponent());
    }

    /**
     * This method checks for a file name containing the
     * current user's username. Prefixed with 'keys' and
     * suffixed with '.txt'. If such a file exists the
     * object data is extracted and used as parameters
     * of the corresponding key constructors.
     * */
    private void extractStoredKeys() throws IOException, ClassNotFoundException, InvalidKeySpecException {
        // Init. file path
        String fileName = this.session.getServletContext().getRealPath("/") +
                "keys" + this.session.getAttribute("username") + ".txt";
        ObjectInputStream fileContent = new ObjectInputStream(Files.newInputStream(Path.of(fileName)));

        // Extract public key
        this.publicKey = this.factory.generatePublic(new RSAPublicKeySpec(
                (BigInteger)fileContent.readObject(),
                (BigInteger)fileContent.readObject()));

        // Extract private key
        this.privateKey = this.factory.generatePrivate(new RSAPrivateKeySpec(
                (BigInteger)fileContent.readObject(),
                (BigInteger)fileContent.readObject()));
    }

    /**
     * Returns an encrypted string. Using the
     * Cipher and private key.
     *
     * @param s
     *        String to encrypt
     *
     * @return Base 64 RSA encrypted string
     * */
    public String encrypt(String s) throws InvalidKeyException, UnsupportedEncodingException, BadPaddingException,
            IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
        return Base64.encodeBase64String(this.cipher.doFinal(s.getBytes("UTF-8")));
    }

    /**
     * Returns an unencrypted string. Using the
     * Cipher and public key.
     *
     * @param s
     *        String to decrypt
     *
     * @return Human readable string
     * */
    public String decrypt(String s) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.publicKey);
        return new String(cipher.doFinal(Base64.decodeBase64(s)), "UTF-8");
    }

    /**
     * Given a path to an encrypted file, this method
     * returns an array list of decrypted strings.
     *
     * @param path
     *        String path to file to decrypt
     *
     * @return ArrayList where each string
     *         is decrypted
     * */
    public ArrayList<String> decryptFile(String path) throws IOException {
        // Check if file for user's numbers exists
        if (!(new File(path).exists())){
            // If file does not exist return empty array list
            return new ArrayList<>();
        }

        // Init. file and buffered reader objects
        FileReader file = new FileReader(path);
        BufferedReader br = new BufferedReader(file);

        // line used to store each line read in
        String line;
        // draws used to store each line as a string
        ArrayList<String> usersDraws = new ArrayList<>();
        // Read each line from the file
        while((line = br.readLine()) != null) {
            usersDraws.add(line);
        }
        // Perform safe closing of buffered reader and file
        br.close();     // Close buffer writer
        file.close();   // Close file

        // For every line in usersDraws overwrite with it's decrypted form
        for (int i = 0; i < usersDraws.size(); i++){
            try {
                // Set the current line to the decrypted form of the current line
                usersDraws.set(i, this.decrypt(usersDraws.get(i)));
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }

        return usersDraws;
    }

}
