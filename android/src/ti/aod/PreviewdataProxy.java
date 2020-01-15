/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2017 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.aod;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;

// This proxy can be created by calling Drau.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = AodModule.class)
public class PreviewdataProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = AodModule.LCAT;
	
	private int station;
	private long interval = 1000;
	private boolean running = false;
	private Timer timer;
	KrollFunction onError;
	KrollFunction onLoad;
	Document doc;

	private String url = "http://srv.deutschlandradio.de/aodpreviewdata.1915.de.rpc?drbm:station_id=";

	// Constructor
	public PreviewdataProxy() {
		super();
	}

	private final class SoupRequestHandler extends AsyncTask<Void, Void, KrollDict> {
		@Override
		protected KrollDict doInBackground(Void[] arg0) {
			KrollDict resultDict = new KrollDict();
			try {
				Log.d(LCAT, url);
				doc = Jsoup.connect(url).ignoreContentType(false).get();
				resultDict.put("time_start",doc.select("time_start").get(0).text());
				resultDict.put("name",doc.select("name").get(0).text());
				resultDict.put("text",doc.select("text").get(0).text());
				resultDict.put("href_text",doc.select("href_text").get(0).text());
				resultDict.put("href",doc.select("href").get(0).text());
			} catch (IOException e) {
				e.printStackTrace();
				if (onError != null)
					onError.call(getKrollObject(), resultDict);
			}
			return resultDict;
		}

		protected void onPostExecute(KrollDict resultDict) {
			if (onLoad != null) {
				resultDict.put("charset", doc.charset().name());
				resultDict.put("location", doc.location());
				resultDict.put("length", doc.toString().length());
				onLoad.call(getKrollObject(), resultDict);
			} else
				Log.w(LCAT, "onload is missing");
		}
	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict opts) {
		super.handleCreationDict(opts);

		if (opts.containsKey("station")) {
			station = opts.getInt("station");
		}

	}

	@Kroll.method
	public void start(KrollDict opts) {
		if (opts.containsKey("interval")) {
			interval = opts.getInt("interval");
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				AsyncTask<Void, Void, KrollDict> doRequest = new SoupRequestHandler();
			}
		}, 0, interval);
	}

	@Kroll.method
	public void stop() {
		timer.cancel();
	}

	
}
