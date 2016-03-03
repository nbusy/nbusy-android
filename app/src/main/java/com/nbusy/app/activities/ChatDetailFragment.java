package com.nbusy.app.activities;

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
import com.nbusy.app.R;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.Message;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.WorkerSingleton;

import java.util.Objects;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

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
        Message msg = chat.addNewOutgoingMessage(messageBody);
        messageAdapter.notifyDataSetChanged();
        messageBox.setText("");

        // send the message to the server
        worker.sendMessages(msg);
    }

    private void setMessagesState(Message[] msgs) {
        for (Message msg : msgs) {
            // only update if message belongs to this chat
            int location = chat.getMessageLocation(msg);
            if (location == 0) {
                return;
            }

            // update the check mark on the updated item only as per:
            //   http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
            View v = messageListView.getChildAt(location - messageListView.getFirstVisiblePosition());
            if (v != null) {
                if (msg.status == Message.Status.SENT_TO_SERVER) {
                    ((TextView)v.findViewById(R.id.check)).setText("✓");
                }
                v.findViewById(R.id.check).setVisibility(View.VISIBLE);
            }
        }
    }

    private void setMessageAdapter() {
        messageAdapter = new MessageListArrayAdapter(getActivity(), chat.messages);
        setListAdapter(messageAdapter);
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
            chat = worker.userProfile.getChat(chatId);
            if (chat.messages.size() == 0) {
                worker.getChatMessages(chatId);
            } else {
                setMessageAdapter();
            }
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

    @Subscribe
    public void chatMessagesRetrieved(Worker.ChatMessagesRetrievedEvent e) {
        if (Objects.equals(e.chatId, chat.id)) {
            setMessageAdapter();
        }
    }
}
