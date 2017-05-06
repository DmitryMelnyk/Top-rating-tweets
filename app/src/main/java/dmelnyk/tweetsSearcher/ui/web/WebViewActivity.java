package dmelnyk.tweetsSearcher.ui.web;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmelnyk.tweetsSearcher.R;

public class WebViewActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    public static final String KEY_URL = "key content";

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        configureProgressBar();
        if (savedInstanceState == null) {
            String urlAddress = getIntent().getStringExtra(KEY_URL);
            Log.d(TAG, "web url = " + urlAddress);
            initializeWebView(urlAddress);
        }

    }

    private void configureProgressBar() {
        progressBar.setMax(100);
    }

    private void initializeWebView(String urlAddress) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (urlAddress.startsWith("http")) {
                    return false;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // show loading progress
        webView.setWebChromeClient( new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        webView.loadUrl(urlAddress);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
