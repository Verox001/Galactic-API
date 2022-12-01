package dev.galactic.star;

import java.io.*;

/**
 * @author PrismoidNW
 */
public class ObjectSerializer {

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
