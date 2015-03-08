
package cn.ljj.test;

import java.util.List;

import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.UserComposer;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.messager.R;
import cn.ljj.test.ConnectionThread.IMessageReceived;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener, IMessageReceived {
    public String TAG = "MainActivity";
    private EditText editLoginId = null;
    private EditText editLoginPassword = null;
    private EditText editContent = null;
    private TextView textRecv = null;
    private User mLocalUser = new User();
    private User mTargetUser = null;
    private Button btnLogin = null;
    private Button btnLogout = null;
    private Button btnSend = null;
    private Spinner mUserChosser = null;
    private UserAdapter mAdapter = null;
    private ConnectionThread mConnectionThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnSend = (Button) findViewById(R.id.btn_send);

        editLoginId = (EditText) findViewById(R.id.edit_id);
        editLoginPassword = (EditText) findViewById(R.id.edit_password);
        editContent = (EditText) findViewById(R.id.edit_msg);
        textRecv = (TextView) findViewById(R.id.text_recv);
        mUserChosser = (Spinner) findViewById(R.id.spinner_user);
        editLoginId.setText("123");
        editLoginPassword.setText("123");
        editContent.setText("content");

        mUserChosser.setOnItemSelectedListener(this);
        mAdapter = new UserAdapter(null, this);
        mUserChosser.setAdapter(mAdapter);

        btnLogin.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        updateViews(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_login:
                mConnectionThread = new ConnectionThread();
                mConnectionThread.start();
                mConnectionThread.setMessageRecvCallback(this);
                IPMessage msg = getLoginMessage();
                mConnectionThread.sendMessage(msg);
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_send:
                sendContent();
                break;
        }
    }

    private void logout(){
        mConnectionThread.sendMessage(getLogoutMessage());
        mConnectionThread.quit();
        updateViews(false);
    }
    private void sendContent(){
        IPMessage msg = new IPMessage();
        msg.setBody(editContent.getText().toString().getBytes());
        msg.setDate(""+System.currentTimeMillis());
        msg.setFromName(mLocalUser.getName());
        msg.setToName(mTargetUser.getName());
        msg.setMessageIndex(0);
        msg.setMessageType(IPMessage.MESSAGE_TYPE_MESSAGE);
        msg.setTransactionId(0);
        msg.setFromId(mLocalUser.getIdentity());
        msg.setToId(mTargetUser.getIdentity());
        msg.setMessageId(0);
        mConnectionThread.sendMessage(msg);
    }
 
    private void getAllUsers(){
        IPMessage msg = new IPMessage();
        msg.setDate(System.currentTimeMillis() + "");
        msg.setMessageType(IPMessage.MESSAGE_TYPE_GET_USERS);
        mConnectionThread.sendMessage(msg);
    }
    
    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
        mTargetUser = (User) mAdapter.getItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapter) {

    }

    private IPMessage getLoginMessage() {
        String id = editLoginId.getText().toString();
        mLocalUser.setName(id);
        mLocalUser.setIdentity(Integer.parseInt(id));
        mLocalUser.setPassword(editLoginPassword.getText().toString());
        mLocalUser.setStatus(User.STATUS_ON_LINE);
        IPMessage msg = null;
        try {
            msg = new IPMessage();
            msg.setBody(UserComposer.composeUser(mLocalUser));
            msg.setDate(System.currentTimeMillis() + "");
            msg.setMessageType(IPMessage.MESSAGE_TYPE_LOGIN);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return msg;
    }

    private IPMessage getLogoutMessage() {
        String id = editLoginId.getText().toString();
        mLocalUser.setName(id);
        mLocalUser.setIdentity(Integer.parseInt(id));
        mLocalUser.setPassword(editLoginPassword.getText().toString());
        mLocalUser.setStatus(User.STATUS_OFF_LINE);
        IPMessage msg = null;
        try {
            msg = new IPMessage();
            msg.setBody(UserComposer.composeUser(mLocalUser));
            msg.setDate(System.currentTimeMillis() + "");
            msg.setMessageType(IPMessage.MESSAGE_TYPE_CHANGE_STATUS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return msg;
    }

    @Override
    public void onMessageReceived(IPMessage msg) {
        handleIncomeMessage(msg);
    }
    
    private void handleIncomeMessage(final IPMessage msg){
        if (msg != null) {
            switch (msg.getMessageType()) {
                case IPMessage.MESSAGE_TYPE_GET_USERS:
                    final List<User> users = UserParser.parseUsers(msg.getBody());
                    Log.e(TAG, "get All user :" + users);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setDataAndNotify(users);
                        }
                    });
                    break;
                case IPMessage.MESSAGE_TYPE_MESSAGE:
                    final String content = new String(msg.getBody());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textRecv.setText(msg.getFromName() + ": " + content + "\r\n" + textRecv.getText());
                        }
                    });
                    break;
                case IPMessage.MESSAGE_TYPE_RESPOND:
                    final String respon = new String(msg.getBody());
                    if("LOGIN_OK".equals(respon)){
                        getAllUsers();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateViews(true);
                            }
                        });
//                    }else if("CHANGE_STATUS_OK".equals(respon)){
//                        
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textRecv.setText(respon + "\r\n" + textRecv.getText());
                            }
                        });
                    }
                    break;
                case IPMessage.MESSAGE_TYPE_CONNECTION_ERROR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            
                        }
                    });
                    break;
            }
        }else{
            mConnectionThread.sendMessage(getLogoutMessage());
            mConnectionThread.quit();
        }
    }
    
    private void updateViews(boolean hadLogin){
        if(hadLogin){
            btnLogin.setEnabled(false);
            btnLogout.setEnabled(true);
            btnSend.setEnabled(true);
            editLoginId.setEnabled(false);
            editLoginPassword.setEnabled(false);
            editContent.setEnabled(true);
            mUserChosser.setEnabled(true);
        }else{
            btnLogin.setEnabled(true);
            btnLogout.setEnabled(false);
            btnSend.setEnabled(false);
            editLoginId.setEnabled(true);
            editLoginPassword.setEnabled(true);
            editContent.setEnabled(false);
            mUserChosser.setEnabled(false);
        }
    }
}
