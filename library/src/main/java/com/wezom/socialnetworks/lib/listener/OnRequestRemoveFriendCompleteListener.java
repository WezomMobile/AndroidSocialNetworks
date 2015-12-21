package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestRemoveFriendCompleteListener extends SocialNetworkListener {
    public void onRequestRemoveFriendComplete(int socialNetworkID, String userID);
}
