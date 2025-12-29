package com.ramadan.sabil23;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for search suggestions
 */
public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SearchSuggestionsAdapter.ViewHolder> {

    /**
     * Suggestion item data model
     */
    public static class SuggestionItem {
        private String placeId;
        private String primaryText;
        private String secondaryText;
        private boolean isRecentSearch;

        public SuggestionItem(String placeId, String primaryText, String secondaryText, boolean isRecentSearch) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.isRecentSearch = isRecentSearch;
        }

        public String getPlaceId() { return placeId; }
        public String getPrimaryText() { return primaryText; }
        public String getSecondaryText() { return secondaryText; }
        public boolean isRecentSearch() { return isRecentSearch; }
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(SuggestionItem item);
    }

    private Context context;
    private List<SuggestionItem> suggestions;
    private OnItemClickListener listener;

    /**
     * Constructor
     */
    public SearchSuggestionsAdapter(Context context, List<SuggestionItem> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    /**
     * Sets the item click listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Updates the suggestions list
     */
    public void updateSuggestions(List<SuggestionItem> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuggestionItem item = suggestions.get(position);

        holder.placeNameText.setText(item.getPrimaryText());
        holder.travelTimeText.setText(item.getSecondaryText());

        // Set icon based on whether it's a recent search
        if (holder.iconView != null) {
            holder.iconView.setImageResource(item.isRecentSearch() ?
                    R.drawable.ic_destination : R.drawable.ic_map_pin);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    /**
     * ViewHolder for suggestion items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView placeNameText;
        TextView travelTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Try to find the correct ImageView ID
            // The original layout might have a different ID for the ImageView
            try {
                iconView = itemView.findViewById(R.id.placeIcon);
            } catch (Exception e) {
                // If placeIcon doesn't exist, try a fallback
                try {
                    iconView = (ImageView) itemView.findViewById(android.R.id.icon);
                } catch (Exception e2) {
                    // If that fails too, just find the first ImageView in the layout
                    if (itemView instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) itemView;
                        for (int i = 0; i < group.getChildCount(); i++) {
                            View child = group.getChildAt(i);
                            if (child instanceof ImageView) {
                                iconView = (ImageView) child;
                                break;
                            }
                        }
                    }
                }
            }

            placeNameText = itemView.findViewById(R.id.placeNameText);
            travelTimeText = itemView.findViewById(R.id.travelTimeText);
        }
    }
}

