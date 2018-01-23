package com.edraw.config;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public enum OutputFormat {

	SVG(ImmutableMap.<String, Map<String, String>>builder().
			put("html", ImmutableMap.<String, String>builder().put("html", "true").build()).
			put("svg", ImmutableMap.<String, String>builder().put("html", "false").build()).build());
	
	private final Map<String, Map<String, String>> extensionsVars;

	private OutputFormat(Map<String, Map<String, String>> vars) {
		this.extensionsVars = vars;
	}

	public Iterable<String> getExtensions() {
		return extensionsVars.keySet();
	}

	public Map<String, String> getVars(final String extension) {
		return extensionsVars.get(extension);
	}

}
