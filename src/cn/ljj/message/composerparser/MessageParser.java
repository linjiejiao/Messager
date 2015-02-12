
package cn.ljj.message.composerparser;

import java.io.IOException;
import java.io.InputStream;

import cn.ljj.message.IPMessage;
import cn.ljj.message.Headers;

public class MessageParser extends BaseParser {

    public static IPMessage parseMessage(InputStream data) throws IOException {
        InputStreamReader dataStream = new InputStreamReader(data);
        IPMessage msg = new IPMessage();
        byte b = (byte) dataStream.read();
        while (b != Headers.MESSAGE_BEGIN) {
            // System.out.println(TAG + " skip byte" + b);
            b = (byte) dataStream.read();
            if (b == -1) {// reach stream end
                throw new IOException("Reach the end of the stream");
            }
        }
        int length = parseLengthInt(dataStream);
        while (dataStream.getIndex() < length - 1) {
            switch (dataStream.read()) {
                case Headers.HEADER_MESSAGE_BODY:
                    msg.setBody(parseByteArray(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_DATE:
                    msg.setDate(parseString(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_FROM:
                    msg.setFrom(parseString(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_TO:
                    msg.setTo(parseString(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_TYPE:
                    msg.setMessageType(parseInt(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_INDEX:
                    msg.setMessageIndex(parseInt(dataStream));
                    break;
                case Headers.HEADER_MESSAGE_ID:
                    msg.setMessageId(parseInt(dataStream));
                    break;
                case Headers.HEADER_TRANSACTION_ID:
                    msg.setTransactionId(parseInt(dataStream));
                    break;
                case Headers.MESSAGE_END:
                    return msg;
                default:
                    return null;
            }
        }
        return msg;
    }
}
