import org.junit.Test;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

/**
 * This class contains two tests. One to ensure the list of
 * secure numbers generated does not contain duplicates.
 * {@link #testRandomSecureArray()} Two, a public and private
 * key pair can be written to a text file and then extracted.
 * {@link #testKeysStoredAndExtracted()}.
 *
 * @author Lee Taylor
 * */
public class Tests {

    /**
     * This test ensures that the numbers generated are unique.
     * As the hashmap cannot store duplicate keys, the length after finishing
     * must be 6. In the case that it is not then a duplicate number was
     * added.
     *
     * This is tested 100 times to ensure it does not generated duplicate numbers.
     * */
    @Test
    public static void testRandomSecureArray(){
        for (int i = 0; i < 100; i++) {
            // Init secure random object
            SecureRandom secureRandom = new SecureRandom();

            // Store random values in a hashmap for testing purposes
            HashMap<Integer, Integer> hashMap = new HashMap<>();

            // Generate 6 unique random secure ints
            ArrayList<Integer> randomInts = new ArrayList<>();

            // Generates secure random integers until 6 ints are stored
            while (randomInts.size() != 6) {
                int randomInt = secureRandom.nextInt(61);
                if (!randomInts.contains(randomInt)) {
                    randomInts.add(randomInt);
                    hashMap.put(randomInt, randomInt);
                }
            }
            Collections.sort(randomInts);

            // Storing each value in a hashmap means that the
            // length of the hashmap's keys should be 6 as duplicates aren't stored
            assertEquals(6, hashMap.keySet().size());
        }
    }

    /**
     * This test indicates public and private keys can be stored in text files.
     * The same key can be extracted and turned back into an object. For further
     * encryption or decryption.
     *
     * @throws IOException
     *         If the directory did not exist or permission was not
     *         granted to write files then throw this exception
     *
     * @throws NoSuchAlgorithmException
     *         Thrown if an invalid key instance is specified
     *
     * @throws InvalidKeySpecException
     *         Thrown if an invalid key spec is specified
     *
     * @throws ClassNotFoundException
     *         Thrown if no definition can be found for the class
     *         when reading an object from a text file
     * */
    @Test
    public static void testKeysStoredAndExtracted() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            ClassNotFoundException {
        // Init. Key object data
        KeyPairGenerator keyGen = null;
        KeyPair pair;
        PrivateKey privateKey;
        PublicKey publicKey;

        // Creates a Key Pair
        try {
            // Key's generated using Rivest, Shamir, Adleman's technique (RSA)
            keyGen = KeyPairGenerator.getInstance("RSA");
            // Number of bits for key generation
            keyGen.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Generate keys
        assert keyGen != null;
        pair = keyGen.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();

        // Init. KeyFactory & output destination
        KeyFactory factory = KeyFactory.getInstance("RSA");
        ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(
                "keys.txt")));

        // Change private key from - SunSignRSA CRT - to just - RSA
        RSAPrivateKeySpec privateSpec = factory.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
        privateKey = factory.generatePrivate(new RSAPrivateKeySpec(
                privateSpec.getModulus(),
                privateSpec.getPrivateExponent()
        ));

        // Write public key data to file
        RSAPublicKeySpec spec = factory.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
        out.writeObject(spec.getModulus());
        out.writeObject(spec.getPublicExponent());

        // Write private key data to file
        out.writeObject(privateSpec.getModulus());
        out.writeObject(privateSpec.getPrivateExponent());

        // Create new objects to store keys extracted from text file
        PublicKey publicKeyExtracted;
        PrivateKey privateKeyExtracted;

        // Init. Input stream
        Path fileDir = Paths.get("keys.txt");
        ObjectInputStream fileContent = new ObjectInputStream(Files.newInputStream(fileDir));

        // Extract public key from text file
        publicKeyExtracted = factory.generatePublic(
                new RSAPublicKeySpec(
                        (BigInteger)fileContent.readObject(),
                        (BigInteger)fileContent.readObject()
                )
        );

        // Extract private key from text file
        privateKeyExtracted = factory.generatePrivate(
                new RSAPrivateKeySpec(
                        (BigInteger)fileContent.readObject(),
                        (BigInteger)fileContent.readObject()
                )
        );

        // Check if key created is the same as key extracted from text file
        assertEquals(publicKey, publicKeyExtracted);
        assertEquals(privateKey, privateKeyExtracted);
    }

    /**
     * All tests written in this class are executed when this class is run.
     * For more details on exceptions see {@link #testKeysStoredAndExtracted()}.
     * */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        testRandomSecureArray();
        testKeysStoredAndExtracted();
    }
}
