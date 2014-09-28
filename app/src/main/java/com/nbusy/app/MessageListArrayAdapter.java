package com.nbusy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.soygul.organizer.dummy.DummyContent;

import java.util.List;

public class MessageListArrayAdapter extends ArrayAdapter<DummyContent.DummyItem> {
    private final Context context;
    private final List<DummyContent.DummyItem> values;

    public MessageListArrayAdapter(Context context, List<DummyContent.DummyItem> values) {
        super(context, R.layout.message_list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_list_row, parent, false);

        DummyContent.DummyItem contact = values.get(position);

        TextView contactName = (TextView) rowView.findViewById(R.id.contact_name);
        contactName.setText(contact.name);

        TextView lastMessage = (TextView) rowView.findViewById(R.id.last_message);
        lastMessage.setText(contact.message);

        TextView sent = (TextView) rowView.findViewById(R.id.sent);
        sent.setText(contact.sent);

        // change the icon for Windows and iPhone
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//        String s = objects[position];
//        if (s.startsWith("iPhone")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        return rowView;
    }

}
