package com.edraw.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.edraw.*;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.lang3.StringUtils;

import com.edraw.config.Variables;
import com.edraw.config.Variables.ExpressionDefinition;
import com.edraw.config.Variables.ValidationAssertion;
import com.edraw.config.Variables.VariableDefinition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableVariablesContext implements VarContext {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurableVariablesContext.class);
	
	//private static final String CONFIGURATION_FILE_NAME = "variables.xml";
	
	private static final Pattern VARIABLE_CATCHER = Pattern.compile("\\$\\{([A-Za-z0-9_\\-]+)\\}");
	
	private final Map<String, Double> numericExpressionContext = Maps.newHashMap();

	private final Map<String, Boolean> booleanExpressionContext = Maps.newHashMap();

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
			if (StringUtils.isEmpty(variableDefinition.getValue())) {
				throw new ValidationError(ErrorMessage.create("Variable '" + variableDefinition.getName() + "' has no value defined"));
			}
			if (variableDefinition.getVariableType() == VariableType.NUMERIC) {
				final double varValue = variableDefinition.getDouble();
				if (variableDefinition.isPrint()) {
					logger.info("Var '" + variableDefinition.getName() + "' has value '" + varValue + "'");
				}
				numericExpressionContext.put(variableDefinition.getName(), varValue);
			} else if (variableDefinition.getVariableType() == VariableType.BOOLEAN) {
				final boolean varValue = variableDefinition.getBoolean();
				if (variableDefinition.isPrint()) {
					logger.info("Var '" + variableDefinition.getName() + "' has value '" + varValue + "'");
				}
				booleanExpressionContext.put(variableDefinition.getName(), varValue);
			}
		}
		for (final ExpressionDefinition expressionDefinition : variables.getExpressions()) {
			final double varValue = evalDoubleExpr(expressionDefinition.getName(), expressionDefinition.getExpression());
			if (expressionDefinition.isPrint()) {
				logger.info("Var '" + expressionDefinition.getName() + "' has value '" + varValue + "'");
			}
			numericExpressionContext.put(expressionDefinition.getName(), varValue);
		}
		final List<ErrorMessage> errorMessages = Lists.newArrayList();
		for (final ValidationAssertion validationAssertion : variables.getValidations()) {
			final double value = evalDoubleExpr(null, validationAssertion.getExpression());
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
				errorMessages.add(ErrorMessage.create(validationAssertion.getName(), "'" + validationAssertion.getExpression() + "'" + validationAssertion.getValidationExepression() + "' is false (" + value + ")"));
			} else {
				logger.info("Validation '" + validationAssertion.getName() + "' has value " + value + validationAssertion.getValidationExepression());
			}
		}
		if (errorMessages.size() > 0) {
			throw new ValidationError(errorMessages);
		}
	}

	private double evalDoubleExpr(final String varName, final String expression) {
		final Matcher expressionMatcher = VARIABLE_CATCHER.matcher(expression);
		String contextualizedExpression = expression;
		while (expressionMatcher.find()) {
			final String variableName = getVariableName(expressionMatcher.group());
			if (!numericExpressionContext.containsKey(variableName)) {
				throw new IllegalStateException("Variable '" + variableName + "' has no value defined");
			} else {
				contextualizedExpression = StringUtils.replace(contextualizedExpression, "${" + variableName + "}", numericExpressionContext.get(variableName) + "");
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
	public <T> T evaluate(String expression, Class<T> dataType) throws Exception {
		if (!isSupportedType(dataType)) {
			throw new IllegalStateException("Type '" + dataType.getName() + "' is not supported");
		}

		if (this.scriptEngine == null) {
		    if (isNumeric(dataType)) {
                try {
                    return (T) new Double(expression);
                } catch (NumberFormatException nfe) {
                    throw new Exception("You have defined no variables configuration file and this expression '" + expression + "' is not a valid number");
                }
            } else if (isBoolean(dataType)) {
		        if ("true".equalsIgnoreCase(expression)) {
		            return (T) new Boolean(true);
                } else if ("false".equalsIgnoreCase(expression)) {
                    return (T) new Boolean(false);
                }
            } else {
                throw new Exception("You have defined no variables configuration file and this expression '" + expression + "' is not a valid boolean");
            }
		}

		final String variableName = getVariableName(expression);

		if (isNumeric(dataType) && this.numericExpressionContext.containsKey(variableName)) {
			return (T) this.numericExpressionContext.get(variableName);
		} else if (isBoolean(dataType) && this.booleanExpressionContext.containsKey(variableName)) {
            return (T) this.booleanExpressionContext.get(variableName);
        }

        if (isNumeric(dataType)) {
            return (T) new Double(evalDoubleExpr("", expression));
        } else if (isBoolean(dataType)) {
            return (T) new Boolean("true".equalsIgnoreCase(expression));
		    //return (T) new Boolean("true".equalsIgnoreCase(scriptEngine.eval(expression).toString()));
        }
        throw new IllegalStateException("Type '" + dataType.getName() + "' is not supported");
	}

	private boolean isNumeric(final Class<?> dataType) {
        if (double.class.equals(dataType)) {
            return true;
        }
        if (Double.class.equals(dataType)) {
            return true;
        }
        return false;
    }

    private boolean isBoolean(final Class<?> dataType) {
        if (boolean.class.equals(dataType)) {
            return true;
        }
        if (Boolean.class.equals(dataType)) {
            return true;
        }
        return false;
    }

    private boolean isSupportedType(final Class<?> dataType) {
	    return isNumeric(dataType) || isBoolean(dataType);
	}

	public static void main(String[] args) throws Exception {
		final Variables vars = new Variables() ;
		vars.getVariables().add(new VariableDefinition("fullUpright", "45"));
		vars.getVariables().add(new VariableDefinition("fullHeight", "1050"));
		vars.getVariables().add(new VariableDefinition("fullSeatwidth", "310"));
		vars.getVariables().add(new VariableDefinition("ratio", "1"));
		
		vars.getExpressions().add(new ExpressionDefinition("upright", "${fullUpright}/${ratio}"));
		vars.getExpressions().add(new ExpressionDefinition("height", "${fullHeight}/${ratio}"));
		vars.getExpressions().add(new ExpressionDefinition("seatwidth", "${fullSeatwidth}/${ratio}"));
		
		System.out.println(JAXBUtils.marshal(vars, true));
	}

}
