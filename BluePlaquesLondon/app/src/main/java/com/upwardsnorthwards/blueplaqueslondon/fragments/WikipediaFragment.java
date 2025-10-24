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
 * Modern Wikipedia article display fragment using Navigation Component architecture.
 * 
 * <p>This fragment displays Wikipedia articles about blue plaque subjects using a WebView
 * with intelligent article resolution. It represents the modern replacement for the legacy
 * {@link com.upwardsnorthwards.blueplaqueslondon.activities.WikipediaActivity}.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Smart Article Resolution:</strong> Uses Wikipedia Search API via {@link WikipediaModel}</li>
 *   <li><strong>Fallback Strategy:</strong> Direct Wikipedia URLs if search fails</li>
 *   <li><strong>Modern Architecture:</strong> Navigation Component integration</li>
 *   <li><strong>Enhanced WebView:</strong> Optimized settings and error handling</li>
 *   <li><strong>Comprehensive Logging:</strong> Detailed debugging and error tracking</li>
 *   <li><strong>Lifecycle Management:</strong> Proper resource cleanup and state handling</li>
 * </ul>
 * 
 * <h3>Article Resolution Process:</h3>
 * <ol>
 *   <li><strong>Primary:</strong> Wikipedia Search API query for plaque subject name</li>
 *   <li><strong>Matching:</strong> Intelligent title matching against search results</li>
 *   <li><strong>Fallback:</strong> Direct Wikipedia URL construction if search fails</li>
 *   <li><strong>Loading:</strong> WebView displays the resolved article</li>
 * </ol>
 * 
 * <h3>Architecture Integration:</h3>
 * <p>Implements the delegate pattern with {@link IWikipediaModelDelegate}:</p>
 * <ul>
 *   <li>{@link #onRetriveWikipediaUrlSuccess(String)} - Handles successful URL resolution</li>
 *   <li>{@link #onRetriveWikipediaUrlFailure()} - Handles search failures with fallback</li>
 * </ul>
 * 
 * <h3>WebView Configuration:</h3>
 * <ul>
 *   <li>JavaScript enabled for modern Wikipedia features</li>
 *   <li>DOM storage enabled for better performance</li>
 *   <li>Wide viewport and overview mode for mobile optimization</li>
 *   <li>Comprehensive error handling and progress tracking</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Navigation handled automatically by Navigation Component
 * Bundle args = new Bundle();
 * args.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, placemark);
 * navController.navigate(R.id.action_mapDetailFragment_to_wikipediaFragment, args);
 * }</pre>
 * 
 * @see WikipediaModel
 * @see IWikipediaModelDelegate
 * @see MapDetailFragment
 * @see com.upwardsnorthwards.blueplaqueslondon.activities.WikipediaActivity
 * 
 * @author Blue Plaques London Team
 * @since 3.0
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
