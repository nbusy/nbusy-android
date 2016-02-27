package com.nbusy.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

    private static final String TAG = ChatDetailFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "item_id"; // fragment argument representing the item ID that this fragment represents
    private final Worker worker = WorkerSingleton.getWorker();
    private Chat chat;
    private MessageListArrayAdapter messageAdapter;
    private ListView messageListView;
    private EditText messageBox;
    private Button sendButton;

    private void sendMessage() {
        // do not submit blank lines
        String messageBody = messageBox.getText().toString().trim();
        if (messageBody.isEmpty()) {
            return;
        }

        // add message to the UI, and clear message box
        Message msg = chat.addMessage(messageBody);
        messageAdapter.notifyDataSetChanged();
        messageBox.setText("");

        // send the message to the server
        worker.sendMessages(new Message[]{msg});
    }

    private void setMessagesState(Message[] msgs) {
        for (Message msg : msgs) {
            // only update if message belongs to this chat
            int location = chat.updateMessage(msg);
            if (location == 0) {
                return;
            }

            // update the check mark on the updated item only as per:
            //   http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
            View v = messageListView.getChildAt(location - messageListView.getFirstVisiblePosition());
            if (v != null) {
                if (msg.status == Message.Status.SentToServer) {
                    ((TextView)v.findViewById(R.id.check)).setText("✓");
                }
                v.findViewById(R.id.check).setVisibility(View.VISIBLE);
            }
        }
    }

    /**************************
     * ListFragment Overrides *
     **************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            String chatId = (String) arguments.get(ARG_ITEM_ID);

            // load the content specified by the fragment arguments
            Message m1 = new Message(UUID.randomUUID().toString(), chatId, "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser);
            Message m2 = new Message(UUID.randomUUID().toString(), chatId, "User ID: " + chatId, null, false, "Test test.", new Date(), Message.Status.DeliveredToUser);

            messageAdapter = new MessageListArrayAdapter(getActivity(), chat.messages);
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

    /**********************************
     * View.OnClickListener Overrides *
     **********************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                sendMessage();
                break;
        }
    }

    /******************************
     * Worker Event Subscriptions *
     ******************************/

    @Subscribe
    public void setMessagesState(Worker.MessagesStatusChangedEvent e) {
        setMessagesState(e.msgs);
    }
}
