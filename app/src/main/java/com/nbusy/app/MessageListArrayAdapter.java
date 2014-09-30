package com.nbusy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageListArrayAdapter extends ArrayAdapter<Messages.Message> {

    public MessageListArrayAdapter(Context context, List<Messages.Message> values) {
        super(context, R.layout.message_list_row, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Messages.Message message = getItem(position);

        // check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.message_list_row, parent, false);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.sent = (TextView) convertView.findViewById(R.id.sent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contactName.setText(message.name);
        viewHolder.lastMessage.setText(message.message);
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
        TextView contactName;
        TextView lastMessage;
        TextView sent;
    }
}
