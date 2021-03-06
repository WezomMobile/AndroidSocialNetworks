package com.socialdemo.fragment.demo;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import android.widget.Toast;
import com.wezom.socialnetworks.lib.impl.FacebookSocialNetwork;
import com.wezom.socialnetworks.lib.impl.TwitterSocialNetwork;
import com.wezom.socialnetworks.lib.impl.VkSocialNetwork;
import com.wezom.socialnetworks.lib.listener.OnPostingCompleteListener;
import com.socialdemo.APIDemosApplication;
import com.socialdemo.fragment.base.BaseDemoFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PostPhotoFragment extends BaseDemoFragment {

    public static final File ANDROID_ASSET_FILE = new File(Environment.getExternalStorageDirectory(), "android.jpg");

    public static PostPhotoFragment newInstance() {
        return new PostPhotoFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTwitterButton.setText("Post photo tweet");
        mLinkedInButton.setVisibility(View.GONE);
        mFacebookButton.setText("Post to Facebook");
        mVkButton.setText("Post photo to VK");
        mGooglePlusButton.setVisibility(View.GONE);

        if (!ANDROID_ASSET_FILE.exists()) {
            copyAssets();
        }
    }

    @Override
    protected void onTwitterAction() {
        if (!checkIsLoginned(TwitterSocialNetwork.ID)) {
            return;
        }

        final String message = "ASN Test: " + UUID.randomUUID();

        showProgress("Posting photo");
        getSocialNetworkManager().getTwitterSocialNetwork()
                                 .requestPostPhoto(ANDROID_ASSET_FILE, message, new DemoOnPostingCompleteListener(message));
    }

    @Override
    protected void onLinkedInAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onVkInAction() {
        if (checkIsLoginned(VkSocialNetwork.ID)) {
            final String message = "ASN Test: " + UUID.randomUUID();
            showProgress("Posting photo");
            getSocialNetworkManager().getVkInSocialNetwork()
                                     .requestPostPhoto(ANDROID_ASSET_FILE, message, new DemoOnPostingCompleteListener(message));
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

        showProgress("Posting photo");
        getSocialNetworkManager().getFacebookSocialNetwork()
                                 .requestPostPhoto(ANDROID_ASSET_FILE, message, new DemoOnPostingCompleteListener(message));
    }

    @Override
    protected void onGooglePlusAction() {
        throw new IllegalStateException("Unsupported");
    }

    @Override
    protected void onWeiboAction() {
        throw new IllegalStateException("Not implemented");
    }

    private void copyAssets() {
        AssetManager assetManager = getActivity().getAssets();
        InputStream  in           = null;
        OutputStream out          = null;
        try {
            in = assetManager.open("android.jpg");
            out = new FileOutputStream(ANDROID_ASSET_FILE);
            copyFile(in, out);
            out.flush();
        }
        catch (IOException e) {
            Log.e(APIDemosApplication.TAG, "ERROR", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int    read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private class DemoOnPostingCompleteListener implements OnPostingCompleteListener {

        private String mmMessage;

        private DemoOnPostingCompleteListener(String message) {
            mmMessage = message;
        }

        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            hideProgress();

            handleSuccess("Success", "Message: '" + mmMessage + "' with PHOTO successfully posted.");
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            hideProgress();
            handleError(errorMessage);
        }
    }
}
