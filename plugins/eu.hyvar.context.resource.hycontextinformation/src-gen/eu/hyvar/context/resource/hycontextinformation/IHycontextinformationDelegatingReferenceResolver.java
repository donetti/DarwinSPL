/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.context.resource.hycontextinformation;

import org.eclipse.emf.ecore.EObject;

/**
 * <p>
 * A delegating reference resolver is an extension of a normal reference resolver
 * that can be configured with another resolver that it may delegate method calls
 * to. This interface can be implemented by additional resolvers to customize
 * resolving using the load option ADDITIONAL_REFERENCE_RESOLVERS.
 * </p>
 * 
 * @see eu.hyvar.context.resource.hycontextinformation.IHycontextinformationOptions
 */
public interface IHycontextinformationDelegatingReferenceResolver<ContainerType extends EObject, ReferenceType extends EObject> extends eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceResolver<ContainerType, ReferenceType> {
	
	/**
	 * Sets the delegate for this resolver.
	 */
	public void setDelegate(eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceResolver<ContainerType, ReferenceType> delegate);
	
}
