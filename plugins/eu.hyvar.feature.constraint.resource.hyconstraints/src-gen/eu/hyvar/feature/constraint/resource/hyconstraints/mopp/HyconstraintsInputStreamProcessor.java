/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.feature.constraint.resource.hyconstraints.mopp;

import java.io.InputStream;

/**
 * A InputStreamProcessor can be used like a normal InputStream, but provides
 * information about the encoding that is used to represent characters as bytes.
 */
public abstract class HyconstraintsInputStreamProcessor extends InputStream {
	
	/**
	 * Returns the encoding of the characters that can be read from this
	 * InputStreamProcessor. This encoding is passed to subsequent streams (e.g., the
	 * ANTLRInputStream).
	 */
	public abstract String getOutputEncoding();
}
