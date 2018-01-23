package com.edraw;

import java.io.OutputStream;
import java.util.Map;

import com.edraw.geom.BluePrint;

public interface BluePrintConverter {

	public void convertTo(final BluePrint bluePrint, final Iterable<String> layers, final OutputStream outputStream, final Map<String, String> parameters);

}
