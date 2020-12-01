package com.example.kinemictestgeste;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    List<String> strings = new ArrayList<String>();

    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(String string) {
        this.strings.add(string);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView messageBody;
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        String message = strings.get(position);
        convertView = messageInflater.inflate(R.layout.message, null);
        messageBody = (TextView) convertView.findViewById(R.id.message_body);
        messageBody.setText(message);
        return convertView;
    }
}
