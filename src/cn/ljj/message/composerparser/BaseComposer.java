
package cn.ljj.message.composerparser;

public class BaseComposer {
    protected static byte[] composeString(String str) {
        byte[] strData = str.getBytes();
        int strDataSize = strData.length;
        int lengthSize = getLengthSize(strDataSize);
        byte[] lengthData = getLengthBytes(lengthSize);
        int wholeSize = lengthSize + strDataSize;
        byte[] ret = new byte[wholeSize];
        for (int i = 0; i < lengthSize; i++) {
            ret[i] = lengthData[i];
        }
        for (int i = 0; i < strDataSize; i++) {
            ret[i + lengthSize] = strData[i];
        }
        return ret;
    }

    protected static byte[] composeInt(int i) {
        return getIntBytes(i);
    }

    protected static byte[] composeByteArray(byte[] data) {
        int dataSize = data.length;
        int lengthSize = getLengthSize(dataSize);
        byte[] lengthData = getLengthBytes(lengthSize);
        int wholeSize = lengthSize + dataSize;
        byte[] ret = new byte[wholeSize];
        for (int i = 0; i < lengthSize; i++) {
            ret[i] = lengthData[i];
        }
        for (int i = 0; i < dataSize; i++) {
            ret[i + lengthSize] = data[i];
        }
        return ret;
    }

    protected static int getLengthSize(int length) {
        int i;
        long max = 127;
        for (i = 0; i < 5; i++) {
            if (length < max) {
                break;
            }
            max = (max << 7) | 0x7fl;
        }
        return i;
    }

    protected static byte[] getLengthBytes(int length) {
        int i = getLengthSize(length);
        int j = i;
        byte[] bytes = new byte[j + 1];
        while (i > 0) {
            long temp = length >>> (i * 7);
            temp = temp & 0x7f;
            bytes[j - i] = (byte) ((temp | 0x80) & 0xff);
            i--;
        }
        bytes[j] = (byte) (length & 0x7f);
        return bytes;
    }

    protected static byte[] getIntBytes(int value) {
        byte[] bytes = new byte[4];
        for (int i = 3; i >= 0; i--) { // DESC
            bytes[i] = (byte) (value & 0xff);
            value = value >> 8;
        }
        return bytes;
    }

}
