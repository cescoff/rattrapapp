package com.edraw.impl;

import java.io.InputStream;
import java.net.URL;

import com.edraw.Resource;
import com.rattrap.utils.JAXBUtils;

public class JAXBResource<O> implements Resource {

	private final String name;
	
	private final O object;
	
	public JAXBResource(String name, O object) {
		super();
		this.name = name;
		this.object = object;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InputStream open() throws Exception {
		return new StringResource(name, JAXBUtils.marshal(object, true)).open();
	}

	@Override
	public URL getURL() throws Exception {
		return new URL("jaxb://" + name);
	}

}
