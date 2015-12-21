package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestAddFriendCompleteListener extends SocialNetworkListener {
    public void onRequestAddFriendComplete(int socialNetworkID, String userID);
}
