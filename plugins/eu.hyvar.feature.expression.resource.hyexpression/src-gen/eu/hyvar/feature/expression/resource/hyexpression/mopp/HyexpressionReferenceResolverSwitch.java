/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.feature.expression.resource.hyexpression.mopp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class HyexpressionReferenceResolverSwitch implements eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolverSwitch {
	
	/**
	 * This map stores a copy of the options the were set for loading the resource.
	 */
	private Map<Object, Object> options;
	
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAbstractFeatureReferenceExpressionFeatureReferenceResolver hyAbstractFeatureReferenceExpressionFeatureReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAbstractFeatureReferenceExpressionFeatureReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyContextInformationReferenceExpressionContextInformationReferenceResolver hyContextInformationReferenceExpressionContextInformationReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyContextInformationReferenceExpressionContextInformationReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAttributeReferenceExpressionFeatureReferenceResolver hyAttributeReferenceExpressionFeatureReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAttributeReferenceExpressionFeatureReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAttributeReferenceExpressionAttributeReferenceResolver hyAttributeReferenceExpressionAttributeReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyAttributeReferenceExpressionAttributeReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyRelativeVersionRestrictionVersionReferenceResolver hyRelativeVersionRestrictionVersionReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyRelativeVersionRestrictionVersionReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyVersionRangeRestrictionLowerVersionReferenceResolver hyVersionRangeRestrictionLowerVersionReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyVersionRangeRestrictionLowerVersionReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyVersionRangeRestrictionUpperVersionReferenceResolver hyVersionRangeRestrictionUpperVersionReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyVersionRangeRestrictionUpperVersionReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyEnumValueEnumReferenceResolver hyEnumValueEnumReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyEnumValueEnumReferenceResolver();
	protected eu.hyvar.feature.expression.resource.hyexpression.analysis.HyEnumValueEnumLiteralReferenceResolver hyEnumValueEnumLiteralReferenceResolver = new eu.hyvar.feature.expression.resource.hyexpression.analysis.HyEnumValueEnumLiteralReferenceResolver();
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyAbstractFeatureReferenceExpression, eu.hyvar.feature.HyFeature> getHyAbstractFeatureReferenceExpressionFeatureReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAbstractFeatureReferenceExpression_Feature(), hyAbstractFeatureReferenceExpressionFeatureReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyContextInformationReferenceExpression, eu.hyvar.context.HyContextualInformation> getHyContextInformationReferenceExpressionContextInformationReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyContextInformationReferenceExpression_ContextInformation(), hyContextInformationReferenceExpressionContextInformationReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyAttributeReferenceExpression, eu.hyvar.feature.HyFeature> getHyAttributeReferenceExpressionFeatureReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression_Feature(), hyAttributeReferenceExpressionFeatureReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyAttributeReferenceExpression, eu.hyvar.feature.HyFeatureAttribute> getHyAttributeReferenceExpressionAttributeReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression_Attribute(), hyAttributeReferenceExpressionAttributeReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyRelativeVersionRestriction, eu.hyvar.feature.HyVersion> getHyRelativeVersionRestrictionVersionReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyRelativeVersionRestriction_Version(), hyRelativeVersionRestrictionVersionReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyVersionRangeRestriction, eu.hyvar.feature.HyVersion> getHyVersionRangeRestrictionLowerVersionReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction_LowerVersion(), hyVersionRangeRestrictionLowerVersionReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.feature.expression.HyVersionRangeRestriction, eu.hyvar.feature.HyVersion> getHyVersionRangeRestrictionUpperVersionReferenceResolver() {
		return getResolverChain(eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction_UpperVersion(), hyVersionRangeRestrictionUpperVersionReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.dataValues.HyEnumValue, eu.hyvar.dataValues.HyEnum> getHyEnumValueEnumReferenceResolver() {
		return getResolverChain(eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue_Enum(), hyEnumValueEnumReferenceResolver);
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<eu.hyvar.dataValues.HyEnumValue, eu.hyvar.dataValues.HyEnumLiteral> getHyEnumValueEnumLiteralReferenceResolver() {
		return getResolverChain(eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue_EnumLiteral(), hyEnumValueEnumLiteralReferenceResolver);
	}
	
	public void setOptions(Map<?, ?> options) {
		if (options != null) {
			this.options = new LinkedHashMap<Object, Object>();
			this.options.putAll(options);
		}
		hyAbstractFeatureReferenceExpressionFeatureReferenceResolver.setOptions(options);
		hyContextInformationReferenceExpressionContextInformationReferenceResolver.setOptions(options);
		hyAttributeReferenceExpressionFeatureReferenceResolver.setOptions(options);
		hyAttributeReferenceExpressionAttributeReferenceResolver.setOptions(options);
		hyRelativeVersionRestrictionVersionReferenceResolver.setOptions(options);
		hyVersionRangeRestrictionLowerVersionReferenceResolver.setOptions(options);
		hyVersionRangeRestrictionUpperVersionReferenceResolver.setOptions(options);
		hyEnumValueEnumReferenceResolver.setOptions(options);
		hyEnumValueEnumLiteralReferenceResolver.setOptions(options);
	}
	
	public void resolveFuzzy(String identifier, EObject container, EReference reference, int position, eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolveResult<EObject> result) {
		if (container == null) {
			return;
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAbstractFeatureReferenceExpression().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeature> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeature>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("feature")) {
				hyAbstractFeatureReferenceExpressionFeatureReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyAbstractFeatureReferenceExpression) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyContextInformationReferenceExpression().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.context.HyContextualInformation> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.context.HyContextualInformation>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("contextInformation")) {
				hyContextInformationReferenceExpressionContextInformationReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyContextInformationReferenceExpression) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeature> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeature>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("feature")) {
				hyAttributeReferenceExpressionFeatureReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyAttributeReferenceExpression) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeatureAttribute> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyFeatureAttribute>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("attribute")) {
				hyAttributeReferenceExpressionAttributeReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyAttributeReferenceExpression) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyRelativeVersionRestriction().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("version")) {
				hyRelativeVersionRestrictionVersionReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyRelativeVersionRestriction) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("lowerVersion")) {
				hyVersionRangeRestrictionLowerVersionReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyVersionRangeRestriction) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.feature.HyVersion>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("upperVersion")) {
				hyVersionRangeRestrictionUpperVersionReferenceResolver.resolve(identifier, (eu.hyvar.feature.expression.HyVersionRangeRestriction) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.dataValues.HyEnum> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.dataValues.HyEnum>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("enum")) {
				hyEnumValueEnumReferenceResolver.resolve(identifier, (eu.hyvar.dataValues.HyEnumValue) container, (EReference) feature, position, true, frr);
			}
		}
		if (eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue().isInstance(container)) {
			HyexpressionFuzzyResolveResult<eu.hyvar.dataValues.HyEnumLiteral> frr = new HyexpressionFuzzyResolveResult<eu.hyvar.dataValues.HyEnumLiteral>(result);
			String referenceName = reference.getName();
			EStructuralFeature feature = container.eClass().getEStructuralFeature(referenceName);
			if (feature != null && feature instanceof EReference && referenceName != null && referenceName.equals("enumLiteral")) {
				hyEnumValueEnumLiteralReferenceResolver.resolve(identifier, (eu.hyvar.dataValues.HyEnumValue) container, (EReference) feature, position, true, frr);
			}
		}
	}
	
	public eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<? extends EObject, ? extends EObject> getResolver(EStructuralFeature reference) {
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAbstractFeatureReferenceExpression_Feature()) {
			return getResolverChain(reference, hyAbstractFeatureReferenceExpressionFeatureReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyContextInformationReferenceExpression_ContextInformation()) {
			return getResolverChain(reference, hyContextInformationReferenceExpressionContextInformationReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression_Feature()) {
			return getResolverChain(reference, hyAttributeReferenceExpressionFeatureReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyAttributeReferenceExpression_Attribute()) {
			return getResolverChain(reference, hyAttributeReferenceExpressionAttributeReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyRelativeVersionRestriction_Version()) {
			return getResolverChain(reference, hyRelativeVersionRestrictionVersionReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction_LowerVersion()) {
			return getResolverChain(reference, hyVersionRangeRestrictionLowerVersionReferenceResolver);
		}
		if (reference == eu.hyvar.feature.expression.HyExpressionPackage.eINSTANCE.getHyVersionRangeRestriction_UpperVersion()) {
			return getResolverChain(reference, hyVersionRangeRestrictionUpperVersionReferenceResolver);
		}
		if (reference == eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue_Enum()) {
			return getResolverChain(reference, hyEnumValueEnumReferenceResolver);
		}
		if (reference == eu.hyvar.dataValues.HyDataValuesPackage.eINSTANCE.getHyEnumValue_EnumLiteral()) {
			return getResolverChain(reference, hyEnumValueEnumLiteralReferenceResolver);
		}
		return null;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public <ContainerType extends EObject, ReferenceType extends EObject> eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<ContainerType, ReferenceType> getResolverChain(EStructuralFeature reference, eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver<ContainerType, ReferenceType> originalResolver) {
		if (options == null) {
			return originalResolver;
		}
		Object value = options.get(eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionOptions.ADDITIONAL_REFERENCE_RESOLVERS);
		if (value == null) {
			return originalResolver;
		}
		if (!(value instanceof Map)) {
			// send this to the error log
			new eu.hyvar.feature.expression.resource.hyexpression.util.HyexpressionRuntimeUtil().logWarning("Found value with invalid type for option " + eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionOptions.ADDITIONAL_REFERENCE_RESOLVERS + " (expected " + Map.class.getName() + ", but was " + value.getClass().getName() + ")", null);
			return originalResolver;
		}
		Map<?,?> resolverMap = (Map<?,?>) value;
		Object resolverValue = resolverMap.get(reference);
		if (resolverValue == null) {
			return originalResolver;
		}
		if (resolverValue instanceof eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver) {
			eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver replacingResolver = (eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver) resolverValue;
			if (replacingResolver instanceof eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver) {
				// pass original resolver to the replacing one
				((eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver) replacingResolver).setDelegate(originalResolver);
			}
			return replacingResolver;
		} else if (resolverValue instanceof Collection) {
			Collection replacingResolvers = (Collection) resolverValue;
			eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver replacingResolver = originalResolver;
			for (Object next : replacingResolvers) {
				if (next instanceof eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceCache) {
					eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver nextResolver = (eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionReferenceResolver) next;
					if (nextResolver instanceof eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver) {
						// pass original resolver to the replacing one
						((eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver) nextResolver).setDelegate(replacingResolver);
					}
					replacingResolver = nextResolver;
				} else {
					// The collection contains a non-resolver. Send a warning to the error log.
					new eu.hyvar.feature.expression.resource.hyexpression.util.HyexpressionRuntimeUtil().logWarning("Found value with invalid type in value map for option " + eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionOptions.ADDITIONAL_REFERENCE_RESOLVERS + " (expected " + eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver.class.getName() + ", but was " + next.getClass().getName() + ")", null);
				}
			}
			return replacingResolver;
		} else {
			// The value for the option ADDITIONAL_REFERENCE_RESOLVERS has an unknown type.
			new eu.hyvar.feature.expression.resource.hyexpression.util.HyexpressionRuntimeUtil().logWarning("Found value with invalid type in value map for option " + eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionOptions.ADDITIONAL_REFERENCE_RESOLVERS + " (expected " + eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionDelegatingReferenceResolver.class.getName() + ", but was " + resolverValue.getClass().getName() + ")", null);
			return originalResolver;
		}
	}
	
}
