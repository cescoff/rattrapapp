package com.edraw.config;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

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
		private double value;

		@XmlAttribute
		private boolean print = false;
		
		public VariableDefinition() {
			super();
		}

		public VariableDefinition(String name, double value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public boolean isPrint() {
			return print;
		}
	
	}
	
	public static class ExpressionDefinition {
		
		@XmlAttribute
		private String name;
		
		@XmlAttribute
		private String expression;

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
