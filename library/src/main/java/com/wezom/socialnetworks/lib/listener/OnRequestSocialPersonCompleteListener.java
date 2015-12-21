package com.wezom.socialnetworks.lib.listener;

import com.wezom.socialnetworks.lib.SocialPerson;
import com.wezom.socialnetworks.lib.listener.base.SocialNetworkListener;

public interface OnRequestSocialPersonCompleteListener extends SocialNetworkListener {
    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson);
}
