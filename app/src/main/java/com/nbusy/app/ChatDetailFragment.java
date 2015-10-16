package com.nbusy.app;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Chat detail screen.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity}
 * on handsets.
 */
public class ChatDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The messages that this fragment is presenting.
     */
    private List<Message> messages;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            // load the dummy content specified by the fragment arguments
            // in a real-world scenario, use a Loader to load content from a content provider
            messages = new ArrayList<>();

            Message m1 = new Message();
            m1.from = "Teoman Soygul";
            m1.message = "Lorem ip sum my message...";
            m1.sent = "8:50";
            messages.add(m1);

            Message m2 = new Message();
            m2.from = "User ID: " + arguments.get(ARG_ITEM_ID);
            m2.message = "Test test.";
            m2.sent = "Just now";
            messages.add(m2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        // show the dummy content as text in a TextView
        if (messages != null) {
            ((TextView) rootView.findViewById(R.id.chat_detail)).setText("wow");
        }

        return rootView;
    }
}
