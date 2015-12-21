package com.github.androidsocialnetworks.apidemos.fragment.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wezom.socialnetworks.lib.SocialPerson;
import com.wezom.socialnetworks.lib.impl.FacebookSocialNetwork;
import com.wezom.socialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.wezom.socialnetworks.lib.impl.LinkedInSocialNetwork;
import com.wezom.socialnetworks.lib.impl.TwitterSocialNetwork;
import com.wezom.socialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.github.androidsocialnetworks.apidemos.APIDemosApplication;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseDemoFragment;

public class OtherUsersProfile extends BaseDemoFragment {


    public static OtherUsersProfile newInstance() {
        return new OtherUsersProfile();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Load Twitter Profile");
        mLinkedInButton.setText("Load LinkedIn Profile");
        mFacebookButton.setVisibility(View.GONE);
        mGooglePlusButton.setVisibility(View.GONE);
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) return;

        showProgress("Loading profile");
        getSocialNetworkManager().getTwitterSocialNetwork().requestSocialPerson(APIDemosApplication.USER_ID_TWITTER,
                new DemoOnRequestSocialPersonCompleteListener()
        );
    }

    @Override
    protected void onLinkedInAction() {
        if (!checkIsLoginned(LinkedInSocialNetwork.ID)) return;

        showProgress("Loading profile");
        getSocialNetworkManager().getLinkedInSocialNetwork().requestSocialPerson(APIDemosApplication.USER_ID_LINKED_IN,
                new DemoOnRequestSocialPersonCompleteListener()
        );
    }

    @Override
    protected void onVkInAction() {

    }

    @Override
    protected void onFacebookAction() {
        if (!checkIsLoginned(FacebookSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load Facebook Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onGooglePlusAction() {
        if (!checkIsLoginned(GooglePlusSocialNetwork.ID)) return;

        Toast.makeText(getActivity(), "Load Google Plus Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onWeiboAction() {
        throw new IllegalStateException("Not implemented");
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
