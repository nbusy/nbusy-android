package com.nbusy.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The chat messages that this fragment is presenting.
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
            // load the content specified by the fragment arguments
            messages = new ArrayList<>();

            Message m1 = new Message("Teoman Soygul", "Lorem ip sum my message...", "8:50");
            messages.add(m1);

            Message m2 = new Message("User ID: " + arguments.get(ARG_ITEM_ID), "Test test.", "Just now");
            messages.add(m2);

            setListAdapter(new MessageListArrayAdapter(getActivity(), messages));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        Button sendButton = (Button) rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        return rootView;
    }

    public void sendMessage() {
        EditText editText = (EditText) getView().findViewById(R.id.edit_message);
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // todo: send message to backend and clear text if successful
        editText.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                sendMessage();
                break;
        }
    }
}
