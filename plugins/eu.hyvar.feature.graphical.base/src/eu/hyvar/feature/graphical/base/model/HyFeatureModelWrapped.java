package eu.hyvar.feature.graphical.base.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.util.EcoreUtil;

import eu.hyvar.evolution.HyEvolutionFactory;
import eu.hyvar.evolution.HyEvolutionUtil;
import eu.hyvar.evolution.HyName;
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
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.feature.HyFeatureLayouterManager;
import eu.hyvar.feature.graphical.base.deltaecore.wrapper.layouter.feature.HyFeatureTreeLayouter;

public class HyFeatureModelWrapped implements PropertyChangeListener {
	protected final String PROPERTY_CHILDREN_SIZE = "PropertyChildrenSize";
	protected final String PROPERTY_CONNECTION_SIZE = "PropertyConnectionSize";

	private final String NEW_FEATURE_NAME = "New Feature";
	
	public static final String PROPERTY_SELECTED_DATE = "PropertySelectedDate";
	public static final String PROPERTY_DATES_COUNT = "PropertyDatesCount";
	private Date selectedDate;
	
	private List<Date> dates;
	
	List<HyParentChildConnection> connections;
	
	/**
	 * Handles the layout mode. If active, rearrange features will be ignored
	 */
	private boolean autoLayoutActive = true;
	
	/*
	 * The real model
	 */
	protected HyFeatureModel model;

	/*
	 * List of all features in the diagram. Connections etc. are all handled by the corresponding 
	 * HyFeatureModel not by the items in this list
	 */
	protected List<HyFeatureWrapped> features;

	protected HashSet<HyGroupWrapped> groups;	

	public HashSet<HyGroupWrapped> getGroups() {
		return groups;
	}
	public void setGroups(HashSet<HyGroupWrapped> groups) {
		this.groups = groups;
	}

	public HyFeatureModel getModel() {
		return model;
	}

	public void setModel(HyFeatureModel model) {
		this.model = model;
	}

	public List<HyFeatureWrapped> getFeatures(Date date) {
		if(date == null)
			return features;

		ArrayList<HyFeatureWrapped> filteredFeatures = new ArrayList<HyFeatureWrapped>();
		for(HyFeatureWrapped feature : features){


			Date validSince = feature.getWrappedModelElement().getValidSince();

			if(validSince == null || validSince.compareTo(date) <= 0){
				filteredFeatures.add(feature);
			}
		}

		return filteredFeatures;
	}

	public void setFeatures(List<HyFeatureWrapped> features) {
		this.features = features;
	}

	protected PropertyChangeSupport changes = new PropertyChangeSupport( this );

	public void addPropertyChangeListener(PropertyChangeListener listener){
		changes.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener){
		changes.removePropertyChangeListener(listener);
	}

	/**
	 * Creates a new and valid name for a feature
	 * @return
	 */
	public String getValidNewFeatureName(){
		int count = 1;
		for(HyFeature modelFeature : getModel().getFeatures()){
			String featureName = modelFeature.getNames().get(0).getName();
			if(featureName.contains(NEW_FEATURE_NAME))
				count++;
		}

		return NEW_FEATURE_NAME+" "+count;
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

	public void addDate(Date date){
		if(dates.contains(date)) return;
		
		int old = dates.size();
		dates.add(dates.size(), date);
		
		Collections.sort(dates);
		
		
		changes.firePropertyChange(PROPERTY_DATES_COUNT, old, dates.size());
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

	public HyFeatureModelWrapped(HyFeatureModel model) {
		this.model = model;
		
		dates = new ArrayList<Date>();
		dates = HyEvolutionUtil.collectDates(model);
				
		dates.add(0, new Date(Long.MIN_VALUE));
		dates.add(dates.size(), new Date(Long.MAX_VALUE));

		features = new ArrayList<HyFeatureWrapped>();
		groups = new HashSet<HyGroupWrapped>();
		connections = new ArrayList<HyParentChildConnection>();

		// add a root feature to the model if the model is empty
		if(model.getRootFeature().size() == 0){
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

		HyFeature root = model.getRootFeature().get(0).getFeature();
		convertFeatures(root, features);
	}

	
	public void changeConnection(HyParentChildConnection oldConnection, HyParentChildConnection newConnection){
		HyGroupComposition oldComposition = oldConnection.getComposition();	
		
		HyFeature parentFeature = newConnection.getSource().getWrappedModelElement();
		HyFeature childFeature = newConnection.getTarget().getWrappedModelElement();

		// find all children valid at given timestamp
		List<HyFeatureChild> children = HyEvolutionUtil.getValidTemporalElements(parentFeature.getParentOf(), selectedDate);
		
		for(HyFeatureChild child : children){
			HyGroupComposition composition = HyEvolutionUtil.getValidTemporalElement(child.getChildGroup().getParentOf(), selectedDate);
			
			// change validity of composition to the selected value
			if(composition.getValidUntil() == null){
							
				
				HyGroupComposition supersedingComposition = EcoreUtil.copy(composition);
				Calendar a = Calendar.getInstance();
				a.setTime(selectedDate);
				a.add(Calendar.SECOND, -1);
				oldComposition.setValidUntil(a.getTime());
				child.getChildGroup().getParentOf().add(supersedingComposition);
				
				// make new composition only valid from current time stamp
				supersedingComposition.setValidSince(selectedDate);
				supersedingComposition.setSupersededElement(composition);
				
				composition.setSupersedingElement(supersedingComposition);
				
				
				supersedingComposition.getFeatures().add(childFeature);
				childFeature.getGroupMembership().add(supersedingComposition);
				//connection.getTarget().setParent(wrappedGroup);	
				//wrappedGroup.addChildFeature(connection.getTarget());
			}
		}
	}

	/*
	
	public void addConnection(HyParentChildConnection connection){

		HyFeature parentFeature = connection.getSource().getWrappedModelElement();
		HyFeature childFeature = connection.getTarget().getWrappedModelElement();

		HyGroup childGroup = null; 
		HyGroupComposition childGroupComposition = null;
		HyGroupWrapped wrappedGroup = null;
		
		// find an and group in order to add new features to that group
		for(HyFeatureChild child : HyEvolutionUtil.getValidTemporalElements(parentFeature.getParentOf(), selectedDate)){
			HyGroupType type = HyEvolutionUtil.getValidTemporalElement(child.getChildGroup().getTypes(), selectedDate);

			if(type.getType() == HyGroupTypeEnum.AND){
				List<HyGroupComposition> compositions = HyEvolutionUtil.getValidTemporalElements(child.getChildGroup().getParentOf(), selectedDate);
				if(compositions.size() > 0){
					childGroup = child.getChildGroup();
					childGroupComposition = compositions.get(0);
				}
			}
		}

		// No and group was found, create a new one instead
		if(childGroupComposition == null && childGroup == null){
			HyFeatureChild childChild = HyFeatureFactory.eINSTANCE.createHyFeatureChild();

			childGroupComposition = HyFeatureFactory.eINSTANCE.createHyGroupComposition();
			childGroup = HyFeatureFactory.eINSTANCE.createHyGroup();

			HyGroupType type = HyFeatureFactory.eINSTANCE.createHyGroupType();
			type.setType(HyGroupTypeEnum.AND);

			childGroup.getTypes().add(type);

			childChild.setChildGroup(childGroup);


			parentFeature.getParentOf().add(childChild);			


			childGroup.getParentOf().add(childGroupComposition);

			wrappedGroup = createWrappedFeatureGroup(childGroup, connection);
			this.groups.add(wrappedGroup);
			this.model.getGroups().add(childGroup);	
			// the feature has an and group which will be used to add the feature into the group feature list
		}else{	
			wrappedGroup = findWrappedGroup(childGroup);

			connection.getTarget().setParent(wrappedGroup);				
		}

		childFeature.getGroupMembership().add(childGroupComposition);
		childGroupComposition.getFeatures().add(childFeature);
		wrappedGroup.addChildFeature(connection.getTarget());

	}	
	*/

	public HyGroupWrapped createWrappedFeatureGroup(HyGroup group, HyParentChildConnection connection){

		HyGroupWrapped wrappedGroup = groupAlreadyInList(group);

		if(wrappedGroup == null){
			wrappedGroup = new HyGroupWrapped(group);
			wrappedGroup.setParentFeature(connection.getSource());
			wrappedGroup.addPropertyChangeListener(this);
		}
		// Create the group to enable visual editing and representation
		//HyGroupWrapped wrappedGroup = new HyGroupWrapped(group);

		connection.getTarget().setParent(wrappedGroup);	

		return wrappedGroup;
	}

	public HyGroupWrapped findAndGroupAtFeature(HyFeatureWrapped wrappedFeature){
		for(HyParentChildConnection connection : wrappedFeature.getParentConnections(selectedDate)){
			HyGroupWrapped wrappedGroup = connection.getTarget().getParentGroup(selectedDate);

			if(wrappedGroup.isAnd(selectedDate))
				return wrappedGroup;
		}

		return null;
	}

	public void addConnection(HyParentChildConnection connection, Date date){
		HyFeature parentFeature = connection.getSource().getWrappedModelElement();
		HyFeature childFeature = connection.getTarget().getWrappedModelElement();

		HyGroup childGroup = null; 
		HyGroupComposition childGroupComposition = null;
		HyGroupWrapped wrappedGroup = null;
		
		// find an and group in order to add new features to that group
		for(HyFeatureChild child : HyEvolutionUtil.getValidTemporalElements(parentFeature.getParentOf(), selectedDate)){
			HyGroupType type = HyEvolutionUtil.getValidTemporalElement(child.getChildGroup().getTypes(), selectedDate);

			
			if(type.getType() == HyGroupTypeEnum.AND){
				List<HyGroupComposition> compositions = HyEvolutionUtil.getValidTemporalElements(child.getChildGroup().getParentOf(), selectedDate);

				if(compositions.size() > 0){
					childGroup = child.getChildGroup();
					childGroupComposition = compositions.get(0);
				}
			}
		}

		// No and group was found, create a new one instead
		if(childGroupComposition == null && childGroup == null){
			HyFeatureChild childChild = HyFeatureFactory.eINSTANCE.createHyFeatureChild();

			childGroupComposition = HyFeatureFactory.eINSTANCE.createHyGroupComposition();
			childGroup = HyFeatureFactory.eINSTANCE.createHyGroup();

			HyGroupType type = HyFeatureFactory.eINSTANCE.createHyGroupType();
			type.setType(HyGroupTypeEnum.AND);

			childGroup.getTypes().add(type);

			childChild.setChildGroup(childGroup);


			parentFeature.getParentOf().add(childChild);			


			childGroup.getParentOf().add(childGroupComposition);

			wrappedGroup = createWrappedFeatureGroup(childGroup, connection);
			this.groups.add(wrappedGroup);
			this.model.getGroups().add(childGroup);	
			// the feature has an and group which will be used to add the feature into the group feature list
		}else{	
			wrappedGroup = findWrappedGroup(childGroup);

			connection.getTarget().setParent(wrappedGroup);				
		}

		childFeature.getGroupMembership().add(childGroupComposition);
		childGroupComposition.getFeatures().add(childFeature);
		wrappedGroup.addChildFeature(connection.getTarget());

			
		
		/*
		HyFeature parentFeature = connection.getSource().getWrappedModelElement();
		HyFeature childFeature = connection.getTarget().getWrappedModelElement();

		HyFeatureChild featureChild = HyEvolutionUtil.getValidTemporalElement(parentFeature.getParentOf(), date);

		// new parent node has no children at selected date, create a new one
		if(featureChild == null){
			featureChild = HyFeatureFactory.eINSTANCE.createHyFeatureChild();
			featureChild.setValidSince(date);

			// with this values we create an and group because there is only one feature contained in it at this date
			HyGroupType type = HyFeatureFactory.eINSTANCE.createHyGroupType();
			type.setType(HyGroupTypeEnum.AND);

			HyGroup group = HyFeatureFactory.eINSTANCE.createHyGroup();
			group.getTypes().add(type);		

			featureChild.setChildGroup(group);

			HyGroupComposition composition = HyFeatureFactory.eINSTANCE.createHyGroupComposition();
			group.getParentOf().add(composition);
			
			System.out.println("No");
		}


		HyGroup group = featureChild.getChildGroup();
		HyGroupComposition composition = HyEvolutionUtil.getValidTemporalElement(group.getParentOf(), date);

		// group has no valid composition at selected date, create one
		if(composition == null){
			System.out.println("No Composition");
		}else{
			// valid group at selected date is not an and group, create a new one
			if(!(HyFeatureEvolutionUtil.isAnd(group, date))){
				System.out.println("No And");
			}else{
				System.out.println("Found");
				featureChild.setParent(parentFeature);
				childFeature.getGroupMembership().add(composition);
				composition.getFeatures().add(childFeature);
			}

		}
		*/
	}
	
	


	public void removeConnection(HyParentChildConnection connection, Date date) {
		// remove the connection visually
		connection.getSource().removeParentToChildConnection(connection);
		connection.getTarget().removeChildToParentConnection(connection);


		HyFeatureWrapped oldTarget = connection.getTarget();

		oldTarget.getParentGroup(date).removeChildFeature(oldTarget, date);
	}
	
	public void rearrangeFeatures(){
		updateLayouter();
		
		// only rearrange features if layout mode is not set to custom layout
		if(!isAutoLayoutActive()) return;
		
		
		HyFeatureTreeLayouter layouter = HyFeatureLayouterManager.getLayouter(this);
		
		for(HyFeature feature : HyEvolutionUtil.getValidTemporalElements(model.getFeatures(), this.selectedDate)){
			Rectangle rectangle = layouter.getBounds(feature);
			
			getWrappedFeature(feature).setPosition(rectangle.getTopLeft());	
		}	
	}


	public void addFeature(HyFeatureWrapped feature){
		int size = features.size();
		features.add(feature);
		
		feature.addPropertyChangeListener(this);

		model.getFeatures().add(feature.getWrappedModelElement());
		changes.firePropertyChange(PROPERTY_CHILDREN_SIZE, size, size+1);
	}

	public List<HyParentChildConnection> getConnections() {
		return connections;
	}
	
	public void removeFeature(HyFeatureWrapped feature, Date date){
		try{
			feature.getParentGroup(date).removeChildFeature(feature, date);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		features.remove(feature);
		model.getFeatures().remove(feature.getWrappedModelElement());
		changes.firePropertyChange(PROPERTY_CHILDREN_SIZE, model.getFeatures().size()+1, model.getFeatures().size());
	}

	public void removeFeature(HyFeatureWrapped feature) {
		removeFeature(feature, new Date());
	}

	public HyFeatureWrapped getWrappedFeature(HyFeature feature){
		for(HyFeatureWrapped wrapped : features){
			if(wrapped.getWrappedModelElement().equals(feature))
				return wrapped;
		}

		return null;
	}

	public HyGroupWrapped getWrappedGroup(HyGroup group){
		for(HyGroupWrapped wrapped : groups){
			if(wrapped.getWrappedModelElement().equals(group))
				return wrapped;
		}

		return null;
	}

	public boolean isRootFeature(HyFeature feature){
		for(HyRootFeature rootFeature : model.getRootFeature()){
			if(rootFeature.getFeature().equals(feature))
				return true;
		}

		return false;
	}


	public void createConnection(HyFeatureWrapped parent, HyFeatureWrapped child){
		HyParentChildConnection connection = new HyParentChildConnection();
		connection.setSource(parent);
		connection.setTarget(child);
		connection.setModel(this);

		parent.addParentToChildConnection(connection);
		child.addChildToParentConnection(connection);	

		connections.add(connection);		

		HyFeatureLayouterManager.updateLayouter(this);
	}
	
	public void updateLayouter(){
		HyFeatureLayouterManager.updateLayouter(this);
	}	

	public void convertFeatures(HyFeature feature, List<HyFeatureWrapped> features){
		HyFeatureWrapped target = getWrappedFeature(feature);
		if(target == null){

			// root feature
			if(isRootFeature(feature)){
				target = new HyRootFeatureWrapped(feature, this);
			}else{
				target = new HyFeatureWrapped(feature, this);
			}

			addFeature(target);
		}

		boolean hasChildren = feature.getParentOf().size() != 0;

		if(hasChildren){
			for(HyFeatureChild featureChild : feature.getParentOf()){
				HyGroup group = featureChild.getChildGroup();

				HyGroupWrapped wrappedGroup = new HyGroupWrapped(group);
				wrappedGroup.setParentFeature(target);


				for(HyGroupComposition composition : group.getParentOf()){
					for(HyFeature childFeature : composition.getFeatures()){
						HyFeatureWrapped wrapped = getWrappedFeature(childFeature);
						if(wrapped == null){
							wrapped = new HyFeatureWrapped(childFeature, this);
							
							addFeature(wrapped);
						}

						createConnection(target, wrapped);

						wrapped.setParent(wrappedGroup);

						wrappedGroup.getFeatures().add(wrapped);
						convertFeatures(childFeature, features);
					}
				}

				groups.add(wrappedGroup);				
			}
		}
	}



	public HyGroupWrapped findWrappedGroup(HyGroup group){
		if(group == null)
			return null;

		for(HyGroupWrapped wrappedGroup : groups){
			if(wrappedGroup.getGroup().equals(group))
				return wrappedGroup;
		}
		return null;
	}

	public void removeGroup(HyGroupWrapped group){
		HyFeatureChild child = group.getWrappedModelElement().getChildOf().get(0);

		if(child.getParent() != null){
			child.getParent().getParentOf().remove(child);
		}
		model.getGroups().remove(group.getWrappedModelElement());

		groups.remove(group);
	}
	public HyGroupWrapped groupAlreadyInList(HyGroup group){
		for(HyGroupWrapped wrapped : groups){
			if(wrapped.getGroup().equals(group))
				return wrapped;
		}

		return null;
	}
	public void addGroup(HyGroupWrapped group) {
		model.getGroups().add(group.getWrappedModelElement());
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(HyFeatureWrapped.PROPERTY_POSITION)){
			HyFeatureTreeLayouter layouter = HyFeatureLayouterManager.getLayouter(this);
			HyFeatureWrapped feature = (HyFeatureWrapped)evt.getSource();
			Rectangle rectangle = layouter.getBounds(feature.getWrappedModelElement());
		
			// disable autolayout
			if(isAutoLayoutActive() && !rectangle.getTopLeft().equals(feature.getPosition(selectedDate))){
				setAutoLayoutActive(false);
			}
		}
		if(evt.getPropertyName().equals(HyGroupWrapped.PROPERTY_CHILD_FEATURES)){
			HyGroupWrapped newGroup = (HyGroupWrapped)evt.getNewValue();

			if(newGroup.getFeatures().isEmpty()){
				this.removeGroup(newGroup);
			}
		}
	}


	
	
	
	
	
	
}