package com.edraw.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import com.edraw.Resource;

public class StringResource implements Resource {

	private final String name;
	
	private final String content;

	public StringResource(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InputStream open() throws Exception {
		return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public URL getURL() throws Exception {
		return new URL("string://" + name);
	}
	
	
	
}
