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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

    public static final String ARG_ITEM_ID = "item_id"; // fragment argument representing the item ID that this fragment represents
    private final Worker worker = WorkerSingleton.getWorker();
    private String chatId;
    private MessageListArrayAdapter messageAdapter;

    // view elements
    private ListView messageListView;
    private EditText messageBox;
    private Button sendButton;

    private void sendMessage() {
        // do not submit blank lines
        String messageBody = messageBox.getText().toString().trim();
        if (messageBody.isEmpty()) {
            return;
        }

        // send the message to server and clear message box
        worker.sendMessage(chatId, messageBody);
        messageBox.setText("");
    }

    /**
     * This function sets the view data.
     * Any changes between the old and the new data is applied as a diff in an efficient way.
     */
    private synchronized void setData(Chat chat) {
        messageAdapter.clear();
        messageAdapter.addAll(chat.messages);

        // todo: implement in place updates as below as an optimization
//        boolean notifyDataSetChanged = false;
//
//        // apply changes to existing messages in case any of them was changed
//        // diffing here is easy as we're always using immutable objects, we can just compare references to get changed ones
//        for (int i = 0; i < messages.size(); i++) {
//            if (messages.get(i) != chat.messages.get(i)) {
//                // update the check mark on the updated item only as per:
//                //   http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
//                View v = messageListView.getChildAt(location - messageListView.getFirstVisiblePosition());
//                if (v != null) {
//                    if (msg.status == Message.Status.SENT_TO_SERVER) {
//                        ((TextView)v.findViewById(R.id.check)).setText("✓");
//                    }
//                    v.findViewById(R.id.check).setVisibility(View.VISIBLE);
//                }
//
//            }
//        }
    }

    /**************************
     * ListFragment Overrides *
     **************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            chatId = (String) arguments.get(ARG_ITEM_ID);
            Chat chat = worker.userProfile.getChat(chatId);
            if (chat.messages.size() == 0) {
                messageAdapter = new MessageListArrayAdapter(getActivity());
                worker.getChatMessages(chatId);
            } else {
                messageAdapter = new MessageListArrayAdapter(getActivity(), chat.messages);
            }
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
    public synchronized void chatUpdatedEventHandler(Worker.ChatUpdatedEvent e) {
        setData(e.chat);
    }
}
