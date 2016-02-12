package com.goldenbros.buzzbee.ui.event_ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.ui.friends_ui.FriendsListAdapter;
import com.goldenbros.buzzbee.util.GetXMLTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kimiko on 2015/7/31.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>  {

    Context mContext;
    OnItemClickListener mItemClickListener;
    List<User> mList;

    // 2
    public UserListAdapter(Context context, UserList list) {
        mList = new ArrayList<>(list.getUserList().values());
        this.mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    // 3
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout userHolder;
        public LinearLayout userInfoHolder;
        public TextView userInfo;
        public ImageView userImage;

        public ViewHolder(View itemView) {
            super(itemView);
            userHolder = (LinearLayout) itemView.findViewById(R.id.userHolder);
            userInfo = (TextView) itemView.findViewById(R.id.userInfo);
            userInfoHolder = (LinearLayout) itemView.findViewById(R.id.userInfoHolder);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);

            userHolder.setOnClickListener(this);
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
        return mList.size();
    }

    // 2
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends, parent, false);
        return new ViewHolder(view);
    }

    // 3
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = mList.get(position);
        holder.userInfo.setText(user.getName());
        Picasso.with(mContext).load(user.getPhotoName()).into(holder.userImage);

        holder.userInfo.setText(user.getName() + "\n" + user.getID());

        Bitmap photo = null;
        try {
            photo = new GetXMLTask().execute(user.getPhotoName()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                float[] hsl = palette.getMutedSwatch().getHsl();
                holder.userInfoHolder.setBackgroundColor(Color.HSVToColor(150, hsl));
            }
        });
    }
}
