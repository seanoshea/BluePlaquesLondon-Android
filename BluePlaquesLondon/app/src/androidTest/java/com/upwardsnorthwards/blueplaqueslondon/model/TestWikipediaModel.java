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

import junit.framework.TestCase;

public class TestWikipediaModel extends TestCase {

    private WikipediaModel model;
    private DummyWikipediaModelDelegate delegate;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.model = new WikipediaModel();
        this.delegate = new DummyWikipediaModelDelegate();
        this.model.setDelegate(delegate);
    }

    public void testMalformedJSON() {
        model.onPostExecute("{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":8723},\"search\":[{\"ns\":0,\"title\":\"Winston Churchill\",\"timestamp\":\"2016-03-22T09:30:12Z\"},{\"ns\":0,\"title\":\"Andrew Cunningham, 1st Viscount Cunningham of Hyndhope\",\"timestamp\":\"2016-02-04T11:38:46Z\"},{\"ns\":0,\"title\":\"Winston Churchill (novelist)\",\"timestamp\":\"2016-03-15T01:21:18Z\"},{\"ns\":0,\"title\":\"Ian Hamilton's March\",\"timestamp\":\"2016-03-08T21:58:49Z\"},{\"ns\":0,\"title\":\"John Churchill, 1st Duke of Marlborough\",\"timestamp\":\"2016-03-09T23:46:22Z\"},{\"ns\":0,\"title\":\"Winston Churchill Avenue\",\"timestamp\":\"2015-04-24T18:35:45Z\"},{\"ns\":0,\"title\":\"Mahdist War\",\"timestamp\":\"2016-03-12T23:05:34Z\"},{\"ns\":0,\"title\":\"Dunkirk evacuation\",\"timestamp\":\"2016-03-18T14:34:40Z\"},{\"ns\":0,\"title\":\"Winston Churchill as writer\",\"timestamp\":\"2016-03-11T16:44:11Z\"},{\"ns\":0,\"title\":\"Winston Churchill (1620\\u20131688)\",\"timestamp\":\"2016-03-17T13:15:23Z\"");
        assertTrue(delegate.isFailedToRetrieveUrl());
        assertEquals(delegate.getReturnedUrl(), null);
    }

    public void testEmptySearchResponseJSON() {
        model.onPostExecute("{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":0},\"search\":[]}}");
        assertTrue(delegate.isFailedToRetrieveUrl());
        assertEquals(delegate.getReturnedUrl(), null);
    }

    public void testSargent() {
        model.onPostExecute("{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":397},\"search\":[{\"ns\":0,\"title\":\"Hans Leo Hassler\",\"timestamp\":\"2016-02-06T02:33:18Z\"},{\"ns\":0,\"title\":\"Malcolm Sargent\",\"timestamp\":\"2016-02-02T06:09:24Z\"},{\"ns\":0,\"title\":\"Michael Tippett\",\"timestamp\":\"2016-03-06T20:56:12Z\"},{\"ns\":0,\"title\":\"1950 in music\",\"timestamp\":\"2016-03-17T15:42:08Z\"},{\"ns\":0,\"title\":\"CLIC Sargent\",\"timestamp\":\"2016-03-01T11:26:32Z\"},{\"ns\":0,\"title\":\"Thomas Beecham\",\"timestamp\":\"2016-03-09T22:20:40Z\"},{\"ns\":0,\"title\":\"William Walton\",\"timestamp\":\"2015-12-04T19:39:03Z\"},{\"ns\":0,\"title\":\"London Philharmonic Orchestra\",\"timestamp\":\"2016-01-27T09:43:02Z\"},{\"ns\":0,\"title\":\"Adrian Boult\",\"timestamp\":\"2016-01-31T16:33:25Z\"},{\"ns\":0,\"title\":\"Edward Elgar\",\"timestamp\":\"2016-03-05T13:44:54Z\"}]}}");
        assertFalse(delegate.isFailedToRetrieveUrl());
        assertEquals(delegate.getReturnedUrl(), "https://en.wikipedia.org/wiki/Malcolm_Sargent");
    }

    private class DummyWikipediaModelDelegate implements  IWikipediaModelDelegate {

        private String returnedUrl;
        private boolean failedToRetrieveUrl;

        @Override
        public void onRetriveWikipediaUrlSuccess(String url) {
            this.failedToRetrieveUrl = false;
            this.returnedUrl = url;
        }

        @Override
        public void onRetriveWikipediaUrlFailure() {
            this.failedToRetrieveUrl = true;
            this.returnedUrl = null;
        }

        public String getReturnedUrl() {
            return returnedUrl;
        }

        public boolean isFailedToRetrieveUrl() {
            return failedToRetrieveUrl;
        }
    }
}
