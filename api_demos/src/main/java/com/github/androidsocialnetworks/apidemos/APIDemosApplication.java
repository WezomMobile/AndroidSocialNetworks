package com.github.androidsocialnetworks.apidemos;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class APIDemosApplication extends Application {

    public static final String TAG = "AndroidSocialNetworks_API_Demos";

    public static final String USER_ID_TWITTER = "2446056205";
    public static final String USER_ID_LINKED_IN = "WQlagxgbbw";

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
    }
}
