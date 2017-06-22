package com.trex.trchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.trex.trchat.R;
import com.trex.trchat.common.DialogUtils;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.trexbusiness.VideoCallBusiness;


public class LoginActivity extends Activity implements TrChatBaseEvent {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private TrChatCoreSdk mTrChatCoreSdk;
    private EditText mUsername;
    private Button mLogin;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.username);
        mLogin = (Button) findViewById(R.id.buttonLogin);
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setContext(getApplicationContext());
        mTrChatCoreSdk.initSdk();
        mTrChatCoreSdk.setBaseEvent(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                Login();
                break;
        }
    }

    private void Login() {
        String name = mUsername.getText().toString();


        /**
         *AnyChat可以连接自主部署的服务器、也可以连接AnyChat视频云平台；
         *连接自主部署服务器的地址为自设的服务器IP地址或域名、端口；
         *连接AnyChat视频云平台的服务器地址为：cloud.anychat.cn；端口为：8906
         */
        this.mTrChatCoreSdk.connect("demo.anychat.cn", 8906);
        /***
         * AnyChat支持多种用户身份验证方式，包括更安全的签名登录，
         * 详情请参考：http://bbs.anychat.cn/forum.php?mod=viewthread&tid=2211&highlight=%C7%A9%C3%FB
         */
        this.mTrChatCoreSdk.login(name, "123");
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
            VideoCallBusiness vb = VideoCallBusiness.getInstance();
            vb.setContext(getApplicationContext());
            vb.setmSelf(dwUserId, mUsername.getText().toString());

            Intent intent = new Intent();
            intent.setClass(this, HomeActivity.class);
            this.startActivity(intent);
        } else if (dwErrorCode == 200) {
            DialogUtils.showToast(LoginActivity.this, "login failed");
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
