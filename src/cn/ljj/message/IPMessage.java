package cn.ljj.message;

import java.util.Arrays;

public class IPMessage {
	private String mFrom;
	private String mTo;
    private String mDate;
	private int mMessageType;
	private int mMessageIndex;
    private int mMessageId;
    private int mTransactionId;
	private byte[] mBody;
	public static final String TAG = "IPMessage";

	public String getFrom() {
		return mFrom;
	}

	public void setFrom(String from) {
		mFrom = from;
	}

	public String getTo() {
		return mTo;
	}

	public void setTo(String to) {
		mTo = to;
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
        return "IPMessage [mFrom=" + mFrom + ", mTo=" + mTo + ", mMessageType=" + mMessageType
                + ", mMessageIndex=" + mMessageIndex + ", mMessageId=" + mMessageId
                + ", mTransationId=" + mTransactionId + ", mDate=" + mDate + ", mBody="
                + Arrays.toString(mBody) + "]";
    }

}
