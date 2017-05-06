package dmelnyk.tweetsSearcher.ui.dialogs.reference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.ui.web.WebViewActivity;

/**
 * Created by dmitry on 05.05.17.
 */

public class RefDialog extends DialogFragment {

    private static AppCompatActivity context;
    private static List<String> references;

    public static RefDialog getInstance(AppCompatActivity context_, List<String> references_) {
        context = context_;
        references = references_;
        return new RefDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.RefDialog_title))
                .setItems(references.toArray(new CharSequence[references.size()]),
                        (view, refIndex) -> runInTheWeb(refIndex))
                .setNegativeButton(context.getString(R.string.RefDialog_negative_button),
                        (ignore, index) -> { /* NOP */});

        return builder.create();
    }

    private void runInTheWeb(int index) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.KEY_URL, references.get(index));
        context.startActivity(intent);
    }
}
