package com.socialdemo.fragment.demo;

import android.os.Bundle;
import android.view.View;
import com.socialdemo.activity.R;
import com.socialdemo.fragment.base.BaseDemoFragment;
import com.wezom.socialnetworks.lib.SocialPerson;
import com.wezom.socialnetworks.lib.impl.*;
import com.wezom.socialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;

public class CurrentUserProfileFragment extends BaseDemoFragment implements View.OnClickListener {

    public static CurrentUserProfileFragment newInstance() {
        return new CurrentUserProfileFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Load Twitter Profile");
        mLinkedInButton.setText("Load LinkedIn Profile");
        mFacebookButton.setText("Load Facebook Profile");
        mVkButton.setText("Load Vk Profile");
        mGooglePlusButton.setText("Load Google Plus Profile");
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        showProgress("Loading profile");
        getSocialNetworkManager().getTwitterSocialNetwork()
                .requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());

    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        showProgress("Loading profile");
        getSocialNetworkManager().getLinkedInSocialNetwork()
                .requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());
    }

    @Override
    protected void onVkInAction() {
        if (!checkIsLoginned(VkSocialNetwork.ID)) return;

        getSocialNetworkManager().getVkInSocialNetwork().requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());
    }

    @Override
    protected void onFacebookAction() {
        if (!checkIsLoginned(FacebookSocialNetwork.ID)) return;

        showProgress("Loading profile");
        getSocialNetworkManager().getFacebookSocialNetwork()
                .requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());
    }

    @Override
    protected void onGooglePlusAction() {
        if (!checkIsLoginned(GooglePlusSocialNetwork.ID)) return;

        getSocialNetworkManager().getGooglePlusSocialNetwork()
                .requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());
    }

    @Override
    protected void onWeiboAction() {
        if (!checkIsLoginned(WeiboSocialNetwork.ID)) return;

        getSocialNetworkManager().getWeiboSocialNetwork()
                .requestCurrentPerson(new DemoOnRequestSocialPersonCompleteListener());

    }

    private class DemoOnRequestSocialPersonCompleteListener implements OnRequestSocialPersonCompleteListener {
        @Override
        public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
            hideProgress();

            getFragmentManager().beginTransaction()
                    .replace(R.id.root_container, ShowProfileFragment.newInstance(socialPerson))
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }
}
