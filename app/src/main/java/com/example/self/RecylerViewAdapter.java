package com.example.self;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.ViewHolder> {
    public List<Journal> journalList;
    public Context context;

    public RecylerViewAdapter(List<Journal> journalList, Context context) {
        this.journalList = journalList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapter.ViewHolder holder, int position) {

        Journal journal = journalList.get(position);
        String title = journal.getTitle();
        String thought = journal.getThought();
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeadded().getSeconds()*1000);

        holder.title.setText(title);
        holder.thought.setText(thought);
        holder.time.setText(timeAgo);
        holder.name.setText(JournalApi.getInstance().getUsername());
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView thought;
        public TextView time;
        public TextView name;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.title_row);
            thought = itemView.findViewById(R.id.thought_row);
            time = itemView.findViewById(R.id.time_row);
            name = itemView.findViewById(R.id.name1);
        }
    }
}
