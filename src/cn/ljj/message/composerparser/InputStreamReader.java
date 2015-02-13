
package cn.ljj.message.composerparser;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamReader {
    private InputStream mDataStream = null;
    private int mIndex = 0;

    public InputStreamReader(InputStream input) {
        if (input == null) {
            throw (new NullPointerException());
        }
        mDataStream = input;
    }

    public int read() throws IOException {
        mIndex++;
        return mDataStream.read();
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        int len = mDataStream.read(buffer, offset, length);
        if (len != -1) {
            mIndex = mIndex + len;
        }
        return len;
    }

    public int getIndex() {
        return mIndex;
    }
 
    public void resetIndex() {
        mIndex = 0;
    }

    public boolean close(){
        if(mDataStream != null){
            try {
                mDataStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
