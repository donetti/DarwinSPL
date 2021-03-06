/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.dataValues.resource.hydatavalue.analysis;

import java.util.Map;
import org.eclipse.emf.ecore.EReference;

public class HyEnumValueEnumReferenceResolver implements eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceResolver<eu.hyvar.dataValues.HyEnumValue, eu.hyvar.dataValues.HyEnum> {
	
	private eu.hyvar.dataValues.resource.hydatavalue.analysis.HydatavalueDefaultResolverDelegate<eu.hyvar.dataValues.HyEnumValue, eu.hyvar.dataValues.HyEnum> delegate = new eu.hyvar.dataValues.resource.hydatavalue.analysis.HydatavalueDefaultResolverDelegate<eu.hyvar.dataValues.HyEnumValue, eu.hyvar.dataValues.HyEnum>();
	
	public void resolve(String identifier, eu.hyvar.dataValues.HyEnumValue container, EReference reference, int position, boolean resolveFuzzy, final eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceResolveResult<eu.hyvar.dataValues.HyEnum> result) {
		delegate.resolve(identifier, container, reference, position, resolveFuzzy, result);
	}
	
	public String deResolve(eu.hyvar.dataValues.HyEnum element, eu.hyvar.dataValues.HyEnumValue container, EReference reference) {
		return delegate.deResolve(element, container, reference);
	}
	
	public void setOptions(Map<?,?> options) {
		// save options in a field or leave method empty if this resolver does not depend
		// on any option
	}
	
}
