package com.edraw.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import com.edraw.Resource;

public class FileResource implements Resource {

	private final File source;
	
	public FileResource(File source) {
		super();
		this.source = source;
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public InputStream open() throws Exception {
		return new FileInputStream(source);
	}

	@Override
	public URL getURL() throws Exception {
		return source.toURI().toURL();
	}

}
