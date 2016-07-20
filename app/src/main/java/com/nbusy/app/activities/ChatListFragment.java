package com.nbusy.app.activities;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.common.eventbus.Subscribe;
import com.nbusy.app.InstanceManager;
import com.nbusy.app.data.Chat;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.app.worker.eventbus.UserProfileRetrievedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A list fragment representing a list of Chats. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ChatDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChatListFragment extends ListFragment {

    private final EventBus eventBus = InstanceManager.getEventBus();
    private ChatListArrayAdapter chatAdapter;
    private AtomicBoolean viewCreated = new AtomicBoolean(false);

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item clicks.
     */
    private Callbacks callbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int activatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must implement.
     * This mechanism allows activities to be notified of item selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(String id);
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.register(this);

        // user profile might be empty if this is the first time user is launching the app so we need this check
        if (InstanceManager.userProfileRetrieved()) {
            setData(InstanceManager.getUserProfile().getChats());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreated.set(true);

        // restore the previously serialized 'activated item' position
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // activities containing this fragment must implement its callbacks
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Host Activity must implement ChatListFragment's callbacks.");
        }

        callbacks = (Callbacks) context;
    }

    // For Android < 6.0 only:
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Host Activity must implement ChatListFragment's callbacks.");
        }

        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // reset the active callbacks interface to the dummy implementation on detach
        callbacks = null;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (callbacks != null) {
            callbacks.onItemSelected(chatAdapter.getItem(position).id);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(activatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        activatedPosition = position;
    }

    private synchronized void setData(Collection<Chat> chats) {
        if (!viewCreated.get()) {
            return;
        }

        chatAdapter = new ChatListArrayAdapter(getActivity(), new ArrayList<>(chats));
        setListAdapter(chatAdapter);
    }

    /***********************
     * Event Subscriptions *
     ***********************/

    @Subscribe
    public void userProfileRetrievedEventHandler(UserProfileRetrievedEvent e) {
        setData(e.profile.getChats());
    }
}
