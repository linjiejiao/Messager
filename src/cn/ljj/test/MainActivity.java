
package cn.ljj.test;

import java.util.List;

import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.UserComposer;
import cn.ljj.message.composerparser.UserParser;
import cn.ljj.messager.R;
import cn.ljj.test.ConnectionThread.IMessageReceived;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener,
        IMessageReceived {
    public static final String TAG = "MainActivity";
    public static final String KEY_SERVER_IP = "key_server_ip";
    public static final String KEY_SERVER_PORT = "key_server_port";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_PASSWORD = "key_user_password";
    public static final String DEF_SERVER_IP = "192.168.1.113";
    public static final String DEF_SERVER_PORT = "8888";

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
    private Dialog mDialog = null;
    private TextView editServerIP = null;
    private TextView editServerPort = null;
    private Button btnOk = null;

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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        editLoginId.setText(sp.getString(KEY_USER_ID, "123"));
        editLoginPassword.setText(sp.getString(KEY_USER_PASSWORD, "123"));
        editContent.setText("content");

        mUserChosser.setOnItemSelectedListener(this);
        mAdapter = new UserAdapter(null, this);
        mUserChosser.setAdapter(mAdapter);

        btnLogin.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        updateViews(false);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            IPMessage msg = (IPMessage) message.obj;
            final String msgBody = new String(msg.getBody());
            switch (msg.getMessageType()) {
                case IPMessage.MESSAGE_TYPE_GET_USERS:
                    List<User> users = UserParser.parseUsers(msg.getBody());
                    mAdapter.setDataAndNotify(users);
                    mUserChosser.setSelection(0);
                    mTargetUser = (User) mAdapter.getItem(0);
                    break;
                case IPMessage.MESSAGE_TYPE_MESSAGE:
                    textRecv.setText(msg.getFromName() + ": " + msgBody + "\r\n"
                            + textRecv.getText());
                    break;
                case IPMessage.MESSAGE_TYPE_RESPOND:
                    if ("LOGIN_OK".equals(msgBody)) {
                        getAllUsers();
                        updateViews(true);
                    } else if ("LOGIN_FAIL".equals(msgBody)) {
                        mConnectionThread.quit();
                        updateViews(false);
                    } else {
                    }
                    textRecv.setText(msgBody + "\r\n" + textRecv.getText());
                    break;
                case IPMessage.MESSAGE_TYPE_CONNECTION_ERROR:
                    mConnectionThread.quit();
                    updateViews(false);
                    textRecv.setText(msgBody + "\r\n" + textRecv.getText());
                    break;
            }
            super.handleMessage(message);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                btnLogin.setEnabled(false);
                mConnectionThread = new ConnectionThread(this);
                mConnectionThread.setMessageRecvCallback(this);
                IPMessage msg = getLoginMessage();
                mConnectionThread.sendMessage(msg);
                mConnectionThread.start();
                saveUserLoinInfo();
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_send:
                sendContent();
                break;
            case R.id.btn_ok:
                configServerAddress();
                getServerConfigDialog().dismiss();
                break;
        }
    }

    private void logout() {
        mConnectionThread.sendMessage(getLogoutMessage());
        mConnectionThread.quit();
        updateViews(false);
    }

    private void sendContent() {
        IPMessage msg = new IPMessage();
        msg.setBody(editContent.getText().toString().getBytes());
        msg.setDate("" + System.currentTimeMillis());
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

    private void getAllUsers() {
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

    private void handleIncomeMessage(final IPMessage msg) {
        if (msg != null) {
            Message message = mHandler.obtainMessage();
            message.obj = msg;
            message.sendToTarget();
        }
    }

    private void updateViews(boolean hadLogin) {
        if (hadLogin) {
            btnLogin.setEnabled(false);
            btnLogout.setEnabled(true);
            btnSend.setEnabled(true);
            editLoginId.setEnabled(false);
            editLoginPassword.setEnabled(false);
            editContent.setEnabled(true);
            mUserChosser.setEnabled(true);
        } else {
            btnLogin.setEnabled(true);
            btnLogout.setEnabled(false);
            btnSend.setEnabled(false);
            editLoginId.setEnabled(true);
            editLoginPassword.setEnabled(true);
            editContent.setEnabled(false);
            mUserChosser.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mConnectionThread != null) {
            mConnectionThread.quit();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getServerConfigDialog().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Dialog getServerConfigDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            View view = getLayoutInflater().inflate(R.layout.layout_server, null);
            mDialog.setContentView(view);
            mDialog.setTitle("Config Server Address");
            editServerIP = (TextView) view.findViewById(R.id.edit_ip);
            editServerPort = (TextView) view.findViewById(R.id.edit_port);
            btnOk = (Button) view.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(this);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        editServerIP.setText(sp.getString(KEY_SERVER_IP, DEF_SERVER_IP));
        editServerPort.setText(sp.getString(KEY_SERVER_PORT, DEF_SERVER_PORT));
        return mDialog;
    }

    private void configServerAddress() {
        String ip = editServerIP.getText().toString();
        String port = editServerPort.getText().toString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(KEY_SERVER_IP, ip);
        editor.putString(KEY_SERVER_PORT, port);
        editor.commit();
    }

    private void saveUserLoinInfo() {
        String id = editLoginId.getText().toString();
        String pw = editLoginPassword.getText().toString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_ID, pw);
        editor.commit();
    }
}
