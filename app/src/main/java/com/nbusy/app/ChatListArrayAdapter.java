package com.nbusy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for listing all chats (peer conversations) within a single list view.
 */
public class ChatListArrayAdapter extends ArrayAdapter<Chats.Chat> {

    public ChatListArrayAdapter(Context context, List<Chats.Chat> values) {
        super(context, R.layout.chat_list_row, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chats.Chat chat = getItem(position);

        // check if an existing view is being reused, otherwise inflate a new view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_list_row, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.sent = (TextView) convertView.findViewById(R.id.sent);

            // view lookup cache stored in tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contactName.setText(chat.name);
        viewHolder.lastMessage.setText(chat.message);
        viewHolder.sent.setText(chat.sent);

        return convertView;
    }

    // view holder pattern template (just like page objects in selenium, minus the auto inflation)
    static class ViewHolder {
        TextView contactName;
        TextView lastMessage;
        TextView sent;
    }
}