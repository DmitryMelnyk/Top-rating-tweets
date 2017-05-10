package dmelnyk.tweetsSearcher.ui.dialogs.reference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.List;

import dmelnyk.tweetsSearcher.R;

/**
 * Created by dmitry on 05.05.17.
 */

public class RefDialog extends DialogFragment {

    private ReferenceListener listener;

    // callback interface for activity
    public interface ReferenceListener {
        void setReference(String reference);
    }

    private static List<String> references;

    public static RefDialog getInstance(List<String> references_) {
        references = references_;
        return new RefDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            listener = (ReferenceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement ReferenceListener callback interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.RefDialog_title))
                .setItems(references.toArray(new CharSequence[references.size()]),
                        (view, refIndex) -> {
                            // send reference
                            listener.setReference(references.get(refIndex));
                        })
                .setNegativeButton(getContext().getString(R.string.RefDialog_negative_button),
                        (ignore, index) -> { /* NOP */});

        return builder.create();
    }
}
