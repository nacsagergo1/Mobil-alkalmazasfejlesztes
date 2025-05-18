package com.example.focibajnoksag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TabellaAdapter extends RecyclerView.Adapter<TabellaAdapter.TabellaViewHolder> {

    private Context context;
    private List<Team> teamList;

    public TabellaAdapter(Context context, List<Team> teamList) {
        this.context = context;
        this.teamList = teamList;
    }

    @Override
    public TabellaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team, parent, false);
        return new TabellaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TabellaViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.teamNameTextView.setText(team.getName());
        holder.teamPointsTextView.setText(String.valueOf(team.getPoints()));
        holder.teamRankTextView.setText((position + 1) + "."); // <- Itt állítjuk be a helyezést
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class TabellaViewHolder extends RecyclerView.ViewHolder {

        TextView teamNameTextView;
        TextView teamPointsTextView;
        TextView teamRankTextView;

        public TabellaViewHolder(View itemView) {
            super(itemView);
            teamNameTextView = itemView.findViewById(R.id.team_name);
            teamPointsTextView = itemView.findViewById(R.id.team_points);
            teamRankTextView = itemView.findViewById(R.id.textViewRank); // <- Itt hivatkozunk rá
        }
    }
}
