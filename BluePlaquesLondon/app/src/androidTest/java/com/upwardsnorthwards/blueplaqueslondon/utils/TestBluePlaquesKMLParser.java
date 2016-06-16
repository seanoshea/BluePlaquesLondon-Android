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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.support.annotation.NonNull;
import android.test.InstrumentationTestCase;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

public class TestBluePlaquesKMLParser extends InstrumentationTestCase {

    private static final String WANAMAKER = "51.50805700012604-0.09657699988338207";
    private static final String GAINSBOROUGH = "51.50587100027157-0.1362019998878026";
    private static final String HOLST = "51.49613193889098-0.2216046653199584";
    private static final String NICHOLSON = "51.55502299983695-0.1724799999779625";
    private static final String MCMILLAN = "51.408840999931140.01513800008473787";
    private static final String TAIT = "51.56953535360053-0.1815846874911598";
    private static final String HUTCHINSON = "51.54615000003784-0.1591900000075996";
    private static final String MANN = "51.551910999706-0.2066150000297029";
    private static final String PEVSNER = "51.56859400014348-0.1821930000271832";
    private static final String TAYLOR = "51.53885100036625-0.1508040000021182";
    private static final String WALTON = "51.49705000054198-0.1539939998990136";
    private static final String POPPER = "51.63527499996276-0.1592689998720098";
    private static final String HAZLITT = "51.51438100024222-0.1321030000283372";
    private static final String ADAM = "51.50897606853864-0.1226314713386643";
    private static final String SHELLEY = "51.51462099986226-0.1370979999471302";
    private static final String MOORE = "51.51716200008351-0.1571309999538215";
    private static final String PELHAM = "51.50705700003604-0.1409309999618671";
    private static final String HANCOCK = "51.49675315496057-0.1808973543618675";

    public void testLoadMapData() {
        BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
        parser.loadMapData(getInstrumentation().getContext());

        List<Placemark> placemarks = parser.getPlacemarks();
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();

        assertTrue(placemarks.size() == 18);
        assertTrue(massagedPlacemarks.size() == 935);
    }

    public void testNames() {
        BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
        parser.loadMapData(getInstrumentation().getContext());

        assertTrue(getPlacemark(parser, WANAMAKER).getName().equals("WANAMAKER, Sam"));
        assertTrue(getPlacemark(parser, GAINSBOROUGH).getName().equals("GAINSBOROUGH, Thomas"));
        assertTrue(getPlacemark(parser, HOLST).getName().equals("HOLST, Gustav"));
        assertTrue(getPlacemark(parser, NICHOLSON).getName().equals("NICHOLSON, William"));
        assertTrue(getPlacemark(parser, MCMILLAN).getName().equals("McMILLAN, Margaret"));
        assertTrue(getPlacemark(parser, TAIT).getName().equals("TAIT, Thomas Smith"));
        assertTrue(getPlacemark(parser, HUTCHINSON).getName().equals("HUTCHINSON, Leslie 'Hutch'"));
        assertTrue(getPlacemark(parser, MANN).getName().equals("MANN, Dame Ida"));
        assertTrue(getPlacemark(parser, PEVSNER).getName().equals("PEVSNER, Sir Nikolaus"));
        assertTrue(getPlacemark(parser, TAYLOR).getName().equals("TAYLOR, A.J.P."));
        assertTrue(getPlacemark(parser, WALTON).getName().equals("WALTON, Sir William"));
        assertTrue(getPlacemark(parser, POPPER).getName().equals("POPPER, Karl"));
        assertTrue(getPlacemark(parser, HAZLITT).getName().equals("HAZLITT, William"));
        assertTrue(getPlacemark(parser, ADAM).getName().equals("ADAM, Robert"));
        assertTrue(getPlacemark(parser, SHELLEY).getName().equals("SHELLEY, Percy Bysshe"));
        assertTrue(getPlacemark(parser, MOORE).getName().equals("MOORE, Tom"));
        assertTrue(getPlacemark(parser, PELHAM).getName().equals("PELHAM, Henry"));
        assertTrue(getPlacemark(parser, HANCOCK).getName().equals("HANCOCK, Tony"));
    }

    public void testOccupations() {
        BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
        parser.loadMapData(getInstrumentation().getContext());

        assertTrue(getPlacemark(parser, WANAMAKER).getTrimmedOccupation().equals("The man behind Shakespeare's Globe"));
        assertTrue(getPlacemark(parser, GAINSBOROUGH).getTrimmedOccupation().equals("Artist, lived here"));
        assertTrue(getPlacemark(parser, HOLST).getTrimmedOccupation().equals("Composer, wrote, The Planets, and, taught here"));
        assertTrue(getPlacemark(parser, NICHOLSON).getTrimmedOccupation().equals("Painter and Printmaker lived here 1904-1906"));
        assertTrue(getPlacemark(parser, MCMILLAN).getTrimmedOccupation().equals("RACHEL McMILLAN 1859-1917 MARGARET McMILLAN 1860-1931 Pioneers of Nursery Education lodged here"));
        assertTrue(getPlacemark(parser, TAIT).getTrimmedOccupation().equals("Architect lived here"));
        assertTrue(getPlacemark(parser, HUTCHINSON).getTrimmedOccupation().equals("Singer and Pianist lived here 1929-1967"));
        assertTrue(getPlacemark(parser, MANN).getTrimmedOccupation().equals("Ophthalmologist lived here 1902-1934"));
        assertTrue(getPlacemark(parser, PEVSNER).getTrimmedOccupation().equals("Architectural Historian lived here from 1936 until his death"));
        assertTrue(getPlacemark(parser, TAYLOR).getTrimmedOccupation().equals("Historian and Broadcaster lived here"));
        assertTrue(getPlacemark(parser, WALTON).getTrimmedOccupation().equals("Composer lived here"));
        assertTrue(getPlacemark(parser, POPPER).getTrimmedOccupation().equals("16 Burlington Rise, EN4"));
        assertTrue(getPlacemark(parser, HAZLITT).getTrimmedOccupation().equals("6 Frith Street, W1"));
        assertTrue(getPlacemark(parser, SHELLEY).getTrimmedOccupation().equals("Poet, lived here, in 1811"));
        assertTrue(getPlacemark(parser, MOORE).getTrimmedOccupation().equals("Irish Poet, lived here"));
        assertTrue(getPlacemark(parser, PELHAM).getTrimmedOccupation().equals("Prime Minister, lived here."));
        assertTrue(getPlacemark(parser, HANCOCK).getTrimmedOccupation().equals("Comedian lived here 1952-1958"));
    }

    public void testAddress() {
        BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
        parser.loadMapData(getInstrumentation().getContext());

        assertTrue(getPlacemark(parser, WANAMAKER).getAddress().equals("New Globe Buildings, Bankside, SE1"));
        assertTrue(getPlacemark(parser, POPPER).getAddress().equals("16 Burlington Rise, EN4"));
        assertTrue(getPlacemark(parser, HAZLITT).getAddress().equals("6 Frith Street, W1"));
        assertTrue(getPlacemark(parser, ADAM).getAddress().equals("1-3 Robert Street, Adelphi, WC2"));
    }

    public void testNote() {
        BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
        parser.loadMapData(getInstrumentation().getContext());

        assertTrue(getPlacemark(parser, GAINSBOROUGH).getNote().equals("Note: Replaces plaque up in 1881 by RSA at No. 80."));
        assertTrue(getPlacemark(parser, MOORE).getNote().equals("Note: This plaque was removed from 28 Bury Street, St James's Westminster in 1962."));
    }

    private Placemark getPlacemark(@NonNull BluePlaquesKMLParser parser, String key) {
        List<Integer> positions = parser.getKeyToArrayPositions().get(key);
        Integer location = positions.get(0);
        Placemark placemark = parser.getPlacemarks().get(location);
        placemark.digestFeatureDescription();
        placemark.digestAnciliaryInformation();
        return placemark;
    }
}