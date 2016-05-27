package com.nbusy.app.activities;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.nbusy.app.InstanceProvider;
import com.nbusy.app.R;
import com.nbusy.app.data.Chat;
import com.nbusy.app.worker.Worker;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A fragment representing a single Chat detail screen, along with the messages in the chat.
 * This fragment is either contained in a {@link ChatListActivity}
 * in two-pane mode (on tablets) or a {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends ListFragment implements View.OnClickListener {

    private static final String TAG = ChatDetailFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "item_id"; // fragment argument representing the item ID that this fragment represents
    private final Worker worker = InstanceProvider.getWorker();
    private AtomicBoolean viewCreated = new AtomicBoolean(false);
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
        worker.sendMessages(chatId, messageBody);
        messageBox.setText("");
    }

    /**
     * This function sets the view data.
     * Any changes between the old and the new data is applied as a diff in an efficient way.
     */
    private synchronized void setData(Chat chat) {
        if (!viewCreated.get()) {
            return;
        }

        messageAdapter.setNotifyOnChange(false);
        messageAdapter.clear();
        messageAdapter.addAll(chat.messages);
        messageAdapter.notifyDataSetChanged();
        setSelection(messageAdapter.getCount() - 1);
        Log.v(TAG, "updated view with: " + messageAdapter.getCount() + " messages");

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
            Optional<Chat> chat = worker.userProfile.get().getChat(chatId);
            if (chat.isPresent() && !chat.get().messages.isEmpty()) {
                messageAdapter = new MessageListArrayAdapter(getActivity(), new ArrayList<>(chat.get().messages));
            } else {
                messageAdapter = new MessageListArrayAdapter(getActivity());
                worker.getChatMessages(chatId);
            }
            setListAdapter(messageAdapter);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewCreated.set(true);
        setSelection(messageAdapter.getCount() - 1);
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
        worker.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        worker.unregister(this);
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
    public synchronized void chatUpdatedEventHandler(ChatsUpdatedEvent e) {
        for (Chat chat : e.chats) {
            if (Objects.equals(chat.id, chatId)) {
                setData(chat);
            }
        }
    }
}
