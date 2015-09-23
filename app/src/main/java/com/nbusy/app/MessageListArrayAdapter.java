package com.nbusy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for listing messages in a chat using a single list view.
 */
public class MessageListArrayAdapter extends ArrayAdapter<Message> {

    public MessageListArrayAdapter(Context context, List<Message> objects) {
        super(context, R.layout.chat_list_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        // check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_list_row, parent, false);
            viewHolder.message = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.sent = (TextView) convertView.findViewById(R.id.sent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.message.setText(message.message);
        viewHolder.sent.setText(message.sent);

        // change the icon for Windows and iPhone
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//        String s = objects[position];
//        if (s.startsWith("iPhone")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        return convertView;
    }

    // view holder pattern template
    static class ViewHolder {
        TextView message;
        TextView sent;
    }
}
