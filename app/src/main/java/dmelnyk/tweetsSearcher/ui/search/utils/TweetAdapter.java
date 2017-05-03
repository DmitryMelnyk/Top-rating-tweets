package dmelnyk.tweetsSearcher.ui.search.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.business.model.Tweet;

/**
 * Created by dmitry on 30.04.17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TwittHolder> {

    private static final long ANIMATE_DURATION = 300; // duration of "hide-visible" animation in ms.
    private ArrayList<Tweet> dataSet;
    private View cardView;

    public TweetAdapter(ArrayList<Tweet> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public TwittHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.twitt_card_view, parent, false);

        return new TwittHolder(cardView);
    }

    @Override
    public void onBindViewHolder(TwittHolder holder, int position) {
        Tweet tweet = dataSet.get(position);
        TextView textViewLikes = holder.textViewLikes;
        TextView textViewRetwitts = holder.textViewRetwitts;
        TextView textViewTwittContent = holder.textViewTwittContent;
        ImageView imageViewAvatar = holder.imageViewAvatar;

        // TODO: initialize views below with data from dataSet
        textViewLikes.setText("" + tweet.getLikes());
        textViewRetwitts.setText("" + tweet.getRetweets());
        textViewTwittContent.setText(tweet.getTweetText());

        animationView(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class TwittHolder extends RecyclerView.ViewHolder {
        TextView textViewLikes;
        TextView textViewRetwitts;
        TextView textViewTwittContent;
        ImageView imageViewAvatar;

        public TwittHolder(View itemView) {
            super(itemView);
            textViewLikes = (TextView) itemView.findViewById(R.id.twittLikes);
            textViewRetwitts = (TextView) itemView.findViewById(R.id.twittRetwitts);
            textViewTwittContent = (TextView) itemView.findViewById(R.id.twittContent);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.twittAvatar);
        }
    }

    // animate last item
    private void animationView(int position) {
        if (position == dataSet.size() - 1) {
            cardView.setAlpha(0);
            cardView.animate()
                    .alpha(1)
                    .setDuration(ANIMATE_DURATION);
        }
    }
}
