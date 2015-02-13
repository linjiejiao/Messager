package cn.ljj.message.composerparser;

import java.io.ByteArrayOutputStream;

import cn.ljj.message.Headers;
import cn.ljj.user.User;

public class UserComposer extends BaseComposer {
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
}
