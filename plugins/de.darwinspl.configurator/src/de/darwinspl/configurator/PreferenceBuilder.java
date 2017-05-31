package de.darwinspl.configurator;

import de.darwinspl.configurator.expression.AtomicFeatureExpression;
import eu.hyvar.dataValues.HyDataValuesFactory;
import eu.hyvar.dataValues.HyNumberValue;
import eu.hyvar.evolution.HyName;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureAttribute;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyNumberAttribute;
import eu.hyvar.feature.expression.HyAdditionExpression;
import eu.hyvar.feature.expression.HyAttributeReferenceExpression;
import eu.hyvar.feature.expression.HyExpression;
import eu.hyvar.feature.expression.HyExpressionFactory;
import eu.hyvar.feature.expression.HyIfPossibleExpression;
import eu.hyvar.feature.expression.HyMultiplicationExpression;
import eu.hyvar.feature.expression.HyNestedExpression;
import eu.hyvar.feature.expression.HySubtractionExpression;
import eu.hyvar.feature.expression.HyValueExpression;
import eu.hyvar.preferences.HyPreference;
import eu.hyvar.preferences.PreferencesFactory;

public class PreferenceBuilder {

	private HyFeatureModel featureModel;

	private HyExpressionFactory expressionFactory;
	private HyPreference preference;

	public PreferenceBuilder(HyFeatureModel featureModel) {
		this.featureModel = featureModel;

		this.expressionFactory = HyExpressionFactory.eINSTANCE;
		this.preference = PreferencesFactory.eINSTANCE.createHyPreference();

	}

	private boolean rootExressionExist() {
		return preference.getRootExpression() != null;
	}

	private void addExpression(HyExpression expression) {
		if (rootExressionExist()) {
			HyExpression root = preference.getRootExpression();
			HyAdditionExpression additionExpression = expressionFactory.createHyAdditionExpression();
			additionExpression.setOperand1(root);
			additionExpression.setOperand2(expression);
			preference.setRootExpression(additionExpression);

		} else {
			preference.setRootExpression(expression);
		}
	}

	private HyExpression invertExpression(HyExpression expression) {
		HyNestedExpression nestedExpression = nestExpression(expression);

		HyValueExpression valueExpression = expressionFactory.createHyValueExpression();
		HyDataValuesFactory dataValuesFactory = HyDataValuesFactory.eINSTANCE;
		HyNumberValue value = dataValuesFactory.createHyNumberValue();
		value.setValue(0);
		valueExpression.setValue(value);

		HySubtractionExpression subtractionExpression = expressionFactory.createHySubtractionExpression();
		subtractionExpression.setOperand1(valueExpression);
		subtractionExpression.setOperand2(nestedExpression);

		return nestExpression(subtractionExpression);
	}

	private HyNestedExpression nestExpression(HyExpression expression) {
		if (expression instanceof HyNestedExpression) {
			return (HyNestedExpression) expression;
		} else {
			HyNestedExpression nestedExpression = expressionFactory.createHyNestedExpression();
			nestedExpression.setOperand(expression);
			return nestedExpression;
		}
	}

	private HyExpression createMaxFeaturesExpression() {
		HyIfPossibleExpression ipExpression = expressionFactory.createHyIfPossibleExpression();

		// preferences to select the most features
		for (HyFeature feature : featureModel.getFeatures()) {

			AtomicFeatureExpression referenceExpression = new AtomicFeatureExpression();
			referenceExpression.setFeature(feature);

			ipExpression.getOperands().add(referenceExpression);

		}
		return nestExpression(ipExpression);
	}

	public PreferenceBuilder addMaxFeatures() {
		addExpression(createMaxFeaturesExpression());
		return this;
	}

	public PreferenceBuilder addMinFeatures() {
		addExpression(invertExpression(createMaxFeaturesExpression()));
		return this;
	}

	private HyExpression createMaxAttributeExpression(String attributeName) {
		HyIfPossibleExpression ifPossibleExpression = expressionFactory.createHyIfPossibleExpression();

		for (HyFeature feature : featureModel.getFeatures()) {
			for (HyFeatureAttribute attribute : feature.getAttributes()) {
				for (HyName name : attribute.getNames()) {
					if (attributeName.equals(name.getName())) {
						HyAttributeReferenceExpression attributeReferenceExpression = expressionFactory
								.createHyAttributeReferenceExpression();
						attributeReferenceExpression.setAttribute(attribute);
						attributeReferenceExpression.setFeature(feature);

						int min = ((HyNumberAttribute) attribute).getMin();
						HyDataValuesFactory dataValuesFactory = HyDataValuesFactory.eINSTANCE;
						HyNumberValue value = dataValuesFactory.createHyNumberValue();
						value.setValue(min);
						HyValueExpression valueExpression = expressionFactory.createHyValueExpression();
						valueExpression.setValue(value);

						AtomicFeatureExpression atomicFeatureExpression = new AtomicFeatureExpression();
						atomicFeatureExpression.setFeature(feature);

						HyMultiplicationExpression multiplicationExpression = expressionFactory
								.createHyMultiplicationExpression();
						multiplicationExpression.setOperand1(valueExpression);
						multiplicationExpression.setOperand2(atomicFeatureExpression);

						ifPossibleExpression.getOperands().add(multiplicationExpression);
					}
				}
			}

		}
		return nestExpression(ifPossibleExpression);
	}

	public PreferenceBuilder addMaxAttributeExpression(String attributeName) {
		addExpression(createMaxAttributeExpression(attributeName));
		return this;
	}

	public PreferenceBuilder addMinAttributeExpression(String attributeName) {
		addExpression(invertExpression(createMaxAttributeExpression(attributeName)));
		return this;
	}

	public HyPreference build() {
		return preference;
	}

}
