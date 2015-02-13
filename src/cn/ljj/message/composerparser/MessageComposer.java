
package cn.ljj.message.composerparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.ljj.message.IPMessage;
import cn.ljj.message.Headers;

public class MessageComposer extends BaseComposer {

    public static byte[] composeMessage(IPMessage msg) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        for(int i : Headers.INT_HEADERS){
            Integer data = getIntData(msg, i);
            if(data == null){
                continue;
            }
            byte[] buffer = composeInt(data);
            bao.write(i);
            bao.write(buffer);
        }
        for(int i : Headers.STRING_HEADERS){
            String data = getStringData(msg, i);
            if(data == null){
                continue;
            }
            byte[] buffer = composeString(data);
            bao.write(i);
            bao.write(getLengthBytes(buffer.length));
            bao.write(buffer);
        }
        for(int i : Headers.BYTE_HEADERS){
            byte[] data = getByteArrayData(msg, i);
            if(data == null){
                continue;
            }
            byte[] buffer = composeByteArray(data);
            bao.write(i);
            bao.write(getLengthBytes(buffer.length));
            bao.write(buffer);
        }
        byte[] data = bao.toByteArray();
        bao.close();
        // header for whole message
        bao = new ByteArrayOutputStream();
        bao.write(Headers.MESSAGE_BEGIN);
        bao.write(getLengthBytes(data.length + 1)); // Headers.MESSAGE_END
        bao.write(data);
        bao.write(Headers.MESSAGE_END);
        return bao.toByteArray();
    }

    private static byte[] getByteArrayData(IPMessage msg, int type){
        byte[] bytes = null;
        switch(type){
            case Headers.HEADER_MESSAGE_BODY:
                bytes = msg.getBody();
                break;
        }
        return bytes;
    }
 
    private static String getStringData(IPMessage msg, int type){
        String str = null;
        switch(type){
            case Headers.HEADER_MESSAGE_FROM:
                str = msg.getFrom();
                break;
            case Headers.HEADER_MESSAGE_TO:
                str = msg.getTo();
                break;
            case Headers.HEADER_MESSAGE_DATE:
                str = msg.getDate();
                break;
        }
        return str;
    }
    
    private static Integer getIntData(IPMessage msg, int type){
        Integer i = null;
        switch(type){
            case Headers.HEADER_TRANSACTION_ID:
                i = msg.getTransactionId();
                break;
            case Headers.HEADER_MESSAGE_TYPE:
                i = msg.getMessageType();
                break;
            case Headers.HEADER_MESSAGE_ID:
                i = msg.getMessageId();
                break;
            case Headers.HEADER_MESSAGE_INDEX:
                i = msg.getMessageIndex();
                break;
        }
        return i;
    }
}
