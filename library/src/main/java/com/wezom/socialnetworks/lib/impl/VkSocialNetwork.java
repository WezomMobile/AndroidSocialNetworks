package com.wezom.socialnetworks.lib.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.vk.sdk.api.*;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.wezom.socialnetworks.lib.AccessToken;
import com.wezom.socialnetworks.lib.OAuthSocialNetwork;
import com.wezom.socialnetworks.lib.SocialPerson;
import com.wezom.socialnetworks.lib.listener.OnLoginCompleteListener;
import com.wezom.socialnetworks.lib.listener.OnPostingCompleteListener;
import com.wezom.socialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sivolotskiy.v on 14.12.2015.
 */

public class VkSocialNetwork extends OAuthSocialNetwork {

    public static final  String   TAG              = VkSocialNetwork.class.getName();
    public static final  String   KEY_ACCESS_TOKEN = "access_token";
    public static final  int      ID               = 6;
    private static final String   PREFERENCES_VK   = "com_vk_sdk_android";
    private static final String[] sMyScope         = new String[]{VKScope.FRIENDS,
                                                                  VKScope.WALL,
                                                                  VKScope.PHOTOS,
                                                                  VKScope.NOHTTPS};

    private VKAccessToken mAccessToken;
    private long          mConsumerKey;
    private String        mVkConsumerSecret;
    private static final int MAX_ATTEMPTS = 3;

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
        SharedPreferences        pref   = mActivity.getSharedPreferences(PREFERENCES_VK, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.commit();
        mAccessToken = null;
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

                    ((OnRequestSocialPersonCompleteListener) mLocalListeners.get(REQUEST_GET_CURRENT_PERSON)).onRequestSocialPersonSuccess(getID(), socialPerson);
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
                mLocalListeners.get(requestId).onError(getID(), requestId, errorInfo.toString(), null);
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
                SharedPreferences        pref   = mActivity.getSharedPreferences(PREFERENCES_VK, Context.MODE_APPEND);
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

    @Override
    public void requestPostMessage(String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostMessage(message, onPostingCompleteListener);
        performPublishWallMessage(message, onPostingCompleteListener);
    }

    @Override
    public void requestPostPhoto(File photo, String message, OnPostingCompleteListener onPostingCompleteListener) {
        super.requestPostPhoto(photo, message, onPostingCompleteListener);

        performPublishWallPhoto(photo, message, onPostingCompleteListener);
    }

    private void performPublishWallMessage(String message, final OnPostingCompleteListener onPostingCompleteListener) {

        Map<String, Object> params = new HashMap<>();
        params.put(VKApiConst.MESSAGE, message);
        VKRequest request = VKApi.wall().post(new VKParameters(params));
        request.attempts = MAX_ATTEMPTS;
        request.executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {
                onPostingCompleteListener.onPostSuccessfully(VkSocialNetwork.ID);
                super.onComplete(response);
            }
        });
    }

    void performPublishWallPhoto(File photo, final String message, final OnPostingCompleteListener onPostingCompleteListener) {
        VKRequest request = VKApi.uploadWallPhotoRequest(photo, 0, 0);
        request.executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onError(VKError error) {

                super.onError(error);
            }

            @Override
            public void onComplete(VKResponse response) {


                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);

                Map<String, Object> params = new HashMap<>();
                params.put(VKApiConst.ATTACHMENTS, "photo" + photoModel.owner_id + "_" + photoModel.getId());
                params.put(VKApiConst.MESSAGE, message);
                VKRequest request = VKApi.wall().post(new VKParameters(params));
                request.attempts = MAX_ATTEMPTS;
                request.executeWithListener(new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        onPostingCompleteListener.onPostSuccessfully(VkSocialNetwork.ID);
                        super.onComplete(response);
                    }

                    @Override
                    public void onError(VKError error) {
                        onPostingCompleteListener.onError(VkSocialNetwork.ID,"",error.errorMessage,null);
                        super.onError(error);
                    }
                });
            }
        });
    }
}
