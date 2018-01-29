package com.edraw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;
import com.rattrap.utils.Log4JConfigurationHelper;
import com.rattrap.utils.LogConfig;
import org.apache.commons.io.IOUtils;

import com.edraw.impl.FileResource;
import com.google.common.collect.ImmutableMap;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		new Log4JConfigurationHelper(new LogConfig("INFO", null, null, true)).configure();
		
		final File workDir = new File("C:\\Users\\g3q\\Desktop\\Perso\\CHAIR2");
		
		final Map<String, String> fileNames = ImmutableMap.<String, String>builder().
				put("Chair.xml", "chairOutputConfig.xml").
				put("Chair_Shape.xml", "chair_ShapeOutputConfig.xml").build();
		
		for (final String fileName : fileNames.keySet()) {
			final File sourceFile = new File(workDir, fileName);
			
			final File outputConfigFile = new File(workDir, fileNames.get(fileName));
			
			final File variablesFile = new File(workDir, "variables.xml");

			final File outputDir = new File(workDir, "output");
			
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			final LaserPlanGenerator laserPlanGenerator = new LaserPlanGenerator(Maps.<String, VariableTranslation>newHashMap(), Maps.<String, VariableTranslation>newHashMap(), new FileResource(variablesFile), new FileResource(sourceFile), new FileResource(outputConfigFile), null, false);
			
			for (final Resource laserPlan : laserPlanGenerator.getLaserPlan()) {
				final InputStream laserInputStream = laserPlan.open();
				final FileOutputStream fileOutputStream = new FileOutputStream(new File(outputDir, laserPlan.getName()));
				try {
					IOUtils.copy(laserInputStream, fileOutputStream);
				} finally {
					fileOutputStream.close();
					laserInputStream.close();
				}
			}
			
		}
		
	}

}
