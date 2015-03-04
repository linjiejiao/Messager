
package cn.ljj.message.composerparser;

import java.io.IOException;

public class BaseParser {

    protected static byte[] parseByteArray(InputStreamReader dataStream) throws IOException {
        byte[] data = null;
        int bodyLength = parseLengthInt(dataStream);
        data = new byte[bodyLength];
        dataStream.read(data, 0, bodyLength);
        return data;
    }

    protected static int parseInt(InputStreamReader dataStream) throws IOException {
        byte[] bytes = new byte[4];
        dataStream.read(bytes, 0, 4);
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int b = bytes[i] >= 0 ? bytes[i]: bytes[i] + 256 ;
            value = (value << 8) | b ;
        }
        return value;
    }

    protected static String parseString(InputStreamReader dataStream) throws IOException {
        String str = null;
        int dateLength = parseLengthInt(dataStream);
        byte[] data = new byte[dateLength];
        dataStream.read(data, 0, dateLength);
        str = new String(data);
        return str;
    }

    protected static int parseLengthInt(InputStreamReader pduDataStream) throws IOException {
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

}
