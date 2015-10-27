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

    private MessageListArrayAdapter messageAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatDetailFragment() {
//        messageQueue = Queue.GetInstance...
    }

//    public ChatDetailFragment(IMessageQueue) {
//    }

//    @Override
//    public void onPause(){
//        super.onPause();
//        if(isFinishing()){
//            // store data
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retain this fragment instance across configuration changes for AsyncTask to post back changes to the correct instance
//        setRetainInstance(true);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            // load the content specified by the fragment arguments
            messages = new ArrayList<>();

            Message m1 = new Message("Teoman Soygul", "Lorem ip sum my message...", "8:50", true);
            messages.add(m1);

            Message m2 = new Message("User ID: " + arguments.get(ARG_ITEM_ID), "Test test.", "Just now", false);
            messages.add(m2);

            messageAdapter = new MessageListArrayAdapter(getActivity(), messages);
            setListAdapter(messageAdapter);
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
        // do not submit blank lines
        EditText editText = (EditText) getView().findViewById(R.id.edit_message);
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // add message to task list and the UI

        messageAdapter.add(new Message("me", message, "now", true));

        // todo: send message to backend and clear text if successful
        editText.setText("");
    }

    public void onMessageSent() {
        // set checkmark view to visible and text to a single checkmark character
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
