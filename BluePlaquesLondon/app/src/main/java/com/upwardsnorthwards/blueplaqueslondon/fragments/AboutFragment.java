// Copyright (c) 2014 - 2016 Upwards Northwards Software Limited
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software
// must display the following acknowledgement:
// This product includes software developed by Upwards Northwards Software Limited.
// 4. Neither the name of Upwards Northwards Software Limited nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY UPWARDS NORTHWARDS SOFTWARE LIMITED ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE UPWARDS NORTHWARDS SOFTWARE LIMITED BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;

/**
 * About screen fragment displaying application information and credits.
 * 
 * <p>This fragment presents information about the Blue Plaques London application,
 * including developer credits, data sources, and acknowledgments. It serves as
 * the application's information and credits screen.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Developer Credits:</strong> Information about the application developer</li>
 *   <li><strong>Data Attribution:</strong> Credits for blue plaque data sources</li>
 *   <li><strong>Interactive Links:</strong> Clickable links to external websites</li>
 *   <li><strong>HTML Content:</strong> Rich text formatting with proper link handling</li>
 *   <li><strong>Modern Compatibility:</strong> Uses {@code Html.FROM_HTML_MODE_LEGACY} for Android 11+</li>
 * </ul>
 * 
 * <h3>Content Sections:</h3>
 * <ul>
 *   <li><strong>Developer Details:</strong> Links to developer's GitHub profile</li>
 *   <li><strong>Map Data Details:</strong> Attribution for blue plaque data maintenance</li>
 *   <li><strong>External Links:</strong> Properly configured clickable links</li>
 * </ul>
 * 
 * <h3>Technical Implementation:</h3>
 * <p>The fragment handles HTML content rendering with:</p>
 * <ul>
 *   <li>{@link Html#fromHtml(String, int)} with {@code FROM_HTML_MODE_LEGACY}</li>
 *   <li>{@link LinkMovementMethod} for clickable link functionality</li>
 *   <li>Proper TextView configuration for link interaction</li>
 * </ul>
 * 
 * <h3>Navigation:</h3>
 * <p>Accessed from the main menu in {@link com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity}
 * via the Navigation Component.</p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Navigation handled automatically by Navigation Component
 * navController.navigate(R.id.action_mapFragment_to_aboutFragment);
 * }</pre>
 * 
 * @see com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity
 * @see Html#fromHtml(String, int)
 * @see LinkMovementMethod
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        // allow users click on the links in the text views
        final TextView developedByTextView = (TextView) view.findViewById(R.id.fragment_about_developed_by);
        final TextView mapDataTextView = (TextView) view.findViewById(R.id.fragment_about_map_data);

        developedByTextView.setText(Html.fromHtml(getResources().getString(R.string.developed_by), Html.FROM_HTML_MODE_LEGACY));
        mapDataTextView.setText(Html.fromHtml(getResources().getString(R.string.map_data), Html.FROM_HTML_MODE_LEGACY));

        developedByTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mapDataTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
