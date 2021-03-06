package de.darwinspl.feature.graphical.base.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.feature.DwFeatureLayouterManager;
import de.darwinspl.feature.graphical.base.deltaecore.wrapper.layouter.feature.DwFeatureTreeLayouter;
import eu.hyvar.evolution.HyEvolutionFactory;
import eu.hyvar.evolution.HyName;
import eu.hyvar.evolution.util.HyEvolutionUtil;
import eu.hyvar.feature.HyFeature;
import eu.hyvar.feature.HyFeatureChild;
import eu.hyvar.feature.HyFeatureFactory;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyFeatureType;
import eu.hyvar.feature.HyFeatureTypeEnum;
import eu.hyvar.feature.HyGroup;
import eu.hyvar.feature.HyGroupComposition;
import eu.hyvar.feature.HyGroupType;
import eu.hyvar.feature.HyGroupTypeEnum;
import eu.hyvar.feature.HyRootFeature;
import eu.hyvar.feature.analyses.DwFeatureModelAnalyses;
import eu.hyvar.feature.analyses.DwFeatureModelAnalysesMarker;
import eu.hyvar.feature.util.HyFeatureModelWellFormednessException;
import eu.hyvar.feature.util.HyFeatureUtil;

public class DwFeatureModelWrapped implements PropertyChangeListener {
	protected final String PROPERTY_CHILDREN_SIZE = "PropertyChildrenSize";
	protected final String PROPERTY_CONNECTION_SIZE = "PropertyConnectionSize";

	private final String NEW_FEATURE_NAME = "New Feature";

	public static final String PROPERTY_SELECTED_DATE = "PropertySelectedDate";
	public static final String PROPERTY_DATES_COUNT = "PropertyDatesCount";
	public static final String PROPERTY_MARKER_ADDED = "PropertyMarkerAdded";
	private Date selectedDate;

	private List<Date> dates;

	/**
	 * Handles the layout mode. If active, rearrange features will be ignored
	 */
	private boolean autoLayoutActive = true;

	/**
	 * The real model
	 */
	protected HyFeatureModel model;

	/**
	 * List of all features in the diagram. Connections etc. are all handled by the
	 * corresponding HyFeatureModel not by the items in this list
	 */
	protected List<DwFeatureWrapped> features;

	protected List<DwGroupWrapped> groups;

	private Hashtable<EObject, DwFeatureModelAnalysesMarker> markers = new Hashtable<EObject, DwFeatureModelAnalysesMarker>();

	public void addMarker(EObject key, DwFeatureModelAnalysesMarker marker) {
		markers.put(key, marker);
	}

	public boolean hasMarkerForElement(EObject key) {
		return markers.containsKey(key);
	}

	public DwFeatureModelAnalysesMarker getMarkerForElement(EObject key) {
		return markers.get(key);
	}

	/**
	 * Returns all valid groups at a specific date
	 * 
	 * @param date
	 * @return
	 */
	public List<DwGroupWrapped> getGroups(Date date) {
		if (date == null)
			return groups;

		ArrayList<DwGroupWrapped> filteredGroups = new ArrayList<DwGroupWrapped>();
		for (DwGroupWrapped group : groups) {

			Date validSince = group.getWrappedModelElement().getValidSince();

			if (validSince == null || validSince.compareTo(date) <= 0) {
				filteredGroups.add(group);
			}
		}

		return filteredGroups;
	}

	public void setGroups(List<DwGroupWrapped> groups) {
		this.groups = groups;
	}

	public HyFeatureModel getModel() {
		return model;
	}

	public void setModel(HyFeatureModel model) {
		this.model = model;
	}

	public List<DwFeatureWrapped> getFeatures(Date date) {
		if (date == null)
			return features;

		ArrayList<DwFeatureWrapped> filteredFeatures = new ArrayList<DwFeatureWrapped>();
		for (DwFeatureWrapped feature : features) {

			Date validSince = feature.getWrappedModelElement().getValidSince();

			if (validSince == null || validSince.compareTo(date) <= 0) {
				filteredFeatures.add(feature);
			}
		}

		return filteredFeatures;
	}

	public void setFeatures(List<DwFeatureWrapped> features) {
		this.features = features;
	}

	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	public DwFeatureWrapped getParentFeatureForGroup(DwGroupWrapped groupWrapped, Date date) {
		if (date == null)
			date = selectedDate;

		HyFeatureChild featureChild = HyEvolutionUtil
				.getValidTemporalElement(groupWrapped.getWrappedModelElement().getChildOf(), date);

		if (featureChild == null)
			return null;

		return findWrappedFeature(featureChild.getParent());
	}

	/**
	 * Creates a new and valid name for a feature
	 * 
	 * @return
	 */
	public String getValidNewFeatureName() {
		int count = 1;
		for (HyFeature modelFeature : getModel().getFeatures()) {
			for (HyName name : modelFeature.getNames()) {
				String featureName = name.getName();
				if (featureName.contains(NEW_FEATURE_NAME))
					count++;
			}
		}

		return NEW_FEATURE_NAME + " " + count;
	}

	public boolean isAutoLayoutActive() {
		return autoLayoutActive;
	}

	public void setAutoLayoutActive(boolean autoLayoutActive) {
		this.autoLayoutActive = autoLayoutActive;
	}

	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
	}

	public boolean addDate(Date date) {
		if (dates.contains(date))
			return false;

		// int old = dates.size();
		dates.add(dates.size(), date);

		Collections.sort(dates);

		// changes.firePropertyChange(PROPERTY_DATES_COUNT, old, dates.size());

		return true;
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		Date old = this.selectedDate;
		this.selectedDate = selectedDate;

		changes.firePropertyChange(PROPERTY_SELECTED_DATE, old, selectedDate);

		rearrangeFeatures();
	}

	private void createRootFeature() {
		HyRootFeature rootFeature = HyFeatureFactory.eINSTANCE.createHyRootFeature();
		HyFeature feature = HyFeatureFactory.eINSTANCE.createHyFeature();
		HyName name = HyEvolutionFactory.eINSTANCE.createHyName();
		HyFeatureType type = HyFeatureFactory.eINSTANCE.createHyFeatureType();
		type.setType(HyFeatureTypeEnum.MANDATORY);

		name.setName("Root Feature");
		feature.getNames().add(name);
		feature.getTypes().add(type);
		rootFeature.setFeature(feature);

		model.getRootFeature().add(rootFeature);

		model.getFeatures().add(feature);
	}

	public DwFeatureModelWrapped(HyFeatureModel model) {
		this.model = model;

		dates = new ArrayList<Date>();
		dates = HyEvolutionUtil.collectDates(model);

		dates.add(0, new Date(Long.MIN_VALUE));

		features = new ArrayList<DwFeatureWrapped>();
		groups = new ArrayList<DwGroupWrapped>();
		// connections = new ArrayList<DwParentChildConnection>();

		// add a root feature to the model if the model is empty
		if (model.getRootFeature().size() == 0) {
			createRootFeature();
		}

		HyFeature root = model.getRootFeature().get(0).getFeature();
		convertFeatures(root, features);

		checkModelForErrors();
	}

	public void changeConnection(DwParentChildConnection oldConnection, DwParentChildConnection newConnection) {
		HyGroupComposition oldComposition = oldConnection.getComposition();

		HyFeature parentFeature = newConnection.getSource().getWrappedModelElement();
		HyFeature childFeature = newConnection.getTarget().getWrappedModelElement();

		// find all children valid at given timestamp
		List<HyFeatureChild> children = HyEvolutionUtil.getValidTemporalElements(parentFeature.getParentOf(),
				selectedDate);

		for (HyFeatureChild child : children) {
			HyGroupComposition composition = HyEvolutionUtil
					.getValidTemporalElement(child.getChildGroup().getParentOf(), selectedDate);

			// change validity of composition to the selected value
			if (composition.getValidUntil() == null) {

				HyGroupComposition supersedingComposition = EcoreUtil.copy(composition);
				Calendar a = Calendar.getInstance();
				a.setTime(selectedDate);
				oldComposition.setValidUntil(a.getTime());
				child.getChildGroup().getParentOf().add(supersedingComposition);

				// make new composition only valid from current time stamp
				supersedingComposition.setValidSince(selectedDate);
				supersedingComposition.setSupersededElement(composition);

				composition.setSupersedingElement(supersedingComposition);

				supersedingComposition.getFeatures().add(childFeature);
				childFeature.getGroupMembership().add(supersedingComposition);
				// connection.getTarget().setParent(wrappedGroup);
				// wrappedGroup.addChildFeature(connection.getTarget());
			}
		}
	}

	private DwGroupWrapped groupAlreadyInList(HyGroup group) {
		for (DwGroupWrapped wrapped : groups) {
			if (wrapped.getWrappedModelElement().equals(group))
				return wrapped;
		}

		return null;
	}

	public DwGroupWrapped createWrappedFeatureGroup(HyGroup group, DwParentChildConnection connection) {

		DwGroupWrapped wrappedGroup = groupAlreadyInList(group);

		if (wrappedGroup == null) {
			wrappedGroup = new DwGroupWrapped(group);
			// wrappedGroup.setParentFeature(connection.getSource());
			wrappedGroup.addPropertyChangeListener(this);
		}
		// Create the group to enable visual editing and representation
		// HyGroupWrapped wrappedGroup = new HyGroupWrapped(group);

		connection.getTarget().setParent(wrappedGroup);

		return wrappedGroup;
	}

	public static Date getCorrectModelDate(Date date) {
		return date.equals(new Date(Long.MIN_VALUE)) ? null : date;
	}

	private HyGroupComposition createGroupComposition(HyGroup group, Date date) {
		HyGroupComposition composition = HyFeatureFactory.eINSTANCE.createHyGroupComposition();
		composition.setValidSince(date);
		composition.setCompositionOf(group);

		return composition;
	}

	/**
	 * Adds a new connection to the feature model. A connection connects two
	 * features and always have a parent and a child feature. The connection is
	 * added at model level meaning that needed elements like HyFeatureChild are
	 * created.
	 * 
	 * @param connection
	 *            Container that specifies the connection
	 * @param date
	 *            The date since the new connection is valid
	 * @param group
	 *            specifies the group where child features are added. Be aware, that
	 *            the group has to be a child of the source feature of the
	 *            connection. If you don't specify the parameter an and group will
	 *            be searched at the target feature and created if not available at
	 *            the given date.
	 */
	public void addConnection(DwParentChildConnection connection, Date date, HyGroup group) {

		HyFeature parentFeature = connection.getSource().getWrappedModelElement();
		HyFeature childFeature = connection.getTarget().getWrappedModelElement();

		HyGroupComposition childGroupComposition = null;

		DwGroupWrapped wrappedGroup = null;
		if (group != null) {
			childGroupComposition = HyEvolutionUtil.getValidTemporalElement(group.getParentOf(), date);

			// split composition to model evolution
			if (!date.equals(new Date(Long.MIN_VALUE))) {
				if (!date.equals(childGroupComposition.getValidSince())) {
					childGroupComposition = DwGroupWrapped.splitComposition(childGroupComposition, null, date);
				}
			}

			wrappedGroup = findWrappedGroup(group);
		} else {
			// find an and group in order to add new features to that group
			for (HyFeatureChild child : HyEvolutionUtil.getValidTemporalElements(parentFeature.getParentOf(), date)) {
				HyGroup childGroup = child.getChildGroup();
				HyGroupType type = HyEvolutionUtil.getValidTemporalElement(child.getChildGroup().getTypes(), date);

				if (type.getType() == HyGroupTypeEnum.AND) {
					childGroupComposition = HyEvolutionUtil.getValidTemporalElement(child.getChildGroup().getParentOf(),
							date);

					// there exists an and group but it hasn't a composition at the desired date so
					// we have to create one
					if (childGroupComposition == null) {
						childGroupComposition = createGroupComposition(childGroup, date);
					} else {
						// split composition to model evolution
						if (!date.equals(new Date(Long.MIN_VALUE))) {
							if (!date.equals(childGroupComposition.getValidSince())) {
								childGroupComposition = DwGroupWrapped.splitComposition(childGroupComposition, null,
										date);
							}
						}
					}

					group = child.getChildGroup();
				}
			}
		}

		// No and group was found, create a new one instead
		if (childGroupComposition == null && group == null) {
			HyFeatureChild childChild = HyFeatureFactory.eINSTANCE.createHyFeatureChild();

			childGroupComposition = HyFeatureFactory.eINSTANCE.createHyGroupComposition();
			childGroupComposition.setValidSince(getCorrectModelDate(date));
			group = HyFeatureFactory.eINSTANCE.createHyGroup();
			group.setValidSince(getCorrectModelDate(date));

			HyGroupType type = HyFeatureFactory.eINSTANCE.createHyGroupType();
			type.setType(HyGroupTypeEnum.AND);
			type.setValidSince(getCorrectModelDate(date));

			group.getTypes().add(type);

			childChild.setChildGroup(group);
			childChild.setValidSince(getCorrectModelDate(date));

			parentFeature.getParentOf().add(childChild);

			group.getParentOf().add(childGroupComposition);

			wrappedGroup = createWrappedFeatureGroup(group, connection);
			this.groups.add(wrappedGroup);
			this.model.getGroups().add(group);
			// the feature has an and group which will be used to add the feature into the
			// group feature list
		} else {
			wrappedGroup = findWrappedGroup(group);

			connection.getTarget().setParent(wrappedGroup);
		}

		childFeature.getGroupMembership().add(childGroupComposition);
		childGroupComposition.getFeatures().add(childFeature);
		wrappedGroup.addChildFeature(connection.getTarget());

		connection.getSource().addOrUpdateParentToChildConnection(connection);
		connection.getTarget().addOrUpdateChildToParentConnection(connection);
	}

	public void removeConnection(DwParentChildConnection connection, Date date) {
		if (date != null && !date.equals(new Date(Long.MIN_VALUE)) && !date.equals(connection.getValidSince())) {
			connection.setValidUntil(date);
		} else {
			// remove the connection visually
			connection.getSource().removeParentToChildConnection(connection);
			connection.getTarget().removeChildToParentConnection(connection);
		}
	}

	public void rearrangeFeatures() {
		updateLayouter();

		// only rearrange features if layout mode is not set to custom layout
		if (!isAutoLayoutActive())
			return;

		DwFeatureTreeLayouter layouter = DwFeatureLayouterManager.getLayouter(this);

		try {
			for (HyFeature feature : HyEvolutionUtil.getValidTemporalElements(model.getFeatures(), this.selectedDate)) {

				if (HyEvolutionUtil.getValidTemporalElements(feature.getGroupMembership(), selectedDate).size() > 0
						|| HyFeatureUtil.isRootFeature(feature, selectedDate)) {
					DwFeatureWrapped featureWrapped = findWrappedFeature(feature);

					Rectangle rectangle = layouter.getBounds(featureWrapped);

					if (featureWrapped.isVisible())
						featureWrapped.addPosition(rectangle.getTopLeft(), selectedDate, true);

				}
			}
		} catch (HyFeatureModelWellFormednessException e) {
			e.printStackTrace();
		}
	}

	public void addFeature(DwFeatureWrapped feature) {
		features.add(feature);

		// feature.addPropertyChangeListener(this);

		model.getFeatures().add(feature.getWrappedModelElement());
	}

	public void removeFeature(int i) {
		DwFeatureWrapped removedFeature = features.remove(i);

		// delete the feature in the real feature model
		model.getFeatures().remove(removedFeature.getWrappedModelElement());

		int size = features.size();
		changes.firePropertyChange(PROPERTY_CHILDREN_SIZE, size + 1, size);
	}

	/**
	 * Removes a feature which is child of this group. Use this function to delete
	 * the feature temporarily since the selected date.
	 * 
	 * @param childFeature
	 *            the feature to remove
	 * @param date
	 */
	public void removeFeature(DwFeatureWrapped childFeature, Date date) {
		DwGroupWrapped group = childFeature.getParentGroup(date);
		DwFeatureWrapped parentFeature = childFeature.getParentFeature(date);
		HyGroupComposition composition = HyEvolutionUtil
				.getValidTemporalElement(group.getWrappedModelElement().getParentOf(), date);

		if (composition != null) {
			HyGroupType type = HyEvolutionUtil.getValidTemporalElement(group.getWrappedModelElement().getTypes(), date);

			// split composition to allow evolution
			if (!date.equals(new Date(Long.MIN_VALUE)) && !date.equals(composition.getValidSince())) {
				if (composition.getFeatures().size() > 1) {
					composition = DwGroupWrapped.splitComposition(composition, childFeature, date);

					// split group types in case that no and type is selected at the selected date
					if (composition.getFeatures().size() <= 2 && type.getType() != HyGroupTypeEnum.AND) {
						DwGroupWrapped.splitGroupType(HyGroupTypeEnum.AND, group, date);
					}
				} else {
					HyFeatureChild featureChild = HyEvolutionUtil
							.getValidTemporalElement(composition.getCompositionOf().getChildOf(), date);
					featureChild.setValidUntil(date);
					composition.setValidUntil(date);

					for (DwParentChildConnection connection : childFeature.getParentConnections(date)) {
						if (connection.getSource().equals(parentFeature)) {
							connection.setValidUntil(date);
						}
					}
				}
			} else if ((type.getType() != HyGroupTypeEnum.AND) && (composition.getFeatures().size() == 2)) {
				type.setType(HyGroupTypeEnum.AND);
			} 
			
			if (date.equals(new Date(Long.MIN_VALUE)) || (composition.getValidSince() != null && date.equals(composition.getValidSince()))) {
				composition.getFeatures().remove(childFeature.getWrappedModelElement());

				if (composition.getFeatures().isEmpty()) {
					for (HyFeatureChild featureChild : composition.getCompositionOf().getChildOf()) {
						featureChild.getParent().getParentOf().remove(featureChild);
					}

					if (composition.getCompositionOf().getParentOf().size() == 1)
						this.model.getGroups().remove(composition.getCompositionOf());
				}

				for (DwParentChildConnection connection : childFeature.getParentConnections(date)) {
					if (connection.getSource().equals(parentFeature)) {
						childFeature.getParentConnections().remove(connection);
						parentFeature.getChildrenConnections().remove(connection);
					}
				}
			}
		}
	}

	public void removeFeature(DwFeatureWrapped feature) {
		features.remove(feature);

		int index = -1;
		for (int i = 0; i < model.getFeatures().size(); i++) {
			if (model.getFeatures().get(i).getId().equals(feature.getWrappedModelElement().getId())) {
				index = i;
			}
		}

		if (index != -1)
			model.getFeatures().remove(index);

		// Inform editparts about the changes made to the model
		// listeners.firePropertyChange(PROPERTY_CHILD_FEATURES, old, this);

	}

	private void createConnection(DwFeatureWrapped parent, DwFeatureWrapped child, Date validSince, Date validUntil) {
		DwParentChildConnection connection = new DwParentChildConnection();
		connection.setSource(parent);
		connection.setTarget(child);
		connection.setValidSince(validSince);
		connection.setValidUntil(validUntil);
		connection.setModel(this);

		parent.addOrUpdateParentToChildConnection(connection);
		child.addOrUpdateChildToParentConnection(connection);

		DwFeatureLayouterManager.updateLayouter(this);
	}

	public void updateLayouter() {
		DwFeatureLayouterManager.updateLayouter(this);
	}

	private Date getValidUntilDateForConnection(DwFeatureWrapped feature, HyGroupComposition composition) {
		if (!composition.getFeatures().contains(feature.getWrappedModelElement()))
			return composition.getValidSince();

		if (composition.getSupersedingElement() != null) {
			HyGroupComposition supersedingComposition = (HyGroupComposition) composition.getSupersedingElement();
			return getValidUntilDateForConnection(feature, supersedingComposition);
		} else
			return null;
	}

	public void convertFeatures(HyFeature feature, List<DwFeatureWrapped> features) {
		DwFeatureWrapped target = findWrappedFeature(feature);
		if (target == null) {

			// root feature
			try {
				if (HyFeatureUtil.isRootFeature(feature, null)) {
					target = new DwRootFeatureWrapped(feature, this);
				} else {
					target = new DwFeatureWrapped(feature, this);
				}
			} catch (HyFeatureModelWellFormednessException e) {
				e.printStackTrace();
				return;
			}

			addFeature(target);
		}

		boolean hasChildren = feature.getParentOf().size() != 0;

		if (hasChildren) {
			for (HyFeatureChild featureChild : feature.getParentOf()) {
				HyGroup group = featureChild.getChildGroup();

				DwGroupWrapped wrappedGroup = new DwGroupWrapped(group);

				for (HyGroupComposition composition : group.getParentOf()) {
					for (HyFeature childFeature : composition.getFeatures()) {
						DwFeatureWrapped childFeatureWrapped = findWrappedFeature(childFeature);
						if (childFeatureWrapped == null) {
							childFeatureWrapped = new DwFeatureWrapped(childFeature, this);

							addFeature(childFeatureWrapped);
						}

						Date since = null;
						if(composition.getValidSince() != null) {
							since = composition.getValidSince();
						}else {
							since = featureChild.getValidSince();
						}
						
						Date until = null;
						if(composition.getValidUntil() != null) {
							until = composition.getValidUntil();
						}else {
							until = featureChild.getValidUntil();
						}						
						
						// Date connectionValidUntil =
						// getValidUntilDateForConnection(childFeatureWrapped, composition);
						createConnection(target, childFeatureWrapped, since, until);

						childFeatureWrapped.setParent(wrappedGroup);

						wrappedGroup.getFeatures().add(childFeatureWrapped);
						convertFeatures(childFeature, features);
					}
				}

				groups.add(wrappedGroup);
			}
		}
	}

	public DwFeatureWrapped findWrappedFeature(HyFeature feature) {
		if (feature == null)
			return null;

		for (DwFeatureWrapped wrappedFeature : features) {
			if (wrappedFeature.getWrappedModelElement().equals(feature))
				return wrappedFeature;
		}

		return null;
	}

	public DwGroupWrapped findWrappedGroup(HyGroup group) {
		if (group == null)
			return null;

		for (DwGroupWrapped wrappedGroup : groups) {
			if (wrappedGroup.getWrappedModelElement().equals(group))
				return wrappedGroup;
		}
		return null;
	}

	public void addGroup(DwGroupWrapped group) {
		groups.add(group);
		model.getGroups().add(group.getWrappedModelElement());
	}

	public void removeGroup(DwGroupWrapped group) {
		HyGroup hyGroup = group.getWrappedModelElement();
		if (!hyGroup.getChildOf().isEmpty()) {

			for (HyFeatureChild child : hyGroup.getChildOf()) {

				if (child.getParent() != null) {
					child.getParent().getParentOf().remove(child);
				}
			}
		}

		model.getGroups().remove(group.getWrappedModelElement());

		groups.remove(group);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DwGroupWrapped.PROPERTY_CHILD_FEATURES)) {
			DwGroupWrapped newGroup = (DwGroupWrapped) evt.getNewValue();

			if (newGroup.getFeatures().isEmpty()) {
				this.removeGroup(newGroup);
			}
		}
	}

	public void checkModelForErrors() {
		@SuppressWarnings("unchecked")
		Set<EObject> keys = ((Hashtable<EObject, DwFeatureModelAnalysesMarker>) this.markers.clone()).keySet();
		this.markers.clear();
		for (EObject key : keys) {
			key.eNotify(new ENotificationImpl(null, 0, null, true, false));
		}

		List<DwFeatureModelAnalysesMarker> markers = DwFeatureModelAnalyses.checkFeatureModelValidity(model);
		for (DwFeatureModelAnalysesMarker marker : markers) {
			for (EObject affectedElement : marker.getAffectedObjects()) {

				this.markers.put(affectedElement, marker);
				affectedElement.eNotify(new ENotificationImpl(null, 0, null, true, false));
			}
		}
	}
}
