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

    // todo: how to get the worker service here:
    // 1) start service in application context and stop on app destroy and get service instance with getApplicationContext: http://stackoverflow.com/questions/987072/using-application-context-everywhere
    // 2) expose worker service as an interface from the host activity and access it directly via getActivity().workerService.doWork()
    // 3) bind it to application context: http://stackoverflow.com/questions/15235773/bind-service-to-fragmentactivity-or-fragment
    // 4) ** get it with IOC and never bother with service aspects as we're all in same process and same thread and just use workerService.sendMsg(...)

    private final Worker worker = WorkerSingleton.getWorker();

    public ChatDetailFragment() {
//        Bundle bundle = getArguments();
//        this.workerService = (WorkerService)bundle.get("workerService");
    }

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The chat messages that this fragment is presenting.
     */
    private List<Message> messages;

    private MessageListArrayAdapter messageAdapter;

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
        EditText editText = (EditText) getView().findViewById(R.id.edit_message);
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // add message to task list and the UI, and clear text

        // block ui thread -or- just display infinite progress bar and ignore re-clicks (or both)
        // workerService.sendMessage(msg, msgSavedCallback, msgDeliveredCallback, msgReadCallback)
        // msgSavedCallback - clear editText as we can register new msgs now
        // msgDeliveredCallback - checkmark view enabled on this newly created view for the new message (but how not to reference the view -or- use retained fragment)
        // or use LBM for loose coupling and not to have a reference to fragment so no need for retained fragment

        messageAdapter.add(new Message("me", message, "now", true));
        editText.setText("");
    }

    @Subscribe
    public void onMessageSent(Worker.StoredMsg storedMsg) {
        // set checkmark view to visible and text to a single checkmark character
        // http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
        // we need map[itemIndex]=messageId map in the fragment so when we receive a broadcast about
        // a certain message with given ID is delivered we can update it
        // if the view is not visible, we can just update the underlying array storage so notifyOnChange won't be called (not to update whole page)
        // underlying data should always be updated regardless of visibility (as well as persistence)

        // would all of this logic be in the adapter?
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
