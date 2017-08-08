package com.trex.trchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trex.trchat.R;
import com.trex.trchat.common.DialogUtils;
import com.trex.trchat.common.PreferenceUtils;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.trexbusiness.TrChatController;
import com.trex.trchat.ui.home.HomePageActivity;
import com.trex.trchat.videocall.model.UserInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class LoginActivity extends Activity implements TrChatBaseEvent {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private TrChatCoreSdk mTrChatCoreSdk;
    private EditText mUserId;
    private EditText mUserPwd;
    private View mAutoLogin;
    private Button mLogin;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAutoLogin = findViewById(R.id.autologin);
        mUserId = (EditText) findViewById(R.id.userid);
        mUserPwd = (EditText) findViewById(R.id.userpwd);
        mLogin = (Button) findViewById(R.id.buttonLogin);
        mLogin.setEnabled(false);

        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setContext(getApplicationContext());
        mTrChatCoreSdk.initSdk();
        mTrChatCoreSdk.setBaseEvent(this);

        mUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.equals("")) {
                    if (!mUserPwd.getText().toString().equals("")) {
                        mLogin.setEnabled(true);
                    } else {
                        mLogin.setEnabled(false);
                    }
                } else {
                    mLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUserPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.equals("")) {
                    if (!mUserId.getText().toString().equals("")) {
                        mLogin.setEnabled(true);
                    } else {
                        mLogin.setEnabled(false);
                    }
                } else {
                    mLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                Login();
//            }
//        });

        String[] info = PreferenceUtils.getLoginInfo(getApplication());
        if (info == null || info.length != 2 || info[0].equals("") || info[1].equals("")) {
            Log.d(TAG, "need login");
            mAutoLogin.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "auto login");
            mAutoLogin.setVisibility(View.VISIBLE);
            Login(info[0], info[1]);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:

                String name = mUserId.getText().toString();
                String pwd = mUserPwd.getText().toString();
                Login(name, pwd);
                break;
        }
    }

    private static final String LOCAL_LOOPBACK_ADDR = "127.0.0.1";
    private static final String INVALID_ADDR = "0.0.0.0";

    private static String getMobileIPAddress() {
        try {
            NetworkInterface ni = NetworkInterface.getByName("hso0"); // インターフェース名
            if (ni == null) {
                Log.d(TAG, "Failed to get mobile interface.");
                return "Failed1";
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                String address = addresses.nextElement().getHostAddress();
                if (!LOCAL_LOOPBACK_ADDR.equals(address) && !INVALID_ADDR.equals(address)) {
                    // Found valid ip address.
                    return address.replace(".", "").replace(" ", "");
                }
            }
            return "Failed2";
        } catch (Exception e) {
            Log.d(TAG, "Exception occured. e=" + e.getMessage());
            return "Failed3";
        }
    }

    private void Login(String name, String pwd) {
//        String name = mUsername.getText().toString();

        /**
         *AnyChat可以连接自主部署的服务器、也可以连接AnyChat视频云平台；
         *连接自主部署服务器的地址为自设的服务器IP地址或域名、端口；
         *连接AnyChat视频云平台的服务器地址为：cloud.anychat.cn；端口为：8906
         */
        this.mTrChatCoreSdk.connect("157.7.165.211", 8906);
//        this.mTrChatCoreSdk.connect("demo.anychat.cn", 8906);
        /***
         * AnyChat支持多种用户身份验证方式，包括更安全的签名登录，
         * 详情请参考：http://bbs.anychat.cn/forum.php?mod=viewthread&tid=2211&highlight=%C7%A9%C3%FB
         */
        this.mTrChatCoreSdk.login(name, pwd);
        mLogin.setClickable(false);
        mProgressDialog = ProgressDialog.show(this, "", "Login");
    }

    @Override
    public void OnTrChatConnectMessage(boolean bSuccess) {
        Log.d(TAG, "OnTrChatConnectMessage bSuccess:" + bSuccess);
        if (!bSuccess) {
            DialogUtils.showToast(LoginActivity.this, "connect failed");

            mLogin.setClickable(true);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void OnTrChatLoginMessage(int dwUserId, int dwErrorCode) {
        Log.d(TAG, "OnTrChatLoginMessage dwUserId:" + dwUserId + " dwErrorCode:" + dwErrorCode);
        if (dwErrorCode == 0) {
            TrChatController business = TrChatController.getInstance(getApplication());

            String name = mTrChatCoreSdk.getUserName(dwUserId);
            String ip = mTrChatCoreSdk.getIpAddr(dwUserId);
            business.setSelf(new UserInfo(dwUserId, name, ip));

            PreferenceUtils.setLoginInfo(getApplication(), mUserId.getText().toString(), mUserPwd.getText().toString());
            Intent intent = new Intent();
            intent.setClass(this, HomePageActivity.class);
            this.startActivity(intent);
            finish();
        } else if (dwErrorCode == 200) {
            DialogUtils.showToast(LoginActivity.this, "login failed");
            mAutoLogin.setVisibility(View.GONE);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void OnTrChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnTrChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnTrChatLinkCloseMessage(int dwErrorCode) {

    }
}
