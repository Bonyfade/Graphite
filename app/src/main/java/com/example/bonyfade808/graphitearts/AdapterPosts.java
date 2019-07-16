package com.example.bonyfade808.graphitearts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    //view holder class

    Context context ;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post
        View view = LayoutInflater.from(context).inflate(R.layout.rows_posts, parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();

        //convert timestamp to dd/mm/yyyy
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa" ,calendar).toString();


        //set data
        holder.uName.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);


        //set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.default_avatar).into(holder.uAvatar);

        }
        catch (Exception e){

        }

        //set post image
        try {
            Picasso.get().load(pImage).into(holder.pImage);

        }
        catch (Exception e){

        }

        //buttons
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
            }
        });

        //buttons
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }

        });
        //buttons
        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Will implement M-Pesa here", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        //views from row posts
        ImageView uAvatar , pImage;
        TextView uName, pTimeTv, pTitleTv, pLikesTv;
        Button likeBtn, commentBtn, buyBtn;

        public MyHolder(View itemView) {
            super(itemView);
            //init views
            uAvatar = itemView.findViewById(R.id.userAvatar);
            pImage = itemView.findViewById(R.id.pImageView);
            uName = itemView.findViewById(R.id.uNameTextview);
            pTimeTv = itemView.findViewById(R.id.pTimeTextview);
            pTitleTv = itemView.findViewById(R.id.pTitleTextview);
            pLikesTv = itemView.findViewById(R.id.pLikesTextview);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            buyBtn = itemView.findViewById(R.id.buyBtn);

        }
    }
}
