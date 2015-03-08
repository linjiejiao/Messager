
package cn.ljj.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import cn.ljj.message.IPMessage;
import cn.ljj.message.composerparser.MessageComposer;
import cn.ljj.message.composerparser.MessageParser;

public class ConnectionThread extends Thread {
    private boolean mStop = false;
    private List<IPMessage> mMessageQueen = new ArrayList<IPMessage>();
    private IMessageReceived mCallback = null;
    private OutputStream mOps = null;
    private InputStream mIps = null;
    private Socket mSocket = null;
    public static final String TAG = "ConnectionThread";

    public synchronized void quit(){
        mStop = true;
        notify();
    }

    @Override
    public void run() {
        setName(TAG);
        if(!connectToServer()){
            return;
        }
        mReceiverThread.start();
        Log.e(TAG , "run");
        while (!mStop) {
            sendMessageQueen();
            waitForMessageSend();
        }
        try {
            mIps.close();
            mOps.close();
            mSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG , "run end");
        super.run();
    }

    public synchronized void sendMessageQueen() {
        Log.e(TAG , "sendMessageQueen");
        try {
            while(mMessageQueen.size() > 0){
                IPMessage msg = mMessageQueen.get(0);
                mOps.write(MessageComposer.composeMessage(msg));
                mMessageQueen.remove(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mStop = true;
            notify();
        }
    }

    public synchronized void sendMessage(IPMessage msg) {
        Log.e(TAG , "sendMessage");
        if (msg != null) {
            mMessageQueen.add(msg);
            notify();
        }
    }

    private synchronized void waitForMessageSend() {
        Log.e(TAG , "waitForMessageSend");
        if(mMessageQueen.size() > 0){   //mMessageQueen is not empty,
            return;
        }
        try {
            wait();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            mStop = true;
            notify();
        }
    }

    private synchronized boolean connectToServer() {
        Log.e(TAG , "connectToServer");
        try {
            InetSocketAddress serverAddr = new InetSocketAddress("192.168.1.113", 8888);
            mSocket = new Socket();
            mSocket.connect(serverAddr);
            mOps = mSocket.getOutputStream();
            mIps = mSocket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            mStop = true;
            notify();
            return false;
        }
        return true;
    }

    Thread mReceiverThread = new Thread() {
        @Override
        public void run() {
            setName("ReceiverThread");
            Log.e("ReceiverThread" , "run");
            while (!mStop) {
                IPMessage msg = MessageParser.parseMessage(mIps);
                Log.e("ReceiverThread" , "receive a msg=" + msg);
                if (mCallback != null) {
                    mCallback.onMessageReceived(msg);
                }
            }
            Log.e("ReceiverThread" , "run end");
        }
    };

    interface IMessageReceived {
        public void onMessageReceived(IPMessage msg);
    }

    public void setMessageRecvCallback(IMessageReceived cbf) {
        mCallback = cbf;
    }
}
