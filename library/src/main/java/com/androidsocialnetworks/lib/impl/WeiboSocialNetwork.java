package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.OAuthSocialNetwork;
import com.androidsocialnetworks.lib.AccessTokenKeeper;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 18.10.2015
 * Time: 10:12
 */
public class WeiboSocialNetwork extends OAuthSocialNetwork {

    public static final int ID = 5;
    private static final String REDIRECT_URL = " http://www.sina.com";

    private final String fConsumerKey;
    private final Activity fContext;

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();

    public static final String SCOPE = " email,direct_messages_read,direct_messages_write, "
            + " friendships_groups_read,friendships_groups_write,statuses_to_me_read, "
            + " follow_app_official_microblog, "
            + " invitation_write ";

    protected WeiboSocialNetwork(Fragment fragment, String consumerKey) {
        super(fragment);

        fContext = fragment.getActivity();
        fConsumerKey = consumerKey;

        if (TextUtils.isEmpty(fConsumerKey)) {
            throw new IllegalArgumentException("consumerKey is invalid");
        }

        initWeiboClient();
    }

    private void initWeiboClient() {
        mAuthInfo = new AuthInfo(fContext, fConsumerKey, REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler(fContext, mAuthInfo);

        mAccessToken = AccessTokenKeeper.readAccessToken(fContext);
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void logout() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            new LogoutAPI(fContext, fConsumerKey,
                    AccessTokenKeeper.readAccessToken(fContext)).logout(mLogoutRequestListener);
        } else {
        }
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public AccessToken getAccessToken() {
        return null;
    }

    private class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // Save token to SharedPreferences
                AccessTokenKeeper.writeAccessToken(fContext, mAccessToken);
            } else {
                String code = values.getString("code");
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }

    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");
                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(fContext);
                        mAccessToken = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }
}
