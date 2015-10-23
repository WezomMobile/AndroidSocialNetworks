package com.github.androidsocialnetworks.apidemos.fragment.demo;

import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.github.androidsocialnetworks.apidemos.fragment.base.BaseLoginDemoFragment;

public class LoginUsingLocalListenersFragment extends BaseLoginDemoFragment {
    public static LoginUsingLocalListenersFragment newInstance() {
        return new LoginUsingLocalListenersFragment();
    }

    @Override
    protected void onTwitterAction() {
        showProgress("Authentificating... Twitter");
        getSocialNetworkManager().getTwitterSocialNetwork().requestLogin(new DemoOnLoginCompleteListener());
    }

    @Override
    protected void onLinkedInAction() {
        showProgress("Authentificating... LinkedIn");
        getSocialNetworkManager().getLinkedInSocialNetwork().requestLogin(new DemoOnLoginCompleteListener());
    }

    @Override
    protected void onFacebookAction() {
        getSocialNetworkManager().getFacebookSocialNetwork().requestLogin(new DemoOnLoginCompleteListener());
    }

    @Override
    protected void onGooglePlusAction() {
        getSocialNetworkManager().getGooglePlusSocialNetwork().requestLogin(new DemoOnLoginCompleteListener());
    }

    @Override
    protected void onWeiboAction() {
        getSocialNetworkManager().getWeiboSocialNetwork().requestLogin(new DemoOnLoginCompleteListener());
    }

    private class DemoOnLoginCompleteListener implements OnLoginCompleteListener {
        @Override
        public void onLoginSuccess(int socialNetworkID) {
            // let's reset buttons, we need to disable buttons
            onSocialNetworkManagerInitialized();

            hideProgress();
            handleSuccess("onLoginSuccess", "Now you can try other API Demos.");
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }
}
