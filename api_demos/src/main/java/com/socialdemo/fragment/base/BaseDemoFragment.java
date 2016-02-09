package com.socialdemo.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.socialdemo.activity.MainActivity;
import com.socialdemo.activity.R;
import com.socialdemo.fragment.dialog.AlertDialogFragment;
import com.socialdemo.fragment.dialog.ProgressDialogFragment;
import com.wezom.socialnetworks.lib.SocialNetwork;
import com.wezom.socialnetworks.lib.SocialNetworkManager;

public abstract class BaseDemoFragment extends Fragment
        implements SocialNetworkManager.OnInitializationCompleteListener, View.OnClickListener {

    public static final  String  SOCIAL_NETWORK_TAG               = "BaseLoginDemoFragment.SOCIAL_NETWORK_TAG";
    private static final String  PROGRESS_DIALOG_TAG              = "BaseDemoFragment.PROGRESS_DIALOG_TAG";
    protected            boolean mSocialNetworkManagerInitialized = false;

    protected Button mTwitterButton;
    protected Button mLinkedInButton;
    protected Button mVkButton;
    protected Button mFacebookButton;
    protected Button mGooglePlusButton;
    protected Button mWeiboButton;

    protected abstract void onTwitterAction();

    protected abstract void onLinkedInAction();

    protected abstract void onVkInAction();

    protected abstract void onFacebookAction();

    protected abstract void onGooglePlusAction();

    protected abstract void onWeiboAction();

    public SocialNetworkManager getSocialNetworkManager() {
        return ((MainActivity) getActivity()).getSocialNetworkManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_buttons, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.d("SocialNetworkManager", getSocialNetworkManager() + "");
        if (getSocialNetworkManager() != null) {
            getSocialNetworkManager().setOnInitializationCompleteListener(this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton = (Button) view.findViewById(R.id.twitter_button);
        mLinkedInButton = (Button) view.findViewById(R.id.linkedin_button);
        mVkButton = (Button) view.findViewById(R.id.vk_button);
        mFacebookButton = (Button) view.findViewById(R.id.facebook_button);
        mGooglePlusButton = (Button) view.findViewById(R.id.google_plus_button);
        mWeiboButton = (Button) view.findViewById(R.id.weibo_button);

        mTwitterButton.setOnClickListener(this);
        mLinkedInButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
        mVkButton.setOnClickListener(this);
        mGooglePlusButton.setOnClickListener(this);
        mWeiboButton.setOnClickListener(this);

        if (mSocialNetworkManagerInitialized) {
            onSocialNetworkManagerInitialized();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        onRequestCancel();
    }

    protected void showProgress(String text) {
        ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(text);
        progressDialogFragment.setTargetFragment(this, 0);
        progressDialogFragment.show(getFragmentManager(), PROGRESS_DIALOG_TAG);
    }

    protected void hideProgress() {
        Fragment fragment = getFragmentManager().findFragmentByTag(PROGRESS_DIALOG_TAG);

        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    protected void handleError(String text) {
        AlertDialogFragment.newInstance("Error", text).show(getFragmentManager(), null);
    }

    protected void handleSuccess(String title, String message) {
        AlertDialogFragment.newInstance(title, message).show(getFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.twitter_button:
                onTwitterAction();
                break;
            case R.id.linkedin_button:
                onLinkedInAction();
                break;
            case R.id.vk_button:
                onVkInAction();
                break;
            case R.id.facebook_button:
                onFacebookAction();
                break;
            case R.id.google_plus_button:
                onGooglePlusAction();
                break;
            case R.id.weibo_button:
                onWeiboAction();
                break;
            default:
                throw new IllegalArgumentException("Can't find click handler for: " + v);
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {

    }

    protected boolean checkIsLoginned(int socialNetworkID) {
        if (getSocialNetworkManager().getSocialNetwork(socialNetworkID).isConnected()) {
            return true;
        }
            AlertDialogFragment.newInstance("Request Login", "This action request login, please go to login demo first.")
                               .show(getFragmentManager(), null);
            return false;
    }

    public void onRequestCancel() {
        //        Log.d(TAG, "BaseDemoFragment.onRequestCancel");

        for (SocialNetwork socialNetwork : getSocialNetworkManager().getInitializedSocialNetworks()) {
            socialNetwork.cancelAll();
        }
    }
}
