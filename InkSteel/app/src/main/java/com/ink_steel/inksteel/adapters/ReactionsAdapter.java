package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.Reaction;

import java.util.List;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ReactionsViewHolder> {

    private List<Reaction> mReactions;

    public ReactionsAdapter(List<Reaction> reactions) {
        mReactions = reactions;
    }

    @Override
    public ReactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReactionsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_reaction, parent, false));
    }

    @Override
    public void onBindViewHolder(ReactionsViewHolder holder, int position) {
        holder.bind(mReactions.get(position));
    }

    @Override
    public int getItemCount() {
        return mReactions.size();
    }

    class ReactionsViewHolder extends RecyclerView.ViewHolder {

        private ImageView reactionIcon;
        private TextView reactionUser;
        private TextView reactionMessage;

        ReactionsViewHolder(View itemView) {
            super(itemView);

            reactionIcon = itemView.findViewById(R.id.post_reaction_icon);
            reactionUser = itemView.findViewById(R.id.post_reaction_user);
            reactionMessage = itemView.findViewById(R.id.post_reaction_text);
        }

        void bind(Reaction reaction) {
            reactionIcon.setImageResource(reaction.getReactionIcon());
            reactionUser.setText(reaction.getUserEmail());
            reactionMessage.setText(reaction.getMessage());
        }
    }
}
