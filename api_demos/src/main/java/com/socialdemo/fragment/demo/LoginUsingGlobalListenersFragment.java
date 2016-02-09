package com.socialdemo.fragment.demo;

import com.wezom.socialnetworks.lib.SocialNetwork;
import com.wezom.socialnetworks.lib.listener.OnLoginCompleteListener;
import com.socialdemo.fragment.base.BaseLoginDemoFragment;

public class LoginUsingGlobalListenersFragment extends BaseLoginDemoFragment
        implements OnLoginCompleteListener {

    private static final String TAG = LoginUsingGlobalListenersFragment.class.getSimpleName();

    public static LoginUsingGlobalListenersFragment newInstance() {
        return new LoginUsingGlobalListenersFragment();
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        hideProgress();
        handleError(errorMessage);
    }

    @Override
    protected void onTwitterAction() {
        showProgress("Authentificating... Twitter");
        getSocialNetworkManager().getTwitterSocialNetwork().requestLogin();
    }

    @Override
    protected void onLinkedInAction() {
        showProgress("Authentificating... LinkedIn");
        getSocialNetworkManager().getLinkedInSocialNetwork().requestLogin();
    }

    @Override
    protected void onVkInAction() {
        showProgress("Authentificating... Vkontakte");
        getSocialNetworkManager().getVkInSocialNetwork().requestLogin();
    }

    @Override
    protected void onFacebookAction() {
        showProgress("Authentificating... FaceBook");
        getSocialNetworkManager().getFacebookSocialNetwork().requestLogin();
    }

    @Override
    protected void onGooglePlusAction() {
        getSocialNetworkManager().getGooglePlusSocialNetwork().requestLogin();
    }

    @Override
    protected void onWeiboAction() {
        getSocialNetworkManager().getWeiboSocialNetwork().requestLogin();
    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        // let's reset buttons, we need to disable buttons
        onSocialNetworkManagerInitialized();

        hideProgress();
        handleSuccess("onLoginSuccess", "Now you can try other API Demos.");
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        super.onSocialNetworkManagerInitialized();

        for (SocialNetwork socialNetwork : getSocialNetworkManager().getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
        }
    }


}
