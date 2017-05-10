package dmelnyk.tweetsSearcher.ui.dialogs.reference.core;

/**
 * Created by dmitry on 05.05.17.
 */
//public class OpenReferenceInBrowser {
//
//    public OpenReferenceInBrowser(AppCompatActivity context, String tweet) {
//        // check if text contains reference.
//        // If text contains more then one references
//        // show dialog-chooser.
//        List<String> refs = HtmlTextUtil.findReferences(tweet);
//        int refCounts = refs.size();
//        switch (refCounts) {
//            case 0:
//                // tweet doesn't contain any reference
//                return;
//            case 1:
//                Intent intent = new Intent(context, WebViewActivity.class);
//                intent.putExtra(WebViewActivity.KEY_URL, refs.get(0));
//                context.startActivity(intent);
//                break;
//            default:
//                showDialogChooser(context, refs);
//
//        }
//    }
//
//    private void showDialogChooser(AppCompatActivity context, List<String> refs) {
//        DialogFragment dialogFragment = new RefDialog().getInstance(context, refs);
//        dialogFragment.show(context.getSupportFragmentManager(), "refDialog");
//    }
//
//
//}
