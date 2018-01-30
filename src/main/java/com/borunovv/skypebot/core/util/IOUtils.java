package com.borunovv.skypebot.core.util;

import java.io.*;

public class IOUtils {

    /**
     * Helper to convert from Reader to String.
     * Note: closes Reader after converting.
     */
    public static String readerToString(Reader reader) {
        String res = "";
        try {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            res = builder.toString();
        } catch (IOException e) {
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return res;
    }

    /**
     * Convert Reader (char) to InputStream (byte).
     * Note: closes the 'reader' after converting.
     */
    public static InputStream readerToInputStream(Reader reader) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        for (int ch = reader.read(); ch != -1; ch = reader.read()) {
            strBuilder.append((char) ch);
        }
        reader.close();
        return new ByteArrayInputStream(strBuilder.toString().getBytes());
    }

    /**
     * Convert InputStream to String.
     */
    public static String inputStreamToString(InputStream inputStream) {
        String result = null;
        try {
            byte[] temp = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream(
                    inputStream.available() > 0 ?
                            inputStream.available() :
                            16);
            int l;
            while ((l = inputStream.read(temp))!= -1) {
                out.write(temp, 0, l);
            }

            close(inputStream);
            return new String(out.toByteArray());
        } catch (IOException e) {
        }

        return result;
    }

    /**
     * Convert InputStream to byte array input stream.
     * Note: auto close the stream.
     */
    public static byte[] inputStreamToByteArray(
            InputStream inputStream) throws IOException {
        byte[] result = null;
        if (inputStream != null) {
            try {
                int initialSize = inputStream.available() > 0 ?
                        inputStream.available() :
                        0;
                ByteArrayOutputStream tempOut =
                        new ByteArrayOutputStream(initialSize);

                byte[] temp = new byte[1024];
                int count = 0;
                int total = 0;

                while ((count = inputStream.read(
                        temp, total, temp.length)) != -1) {
                    tempOut.write(temp, 0, count);
                }

                result = tempOut.toByteArray();
                tempOut.close();
            } finally {
                close(inputStream);
            }
        }
        return result;
    }

    /**
     * Convert String to InputStream.
     */
    public static InputStream stringToInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }


    /**
     * Close stream.
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't close stream.", e);
        }
    }

    /**
     * Copy one stream to another.
     * Note: do not close any of streams.
     * You should care about it yourself !
     */
    public static void copy(InputStream input, OutputStream output)
            throws IllegalArgumentException, IOException {
        if (input == null || output == null) {
            throw new IllegalArgumentException();
        }

        byte[] buf = new byte[8192];
        while (true) {
            int length = input.read(buf);
            if (length < 0)
                break;

            output.write(buf, 0, length);
        }
    }
}
