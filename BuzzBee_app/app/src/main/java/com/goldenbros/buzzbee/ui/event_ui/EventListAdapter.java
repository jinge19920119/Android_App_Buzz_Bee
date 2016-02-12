package com.goldenbros.buzzbee.ui.event_ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenbros.buzzbee.util.GetXMLTask;
import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimiko on 2015/7/23.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    Context mContext;
    OnItemClickListener mItemClickListener;
    List<Event> e2List;

    // 2
    public EventListAdapter(Context context, List<Event> list) {
        this.mContext = context;
        e2List = list;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    // 3
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout eventHolder;
        public LinearLayout eventNameHolder;
        public TextView eventName;
        public ImageView eventImage;

        public ViewHolder(View itemView) {
            super(itemView);
            eventHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventNameHolder = (LinearLayout) itemView.findViewById(R.id.eventNameHolder);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);

            eventHolder.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
    //    return new PlaceData().placeList().size();
        return e2List.size();
    }

    // 2
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_events, parent, false);
        return new ViewHolder(view);
    }

    // 3
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       final Event event = e2List.get(position);
        holder.eventName.setText(event.getName());
        Picasso.with(mContext).load(event.getPhotoFilename()).into(holder.eventImage);

        Bitmap photo = null;
        try {
            photo = new GetXMLTask().execute(event.getPhotoFilename()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int bgColor = palette.getMutedColor(mContext.getResources().getColor(android.R.color.black));
                holder.eventNameHolder.setBackgroundColor(bgColor);
            }
        });
    }
}
