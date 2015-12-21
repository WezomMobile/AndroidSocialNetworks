package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnLoginCompleteListener extends SocialNetworkListener {
    public void onLoginSuccess(int socialNetworkID);
}
