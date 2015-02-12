
package cn.ljj.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import cn.ljj.user.User;

public class ComposerParser {
    public final static String TAG = "ComposerParser";

    public static IPMessage parseMessage(InputStream dataStream) throws IOException {
        IPMessage msg = new IPMessage();
        int i = 0;
        byte b = (byte) dataStream.read();
        while (b != Headers.MESSAGE_BEGIN) {
            // System.out.println(TAG + " skip byte" + b);
            b = (byte) dataStream.read();
            if (b == -1) {// reach stream end
                throw new IOException("Reach the end of the stream");
            }
        }
        int length = parseLengthInt(dataStream);
        while (i < length-1) {
            i++;
            switch (dataStream.read()) {
                case Headers.HEADER_MESSAGE_BODY:
                    int bodyLength = parseLengthInt(dataStream);
                    i += getLengthSize(bodyLength);
                    byte[] body = new byte[bodyLength];
                    dataStream.read(body, 0, bodyLength);
                    msg.setBody(body);
                    i += bodyLength;
                    break;
                case Headers.HEADER_MESSAGE_DATE:
                    int dateLength = parseLengthInt(dataStream);
                    i += getLengthSize(dateLength);
                    byte[] date = new byte[dateLength];
                    dataStream.read(date, 0, dateLength);
                    msg.setDate(new String(date));
                    i += dateLength;
                    break;
                case Headers.HEADER_MESSAGE_FROM:
                    int fromLength = parseLengthInt(dataStream);
                    i += getLengthSize(fromLength);
                    byte[] from = new byte[fromLength];
                    dataStream.read(from, 0, fromLength);
                    msg.setFrom(new String(from));
                    i += fromLength;
                    break;
                case Headers.HEADER_MESSAGE_TO:
                    int toLength = parseLengthInt(dataStream);
                    i += getLengthSize(toLength);
                    byte[] to = new byte[toLength];
                    dataStream.read(to, 0, toLength);
                    msg.setTo(new String(to));
                    i += toLength;
                    break;
                case Headers.HEADER_MESSAGE_TYPE:
                    msg.setMessageType(dataStream.read());
                    i++;
                    break;
                case Headers.HEADER_MESSAGE_INDEX:
                    byte[] index = new byte[4];
                    dataStream.read(index, 0, 4);
                    msg.setMessageIndex(parseInt(index));
                    i += 4;
                case Headers.MESSAGE_END:
                    return msg;
                default:
                    return null;
            }
        }
        return msg;
    }

    public static byte[] composeMessage(IPMessage msg) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            // type
            bao.write(Headers.HEADER_MESSAGE_TYPE);
            bao.write(msg.getMessageType());
            // from
            bao.write(Headers.HEADER_MESSAGE_FROM);
            bao.write(getLengthBytes(msg.getFrom().getBytes().length));
            bao.write(msg.getFrom().getBytes());
            // to
            bao.write(Headers.HEADER_MESSAGE_TO);
            bao.write(getLengthBytes(msg.getTo().getBytes().length));
            bao.write(msg.getTo().getBytes());
            // date
            bao.write(Headers.HEADER_MESSAGE_DATE);
            bao.write(getLengthBytes(msg.getDate().getBytes().length));
            bao.write(msg.getDate().getBytes());
            // body
            bao.write(Headers.HEADER_MESSAGE_BODY);
            bao.write(getLengthBytes(msg.getBody().length));
            bao.write(msg.getBody());
            // index
            bao.write(Headers.HEADER_MESSAGE_INDEX);
            bao.write(getIntBytes(msg.getMessageIndex()));
            data = bao.toByteArray();
            bao.close();
            // The header of whole message
            bao = new ByteArrayOutputStream();
            bao.write(Headers.MESSAGE_BEGIN);
            bao.write(getLengthBytes(data.length));
            bao.write(data);
            //
            data = bao.toByteArray();
            bao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static User parseUser(byte[] data) {
        User user = new User();
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        int i = 0;
        int length = data.length;
        try {
            while (i < length) {
                i++;
                switch (dataStream.read()) {
                    case Headers.HEADER_USER_NAME:
                        int nameLength = parseLengthInt(dataStream);
                        i += getLengthSize(nameLength);
                        byte[] name = new byte[nameLength];
                        dataStream.read(name, 0, nameLength);
                        user.setName(new String(name));
                        i += nameLength;
                        break;
                    case Headers.HEADER_USER_IDENTITY:
                        int identityLength = parseLengthInt(dataStream);
                        i += getLengthSize(identityLength);
                        byte[] identity = new byte[identityLength];
                        dataStream.read(identity, 0, identityLength);
                        user.setIdentity(new String(identity));
                        i += identityLength;
                        break;
                    case Headers.HEADER_USER_PASSWORD:
                        int passwordLength = parseLengthInt(dataStream);
                        i += getLengthSize(passwordLength);
                        byte[] password = new byte[passwordLength];
                        dataStream.read(password, 0, passwordLength);
                        user.setmPassword(new String(password));
                        i += passwordLength;
                        break;
                }
            }
            dataStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public static byte[] composeUser(User user) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            // name
            bao.write(Headers.HEADER_USER_NAME);
            byte[] name = user.getName().getBytes();
            bao.write(getLengthBytes(name.length));
            bao.write(name);
            // from
            bao.write(Headers.HEADER_USER_IDENTITY);
            byte[] identity = user.getIdentity().getBytes();
            bao.write(getLengthBytes(identity.length));
            bao.write(identity);
            // password
            bao.write(Headers.HEADER_USER_PASSWORD);
            byte[] password = user.getPassword().getBytes();
            bao.write(getLengthBytes(password.length));
            bao.write(password);
            //
            data = bao.toByteArray();
            bao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static int getLengthSize(long length) {
        int i;
        long max = 127;
        for (i = 0; i < 5; i++) {
            if (length < max) {
                break;
            }
            max = (max << 7) | 0x7fl;
        }
        return i;
    }

    private static byte[] getLengthBytes(long length) {
        int i = getLengthSize(length);
        int j = i;
        byte[] bytes = new byte[j + 1];
        while (i > 0) {
            long temp = length >>> (i * 7);
            temp = temp & 0x7f;
            bytes[j - i] = (byte) ((temp | 0x80) & 0xff);
            i--;
        }
        bytes[j] = (byte) (length & 0x7f);
        return bytes;
    }

    private static int parseLengthInt(InputStream pduDataStream) throws IOException {
        int result = 0;
        int temp = pduDataStream.read();
        if (temp == -1) {
            throw new IOException("Reach the end of the stream");
        }
        while ((temp & 0x80) != 0) {
            result = result << 7;
            result |= temp & 0x7F;
            temp = pduDataStream.read();
            if (temp == -1) {
                throw new IOException("Reach the end of the stream");
            }
        }
        result = result << 7;
        result |= temp & 0x7F;
        return result;
    }

    private static byte[] getIntBytes(long value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value & 0xff);
            value = value >> 8;
        }
        return bytes;
    }

    private static int parseInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value = value << 8 + bytes[i];
        }
        return value;
    }
}
