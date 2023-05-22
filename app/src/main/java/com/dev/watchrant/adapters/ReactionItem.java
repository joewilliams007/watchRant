package com.dev.watchrant.adapters;

import com.dev.watchrant.classes.sky.Reactions;

public class ReactionItem {
    private Reactions reaction;

    public ReactionItem(Reactions reaction) {
        this.reaction = reaction;
    }

    public Reactions getReaction() {
        return reaction;
    }
}
