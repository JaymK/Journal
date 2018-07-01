package com.example.journal;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class EntryViewHolder extends RecyclerView.ViewHolder {

       View mView;
       TextView textTitle, textTime;
       CardView entryCard;

        public EntryViewHolder(View itemView) {
           super(itemView);
            mView = itemView;
            textTitle = mView.findViewById(R.id.entry_title);
            textTime = mView.findViewById(R.id.entry_time);
            entryCard = mView.findViewById(R.id.entry_card);
        }

        public void setEntryTitle(String title) {
           textTitle.setText(title);
       }
        public void setEntryTime(String time) {
           textTime.setText(time);
       }
}