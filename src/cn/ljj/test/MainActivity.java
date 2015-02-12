
package cn.ljj.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import cn.ljj.message.ComposerParser;
import cn.ljj.message.IPMessage;
import cn.ljj.message.Headers;
import cn.ljj.user.User;
import ljj.test.R;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    protected String TAG = "MainActivity";
    EditText editName;
    EditText editContent;
    EditText editTo;
    TextView textRecv;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        editName = (EditText) findViewById(R.id.edit_name);
        editContent = (EditText) findViewById(R.id.edit_msg);
        editTo = (EditText) findViewById(R.id.edit_to);
        textRecv = (TextView) findViewById(R.id.text_recv);
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IPMessage msg = new IPMessage();
                user = new User();
                user.setName(editName.getText().toString());
                user.setIdentity("1234567890");
                user.setmPassword("123456789");

                msg.setBody(ComposerParser.composeUser(user));
                msg.setDate("date");
                msg.setFrom(user.getName());
                msg.setTo("host");
                msg.setMessageIndex(1);
                msg.setMessageType(Headers.MESSAGE_TYPE_LOGIN);
                try {
                    ops.write("abcdefg".getBytes());
                    ops.write(ComposerParser.composeMessage(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                    new Thread(mClientThread).start();
                }
            }
        });
        Button btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    return;
                }
                IPMessage msg = new IPMessage();
                msg.setBody(editContent.getText().toString().getBytes());
                msg.setDate("date");
                msg.setFrom(user.getName());
                msg.setTo(editTo.getText().toString());
                msg.setMessageIndex(1);
                msg.setMessageType(Headers.MESSAGE_TYPE_MESSAGE);
                try {
                    ops.write("abcdefg".getBytes());
                    ops.write(ComposerParser.composeMessage(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                    new Thread(mClientThread).start();
                }
            }
        });
        new Thread(mClientThread).start();
    }

    OutputStream ops = null;
    InputStream ips = null;
    Runnable mClientThread = new Runnable() {

        @Override
        public void run() {
            try {
                InetSocketAddress serverAddr = new InetSocketAddress("10.0.129.209", 8888);
                Socket s = new Socket();
                s.connect(serverAddr);
                ops = s.getOutputStream();
                ips = s.getInputStream();
                new Thread(mReceiverThread).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable mReceiverThread = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e(TAG, "ReceiverThread run");
                while (true) {
                    IPMessage msg = ComposerParser.parseMessage(ips);
                    if(msg != null){
                        final String content = new String(msg.getBody());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textRecv.setText(textRecv.getText() + "\r\n" + content);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (ops != null) {
            try {
                ops.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

}
