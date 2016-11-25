package eu.hyvar.feature.exporter.hfm_exporter.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;

import de.christophseidl.util.eclipse.ResourceUtil;
import de.christophseidl.util.eclipse.ui.SelectionUtil;
import de.christophseidl.util.ecore.EcoreIOUtil;
import eu.hyvar.context.HyContextModel;
import eu.hyvar.context.contextValidity.HyValidityModel;
import eu.hyvar.context.contextValidity.util.HyValidityModelUtil;
import eu.hyvar.context.information.contextValue.HyContextValueModel;
import eu.hyvar.context.information.util.HyContextInformationUtil;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.configuration.HyConfiguration;
import eu.hyvar.feature.configuration.util.HyConfigurationUtil;
import eu.hyvar.feature.constraint.HyConstraintModel;
import eu.hyvar.feature.constraint.util.HyConstraintUtil;
import eu.hyvar.feature.util.HyFeatureUtil;
import eu.hyvar.preferences.HyPreferenceModel;
import eu.hyvar.preferences.util.HyPreferenceModelUtil;
import eu.hyvar.reconfigurator.input.exporter.ContextConstraintExporterJson;

public class ExportToHyVarRecCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile featureModelFile = SelectionUtil
				.getFirstSelectedIFileWithExtension(HyFeatureUtil.getFeatureModelFileExtensionForXmi());

		List<IFile> modelFiles = new ArrayList<IFile>();
		if (featureModelFile == null || !featureModelFile.exists()) {
			// Error
			return null;
		} else {
			modelFiles.add(featureModelFile);
		}
		
		IFile constraintModelFile = ResourceUtil.deriveFile(featureModelFile, HyConstraintUtil.getConstraintModelFileExtensionForXmi());
		IFile contextModelFile = ResourceUtil.deriveFile(featureModelFile, HyContextInformationUtil.getContextModelFileExtensionForConcreteSyntax());
		IFile validityModelFile = ResourceUtil.deriveFile(featureModelFile, HyValidityModelUtil.getValidityModelFileExtensionForConcreteSyntax());
		IFile preferenceModelFile = ResourceUtil.deriveFile(featureModelFile, HyPreferenceModelUtil.getPreferenceModelFileExtensionForXmi());
		IFile configurationFile = ResourceUtil.deriveFile(featureModelFile, HyConfigurationUtil.getConfigurationModelFileExtensionForXmi());
		IFile contextValueModelFile = ResourceUtil.deriveFile(featureModelFile, HyConfigurationUtil.getConfigurationModelFileExtensionForXmi());
		
		if(constraintModelFile != null && constraintModelFile.exists()) {
			modelFiles.add(constraintModelFile);
		}
		
		if(contextModelFile != null && contextModelFile.exists()) {
			modelFiles.add(contextModelFile);
		}
		
		if(validityModelFile != null && validityModelFile.exists()) {
			modelFiles.add(validityModelFile);
		}
		
		if(preferenceModelFile != null && preferenceModelFile.exists()) {
			modelFiles.add(preferenceModelFile);
		}
		
		if(configurationFile != null && configurationFile.exists()) {
			modelFiles.add(configurationFile);
		}
		
		if(contextValueModelFile != null && contextValueModelFile.exists()) {
			modelFiles.add(contextValueModelFile);
		}
		
		
		List<EObject> models = EcoreIOUtil.loadModels(modelFiles);
		
		HyFeatureModel featureModel = null;
		HyConstraintModel constraintModel = null;
		HyContextModel contextModel = null;
		HyValidityModel validityModel = null;
		HyPreferenceModel preferenceModel = null;
		HyConfiguration configuration = null;
		HyContextValueModel contextValueModel = null;
		
		for(EObject object: models) {
			if(object instanceof HyFeatureModel) {
				featureModel = (HyFeatureModel) object;
			}
			else if(object instanceof HyConstraintModel) {
				constraintModel = (HyConstraintModel) object;
			}
			else if(object instanceof HyContextModel) {
				contextModel = (HyContextModel) object;
			}
			else if(object instanceof HyValidityModel) {
				validityModel = (HyValidityModel) object;
			}
			else if(object instanceof HyPreferenceModel) {
				preferenceModel = (HyPreferenceModel) object;
			}
			else if(object instanceof HyConfiguration) {
				configuration = (HyConfiguration) object;
			}
			else if(object instanceof HyContextValueModel) {
				contextValueModel = (HyContextValueModel) object;
			}
		}
		
		
		ContextConstraintExporterJson hyvarrecExporter = new ContextConstraintExporterJson();
		// TODO allow date selection somehow
		
		String hyVarRecString = hyvarrecExporter.exportContextMappingModel(contextModel, validityModel, featureModel, constraintModel, configuration, preferenceModel, contextValueModel, new Date());
		
		IFile hyVarRecOutputFile = ResourceUtil.getFileInSameContainer(featureModelFile, ResourceUtil.getBaseFilename(featureModelFile)+"_HyVarRecOutput.json");
		
		InputStream source = new ByteArrayInputStream(hyVarRecString.getBytes());
		try {
			hyVarRecOutputFile.create(source, IResource.REPLACE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Save HyVarRec String
		return null;
	}

}