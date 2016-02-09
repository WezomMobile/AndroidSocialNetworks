package com.socialdemo.fragment.demo;

import android.os.Bundle;
import android.view.View;

import android.widget.Toast;
import com.wezom.socialnetworks.lib.impl.*;
import com.wezom.socialnetworks.lib.listener.OnPostingCompleteListener;
import com.socialdemo.fragment.base.BaseDemoFragment;

import java.util.UUID;

public class PostMessageFragment extends BaseDemoFragment {

    public static PostMessageFragment newInstance() {
        return new PostMessageFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Post tweet");
        mLinkedInButton.setText("Update LinkedIn status");
        mFacebookButton.setText("Post to Facebook");
        mVkButton.setText("Post to Vkontakte");
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) {
            return;
        }

        final String message = "ASN Test: " + UUID.randomUUID();

        showProgress("Posting message");
        getSocialNetworkManager().getTwitterSocialNetwork()
                                 .requestPostMessage(message, new DemoOnPostingCompleteListener(message));
    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) {
            return;
        }

        final String message = "ASN Test: " + UUID.randomUUID();

        showProgress("Posting message");
        getSocialNetworkManager().getLinkedInSocialNetwork()
                                 .requestPostMessage(message, new DemoOnPostingCompleteListener(message));
    }

    @Override
    protected void onVkInAction() {
        if (checkIsLoginned(VkSocialNetwork.ID)) {
            final String message = "ASN Test: " + UUID.randomUUID();
            showProgress("Posting message");
            getSocialNetworkManager().getVkInSocialNetwork().requestPostMessage(message, new DemoOnPostingCompleteListener(message));
        }
        else {
            Toast.makeText(getActivity(), "Login to post a message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onFacebookAction() {
        if (!checkIsLoginned(FacebookSocialNetwork.ID)) {
            return;
        }

        final String message = "ASN Test: " + UUID.randomUUID();

        showProgress("Posting message");
        getSocialNetworkManager().getFacebookSocialNetwork()
                                 .requestPostMessage(message, new DemoOnPostingCompleteListener(message));
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onWeiboAction() {
        if (!checkIsLoginned(WeiboSocialNetwork.ID)) {
            return;
        }

        final String message = "Weibo Test message: " + UUID.randomUUID();

        showProgress("Posting message");
        getSocialNetworkManager().getWeiboSocialNetwork()
                                 .requestPostMessage(message, new DemoOnPostingCompleteListener(message));
    }

    private class DemoOnPostingCompleteListener implements OnPostingCompleteListener {

        private String mmMessage;

        private DemoOnPostingCompleteListener(String message) {
            mmMessage = message;
        }

        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            hideProgress();

            handleSuccess("Success", "Message: '" + mmMessage + "' successfully posted.");
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }
}
