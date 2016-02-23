package com.nbusy.app;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for listing all messages of a chat, in a list view.
 */
public class MessageListArrayAdapter extends ArrayAdapter<Message> {

    private final LayoutInflater inflater;

    // view holder pattern template (just like page objects in selenium, minus the auto inflation)
    static class ViewHolder {
        TextView body;
        LinearLayout metadata;
        TextView check;
        TextView sent;
    }

    public MessageListArrayAdapter(Context context, List<Message> values) {
        super(context, R.layout.message_list_row, values);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        // check if an existing view is being reused, otherwise inflate a new view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.message_list_row, parent, false);
            viewHolder.body = (TextView) convertView.findViewById(R.id.body);
            viewHolder.metadata = (LinearLayout) convertView.findViewById(R.id.metadata);
            viewHolder.check = (TextView) convertView.findViewById(R.id.check);
            viewHolder.sent = (TextView) convertView.findViewById(R.id.sent);

            // view lookup cache stored in tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.body.setText(message.body);
        viewHolder.sent.setText(message.sent);
        if (!message.sentToServer && !message.delivered) {
            viewHolder.check.setVisibility(View.GONE);
        } else if (message.sentToServer && !message.delivered) {
            viewHolder.check.setText("✓");
        } else {
            viewHolder.check.setText("✓✓");
        }

        if (message.owner) {
            viewHolder.body.setGravity(Gravity.END);
            viewHolder.metadata.setGravity(Gravity.END);
        }

        return convertView;
    }
}
