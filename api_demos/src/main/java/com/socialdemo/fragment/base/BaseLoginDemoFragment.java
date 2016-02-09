package com.socialdemo.fragment.base;

import android.graphics.Color;
import android.view.View;

public abstract class BaseLoginDemoFragment extends BaseDemoFragment implements View.OnClickListener {

    @Override
    public void onSocialNetworkManagerInitialized() {
        if (getSocialNetworkManager().getTwitterSocialNetwork().isConnected()) {
            mTwitterButton.setText("Twitter connected");
            mTwitterButton.setBackgroundColor(Color.LTGRAY);
            mTwitterButton.setOnClickListener(null);
        }

        if (getSocialNetworkManager().getLinkedInSocialNetwork().isConnected()) {
            mLinkedInButton.setText("LinkedIn connected");
            mLinkedInButton.setBackgroundColor(Color.LTGRAY);
            mLinkedInButton.setOnClickListener(null);
        }

        if (getSocialNetworkManager().getFacebookSocialNetwork().isConnected()) {
            mFacebookButton.setText("Facebook connected");
            mFacebookButton.setBackgroundColor(Color.LTGRAY);
            mFacebookButton.setOnClickListener(null);
        }

        if (getSocialNetworkManager().getVkInSocialNetwork().isConnected()) {
            mFacebookButton.setText("Vk connected");
            mFacebookButton.setBackgroundColor(Color.LTGRAY);
            mFacebookButton.setOnClickListener(null);
        }

        if (getSocialNetworkManager().getGooglePlusSocialNetwork().isConnected()) {
            mGooglePlusButton.setText("Google Plus connected");
            mGooglePlusButton.setBackgroundColor(Color.LTGRAY);
            mGooglePlusButton.setOnClickListener(null);
        }

        if (getSocialNetworkManager().getWeiboSocialNetwork().isConnected()) {
            mWeiboButton.setText("Weibo connected");
            mWeiboButton.setBackgroundColor(Color.LTGRAY);
            mWeiboButton.setOnClickListener(null);
        }
    }
}
