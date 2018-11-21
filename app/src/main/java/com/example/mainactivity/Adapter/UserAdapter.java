package com.example.mainactivity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.Logic.UserLogic;
import com.example.mainactivity.MessageActivity;
import com.example.mainactivity.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserLogic.User> mUsers;

    public UserAdapter(Context mContext, List<UserLogic.User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final UserLogic.User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }


    }



}
