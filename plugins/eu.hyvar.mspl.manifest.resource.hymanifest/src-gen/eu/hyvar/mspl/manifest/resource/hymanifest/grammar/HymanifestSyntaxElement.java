/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.mspl.manifest.resource.hymanifest.grammar;

import org.eclipse.emf.ecore.EClass;

/**
 * The abstract super class for all elements of a grammar. This class provides
 * methods to traverse the grammar rules.
 */
public abstract class HymanifestSyntaxElement {
	
	private HymanifestSyntaxElement[] children;
	private HymanifestSyntaxElement parent;
	private eu.hyvar.mspl.manifest.resource.hymanifest.grammar.HymanifestCardinality cardinality;
	
	public HymanifestSyntaxElement(eu.hyvar.mspl.manifest.resource.hymanifest.grammar.HymanifestCardinality cardinality, HymanifestSyntaxElement[] children) {
		this.cardinality = cardinality;
		this.children = children;
		if (this.children != null) {
			for (HymanifestSyntaxElement child : this.children) {
				child.setParent(this);
			}
		}
	}
	
	/**
	 * Sets the parent of this syntax element. This method must be invoked at most
	 * once.
	 */
	public void setParent(HymanifestSyntaxElement parent) {
		assert this.parent == null;
		this.parent = parent;
	}
	
	/**
	 * Returns the parent of this syntax element. This parent is determined by the
	 * containment hierarchy in the CS model.
	 */
	public HymanifestSyntaxElement getParent() {
		return parent;
	}
	
	public HymanifestSyntaxElement[] getChildren() {
		if (children == null) {
			return new HymanifestSyntaxElement[0];
		}
		return children;
	}
	
	public EClass getMetaclass() {
		return parent.getMetaclass();
	}
	
	public eu.hyvar.mspl.manifest.resource.hymanifest.grammar.HymanifestCardinality getCardinality() {
		return cardinality;
	}
	
}
