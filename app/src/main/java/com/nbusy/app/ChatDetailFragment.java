package com.nbusy.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

    private final Worker worker = WorkerSingleton.getWorker();
    private EditText messageBox;
    private Button sendButton;
    private String chatId;

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The chat messages that this fragment is presenting.
     */
    private List<Message> messages;

    private MessageListArrayAdapter messageAdapter;

    public ChatDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            chatId = (String) arguments.get(ARG_ITEM_ID);

            // load the content specified by the fragment arguments
            messages = new ArrayList<>();

            Message m1 = new Message("Teoman Soygul", "Lorem ip sum my message...", "8:50", true);
            messages.add(m1);

            Message m2 = new Message("User ID: " + chatId, "Test test.", "Just now", false);
            messages.add(m2);

            messageAdapter = new MessageListArrayAdapter(getActivity(), messages);
            setListAdapter(messageAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        sendButton = (Button) rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        messageBox = (EditText) rootView.findViewById(R.id.edit_message);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        worker.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        worker.getEventBus().unregister(this);
    }

    public void sendMessage() {
        // do not submit blank lines
        String message = messageBox.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // temporarily disable message box and the send button until message is saved to disk
        messageBox.setEnabled(false);
        sendButton.setEnabled(false);

        // send message to the server
        Message msg = new Message("me", message, "now", true);
        worker.sendMessage(msg);
    }

    @Subscribe
    public void addMessageToScreen(Worker.MessageSavedEvent e) {
        // add message to the UI, and clear message box
        messageAdapter.add(e);
        messageBox.setText("");

        // enable message box and the send button again
        messageBox.setEnabled(true);
        sendButton.setEnabled(true);
    }

    @Subscribe
    public void addCheckMarkToMessage(Worker.MessageSentEvent e) {
        // set checkmark view to visible and text to a single checkmark character
        // http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
        // we need map[itemIndex]=messageId map in the fragment so when we receive a broadcast about
        // a certain message with given ID is delivered we can update it
        // if the view is not visible, we can just update the underlying array storage so notifyOnChange won't be called (not to update whole page)
        // underlying data should always be updated regardless of visibility (as well as persistence)

        // would all of this logic be in the adapter?
    }

    @Subscribe
    public void addDoubleCheckMarkToMessage(Worker.MessageDeliveredEvent e) {

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
