package com.github.androidsocialnetworks.apidemos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.wezom.socialnetworks.lib.SocialNetworkManager;
import com.github.androidsocialnetworks.apidemos.R;
import com.github.androidsocialnetworks.apidemos.fragment.APIDemosListFragment;


public class MainActivity extends AppCompatActivity {

    private SocialNetworkManager mSocialNetworkManager;

    public SocialNetworkManager getSocialNetworkManager() {
        return mSocialNetworkManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSocialNetworkManager = SocialNetworkManager.Builder.from(this)
                .twitter("3IYEDC9Pq5SIjzENhgorlpera", "fawjHMhyzhrfcFKZVB6d5YfiWbWGmgX7vPfazi61xZY9pdD1aE")
                .linkedIn("77ieoe71pon7wq", "pp5E8hkdY9voGC9y", "r_basicprofile+rw_nus+r_network+w_messages")
                .weibo("3288380285", "e7c4cd9cd6fbddcb0bd0559ab99d0666")
                .vk(5187494,"QF5qIydJzs4bSEdyBrAX")
                .facebook()
                .googlePlus()
                .build();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_container, APIDemosListFragment.newInstance())
                    .commit();
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSocialNetworkManager.onActivityResult(requestCode, resultCode, data);
    }
}
