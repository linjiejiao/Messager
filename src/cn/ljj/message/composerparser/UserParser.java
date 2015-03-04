
package cn.ljj.message.composerparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cn.ljj.message.Headers;
import cn.ljj.user.User;

public class UserParser extends BaseParser {
    public static User parseUser(byte[] data) {
        User user = new User();
        InputStreamReader dataStream = null;
        try {
            dataStream = new InputStreamReader(new ByteArrayInputStream(data));
            while (dataStream.getIndex() < data.length) {
                switch (dataStream.read()) {
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
