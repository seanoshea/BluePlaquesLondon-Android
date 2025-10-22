package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.IWikipediaModelDelegate;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

/**
 * Fragment for displaying Wikipedia articles about blue plaque subjects.
 */
public class WikipediaFragment extends Fragment implements IWikipediaModelDelegate {

    private static final String TAG = "WikipediaFragment";
    private WebView webView;
    private Placemark placemark;
    private WikipediaModel wikipediaModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_wikipedia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.activity_wikipedia_web_view);
        getPlacemarkFromArguments();
        setupWebView();
    }

    private void getPlacemarkFromArguments() {
        if (getArguments() != null) {
            placemark = getArguments().getParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY);
        }
    }

    private void setupWebView() {
        if (placemark == null) {
            Log.e(TAG, "Placemark is null, cannot setup WebView");
            return;
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView error: " + error.getDescription());
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "Loading progress: " + newProgress + "%");
            }
        });

        loadWikipediaPage();

        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.WIKIPEDIA_BUTTON_PRESSED_EVENT,
                placemark.getTrimmedName());
    }

    private void loadWikipediaPage() {
        if (placemark == null) {
            Log.e(TAG, "Placemark is null, cannot load Wikipedia page");
            return;
        }

        String name = placemark.getTrimmedName();
        if (name == null || name.isEmpty()) {
            Log.e(TAG, "Placemark name is null or empty, cannot load Wikipedia page");
            return;
        }

        Log.d(TAG, "Loading Wikipedia page for: " + name);
        
        // Use WikipediaModel to search for the article
        wikipediaModel = new WikipediaModel();
        wikipediaModel.setDelegate(this);
        wikipediaModel.execute(name, getString(R.string.wikipedia_url));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
        }
        if (wikipediaModel != null) {
            wikipediaModel.onPause();
        }
    }

    @Override
    public void onRetriveWikipediaUrlSuccess(String url) {
        Log.d(TAG, "Wikipedia URL retrieved successfully: " + url);
        if (webView != null && url != null) {
            webView.loadUrl(url);
        } else {
            Log.e(TAG, "Cannot load URL - webView or url is null");
        }
    }

    @Override
    public void onRetriveWikipediaUrlFailure() {
        Log.e(TAG, "Failed to retrieve Wikipedia URL for: " + (placemark != null ? placemark.getTrimmedName() : "unknown"));
        if (placemark != null) {
            // Fallback to direct Wikipedia URL
            String name = placemark.getTrimmedName();
            if (name != null && !name.isEmpty()) {
                String fallbackUrl = "https://en.wikipedia.org/wiki/" + name.replace(" ", "_");
                Log.d(TAG, "Using fallback URL: " + fallbackUrl);
                if (webView != null) {
                    webView.loadUrl(fallbackUrl);
                } else {
                    Log.e(TAG, "WebView is null, cannot load fallback URL");
                }
            } else {
                Log.e(TAG, "Placemark name is null or empty, cannot create fallback URL");
            }
        }
    }
}
