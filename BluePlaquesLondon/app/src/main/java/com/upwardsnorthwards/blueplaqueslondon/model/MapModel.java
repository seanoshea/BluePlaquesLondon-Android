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

package com.upwardsnorthwards.blueplaqueslondon.model;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesKMLParser;

import java.util.ArrayList;
import java.util.List;

public class MapModel {

    private final BluePlaquesKMLParser parser;

    public MapModel() {
        parser = new BluePlaquesKMLParser();
    }

    public void loadMapData(final Context context) {
        parser.loadMapData(context);
    }

    @NonNull
    public ArrayList<Placemark> getPlacemarksAtIndices(@NonNull final List<Integer> indices) {
        final ArrayList<Placemark> placemarksAtIndices = new ArrayList<>();
        for (final Integer index : indices) {
            placemarksAtIndices.add(parser.getPlacemarks().get(index));
        }
        return placemarksAtIndices;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public Placemark getPlacemarkClosestToPlacemark(@NonNull final Location location) {
        Placemark closestPlacemark = null;
        float currentDistance = 0;
        final float[] results = new float[1];
        for (final Placemark placemark : getMassagedPlacemarks()) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), placemark.getLatitude(), placemark.getLongitude(), results);
            final float distance = results[0];
            if (closestPlacemark == null) {
                currentDistance = distance;
                closestPlacemark = placemark;
            } else if (distance < currentDistance) {
                currentDistance = distance;
                closestPlacemark = placemark;
            }
        }
        return closestPlacemark;
    }

    public List<Placemark> getMassagedPlacemarks() {
        return parser.getMassagedPlacemarks();
    }

    public BluePlaquesKMLParser getParser() {
        return parser;
    }

}
