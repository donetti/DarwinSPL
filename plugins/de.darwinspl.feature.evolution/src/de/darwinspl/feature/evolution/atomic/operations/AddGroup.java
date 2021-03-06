/**
 * 
 */
package de.darwinspl.feature.evolution.atomic.operations;

import java.util.Date;

import de.darwinspl.feature.evolution.invoker.EvolutionOperation;
import eu.hyvar.feature.HyFeatureFactory;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.HyGroup;

/**
 * atomic operation which add a group to a tfm
 */
public class AddGroup extends EvolutionOperation {

	private static final HyFeatureFactory factory = HyFeatureFactory.eINSTANCE;
	
	private HyGroup group;
	private HyFeatureModel tfm;
	
	public AddGroup(Date timestamp, HyFeatureModel tfm) {
		
		this.timestamp = timestamp;
		this.tfm = tfm;
		
	}
	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#execute()
	 */
	@Override
	public void execute() {
		
		group = factory.createHyGroup();
		
		group.setValidSince(timestamp);
		group.setValidUntil(null);
		
		tfm.getGroups().add(group);

	}

	/* (non-Javadoc)
	 * @see de.darwinspl.feature.evolution.basic.operations.ComplexOperation#undo()
	 */
	@Override
	public void undo() {
		//check if the execute method was executed, otherwise leave this method
		if (group == null) {
			return;
		}
		
		tfm.getGroups().remove(group);
		group = null;

	}
	
	//Getter
	public HyGroup getGroup() {
		return group;
	}

}
