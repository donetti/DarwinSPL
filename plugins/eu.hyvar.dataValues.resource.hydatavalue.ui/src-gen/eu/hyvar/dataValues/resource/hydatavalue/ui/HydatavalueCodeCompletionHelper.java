/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.dataValues.resource.hydatavalue.ui;

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
public class HydatavalueCodeCompletionHelper {
	
	private eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueAttributeValueProvider attributeValueProvider = new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueAttributeValueProvider();
	
	private eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueMetaInformation metaInformation = new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueMetaInformation();
	
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
	public eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal[] computeCompletionProposals(eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource originalResource, String content, int cursorOffset) {
		ResourceSet resourceSet = new ResourceSetImpl();
		// the shadow resource needs the same URI because reference resolvers may use the
		// URI to resolve external references
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource resource = (eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource) resourceSet.createResource(originalResource.getURI());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueMetaInformation metaInformation = resource.getMetaInformation();
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextParser parser = metaInformation.createParser(inputStream, null);
		eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[] expectedElements = parseToExpectedElements(parser, resource, cursorOffset);
		if (expectedElements == null) {
			return new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal[0];
		}
		if (expectedElements.length == 0) {
			return new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal[0];
		}
		List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedAfterCursor = Arrays.asList(getElementsExpectedAt(expectedElements, cursorOffset));
		List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedBeforeCursor = Arrays.asList(getElementsExpectedAt(expectedElements, cursorOffset - 1));
		setPrefixes(expectedAfterCursor, content, cursorOffset);
		setPrefixes(expectedBeforeCursor, content, cursorOffset);
		
		// First, we derive all possible proposals from the set of elements that are
		// expected at the cursor position.
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> allProposals = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> rightProposals = deriveProposals(expectedAfterCursor, content, resource, cursorOffset);
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> leftProposals = deriveProposals(expectedBeforeCursor, content, resource, cursorOffset - 1);
		removeKeywordsEndingBeforeIndex(leftProposals, cursorOffset);
		
		// Second, the set of left proposals (i.e., the ones before the cursor) is checked
		// for emptiness. If the set is empty, the right proposals (i.e., the ones after
		// the cursor) are also considered. If the set is not empty, the right proposal
		// are discarded, because it does not make sense to propose them until the element
		// before the cursor was completed.
		allProposals.addAll(leftProposals);
		// Count the proposals before the cursor that match the prefix
		int leftMatchingProposals = 0;
		for (eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal leftProposal : leftProposals) {
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
		final List<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> sortedProposals = new ArrayList<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>(allProposals);
		Collections.sort(sortedProposals);
		EObject root = null;
		if (!resource.getContents().isEmpty()) {
			root = resource.getContents().get(0);
		}
		for (eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal proposal : sortedProposals) {
			proposal.setRoot(root);
		}
		
		return sortedProposals.toArray(new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal[sortedProposals.size()]);
	}
	
	public eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[] parseToExpectedElements(eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextParser parser, eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource resource, int cursorOffset) {
		final List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements = parser.parseToExpectedElements(null, resource, cursorOffset);
		if (expectedElements == null) {
			return new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[0];
		}
		removeDuplicateEntries(expectedElements);
		removeInvalidEntriesAtEnd(expectedElements);
		return expectedElements.toArray(new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[expectedElements.size()]);
	}
	
	/**
	 * Removes all expected elements that refer to the same terminal and that start at
	 * the same position.
	 */
	protected void removeDuplicateEntries(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements) {
		int size = expectedElements.size();
		// We split the list of expected elements into buckets where each bucket contains
		// the elements that start at the same position.
		Map<Integer, List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal>> map = new LinkedHashMap<Integer, List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal>>();
		for (int i = 0; i < size; i++) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtIndex = expectedElements.get(i);
			int start1 = elementAtIndex.getStartExcludingHiddenTokens();
			List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> list = map.get(start1);
			if (list == null) {
				list = new ArrayList<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal>();
				map.put(start1, list);
			}
			list.add(elementAtIndex);
		}
		
		// Then, we remove all duplicate elements from each bucket individually.
		for (int position : map.keySet()) {
			List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> list = map.get(position);
			removeDuplicateEntriesFromBucket(list);
		}
		
		// After removing all duplicates, we merge the buckets.
		expectedElements.clear();
		for (int position : map.keySet()) {
			List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> list = map.get(position);
			expectedElements.addAll(list);
		}
	}
	
	/**
	 * Removes all expected elements that refer to the same terminal. Attention: This
	 * method assumes that the given list of expected terminals contains only elements
	 * that start at the same position.
	 */
	protected void removeDuplicateEntriesFromBucket(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements) {
		int size = expectedElements.size();
		for (int i = 0; i < size - 1; i++) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtIndex = expectedElements.get(i);
			eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueExpectedElement terminal = elementAtIndex.getTerminal();
			for (int j = i + 1; j < size;) {
				eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtNext = expectedElements.get(j);
				if (terminal.equals(elementAtNext.getTerminal())) {
					expectedElements.remove(j);
					size--;
				} else {
					j++;
				}
			}
		}
	}
	
	protected void removeInvalidEntriesAtEnd(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements) {
		for (int i = 0; i < expectedElements.size() - 1;) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtIndex = expectedElements.get(i);
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtNext = expectedElements.get(i + 1);
			
			// If the two expected elements have a different parent in the syntax definition,
			// we must not discard the second element, because it probably stems from a parent
			// rule.
			eu.hyvar.dataValues.resource.hydatavalue.grammar.HydatavalueSyntaxElement symtaxElementOfThis = elementAtIndex.getTerminal().getSymtaxElement();
			eu.hyvar.dataValues.resource.hydatavalue.grammar.HydatavalueSyntaxElement symtaxElementOfNext = elementAtNext.getTerminal().getSymtaxElement();
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
	protected void removeKeywordsEndingBeforeIndex(Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> proposals, int index) {
		List<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> toRemove = new ArrayList<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
		for (eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal proposal : proposals) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal = proposal.getExpectedTerminal();
			eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueExpectedElement terminal = expectedTerminal.getTerminal();
			if (terminal instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString) {
				eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString csString = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString) terminal;
				int startExcludingHiddenTokens = expectedTerminal.getStartExcludingHiddenTokens();
				if (startExcludingHiddenTokens + csString.getValue().length() - 1 < index) {
					toRemove.add(proposal);
				}
			}
		}
		proposals.removeAll(toRemove);
	}
	
	protected String findPrefix(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedAtCursor, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return "";
		}
		
		int end = 0;
		for (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedElement : expectedElements) {
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
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> deriveProposals(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements, String content, eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource resource, int cursorOffset) {
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
		for (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedElement : expectedElements) {
			resultSet.addAll(deriveProposals(expectedElement, content, resource, cursorOffset));
		}
		return resultSet;
	}
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> deriveProposals(final eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, String content, eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTextResource resource, int cursorOffset) {
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueExpectedElement expectedElement = (eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueExpectedElement) expectedTerminal.getTerminal();
		if (expectedElement instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString csString = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString) expectedElement;
			return handleKeyword(expectedTerminal, csString, expectedTerminal.getPrefix(), expectedTerminal.getContainer());
		} else if (expectedElement instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedBooleanTerminal) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedBooleanTerminal expectedBooleanTerminal = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedBooleanTerminal) expectedElement;
			return handleBooleanTerminal(expectedTerminal, expectedBooleanTerminal, expectedTerminal.getPrefix());
		} else if (expectedElement instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedEnumerationTerminal) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedEnumerationTerminal expectedEnumerationTerminal = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedEnumerationTerminal) expectedElement;
			return handleEnumerationTerminal(expectedTerminal, expectedEnumerationTerminal, expectedTerminal.getPrefix());
		} else if (expectedElement instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedStructuralFeature) {
			final eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedStructuralFeature expectedFeature = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedStructuralFeature) expectedElement;
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
			final Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> proposals = new ArrayList<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
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
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleEnumAttribute(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedStructuralFeature expectedFeature, EEnum enumType, String prefix, EObject container) {
		Collection<EEnumLiteral> enumLiterals = enumType.getELiterals();
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> result = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
		for (EEnumLiteral literal : enumLiterals) {
			String unResolvedLiteral = literal.getLiteral();
			// use token resolver to get de-resolved value of the literal
			eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
			eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(expectedFeature.getTokenName());
			String resolvedLiteral = tokenResolver.deResolve(unResolvedLiteral, expectedFeature.getFeature(), container);
			boolean matchesPrefix = matches(resolvedLiteral, prefix);
			result.add(new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal(expectedTerminal, resolvedLiteral, prefix, matchesPrefix, expectedFeature.getFeature(), container));
		}
		return result;
	}
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleNCReference(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, EObject container, EReference reference, String prefix, String tokenName) {
		// proposals for non-containment references are derived by calling the reference
		// resolver switch in fuzzy mode.
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceResolverSwitch resolverSwitch = metaInformation.getReferenceResolverSwitch();
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
		eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceResolveResult<EObject> result = new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueReferenceResolveResult<EObject>(true);
		resolverSwitch.resolveFuzzy(prefix, container, reference, 0, result);
		Collection<eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceMapping<EObject>> mappings = result.getMappings();
		if (mappings != null) {
			Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
			for (eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueReferenceMapping<EObject> mapping : mappings) {
				Image image = null;
				if (mapping instanceof eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueElementMapping<?>) {
					eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueElementMapping<?> elementMapping = (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueElementMapping<?>) mapping;
					Object target = elementMapping.getTargetElement();
					// de-resolve reference to obtain correct identifier
					eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(tokenName);
					final String identifier = tokenResolver.deResolve(elementMapping.getIdentifier(), reference, container);
					if (target instanceof EObject) {
						image = getImage((EObject) target);
					}
					boolean matchesPrefix = matches(identifier, prefix);
					eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal proposal = new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal(expectedTerminal, identifier, prefix, matchesPrefix, reference, container, image);
					proposal.setReferenceTarget(target);
					resultSet.add(proposal);
				}
			}
			return resultSet;
		}
		return Collections.emptyList();
	}
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleAttribute(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedStructuralFeature expectedFeature, EObject container, EAttribute attribute, String prefix) {
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> resultSet = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>();
		Object[] defaultValues = attributeValueProvider.getDefaultValues(attribute);
		if (defaultValues != null) {
			for (Object defaultValue : defaultValues) {
				if (defaultValue != null) {
					eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
					String tokenName = expectedFeature.getTokenName();
					if (tokenName != null) {
						eu.hyvar.dataValues.resource.hydatavalue.IHydatavalueTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(tokenName);
						if (tokenResolver != null) {
							String defaultValueAsString = tokenResolver.deResolve(defaultValue, attribute, container);
							boolean matchesPrefix = matches(defaultValueAsString, prefix);
							resultSet.add(new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal(expectedTerminal, defaultValueAsString, prefix, matchesPrefix, expectedFeature.getFeature(), container));
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
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleKeyword(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedCsString csString, String prefix, EObject container) {
		String proposal = csString.getValue();
		boolean matchesPrefix = matches(proposal, prefix);
		return Collections.singleton(new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal(expectedTerminal, proposal, prefix, matchesPrefix, null, container));
	}
	
	/**
	 * Creates a set of (two) completion proposals from the given boolean terminal.
	 */
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleBooleanTerminal(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedBooleanTerminal expectedBooleanTerminal, String prefix) {
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> result = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>(2);
		eu.hyvar.dataValues.resource.hydatavalue.grammar.HydatavalueBooleanTerminal booleanTerminal = expectedBooleanTerminal.getBooleanTerminal();
		result.addAll(handleLiteral(expectedTerminal, booleanTerminal.getAttribute(), prefix, booleanTerminal.getTrueLiteral()));
		result.addAll(handleLiteral(expectedTerminal, booleanTerminal.getAttribute(), prefix, booleanTerminal.getFalseLiteral()));
		return result;
	}
	
	/**
	 * Creates a set of completion proposals from the given enumeration terminal. For
	 * each enumeration literal one proposal is created.
	 */
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleEnumerationTerminal(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedEnumerationTerminal expectedEnumerationTerminal, String prefix) {
		Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> result = new LinkedHashSet<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal>(2);
		eu.hyvar.dataValues.resource.hydatavalue.grammar.HydatavalueEnumerationTerminal enumerationTerminal = expectedEnumerationTerminal.getEnumerationTerminal();
		Map<String, String> literalMapping = enumerationTerminal.getLiteralMapping();
		for (String literalName : literalMapping.keySet()) {
			result.addAll(handleLiteral(expectedTerminal, enumerationTerminal.getAttribute(), prefix, literalMapping.get(literalName)));
		}
		return result;
	}
	
	protected Collection<eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal> handleLiteral(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal, EAttribute attribute, String prefix, String literal) {
		if ("".equals(literal)) {
			return Collections.emptySet();
		}
		boolean matchesPrefix = matches(literal, prefix);
		return Collections.singleton(new eu.hyvar.dataValues.resource.hydatavalue.ui.HydatavalueCompletionProposal(expectedTerminal, literal, prefix, matchesPrefix, null, null));
	}
	
	/**
	 * Calculates the prefix for each given expected element. The prefix depends on
	 * the current document content, the cursor position, and the position where the
	 * element is expected.
	 */
	protected void setPrefixes(List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedElements, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return;
		}
		for (eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedElement : expectedElements) {
			String prefix = findPrefix(expectedElements, expectedElement, content, cursorOffset);
			expectedElement.setPrefix(prefix);
		}
	}
	
	public eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[] getElementsExpectedAt(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[] allExpectedElements, int cursorOffset) {
		List<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal> expectedAtCursor = new ArrayList<eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal>();
		for (int i = 0; i < allExpectedElements.length; i++) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedElement = allExpectedElements[i];
			int startIncludingHidden = expectedElement.getStartIncludingHiddenTokens();
			int end = getEnd(allExpectedElements, i);
			if (cursorOffset >= startIncludingHidden && cursorOffset <= end) {
				expectedAtCursor.add(expectedElement);
			}
		}
		return expectedAtCursor.toArray(new eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[expectedAtCursor.size()]);
	}
	
	/**
	 * Calculates the end index of the expected element at allExpectedElements[index].
	 * To determine the end, the subsequent expected elements from the array of all
	 * expected elements are used. An element is considered to end one character
	 * before the next elements starts.
	 */
	protected int getEnd(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal[] allExpectedElements, int indexInList) {
		eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtIndex = allExpectedElements[indexInList];
		int startIncludingHidden = elementAtIndex.getStartIncludingHiddenTokens();
		int startExcludingHidden = elementAtIndex.getStartExcludingHiddenTokens();
		for (int i = indexInList + 1; i < allExpectedElements.length; i++) {
			eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal elementAtI = allExpectedElements[i];
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
		return (proposal.toLowerCase().startsWith(prefix.toLowerCase()) || eu.hyvar.dataValues.resource.hydatavalue.util.HydatavalueStringUtil.matchCamelCase(prefix, proposal) != null) && !proposal.equals(prefix);
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
	
	protected EObject findCorrectContainer(eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueExpectedTerminal expectedTerminal) {
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
		eu.hyvar.dataValues.resource.hydatavalue.grammar.HydatavalueContainmentTrace containmentTrace = expectedTerminal.getContainmentTrace();
		EClass startClass = containmentTrace.getStartClass();
		eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueContainedFeature currentLink = null;
		eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueContainedFeature previousLink = null;
		eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueContainedFeature[] containedFeatures = containmentTrace.getPath();
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
						eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueContainedFeature link = previousLink;
						eu.hyvar.dataValues.resource.hydatavalue.util.HydatavalueEObjectUtil.setFeature(parent, link.getFeature(), previousParent, false);
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
					eu.hyvar.dataValues.resource.hydatavalue.util.HydatavalueEObjectUtil.setFeature(finalHookableParent, finalFeature, finalParent, false);
				}
			});
		}
		return correctContainer;
	}
	
	/**
	 * Walks up the containment hierarchy to find an EObject that is able to hold
	 * (contain) the given object.
	 */
	protected EObject findHookParent(EObject container, EClass startClass, eu.hyvar.dataValues.resource.hydatavalue.mopp.HydatavalueContainedFeature currentLink, EObject object) {
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
