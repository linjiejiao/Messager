
package cn.ljj.message;

public class Headers {
    public static final byte MESSAGE_END = (byte)0xff;

    public static final byte MESSAGE_HEADER_BASE = 0x40;
    // Message Type Header
    public static final byte MESSAGE_BEGIN = MESSAGE_HEADER_BASE;
    // Message Type Header
    public static final byte HEADER_MESSAGE_TYPE = MESSAGE_HEADER_BASE + 0x01;
    // Message Sender(Source) Header
    public static final byte HEADER_MESSAGE_FROM_ID = MESSAGE_HEADER_BASE + 0x02;
    // Message Final Receiver(destination) Header
    public static final byte HEADER_MESSAGE_TO_ID = MESSAGE_HEADER_BASE + 0x03;
    // Message Sent Date
    public static final byte HEADER_MESSAGE_DATE = MESSAGE_HEADER_BASE + 0x04;
    // Message Body, could be text or a files
    public static final byte HEADER_MESSAGE_BODY = MESSAGE_HEADER_BASE + 0x05;
    // Index in the conversation, or the part index if this message is a part of
    // a file.
    public static final byte HEADER_MESSAGE_INDEX = MESSAGE_HEADER_BASE + 0x06;
    // A specific id for a message
    public static final byte HEADER_MESSAGE_ID = MESSAGE_HEADER_BASE + 0x07;
    // A specific id for a transaction
    public static final byte HEADER_TRANSACTION_ID = MESSAGE_HEADER_BASE + 0x08;
    // The name of the sender user
    public static final byte HEADER_MESSAGE_FROM_NAME = MESSAGE_HEADER_BASE + 0x09;
    // The name of the target user
    public static final byte HEADER_MESSAGE_TO_NAME = MESSAGE_HEADER_BASE + 0x0a;

    public static final byte USER_HEADER_BASE = (byte) 0x80;
    // User Begin
    public static final byte HEADER_USER_BEGIN = USER_HEADER_BASE;
    // User Name
    public static final byte HEADER_USER_NAME = USER_HEADER_BASE + 0x01;
    // User Identity
    public static final byte HEADER_USER_IDENTITY = USER_HEADER_BASE + 0x02;
    // User Password
    public static final byte HEADER_USER_PASSWORD = USER_HEADER_BASE + 0x03;
    // User Password
    public static final byte HEADER_USER_STATUS = USER_HEADER_BASE + 0x04;
    // User End
    public static final byte HEADER_USER_END = USER_HEADER_BASE + 0x05;

}
