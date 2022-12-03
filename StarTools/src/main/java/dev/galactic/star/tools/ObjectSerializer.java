package dev.galactic.star.tools;

import java.io.*;

/**
 * The way to serialize objects. NOTE: The class you want to serialize needs to implement Java's Serializable
 * Interface or else you will get a NotSerializableException.
 *
 * @author PrismoidNW
 */
public class ObjectSerializer {


    /**
     * The default constructor. There is no need to instantiate this class.
     *
     * @throws InstantiationException When you instantiate this class.
     */
    public ObjectSerializer() throws InstantiationException {
        throw new InstantiationException("You shouldn't instantiate this class as it is a utility class.");
    }

    /**
     * Converts an Object into an array of bytes.
     *
     * @param object Can be any object.
     * @return Byte array.
     */
    public static byte[] objectToByteArray(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an Object to Base64.
     *
     * @param object Object.
     * @return String.
     */
    public static String toBase64(Object object) {
        return Base64.encode(objectToByteArray(object));
    }

    /**
     * Converts a Base64 String to an Object.
     *
     * @param base64Data Base64 String.
     * @return Object.
     */
    public static Object fromBase64(String base64Data) {
        return Base64.decode(base64Data);
    }

    /**
     * Converts a byte array into an Object.
     *
     * @param byteArray An array of bytes.
     * @return Object
     */
    public static Object byteArrayToObject(byte[] byteArray) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
