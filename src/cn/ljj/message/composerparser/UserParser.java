
package cn.ljj.message.composerparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cn.ljj.message.Headers;
import cn.ljj.user.User;

public class UserParser extends BaseParser {
    public static User parseUser(byte[] data) {
        User user = new User();
        InputStreamReader dataStream = null;
        int i = 0;
        try {
            dataStream = new InputStreamReader(new ByteArrayInputStream(data));
            while (i < data.length) {
                i++;
                switch (dataStream.read()) {
                    case Headers.HEADER_USER_NAME:
                        user.setName(parseString(dataStream));
                        break;
                    case Headers.HEADER_USER_IDENTITY:
                        user.setIdentity(parseString(dataStream));
                        break;
                    case Headers.HEADER_USER_PASSWORD:
                        user.setmPassword(parseString(dataStream));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(dataStream != null){
                dataStream.close();
            }
        }
        return user;
    }
}
