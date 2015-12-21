package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnCheckIsFriendCompleteListener extends SocialNetworkListener {
    public void onCheckIsFriendComplete(int socialNetworkID, String userID, boolean isFriend);
}
