package com.edraw;

public interface VarContext {

	public <T> T evaluate(final String expression, Class<T> dataType) throws Exception;

}
