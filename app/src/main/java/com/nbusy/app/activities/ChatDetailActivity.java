package com.nbusy.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.common.base.Optional;
import com.nbusy.app.InstanceManager;
import com.nbusy.app.R;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.UserProfile;

/**
 * An activity representing a single Chat detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ChatListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ChatDetailFragment}.
 */
public class ChatDetailActivity extends Activity {

    private final UserProfile userProfile = InstanceManager.getUserProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Show the Up button in the action bar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape)
        // in this case, the fragment will automatically be re-added to its container so we don't need to manually add it
        if (savedInstanceState != null) {
            return;
        }

        // Set activity title
        String chatId = getIntent().getStringExtra(ChatDetailFragment.ARG_ITEM_ID);
        Optional<Chat> chat = userProfile.getChat(chatId);
        setTitle(chat.isPresent() ? chat.get().peerName : "New Chat");


        // create the detail fragment and add it to the activity using a fragment transaction.
        Bundle arguments = new Bundle();
        arguments.putString(ChatDetailFragment.ARG_ITEM_ID, chatId);
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().add(R.id.chat_detail_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // this ID represents the Home or Up button
            // in the case of this activity, the Up button is shown
            navigateUpTo(new Intent(this, ChatListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
