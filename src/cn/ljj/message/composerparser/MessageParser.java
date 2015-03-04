
package cn.ljj.message.composerparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import cn.ljj.message.IPMessage;
import cn.ljj.message.Headers;

public class MessageParser extends BaseParser {

    public static IPMessage parseMessage(InputStream data) throws IOException {
        InputStreamReader dataStream = new InputStreamReader(data);
        byte b = (byte) dataStream.read();
        while (b != Headers.MESSAGE_BEGIN) {
            System.out.println("parseMessage skip byte: " + b);
            b = (byte) dataStream.read();
            if (b == -1) {// reach stream end
                throw new IOException("Reach the end of the stream");
            }
        }
        int length = parseLengthInt(dataStream);
        byte[] messageData = new byte[length];
        data.read(messageData, 0, length);
        return parseMessage(messageData);
    }

    private static IPMessage parseMessage(byte[] data) {
        IPMessage msg = null;
        InputStreamReader dataStream = null;
        try {
            dataStream = new InputStreamReader(new ByteArrayInputStream(data));
            msg = new IPMessage();
            while (dataStream.getIndex() < data.length - 1) {
                int header = dataStream.read();
                switch (header) {
                    case Headers.HEADER_MESSAGE_BODY:
                        msg.setBody(parseByteArray(dataStream));
                        break;
                    case Headers.HEADER_MESSAGE_DATE:
                        msg.setDate(parseString(dataStream));
                        break;
                    case Headers.HEADER_MESSAGE_FROM_ID:
                        msg.setFromId(parseInt(dataStream));
                        break;
                    case Headers.HEADER_MESSAGE_TO_ID:
                        msg.setToId(parseInt(dataStream));
                        break;
                    case Headers.HEADER_MESSAGE_FROM_NAME:
                        msg.setFromName(parseString(dataStream));
                        break;
                    case Headers.HEADER_MESSAGE_TO_NAME:
                        msg.setToName(parseString(dataStream));
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataStream != null) {
                dataStream.close();
            }
        }
        return msg;
    }
}
