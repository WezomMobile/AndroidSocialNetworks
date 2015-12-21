package com.wezom.socialnetworks.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.wezom.socialnetworks.lib.impl.FacebookSocialNetwork;
import com.wezom.socialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.wezom.socialnetworks.lib.impl.LinkedInSocialNetwork;
import com.wezom.socialnetworks.lib.impl.TwitterSocialNetwork;
import com.wezom.socialnetworks.lib.impl.VkSocialNetwork;
import com.wezom.socialnetworks.lib.impl.WeiboSocialNetwork;
import com.facebook.internal.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialNetworkManager {

    private static final String TAG = SocialNetworkManager.class.getSimpleName();

    private Map<Integer, SocialNetwork> mSocialNetworksMap = new HashMap<Integer, SocialNetwork>();
    private OnInitializationCompleteListener mOnInitializationCompleteListener;

    private SocialNetworkManager(Activity activity, String twitterConsumerKey, String twitterConsumerSecret, String linkedInConsumerKey,
                                 String linkedInConsumerSecret, String linkedInPermissions, String weiboConsumerKey, String weiboConsumerSecret,
                                 long vkConsumerKey,String vkConsumerSecret,
                                 boolean isFacebook, boolean isGooglePlus) {

        if (!TextUtils.isEmpty(twitterConsumerKey) || !TextUtils.isEmpty(twitterConsumerSecret)) {
            mSocialNetworksMap.put(TwitterSocialNetwork.ID,
                    new TwitterSocialNetwork(activity, twitterConsumerKey, twitterConsumerSecret));
        }

        if (!TextUtils.isEmpty(linkedInConsumerKey) || !TextUtils.isEmpty(linkedInConsumerSecret)) {
            mSocialNetworksMap.put(LinkedInSocialNetwork.ID,
                    new LinkedInSocialNetwork(activity, linkedInConsumerKey, linkedInConsumerSecret, linkedInPermissions));
        }

        if (!TextUtils.isEmpty(weiboConsumerKey)) {
            mSocialNetworksMap.put(WeiboSocialNetwork.ID,
                    new WeiboSocialNetwork(activity, weiboConsumerKey, weiboConsumerSecret));
        }

        if (!TextUtils.isEmpty(vkConsumerSecret)) {
            mSocialNetworksMap.put(VkSocialNetwork.ID,
                    new VkSocialNetwork(activity, vkConsumerKey, vkConsumerSecret));
        }

        if (isFacebook) {
            mSocialNetworksMap.put(FacebookSocialNetwork.ID, new FacebookSocialNetwork(activity));
        }

        if (isGooglePlus) {
            mSocialNetworksMap.put(GooglePlusSocialNetwork.ID, new GooglePlusSocialNetwork(activity));
        }

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onCreate(null);
        }
    }

    public void onStart() {
        Log.d(TAG, "SocialNetworkManager.onStart");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStart();
        }
    }

    public void onResume() {
        Log.d(TAG, "SocialNetworkManager.onResume");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onResume();
        }

        if (mOnInitializationCompleteListener != null) {
            Log.d(TAG, "SocialNetworkManager.onResume: mOnInitializationCompleteListener != null");
            mOnInitializationCompleteListener.onSocialNetworkManagerInitialized();
        }
    }

    public void onPause() {
        Log.d(TAG, "SocialNetworkManager.onPause");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onPause();
        }
    }

    public void onStop() {
        Log.d(TAG, "SocialNetworkManager.onStop");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStop();
        }
    }

    public void onDestroy() {
        Log.d(TAG, "SocialNetworkManager.onDestroy");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onDestroy();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "SocialNetworkManager.onSaveInstanceState");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onSaveInstanceState(outState);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "SocialNetworkManager.onActivityResult: " + requestCode + " : " + resultCode);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onActivityResult(requestCode, resultCode, data);
        }
    }

    public TwitterSocialNetwork getTwitterSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(TwitterSocialNetwork.ID)) {
            throw new SocialNetworkException("Twitter wasn't initialized...");
        }

        return (TwitterSocialNetwork) mSocialNetworksMap.get(TwitterSocialNetwork.ID);
    }

    public LinkedInSocialNetwork getLinkedInSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(LinkedInSocialNetwork.ID)) {
            throw new SocialNetworkException("LinkedIn wasn't initialized...");
        }

        return (LinkedInSocialNetwork) mSocialNetworksMap.get(LinkedInSocialNetwork.ID);
    }

    public VkSocialNetwork getVkInSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(VkSocialNetwork.ID)) {
            throw new SocialNetworkException("Vk wasn't initialized...");
        }

        return (VkSocialNetwork) mSocialNetworksMap.get(VkSocialNetwork.ID);
    }

    public FacebookSocialNetwork getFacebookSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(FacebookSocialNetwork.ID)) {
            throw new IllegalStateException("Facebook wasn't initialized...");
        }

        return (FacebookSocialNetwork) mSocialNetworksMap.get(FacebookSocialNetwork.ID);
    }

    public GooglePlusSocialNetwork getGooglePlusSocialNetwork() {
        if (!mSocialNetworksMap.containsKey(GooglePlusSocialNetwork.ID)) {
            throw new IllegalStateException("Google+ wasn't initialized...");
        }

        return (GooglePlusSocialNetwork) mSocialNetworksMap.get(GooglePlusSocialNetwork.ID);
    }

    public WeiboSocialNetwork getWeiboSocialNetwork() {
        if (!mSocialNetworksMap.containsKey(WeiboSocialNetwork.ID)) {
            throw new IllegalStateException("Weibo wasn't initialized...");
        }

        return (WeiboSocialNetwork) mSocialNetworksMap.get(WeiboSocialNetwork.ID);
    }

    public SocialNetwork getSocialNetwork(int id) throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(id)) {
            throw new SocialNetworkException("Social network with id = " + id + " not found");
        }

        return mSocialNetworksMap.get(id);
    }

    public void addSocialNetwork(SocialNetwork socialNetwork) {
        if (mSocialNetworksMap.get(socialNetwork.getID()) != null) {
            throw new SocialNetworkException("Social network with id = " + socialNetwork.getID() + " already exists");
        }

        mSocialNetworksMap.put(socialNetwork.getID(), socialNetwork);
    }

    public List<SocialNetwork> getInitializedSocialNetworks() {
        return Collections.unmodifiableList(new ArrayList<SocialNetwork>(mSocialNetworksMap.values()));
    }

    public void setOnInitializationCompleteListener(OnInitializationCompleteListener onInitializationCompleteListener) {
        mOnInitializationCompleteListener = onInitializationCompleteListener;
    }

    public interface OnInitializationCompleteListener {
        void onSocialNetworkManagerInitialized();
    }

    public static class Builder {

        private Activity mActivity;
        private String mTwitterConsumerKey;
        private String mTwitterConsumerSecret;
        private String mLinkedInConsumerKey;
        private String mLinkedInConsumerSecret;
        private String mLinkedInPermissions;
        private String mWeiboConsumerKey;
        private String mWeiboConsumerSecret;
        private long mVkConsumerKey;
        private String mVkConsumerSecret;
        private boolean mFacebook;
        private boolean mGooglePlus;

        private Builder(Activity activity) {
            mActivity = activity;
        }

        public static Builder from(Activity activity) {
            return new Builder(activity);
        }

        public Builder twitter(String consumerKey, String consumerSecret) {
            mTwitterConsumerKey = consumerKey;
            mTwitterConsumerSecret = consumerSecret;
            return this;
        }

        public Builder linkedIn(String consumerKey, String consumerSecret, String permissions) {
            mLinkedInConsumerKey = consumerKey;
            mLinkedInConsumerSecret = consumerSecret;
            mLinkedInPermissions = permissions;
            return this;
        }

        public Builder weibo(String consumerKey, String consumerSecret) {
            mWeiboConsumerKey = consumerKey;
            mWeiboConsumerSecret = consumerSecret;
            return this;
        }

        public Builder vk(long consumerKey, String consumerSecret) {
            mVkConsumerKey = consumerKey;
           mVkConsumerSecret = consumerSecret;
            return this;
        }

        // https://developers.facebook.com/docs/android/getting-started/
        public Builder facebook() {
            String applicationID = Utility.getMetadataApplicationId(mActivity);

            if (applicationID == null) {
                throw new IllegalStateException("applicationID can't be null\n" +
                        "Please check https://developers.facebook.com/docs/android/getting-started/");
            }

            mFacebook = true;

            return this;
        }

        public Builder googlePlus() {
            mGooglePlus = true;
            return this;
        }

        public SocialNetworkManager build() {
            SocialNetworkManager socialNetworkManager =
                    new SocialNetworkManager(mActivity, mTwitterConsumerKey, mTwitterConsumerSecret, mLinkedInConsumerKey,
                            mLinkedInConsumerSecret, mLinkedInPermissions, mWeiboConsumerKey, mWeiboConsumerSecret,
                            mVkConsumerKey,mVkConsumerSecret,mFacebook, mGooglePlus);
            return socialNetworkManager;
        }
    }
}
