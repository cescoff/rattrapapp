package com.edraw;

import java.io.InputStream;
import java.net.URL;

public interface Resource {

	public String getName();
	
	public InputStream open() throws Exception;
	
	public URL getURL() throws Exception;
	
}
