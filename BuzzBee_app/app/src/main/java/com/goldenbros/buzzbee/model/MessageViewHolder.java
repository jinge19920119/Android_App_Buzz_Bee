package com.goldenbros.buzzbee.model;

import android.widget.TextView;

/**
 * Created by kimiko on 2015/7/30.
 */
public class MessageViewHolder {
    TextView text;

    public MessageViewHolder(TextView text) {
        this.text = text;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

}
