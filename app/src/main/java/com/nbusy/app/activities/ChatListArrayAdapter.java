package com.nbusy.app.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nbusy.app.R;
import com.nbusy.app.data.Chat;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter for listing all chats (peer conversations) within a list view.
 */
public class ChatListArrayAdapter extends ArrayAdapter<Chat> {

    private final LayoutInflater inflater;

    // view holder pattern template (just like page objects in selenium, minus the auto inflation)
    static class ViewHolder {
        TextView contactName;
        TextView lastMessage;
        TextView sent;
    }

    public ChatListArrayAdapter(Context context, List<Chat> values) {
        super(context, R.layout.chat_list_row, values);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);

        // check if an existing view is being reused, otherwise inflate a new view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_list_row, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.sent = (TextView) convertView.findViewById(R.id.sent);

            // view lookup cache stored in tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contactName.setText(chat.peerName);
        viewHolder.lastMessage.setText(chat.lastMessage);
        String sent = new SimpleDateFormat("HH:mm").format(chat.sent);
        viewHolder.sent.setText(sent);

        return convertView;
    }
}