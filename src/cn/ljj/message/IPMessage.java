
package cn.ljj.message;

import java.util.Arrays;

public class IPMessage {
    public static final String TAG = "IPMessage";

    private int mFromId;
    private int mToId;
    private String mFromName;
    private String mToName;
    private String mDate;
    private int mMessageType;
    private int mMessageIndex;
    private int mMessageId;
    private int mTransactionId;
    private byte[] mBody;

    public int getFromId() {
        return mFromId;
    }

    public void setFromId(int from) {
        mFromId = from;
    }

    public int getToId() {
        return mToId;
    }

    public void setToId(int to) {
        mToId = to;
    }

    public String getFromName() {
        return mFromName;
    }

    public void setFromName(String fromName) {
        mFromName = fromName;
    }

    public String getToName() {
        return mToName;
    }

    public void setToName(String toName) {
        mToName = toName;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void setMessageType(int type) {
        mMessageType = type;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public byte[] getBody() {
        return mBody;
    }

    public void setBody(byte[] body) {
        mBody = body;
    }

    public int getMessageIndex() {
        return mMessageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        mMessageIndex = messageIndex;
    }

    public int getMessageId() {
        return mMessageId;
    }

    public void setMessageId(int messageId) {
        mMessageId = messageId;
    }

    public int getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(int transactionId) {
        mTransactionId = transactionId;
    }

    @Override
    public String toString() {
        return "IPMessage [mFromId=" + mFromId + ", mToId=" + mToId + ", mFromName=" + mFromName
                + ", mToName=" + mToName + ", mDate=" + mDate + ", mMessageType=" + mMessageType
                + ", mMessageIndex=" + mMessageIndex + ", mMessageId=" + mMessageId
                + ", mTransactionId=" + mTransactionId + ", mBody=" + Arrays.toString(mBody) + "]";
    }

    public static final int[] INT_HEADERS = new int[] {
            Headers.HEADER_MESSAGE_FROM_ID, Headers.HEADER_MESSAGE_TO_ID,
            Headers.HEADER_MESSAGE_INDEX, Headers.HEADER_MESSAGE_ID, Headers.HEADER_TRANSACTION_ID,
            Headers.HEADER_MESSAGE_TYPE
    };

    public static final int[] STRING_HEADERS = new int[] {
            Headers.HEADER_MESSAGE_DATE, Headers.HEADER_MESSAGE_FROM_NAME,
            Headers.HEADER_MESSAGE_TO_NAME
    };

    public static final int[] BYTE_HEADERS = new int[] {
        Headers.HEADER_MESSAGE_BODY
    };
}
