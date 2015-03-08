
package cn.ljj.message.composerparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.ljj.message.Headers;
import cn.ljj.message.User;

public class UserComposer extends BaseComposer {

    public static byte[] composeUser(User user) {
        byte[] composeData = null;
        ByteArrayOutputStream bao = null;
        try {
            bao = new ByteArrayOutputStream();
            for (int i : User.INT_HEADERS) {
                Integer data = getIntData(user, i);
                if (data == null) {
                    continue;
                }
                byte[] buffer = composeInt(data);
                bao.write(i);
                bao.write(buffer);
            }
            for (int i : User.STRING_HEADERS) {
                String data = getStringData(user, i);
                if (data == null) {
                    continue;
                }
                byte[] buffer = composeString(data);
                bao.write(i);
                bao.write(getLengthBytes(buffer.length));
                bao.write(buffer);
            }
            byte[] data = bao.toByteArray();
            bao.close();
            // header for whole user
            bao = new ByteArrayOutputStream();
            bao.write(Headers.HEADER_USER_BEGIN);
            bao.write(getLengthBytes(data.length + 1)); // 1 is for
                                                        // Headers.HEADER_USER_END
            bao.write(data);
            bao.write(Headers.HEADER_USER_END);
            composeData = bao.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bao.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return composeData;
    }

    private static String getStringData(User user, int type) {
        String str = null;
        switch (type) {
            case Headers.HEADER_USER_NAME:
                str = user.getName();
                break;
            case Headers.HEADER_USER_PASSWORD:
                str = user.getPassword();
                break;
        }
        return str;
    }

    private static Integer getIntData(User user, int type) {
        Integer i = null;
        switch (type) {
            case Headers.HEADER_USER_IDENTITY:
                i = user.getIdentity();
                break;
            case Headers.HEADER_USER_STATUS:
                i = user.getStatus();
                break;
        }
        return i;
    }
}
