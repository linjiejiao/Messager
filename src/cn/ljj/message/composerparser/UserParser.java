
package cn.ljj.message.composerparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ljj.message.Headers;
import cn.ljj.message.User;

public class UserParser extends BaseParser {
    public static User parseUser(byte[] data) {
        User user = null;
        InputStreamReader dataStream = null;
        try {
            dataStream = new InputStreamReader(new ByteArrayInputStream(data));
            user = parseUser(dataStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dataStream != null) {
                dataStream.close();
            }
        }
        return user;
    }

    public static List<User> parseUsers(byte[] data) {
        List<User> users = new ArrayList<User>();
        InputStreamReader dataStream = null;
        try {
            dataStream = new InputStreamReader(new ByteArrayInputStream(data));
            int index = 0;
            while (index < data.length) {
                User user = parseUser(dataStream);
                System.out.println("parseUsers user: " + user);
                if (user != null) {
                    users.add(user);
                }else{
                    break;
                }
                index += dataStream.getIndex();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dataStream != null) {
                dataStream.close();
            }
        }
        return users;
    }

    private static User parseUser(InputStreamReader dataStream) {
        User user = new User();
        try {
            dataStream.resetIndex();
            byte b = (byte) dataStream.read();
            while (b != Headers.HEADER_USER_BEGIN ) {
                System.out.println("parseUser skip byte: " + b);
                b = (byte) dataStream.read();
                if (b == -1) {// reach stream end
                    throw new IOException("Reach the end of the stream");
                }
            }
            int length = parseLengthInt(dataStream);
            while (dataStream.getIndex() <= length + 1) {
                byte header=(byte) dataStream.read();
                switch (header) {
                    case Headers.HEADER_USER_NAME:
                        user.setName(parseString(dataStream));
                        break;
                    case Headers.HEADER_USER_IDENTITY:
                        user.setIdentity(parseInt(dataStream));
                        break;
                    case Headers.HEADER_USER_PASSWORD:
                        user.setPassword(parseString(dataStream));
                        break;
                    case Headers.HEADER_USER_STATUS:
                        user.setStatus(parseInt(dataStream));
                        break;
                    case Headers.HEADER_USER_END:
                        return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }
}
