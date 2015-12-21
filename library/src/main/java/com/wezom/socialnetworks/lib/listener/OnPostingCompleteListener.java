package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnPostingCompleteListener extends SocialNetworkListener {
    public void onPostSuccessfully(int socialNetworkID);
}
