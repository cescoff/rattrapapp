package com.edraw.impl;

import java.io.InputStream;
import java.net.URL;


import com.edraw.Resource;
import org.apache.commons.lang3.StringUtils;

public class URLResource implements Resource {

	private final String name;
	
	private final String url;

	public URLResource(String url) {
		super();
		if (StringUtils.isEmpty(url)) {
			this.name = null;
		} else {
			int lastSlash = StringUtils.lastIndexOf(url, "/");
			if (lastSlash < url.length()) {
				this.name = StringUtils.substring(url, lastSlash + 1);
			} else {
				this.name = null;
			}
		}
		this.url = url;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InputStream open() throws Exception {
		final URL realURL = new URL(url);
		return realURL.openStream();
	}

	@Override
	public URL getURL() throws Exception {
		return new URL(url);
	}

}
