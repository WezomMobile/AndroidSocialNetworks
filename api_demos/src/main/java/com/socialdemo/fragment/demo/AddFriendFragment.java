package com.socialdemo.fragment.demo;

import android.os.Bundle;
import android.view.View;

import com.wezom.socialnetworks.lib.impl.LinkedInSocialNetwork;
import com.wezom.socialnetworks.lib.impl.TwitterSocialNetwork;
import com.wezom.socialnetworks.lib.listener.OnRequestAddFriendCompleteListener;
import com.socialdemo.APIDemosApplication;
import com.socialdemo.fragment.base.BaseDemoFragment;

public class AddFriendFragment extends BaseDemoFragment {
    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Follow Anton Krasov");
        mLinkedInButton.setText("Send invite to Anton Krasov");
        mFacebookButton.setVisibility(View.GONE);
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        showProgress("Following Anton Krasov");
        getSocialNetworkManager().getTwitterSocialNetwork().requestAddFriend(
                APIDemosApplication.USER_ID_TWITTER,
                new DemoTwitterOnRequestAddFriendCompleteListener()
        );
    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        showProgress("Following Anton Krasov");
        getSocialNetworkManager().getLinkedInSocialNetwork().requestAddFriend(
                APIDemosApplication.USER_ID_LINKED_IN,
                new DemoLinkedInOnRequestAddFriendCompleteListener()
        );
    }

    @Override
    protected void onVkInAction() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected void onFacebookAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onWeiboAction() {
        throw new IllegalStateException("Not implemented");
    }

    private class DemoTwitterOnRequestAddFriendCompleteListener implements OnRequestAddFriendCompleteListener {
        @Override
        public void onRequestAddFriendComplete(int socialNetworkID, String userID) {
            hideProgress();

            handleSuccess("Add friend", "Now you follow Anton Krasov!");
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }

    private class DemoLinkedInOnRequestAddFriendCompleteListener implements OnRequestAddFriendCompleteListener {
        @Override
        public void onRequestAddFriendComplete(int socialNetworkID, String userID) {
            hideProgress();

            handleSuccess("Add friend", "Invite was successfully sent to Anton Krasov!");
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }
}
