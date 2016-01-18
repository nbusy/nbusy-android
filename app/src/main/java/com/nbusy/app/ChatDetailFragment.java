package com.nbusy.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

    public static final String ARG_ITEM_ID = "item_id"; // fragment argument representing the item ID that this fragment represents
    private final Worker worker = WorkerSingleton.getWorker();
    private String chatId;
    private List<Message> messages; // chat messages that this fragment is presenting
    private Map<String, Integer> messageIDtoIndex; // message ID -> messages[index]
    private MessageListArrayAdapter messageAdapter;
    private ListView messageListView;
    private EditText messageBox;
    private Button sendButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            chatId = (String) arguments.get(ARG_ITEM_ID);

            // load the content specified by the fragment arguments
            messages = new ArrayList<>();
            messageIDtoIndex = new HashMap<>();

            Message m1 = new Message("1", "Teoman Soygul", "Lorem ip sum my message...", "8:50", true);
            m1.sentToServer = m1.delivered = true;
            Message m2 = new Message("2", "User ID: " + chatId, "Test test.", "Just now", false);
            m2.sentToServer = m2.delivered = true;
            messages.add(m1);
            messageIDtoIndex.put(m1.id, messages.size() - 1);
            messages.add(m2);
            messageIDtoIndex.put(m2.id, messages.size() - 1);

            messageAdapter = new MessageListArrayAdapter(getActivity(), messages);
            setListAdapter(messageAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        messageListView = (ListView) rootView.findViewById(android.R.id.list);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                sendMessage();
                break;
        }
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
        Message msg = new Message("3", "me", message, "now", false);
        worker.sendMessage(msg);
    }

    /***********************
     * Event Subscriptions *
     ***********************/

    @Subscribe
    public void addMessageToScreen(Worker.MessageSavedEvent e) {
        // add message to the UI, and clear message box
        messageAdapter.add(e);
        messageIDtoIndex.put(e.id, messages.size()-1);
        messageBox.setText("");

        // enable message box and the send button again
        messageBox.setEnabled(true);
        sendButton.setEnabled(true);
    }

    @Subscribe
    public void addCheckMarkToMessage(Worker.MessageSentEvent e) {
        // update the check mark on the updated item only as per
        // http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
        int location = messageIDtoIndex.get(e.id);
        View v = messageListView.getChildAt(location);
        if (v != null) {
            v.findViewById(R.id.check).setVisibility(View.VISIBLE);
            messages.get(location).sentToServer = true;
            return;
        }

        // element is not visible on the view yet so we have to update the entire list view via adapter now
        messageAdapter.getItem(location).sentToServer = true;




        // set check mark view to visible and text to a single check mark character
        // http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
        // we need map[itemIndex]=messageId map in the fragment so when we receive a broadcast about
        // a certain message with given ID is delivered we can update it
        // if the view is not visible, we can just update the underlying array storage (private List<Message> messages)
        // so notifyOnChange won't be called (not to update whole page)
        // underlying data should always be updated regardless of visibility (as well as persistence)

        // would all of this logic be in the adapter?
    }

    @Subscribe
    public void addDoubleCheckMarkToMessage(Worker.MessageDeliveredEvent e) {

    }
}
