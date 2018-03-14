package com.edraw.config;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.VariableType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

@XmlRootElement(name = "variables")
@XmlAccessorType(XmlAccessType.FIELD)
public class Variables {

	@XmlElement(name = "variable")
	private Collection<VariableDefinition> variables = Lists.newArrayList();
	
	@XmlElement(name = "expression")
	private Collection<ExpressionDefinition> expressions = Lists.newArrayList();

	@XmlElement(name = "validation")
	private Collection<ValidationAssertion> validations = Lists.newArrayList();
	
	public Collection<VariableDefinition> getVariables() {
		return variables;
	}

	public Collection<ExpressionDefinition> getExpressions() {
		return expressions;
	}

	public Collection<ValidationAssertion> getValidations() {
		return validations;
	}

	public Variables() {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class VariableDefinition {
		
		@XmlAttribute
		private String name;
		
		@XmlAttribute
		private String value;

		@XmlAttribute(name = "type")
		private VariableType variableType = VariableType.NUMERIC;

		@XmlAttribute(name = "type")
		private boolean print = false;
		
		public VariableDefinition() {
			super();
		}

		public VariableDefinition(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public boolean isPrint() {
			return print;
		}

		public VariableType getVariableType() { return variableType; }

		public double getDouble() {
			if (StringUtils.isEmpty(value)) {
				throw new IllegalStateException("No value defined for variable '" + name + "'");
			}
			if (variableType == null || variableType == VariableType.NUMERIC) {
				try {
					return Double.parseDouble(value);
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException("Variable '" + name + "' with type '" + variableType + "' and value '" + value + "' is not a valid numeric");
				}
			}
			throw new IllegalArgumentException("Variable '" + name + "' with type '" + variableType + "' and value '" + value + "' is not a valid numeric");
		}

		public boolean getBoolean() {
			if (StringUtils.isEmpty(value)) {
				throw new IllegalStateException("No value defined for variable '" + name + "'");
			}
			if (variableType == null || variableType == VariableType.BOOLEAN) {
				if ("true".equalsIgnoreCase(value)) {
					return true;
				} else if ("false".equalsIgnoreCase(value)) {
					return false;
				} else {
					throw new IllegalArgumentException("Variable '" + name + "' with type '" + variableType + "' and value '" + value + "' is not a valid boolean");
				}
			}
			throw new IllegalArgumentException("Variable '" + name + "' with type '" + variableType + "' and value '" + value + "' is not a valid boolean");
		}

	}
	
	public static class ExpressionDefinition {
		
		@XmlAttribute
		private String name;
		
		@XmlAttribute
		private String expression;

		@XmlAttribute(name = "type")
		private VariableType variableType = VariableType.NUMERIC;

		@XmlAttribute
		private boolean print = false;
		
		public ExpressionDefinition() {
			super();
		}

		public ExpressionDefinition(String name, String expression) {
			super();
			this.name = name;
			this.expression = expression;
		}

		public String getName() {
			return name;
		}

		public String getExpression() {
			return expression;
		}

		public boolean isPrint() {
			return print;
		}

		public VariableType getVariableType() { return variableType; }
	}
	
	public static class ValidationAssertion {
		
		@XmlAttribute
		private String name;
		
		@XmlAttribute
		private String expression;
		
		@XmlAttribute(name = "validation")
		private String validationExepression;

		public String getName() {
			return name;
		}

		public String getExpression() {
			return expression;
		}

		public String getValidationExepression() {
			return validationExepression;
		}
		
	}
	
}
