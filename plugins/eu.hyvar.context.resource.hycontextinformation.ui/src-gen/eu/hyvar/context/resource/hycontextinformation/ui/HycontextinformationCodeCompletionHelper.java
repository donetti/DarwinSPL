/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.context.resource.hycontextinformation.ui;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A CodeCompletionHelper can be used to derive completion proposals for partial
 * documents. It runs the parser generated by EMFText in a special mode (i.e., the
 * rememberExpectedElements mode). Based on the elements that are expected by the
 * parser for different regions in the document, valid proposals are computed.
 */
public class HycontextinformationCodeCompletionHelper {
	
	private eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationAttributeValueProvider attributeValueProvider = new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationAttributeValueProvider();
	
	private eu.hyvar.context.resource.hycontextinformation.IHycontextinformationMetaInformation metaInformation = new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationMetaInformation();
	
	/**
	 * <p>
	 * Computes a set of proposals for the given document assuming the cursor is at
	 * 'cursorOffset'. The proposals are derived using the meta information, i.e., the
	 * generated language plug-in.
	 * </p>
	 * 
	 * @param originalResource the resource to compute completions for
	 * @param content the documents content
	 * @param cursorOffset the current offset of the cursor
	 * 
	 * @return an array of completion proposals
	 */
	public eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal[] computeCompletionProposals(eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource originalResource, String content, int cursorOffset) {
		ResourceSet resourceSet = new ResourceSetImpl();
		// the shadow resource needs the same URI because reference resolvers may use the
		// URI to resolve external references
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource resource = (eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource) resourceSet.createResource(originalResource.getURI());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationMetaInformation metaInformation = resource.getMetaInformation();
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextParser parser = metaInformation.createParser(inputStream, null);
		eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[] expectedElements = parseToExpectedElements(parser, resource, cursorOffset);
		if (expectedElements == null) {
			return new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal[0];
		}
		if (expectedElements.length == 0) {
			return new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal[0];
		}
		List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedAfterCursor = Arrays.asList(getElementsExpectedAt(expectedElements, cursorOffset));
		List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedBeforeCursor = Arrays.asList(getElementsExpectedAt(expectedElements, cursorOffset - 1));
		setPrefixes(expectedAfterCursor, content, cursorOffset);
		setPrefixes(expectedBeforeCursor, content, cursorOffset);
		
		// First, we derive all possible proposals from the set of elements that are
		// expected at the cursor position.
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> allProposals = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> rightProposals = deriveProposals(expectedAfterCursor, content, resource, cursorOffset);
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> leftProposals = deriveProposals(expectedBeforeCursor, content, resource, cursorOffset - 1);
		removeKeywordsEndingBeforeIndex(leftProposals, cursorOffset);
		
		// Second, the set of left proposals (i.e., the ones before the cursor) is checked
		// for emptiness. If the set is empty, the right proposals (i.e., the ones after
		// the cursor) are also considered. If the set is not empty, the right proposal
		// are discarded, because it does not make sense to propose them until the element
		// before the cursor was completed.
		allProposals.addAll(leftProposals);
		// Count the proposals before the cursor that match the prefix
		int leftMatchingProposals = 0;
		for (eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal leftProposal : leftProposals) {
			if (leftProposal.isMatchesPrefix()) {
				leftMatchingProposals++;
			}
		}
		if (leftMatchingProposals == 0) {
			allProposals.addAll(rightProposals);
		}
		
		// Third, the proposals are sorted according to their relevance. Proposals that
		// matched the prefix are preferred over ones that did not. Finally, proposals are
		// sorted alphabetically.
		final List<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> sortedProposals = new ArrayList<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>(allProposals);
		Collections.sort(sortedProposals);
		EObject root = null;
		if (!resource.getContents().isEmpty()) {
			root = resource.getContents().get(0);
		}
		for (eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal proposal : sortedProposals) {
			proposal.setRoot(root);
		}
		
		return sortedProposals.toArray(new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal[sortedProposals.size()]);
	}
	
	public eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[] parseToExpectedElements(eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextParser parser, eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource resource, int cursorOffset) {
		final List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements = parser.parseToExpectedElements(null, resource, cursorOffset);
		if (expectedElements == null) {
			return new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[0];
		}
		removeDuplicateEntries(expectedElements);
		removeInvalidEntriesAtEnd(expectedElements);
		return expectedElements.toArray(new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[expectedElements.size()]);
	}
	
	/**
	 * Removes all expected elements that refer to the same terminal and that start at
	 * the same position.
	 */
	protected void removeDuplicateEntries(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements) {
		int size = expectedElements.size();
		// We split the list of expected elements into buckets where each bucket contains
		// the elements that start at the same position.
		Map<Integer, List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal>> map = new LinkedHashMap<Integer, List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal>>();
		for (int i = 0; i < size; i++) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtIndex = expectedElements.get(i);
			int start1 = elementAtIndex.getStartExcludingHiddenTokens();
			List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> list = map.get(start1);
			if (list == null) {
				list = new ArrayList<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal>();
				map.put(start1, list);
			}
			list.add(elementAtIndex);
		}
		
		// Then, we remove all duplicate elements from each bucket individually.
		for (int position : map.keySet()) {
			List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> list = map.get(position);
			removeDuplicateEntriesFromBucket(list);
		}
		
		// After removing all duplicates, we merge the buckets.
		expectedElements.clear();
		for (int position : map.keySet()) {
			List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> list = map.get(position);
			expectedElements.addAll(list);
		}
	}
	
	/**
	 * Removes all expected elements that refer to the same terminal. Attention: This
	 * method assumes that the given list of expected terminals contains only elements
	 * that start at the same position.
	 */
	protected void removeDuplicateEntriesFromBucket(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements) {
		int size = expectedElements.size();
		for (int i = 0; i < size - 1; i++) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtIndex = expectedElements.get(i);
			eu.hyvar.context.resource.hycontextinformation.IHycontextinformationExpectedElement terminal = elementAtIndex.getTerminal();
			for (int j = i + 1; j < size;) {
				eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtNext = expectedElements.get(j);
				if (terminal.equals(elementAtNext.getTerminal())) {
					expectedElements.remove(j);
					size--;
				} else {
					j++;
				}
			}
		}
	}
	
	protected void removeInvalidEntriesAtEnd(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements) {
		for (int i = 0; i < expectedElements.size() - 1;) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtIndex = expectedElements.get(i);
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtNext = expectedElements.get(i + 1);
			
			// If the two expected elements have a different parent in the syntax definition,
			// we must not discard the second element, because it probably stems from a parent
			// rule.
			eu.hyvar.context.resource.hycontextinformation.grammar.HycontextinformationSyntaxElement symtaxElementOfThis = elementAtIndex.getTerminal().getSymtaxElement();
			eu.hyvar.context.resource.hycontextinformation.grammar.HycontextinformationSyntaxElement symtaxElementOfNext = elementAtNext.getTerminal().getSymtaxElement();
			boolean differentParent = symtaxElementOfNext.getParent() != symtaxElementOfThis.getParent();
			
			boolean sameStartExcludingHiddenTokens = elementAtIndex.getStartExcludingHiddenTokens() == elementAtNext.getStartExcludingHiddenTokens();
			boolean differentFollowSet = elementAtIndex.getFollowSetID() != elementAtNext.getFollowSetID();
			if (sameStartExcludingHiddenTokens && differentFollowSet && !differentParent) {
				expectedElements.remove(i + 1);
			} else {
				i++;
			}
		}
	}
	
	/**
	 * Removes all proposals for keywords that end before the given index.
	 */
	protected void removeKeywordsEndingBeforeIndex(Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> proposals, int index) {
		List<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> toRemove = new ArrayList<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
		for (eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal proposal : proposals) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal = proposal.getExpectedTerminal();
			eu.hyvar.context.resource.hycontextinformation.IHycontextinformationExpectedElement terminal = expectedTerminal.getTerminal();
			if (terminal instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString) {
				eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString csString = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString) terminal;
				int startExcludingHiddenTokens = expectedTerminal.getStartExcludingHiddenTokens();
				if (startExcludingHiddenTokens + csString.getValue().length() - 1 < index) {
					toRemove.add(proposal);
				}
			}
		}
		proposals.removeAll(toRemove);
	}
	
	protected String findPrefix(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedAtCursor, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return "";
		}
		
		int end = 0;
		for (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedElement : expectedElements) {
			if (expectedElement == expectedAtCursor) {
				final int start = expectedElement.getStartExcludingHiddenTokens();
				if (start >= 0  && start < Integer.MAX_VALUE) {
					end = start;
				}
				break;
			}
		}
		end = Math.min(end, cursorOffset);
		final String prefix = content.substring(end, Math.min(content.length(), cursorOffset));
		return prefix;
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> deriveProposals(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements, String content, eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource resource, int cursorOffset) {
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
		for (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedElement : expectedElements) {
			resultSet.addAll(deriveProposals(expectedElement, content, resource, cursorOffset));
		}
		return resultSet;
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> deriveProposals(final eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, String content, eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTextResource resource, int cursorOffset) {
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationExpectedElement expectedElement = (eu.hyvar.context.resource.hycontextinformation.IHycontextinformationExpectedElement) expectedTerminal.getTerminal();
		if (expectedElement instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString csString = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString) expectedElement;
			return handleKeyword(expectedTerminal, csString, expectedTerminal.getPrefix(), expectedTerminal.getContainer());
		} else if (expectedElement instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedBooleanTerminal) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedBooleanTerminal expectedBooleanTerminal = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedBooleanTerminal) expectedElement;
			return handleBooleanTerminal(expectedTerminal, expectedBooleanTerminal, expectedTerminal.getPrefix());
		} else if (expectedElement instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedEnumerationTerminal) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedEnumerationTerminal expectedEnumerationTerminal = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedEnumerationTerminal) expectedElement;
			return handleEnumerationTerminal(expectedTerminal, expectedEnumerationTerminal, expectedTerminal.getPrefix());
		} else if (expectedElement instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedStructuralFeature) {
			final eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedStructuralFeature expectedFeature = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedStructuralFeature) expectedElement;
			final EStructuralFeature feature = expectedFeature.getFeature();
			final EClassifier featureType = feature.getEType();
			final EObject container = findCorrectContainer(expectedTerminal);
			
			// Here it gets really crazy. We need to modify the model in a way that reflects
			// the state the model would be in, if the expected terminal were present. After
			// computing the corresponding completion proposals, the original state of the
			// model is restored. This procedure is required, because different models can be
			// required for different completion situations. This can be particularly observed
			// when the user has not yet typed a character that starts an element to be
			// completed.
			final Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> proposals = new ArrayList<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
			expectedTerminal.materialize(new Runnable() {
				
				public void run() {
					if (feature instanceof EReference) {
						EReference reference = (EReference) feature;
						if (featureType instanceof EClass) {
							if (reference.isContainment()) {
								// the FOLLOW set should contain only non-containment references
								assert false;
							} else {
								proposals.addAll(handleNCReference(expectedTerminal, container, reference, expectedTerminal.getPrefix(), expectedFeature.getTokenName()));
							}
						}
					} else if (feature instanceof EAttribute) {
						EAttribute attribute = (EAttribute) feature;
						if (featureType instanceof EEnum) {
							EEnum enumType = (EEnum) featureType;
							proposals.addAll(handleEnumAttribute(expectedTerminal, expectedFeature, enumType, expectedTerminal.getPrefix(), container));
						} else {
							// handle EAttributes (derive default value depending on the type of the
							// attribute, figure out token resolver, and call deResolve())
							proposals.addAll(handleAttribute(expectedTerminal, expectedFeature, container, attribute, expectedTerminal.getPrefix()));
						}
					} else {
						// there should be no other subclass of EStructuralFeature
						assert false;
					}
				}
			});
			// Return the proposals that were computed in the closure call.
			return proposals;
		} else {
			// there should be no other class implementing IExpectedElement
			assert false;
		}
		return Collections.emptyList();
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleEnumAttribute(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedStructuralFeature expectedFeature, EEnum enumType, String prefix, EObject container) {
		Collection<EEnumLiteral> enumLiterals = enumType.getELiterals();
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> result = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
		for (EEnumLiteral literal : enumLiterals) {
			String unResolvedLiteral = literal.getLiteral();
			// use token resolver to get de-resolved value of the literal
			eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
			eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(expectedFeature.getTokenName());
			String resolvedLiteral = tokenResolver.deResolve(unResolvedLiteral, expectedFeature.getFeature(), container);
			boolean matchesPrefix = matches(resolvedLiteral, prefix);
			result.add(new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal(expectedTerminal, resolvedLiteral, prefix, matchesPrefix, expectedFeature.getFeature(), container));
		}
		return result;
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleNCReference(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, EObject container, EReference reference, String prefix, String tokenName) {
		// proposals for non-containment references are derived by calling the reference
		// resolver switch in fuzzy mode.
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceResolverSwitch resolverSwitch = metaInformation.getReferenceResolverSwitch();
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
		eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceResolveResult<EObject> result = new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationReferenceResolveResult<EObject>(true);
		resolverSwitch.resolveFuzzy(prefix, container, reference, 0, result);
		Collection<eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceMapping<EObject>> mappings = result.getMappings();
		if (mappings != null) {
			Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
			for (eu.hyvar.context.resource.hycontextinformation.IHycontextinformationReferenceMapping<EObject> mapping : mappings) {
				Image image = null;
				if (mapping instanceof eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationElementMapping<?>) {
					eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationElementMapping<?> elementMapping = (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationElementMapping<?>) mapping;
					Object target = elementMapping.getTargetElement();
					// de-resolve reference to obtain correct identifier
					eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(tokenName);
					final String identifier = tokenResolver.deResolve(elementMapping.getIdentifier(), reference, container);
					if (target instanceof EObject) {
						image = getImage((EObject) target);
					}
					boolean matchesPrefix = matches(identifier, prefix);
					eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal proposal = new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal(expectedTerminal, identifier, prefix, matchesPrefix, reference, container, image);
					proposal.setReferenceTarget(target);
					resultSet.add(proposal);
				}
			}
			return resultSet;
		}
		return Collections.emptyList();
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleAttribute(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedStructuralFeature expectedFeature, EObject container, EAttribute attribute, String prefix) {
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>();
		Object[] defaultValues = attributeValueProvider.getDefaultValues(attribute);
		if (defaultValues != null) {
			for (Object defaultValue : defaultValues) {
				if (defaultValue != null) {
					eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
					String tokenName = expectedFeature.getTokenName();
					if (tokenName != null) {
						eu.hyvar.context.resource.hycontextinformation.IHycontextinformationTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(tokenName);
						if (tokenResolver != null) {
							String defaultValueAsString = tokenResolver.deResolve(defaultValue, attribute, container);
							boolean matchesPrefix = matches(defaultValueAsString, prefix);
							resultSet.add(new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal(expectedTerminal, defaultValueAsString, prefix, matchesPrefix, expectedFeature.getFeature(), container));
						}
					}
				}
			}
		}
		return resultSet;
	}
	
	/**
	 * Creates a set of completion proposals from the given keyword.
	 */
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleKeyword(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedCsString csString, String prefix, EObject container) {
		String proposal = csString.getValue();
		boolean matchesPrefix = matches(proposal, prefix);
		return Collections.singleton(new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal(expectedTerminal, proposal, prefix, matchesPrefix, null, container));
	}
	
	/**
	 * Creates a set of (two) completion proposals from the given boolean terminal.
	 */
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleBooleanTerminal(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedBooleanTerminal expectedBooleanTerminal, String prefix) {
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> result = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>(2);
		eu.hyvar.context.resource.hycontextinformation.grammar.HycontextinformationBooleanTerminal booleanTerminal = expectedBooleanTerminal.getBooleanTerminal();
		result.addAll(handleLiteral(expectedTerminal, booleanTerminal.getAttribute(), prefix, booleanTerminal.getTrueLiteral()));
		result.addAll(handleLiteral(expectedTerminal, booleanTerminal.getAttribute(), prefix, booleanTerminal.getFalseLiteral()));
		return result;
	}
	
	/**
	 * Creates a set of completion proposals from the given enumeration terminal. For
	 * each enumeration literal one proposal is created.
	 */
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleEnumerationTerminal(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedEnumerationTerminal expectedEnumerationTerminal, String prefix) {
		Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> result = new LinkedHashSet<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal>(2);
		eu.hyvar.context.resource.hycontextinformation.grammar.HycontextinformationEnumerationTerminal enumerationTerminal = expectedEnumerationTerminal.getEnumerationTerminal();
		Map<String, String> literalMapping = enumerationTerminal.getLiteralMapping();
		for (String literalName : literalMapping.keySet()) {
			result.addAll(handleLiteral(expectedTerminal, enumerationTerminal.getAttribute(), prefix, literalMapping.get(literalName)));
		}
		return result;
	}
	
	protected Collection<eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal> handleLiteral(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal, EAttribute attribute, String prefix, String literal) {
		if ("".equals(literal)) {
			return Collections.emptySet();
		}
		boolean matchesPrefix = matches(literal, prefix);
		return Collections.singleton(new eu.hyvar.context.resource.hycontextinformation.ui.HycontextinformationCompletionProposal(expectedTerminal, literal, prefix, matchesPrefix, null, null));
	}
	
	/**
	 * Calculates the prefix for each given expected element. The prefix depends on
	 * the current document content, the cursor position, and the position where the
	 * element is expected.
	 */
	protected void setPrefixes(List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedElements, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return;
		}
		for (eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedElement : expectedElements) {
			String prefix = findPrefix(expectedElements, expectedElement, content, cursorOffset);
			expectedElement.setPrefix(prefix);
		}
	}
	
	public eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[] getElementsExpectedAt(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[] allExpectedElements, int cursorOffset) {
		List<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal> expectedAtCursor = new ArrayList<eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal>();
		for (int i = 0; i < allExpectedElements.length; i++) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedElement = allExpectedElements[i];
			int startIncludingHidden = expectedElement.getStartIncludingHiddenTokens();
			int end = getEnd(allExpectedElements, i);
			if (cursorOffset >= startIncludingHidden && cursorOffset <= end) {
				expectedAtCursor.add(expectedElement);
			}
		}
		return expectedAtCursor.toArray(new eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[expectedAtCursor.size()]);
	}
	
	/**
	 * Calculates the end index of the expected element at allExpectedElements[index].
	 * To determine the end, the subsequent expected elements from the array of all
	 * expected elements are used. An element is considered to end one character
	 * before the next elements starts.
	 */
	protected int getEnd(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal[] allExpectedElements, int indexInList) {
		eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtIndex = allExpectedElements[indexInList];
		int startIncludingHidden = elementAtIndex.getStartIncludingHiddenTokens();
		int startExcludingHidden = elementAtIndex.getStartExcludingHiddenTokens();
		for (int i = indexInList + 1; i < allExpectedElements.length; i++) {
			eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal elementAtI = allExpectedElements[i];
			int startIncludingHiddenForI = elementAtI.getStartIncludingHiddenTokens();
			int startExcludingHiddenForI = elementAtI.getStartExcludingHiddenTokens();
			if (startIncludingHidden != startIncludingHiddenForI || startExcludingHidden != startExcludingHiddenForI) {
				return startIncludingHiddenForI - 1;
			}
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Checks whether the given proposed string matches the prefix. The two strings
	 * are compared ignoring the case. The prefix is also considered to match if is a
	 * camel case representation of the proposal.
	 */
	protected boolean matches(String proposal, String prefix) {
		if (proposal == null || prefix == null) {
			return false;
		}
		return (proposal.toLowerCase().startsWith(prefix.toLowerCase()) || eu.hyvar.context.resource.hycontextinformation.util.HycontextinformationStringUtil.matchCamelCase(prefix, proposal) != null) && !proposal.equals(prefix);
	}
	
	protected Image getImage(EObject element) {
		if (!Platform.isRunning()) {
			return null;
		}
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		AdapterFactoryLabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
		return labelProvider.getImage(element);
	}
	
	protected EObject findCorrectContainer(eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationExpectedTerminal expectedTerminal) {
		EObject container = expectedTerminal.getContainer();
		EClass ruleMetaclass = expectedTerminal.getTerminal().getRuleMetaclass();
		if (ruleMetaclass.isInstance(container)) {
			// container is correct for expected terminal
			return container;
		}
		// the container is wrong
		EObject parent = null;
		EObject previousParent = null;
		EObject correctContainer = null;
		EObject hookableParent = null;
		eu.hyvar.context.resource.hycontextinformation.grammar.HycontextinformationContainmentTrace containmentTrace = expectedTerminal.getContainmentTrace();
		EClass startClass = containmentTrace.getStartClass();
		eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationContainedFeature currentLink = null;
		eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationContainedFeature previousLink = null;
		eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationContainedFeature[] containedFeatures = containmentTrace.getPath();
		for (int i = 0; i < containedFeatures.length; i++) {
			currentLink = containedFeatures[i];
			if (i > 0) {
				previousLink = containedFeatures[i - 1];
			}
			EClass containerClass = currentLink.getContainerClass();
			hookableParent = findHookParent(container, startClass, currentLink, parent);
			if (hookableParent != null) {
				// we found the correct parent
				break;
			} else {
				previousParent = parent;
				parent = containerClass.getEPackage().getEFactoryInstance().create(containerClass);
				if (parent != null) {
					if (previousParent == null) {
						// replace container for expectedTerminal with correctContainer
						correctContainer = parent;
					} else {
						// This assignment is only performed to get rid of a warning about a potential
						// null pointer access. Variable 'previousLink' cannot be null here, because it is
						// initialized for all loop iterations where 'i' is greather than 0 and for the
						// case where 'i' equals zero, this path is never executed, because
						// 'previousParent' is null in this case.
						eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationContainedFeature link = previousLink;
						eu.hyvar.context.resource.hycontextinformation.util.HycontextinformationEObjectUtil.setFeature(parent, link.getFeature(), previousParent, false);
					}
				}
			}
		}
		
		if (correctContainer == null) {
			correctContainer = container;
		}
		
		if (currentLink == null) {
			return correctContainer;
		}
		
		hookableParent = findHookParent(container, startClass, currentLink, parent);
		
		final EObject finalHookableParent = hookableParent;
		final EStructuralFeature finalFeature = currentLink.getFeature();
		final EObject finalParent = parent;
		if (parent != null && hookableParent != null) {
			expectedTerminal.setAttachmentCode(new Runnable() {
				
				public void run() {
					eu.hyvar.context.resource.hycontextinformation.util.HycontextinformationEObjectUtil.setFeature(finalHookableParent, finalFeature, finalParent, false);
				}
			});
		}
		return correctContainer;
	}
	
	/**
	 * Walks up the containment hierarchy to find an EObject that is able to hold
	 * (contain) the given object.
	 */
	protected EObject findHookParent(EObject container, EClass startClass, eu.hyvar.context.resource.hycontextinformation.mopp.HycontextinformationContainedFeature currentLink, EObject object) {
		EClass containerClass = currentLink.getContainerClass();
		while (container != null) {
			if (containerClass.isInstance(object)) {
				if (startClass.equals(container.eClass())) {
					return container;
				}
			}
			container = container.eContainer();
		}
		return null;
	}
	
}