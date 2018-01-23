package com.edraw.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rattrap.utils.JAXBUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edraw.Resource;
import com.edraw.VarContext;
import com.edraw.config.Variables;
import com.edraw.config.Variables.ExpressionDefinition;
import com.edraw.config.Variables.ValidationAssertion;
import com.edraw.config.Variables.VariableDefinition;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurableVariablesContext implements VarContext {

	private static final Logger logger = Logger.getLogger(ConfigurableVariablesContext.class);
	
	//private static final String CONFIGURATION_FILE_NAME = "variables.xml";
	
	private static final Pattern VARIABLE_CATCHER = Pattern.compile("\\$\\{([A-Za-z0-9_\\-]+)\\}");
	
	private final Map<String, Double> expressionContext = Maps.newHashMap();
	
	private final ScriptEngine scriptEngine;
	
	public ConfigurableVariablesContext(Resource configResource) {
		super();
		ScriptEngineManager mgr = new ScriptEngineManager();
	    this.scriptEngine = mgr.getEngineByName("JavaScript");
	    
		Variables variables;
		try {
			variables = JAXBUtils.unmarshal(Variables.class, configResource.open());
		} catch (Exception e) {
			throw new IllegalStateException("Configuration file '" + configResource.getName() + "' is not well formed or not accessible", e);
		}
		for (final VariableDefinition variableDefinition : variables.getVariables()) {
			final double varValue = variableDefinition.getValue();
			if (variableDefinition.isPrint()) {
				logger.info("Var '" + variableDefinition.getName() + "' has value '" + varValue + "'");
			}
			expressionContext.put(variableDefinition.getName(), varValue);
		}
		for (final ExpressionDefinition expressionDefinition : variables.getExpressions()) {
			final double varValue = evalExpr(expressionDefinition.getName(), expressionDefinition.getExpression());
			if (expressionDefinition.isPrint()) {
				logger.info("Var '" + expressionDefinition.getName() + "' has value '" + varValue + "'");
			}
			expressionContext.put(expressionDefinition.getName(), varValue);
		}
		final List<String> errorMessages = Lists.newArrayList();
		for (final ValidationAssertion validationAssertion : variables.getValidations()) {
			final double value = evalExpr(null, validationAssertion.getExpression());
			boolean validationFailed = false;
			if (">=0".equals(validationAssertion.getValidationExepression())) {
				if (value < 0) {
					validationFailed = true;
				}
			}
			if (">0".equals(validationAssertion.getValidationExepression())) {
				if (value <= 0) {
					validationFailed = true;
				}
			}
			if ("<=0".equals(validationAssertion.getValidationExepression())) {
				if (value > 0) {
					validationFailed = true;
				}
			}
			if ("<0".equals(validationAssertion.getValidationExepression())) {
				if (value >= 0) {
					validationFailed = true;
				}
			}
			if ("==0".equals(validationAssertion.getValidationExepression())) {
				if (value != 0) {
					validationFailed = true;
				}
			}
			if (validationFailed) {
				errorMessages.add("Failed to validation '" + validationAssertion.getName() + "' '" + validationAssertion.getExpression() + "'" + validationAssertion.getValidationExepression() + "' is false (" + value + ")");
			} else {
				logger.info("Validation '" + validationAssertion.getName() + "' has value " + value + validationAssertion.getValidationExepression());
			}
		}
		if (errorMessages.size() > 0) {
			throw new IllegalStateException("Validation errors : '" + Joiner.on("', '").join(errorMessages) + "'");
		}
	}

	private double evalExpr(final String varName, final String expression) {
		final Matcher expressionMatcher = VARIABLE_CATCHER.matcher(expression);
		String contextualizedExpression = expression;
		while (expressionMatcher.find()) {
			final String variableName = getVariableName(expressionMatcher.group());
			if (!expressionContext.containsKey(variableName)) {
				throw new IllegalStateException("Variable '" + variableName + "' has no value defined");
			} else {
				contextualizedExpression = StringUtils.replace(contextualizedExpression, "${" + variableName + "}", expressionContext.get(variableName) + "");
			}
		}
		String evaluatedExpression;
		try {
			evaluatedExpression = this.scriptEngine.eval(contextualizedExpression).toString();
		} catch (ScriptException e) {
			final String expressionNamePrefix;
			if (StringUtils.isNotEmpty(varName)) {
				expressionNamePrefix = "named '" + varName + "'";
			} else {
				expressionNamePrefix = "";
			}
			throw new IllegalStateException("Wrong expression named " + expressionNamePrefix + " " + contextualizedExpression + "' generated from '" + expression + "'", e);
		}
		try {
			return Double.parseDouble(evaluatedExpression);
		} catch (NumberFormatException nfe) {
			throw new IllegalStateException("Cannot parse double value '" + evaluatedExpression + "' resulting from eval of '" + contextualizedExpression + "'");
		}
	}
	
	private String getVariableName(final String variableExpression) {
		return StringUtils.remove(StringUtils.remove(variableExpression, "${"), "}");
	}
	
	@Override
	public double evaluate(String expression) throws Exception {
		if (this.scriptEngine == null) {
			try {
				return Double.parseDouble(expression);
			} catch (NumberFormatException nfe) {
				throw new Exception("You have defined no variables configuration file and this expression '" + expression + "' is not a valid number");
			}
		}

		if (this.expressionContext.containsKey(expression)) {
			return this.expressionContext.get(expression);
		}

		return evalExpr("", expression);
	}

	public static void main(String[] args) throws Exception {
		final Variables vars = new Variables() ;
		vars.getVariables().add(new VariableDefinition("fullUpright", 45));
		vars.getVariables().add(new VariableDefinition("fullHeight", 1050));
		vars.getVariables().add(new VariableDefinition("fullSeatwidth", 310));
		vars.getVariables().add(new VariableDefinition("ratio", 1));
		
		vars.getExpressions().add(new ExpressionDefinition("upright", "${fullUpright}/${ratio}"));
		vars.getExpressions().add(new ExpressionDefinition("height", "${fullHeight}/${ratio}"));
		vars.getExpressions().add(new ExpressionDefinition("seatwidth", "${fullSeatwidth}/${ratio}"));
		
		System.out.println(JAXBUtils.marshal(vars, true));
	}

}
