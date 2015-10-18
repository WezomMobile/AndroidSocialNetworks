package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.OAuthSocialNetwork;
import com.androidsocialnetworks.lib.AccessTokenKeeper;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnPostingCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 18.10.2015
 * Time: 10:12
 */
public class WeiboSocialNetwork extends OAuthSocialNetwork {

    private static final String TAG = WeiboSocialNetwork.class.getSimpleName();

    public static final int ID = 5;
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog,"
            + "invitation_write ";

    private Activity mContext;
    private String mConsumerKey;
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();
    private UsersAPI mUsersAPI;
    private StatusesAPI mStatusesAPI;


    protected WeiboSocialNetwork(Fragment fragment, String consumerKey) {
        super(fragment);

        mContext = fragment.getActivity();
        mConsumerKey = consumerKey;

        if (TextUtils.isEmpty(mConsumerKey)) {
            throw new IllegalArgumentException("consumerKey is invalid");
        }

        initWeibo();
    }

    private void initWeibo() {
        mAuthInfo = new AuthInfo(mContext, mConsumerKey, REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler(mContext, mAuthInfo);

        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
    }

    @Override
    public boolean isConnected() {
        return mAccessToken != null && mAccessToken.isSessionValid();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);

        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    public void logout() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            new LogoutAPI(mContext, mConsumerKey, mAccessToken).logout(mLogoutRequestListener);
        }
    }

    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        if (mAccessToken == null) {
            if (mLocalListeners.get(REQUEST_GET_CURRENT_PERSON) != null) {
                mLocalListeners.get(REQUEST_GET_CURRENT_PERSON).onError(getID(),
                        REQUEST_GET_CURRENT_PERSON, "please login first", null);
            }
            return;
        }

        long uid = Long.parseLong(mAccessToken.getUid());
        mUsersAPI.show(uid, mPersonInfoListener);

        mUsersAPI = new UsersAPI(mContext, mConsumerKey, mAccessToken);
    }

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);

        mStatusesAPI = new StatusesAPI(mContext, mConsumerKey, mAccessToken);
        mStatusesAPI.update(message, null, null, mPostMessageListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public AccessToken getAccessToken() {
        return new AccessToken(AccessTokenKeeper.readAccessToken(mContext).getToken(), null);
    }

    private class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // Save token to SharedPreferences
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                    ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                    mLocalListeners.remove(REQUEST_LOGIN);
                }
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
            handleError(e, REQUEST_LOGIN);
        }
    }

    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    String result = responseJson.getString("result");
                    if (result.equalsIgnoreCase("true")) {
                        AccessTokenKeeper.clear(mContext);
                        mAccessToken = null;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            handleError(e, null);
        }
    }

    private RequestListener mPersonInfoListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                User user = User.parse(response);
                if (user != null) {
                    SocialPerson socialPerson = new SocialPerson();
                    socialPerson.id = user.id;
                    socialPerson.name = user.name;
                    socialPerson.nickname = user.screen_name;
                    socialPerson.profileURL = user.profile_url;
                    socialPerson.avatarURL = user.avatar_large;

                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                            .onRequestSocialPersonSuccess(getID(), socialPerson);
                    mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            handleError(e, REQUEST_GET_CURRENT_PERSON);
        }
    };

    private RequestListener mPostMessageListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            ((OnPostingCompleteListener) mLocalListeners.get(REQUEST_POST_MESSAGE)).onPostSuccessfully(getID());
            mLocalListeners.remove(REQUEST_POST_MESSAGE);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            handleError(e, REQUEST_POST_MESSAGE);
        }
    };

    private void handleError(WeiboException e, String requestId) {
        Log.e(TAG, e.getMessage());
        if (!TextUtils.isEmpty(requestId)) {
            ErrorInfo errorInfo = ErrorInfo.parse(e.getMessage());
            if (mLocalListeners.get(requestId) != null) {
                mLocalListeners.get(requestId).onError(
                        getID(), requestId, errorInfo.toString(), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }
    }
}
