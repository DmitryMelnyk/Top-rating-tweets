package dmelnyk.tweetsSearcher.ui.search.utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.business.model.Tweet;

import dmelnyk.tweetsSearcher.ui.dialogs.reference.RefDialog;

/**
 * Created by dmitry on 30.04.17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetHolder> {

    private static final long ANIMATE_DURATION = 600; // duration of "hide-visible" animation in ms.
    public static final int REFERENCE_CODE = 1;

    private ArrayList<Tweet> dataSet;
    private View cardView;
    private AppCompatActivity context;

    public TweetAdapter(ArrayList<Tweet> dataSet, AppCompatActivity context) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.twitt_card_view, parent, false);

        return new TweetHolder(cardView);
    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        Tweet tweet = dataSet.get(position);

        TextView textViewNickname = holder.textViewNickname;
        TextView textViewDate = holder.textViewDate;
        TextView textViewLikes = holder.textViewLikes;
        TextView textViewRetweets = holder.textViewRetweets;
        TextView textViewTweetContent = holder.textViewTweetContent;
        ImageView imageViewAvatar = holder.imageViewAvatar;

        textViewNickname.setText(tweet.getUserName());
        textViewDate.setText(tweet.getTweetDate());
        textViewLikes.setText("" + tweet.getLikes());
        textViewRetweets.setText("" + tweet.getRetweets());
        textViewTweetContent.setText(HtmlTextUtil.getHtmlFormattedText(tweet.getTweetText()));

        // loading image
        Picasso.with(context)
                .load(tweet.getUserImageUrl())
                .resize(85, 85)
                .placeholder(R.drawable.tweet_lg)
                .error(R.drawable.tweet_lg)
                .into(imageViewAvatar);

        animationView(position);

        textViewTweetContent.setOnClickListener(
                click -> new OpenReferenceInBrowser(tweet.getTweetText()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class TweetHolder extends RecyclerView.ViewHolder {
        TextView textViewNickname;
        TextView textViewDate;
        TextView textViewLikes;
        TextView textViewRetweets;
        TextView textViewTweetContent;
        ImageView imageViewAvatar;

        public TweetHolder(View itemView) {
            super(itemView);
            textViewNickname = (TextView) itemView.findViewById(R.id.nickname);
            textViewDate = (TextView) itemView.findViewById(R.id.date);
            textViewLikes = (TextView) itemView.findViewById(R.id.tweetLikes);
            textViewRetweets = (TextView) itemView.findViewById(R.id.tweetRetwitts);
            textViewTweetContent = (TextView) itemView.findViewById(R.id.tweetContent);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.tweetAvatar);
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

    public class OpenReferenceInBrowser {

        public OpenReferenceInBrowser(String tweet) {
            // check if text contains reference.
            // If text contains more then one references
            // show dialog-chooser.
            List<String> refs = HtmlTextUtil.findReferences(tweet);
            int refCounts = refs.size();
            switch (refCounts) {
                // tweet doesn't contain any reference
                case 0:
                    return;
                // tweet contains one reference
                case 1:
                    ((RefDialog.ReferenceListener) context).setReference(refs.get(0));
                    break;
                // tweet contains more then one reference
                default:
                    showDialogChooser(refs);

            }
        }

        private void showDialogChooser(List<String> refs) {
            DialogFragment dialogFragment = new RefDialog().getInstance(refs);
            dialogFragment.show(context.getSupportFragmentManager(), "refDialog");
        }
    }


}
