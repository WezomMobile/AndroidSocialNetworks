package com.androidsocialnetworks.lib.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.OAuthSocialNetwork;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;


/**
 * Created by Sivolotskiy.v on 14.12.2015.
 */

public class VkSocialNetwork extends OAuthSocialNetwork {
    public static final String TAG = VkSocialNetwork.class.getName();
    public static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String PREFERENCES_VK = "com_vk_sdk_android";
    public static final int ID = 6;
    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS
    };

    private VKAccessToken mAccessToken;
    private long mConsumerKey;
    private String mVkConsumerSecret;

    public VkSocialNetwork(Activity activity, long consumerKey, String vkConsumerSecret) {
        super(activity);
        this.mConsumerKey = consumerKey;
        this.mVkConsumerSecret = vkConsumerSecret;

    }

    @Override
    public boolean isConnected() {
        return mAccessToken != null && !mAccessToken.isExpired();
    }

    @Override
    public void logout() {

    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public AccessToken getAccessToken() {
        SharedPreferences pref = mActivity.getSharedPreferences(PREFERENCES_VK, Context.MODE_APPEND);
        return new AccessToken(pref.getString(KEY_ACCESS_TOKEN, ""), null);
    }

    @Override
    public void requestLogin(OnLoginCompleteListener onLoginCompleteListener) {
        super.requestLogin(onLoginCompleteListener);
        VKSdk.login(mActivity, sMyScope);
    }


    @Override
    public void requestCurrentPerson(OnRequestSocialPersonCompleteListener onRequestSocialPersonCompleteListener) {
        super.requestCurrentPerson(onRequestSocialPersonCompleteListener);

        VKRequest request = VKApi.users().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                if (response != null) {

                    VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                    Log.d("User name", user.toString());
                    SocialPerson socialPerson = new SocialPerson();
                    socialPerson.id = user.id + "";
                    socialPerson.name = user.first_name;
                    socialPerson.nickname = user.last_name;
                    socialPerson.avatarURL = user.photo_max_orig;

                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON))
                            .onRequestSocialPersonSuccess(getID(), socialPerson);
                    mLocalListeners.remove(REQUEST_GET_CURRENT_PERSON);
                }

            }

            @Override
            public void onError(VKError error) {
                handleError(error, REQUEST_GET_CURRENT_PERSON);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                //I don't really believe in progress
            }
        });
    }

    private void handleError(VKError e, String requestId) {
        Log.e(TAG, e.errorMessage);
        if (!TextUtils.isEmpty(requestId)) {
            ErrorInfo errorInfo = ErrorInfo.parse(e.errorMessage);
            if (mLocalListeners.get(requestId) != null) {
                mLocalListeners.get(requestId).onError(
                        getID(), requestId, errorInfo.toString(), null);
                mLocalListeners.remove(REQUEST_LOGIN);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken token) {
                mAccessToken = token;
                SharedPreferences pref = mActivity.getSharedPreferences(PREFERENCES_VK, Context.MODE_APPEND);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(KEY_ACCESS_TOKEN, token.accessToken);
                editor.commit();
                if (mLocalListeners.get(REQUEST_LOGIN) != null) {
                    ((OnLoginCompleteListener) mLocalListeners.get(REQUEST_LOGIN)).onLoginSuccess(getID());
                    mLocalListeners.remove(REQUEST_LOGIN);
                }
            }

            @Override
            public void onError(VKError error) {
                handleError(error, REQUEST_LOGIN);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
