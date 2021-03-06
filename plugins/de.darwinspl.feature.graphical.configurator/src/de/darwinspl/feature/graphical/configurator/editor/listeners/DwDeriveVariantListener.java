package de.darwinspl.feature.graphical.configurator.editor.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deltaecore.common.eclipse.controls.DEVariantComposite;
import org.deltaecore.feature.DEFeatureModel;
import org.deltaecore.feature.configuration.DEConfiguration;
import org.deltaecore.feature.configuration.util.DEConfigurationIOUtil;
import org.deltaecore.feature.graphical.configurator.components.DEGenerateVariantDialog;
import org.deltaecore.feature.graphical.configurator.components.DEVariantGeneratorsComposite;
import org.deltaecore.feature.util.DEFeatureIOUtil;
//import org.deltaecore.feature.graphical.configurator.editor.DEConfiguratorGraphicalEditor.ValidateConfigurationResult;
import org.deltaecore.feature.variant.DEVariantGenerator;
import org.deltaecore.suite.mapping.DEMappingModel;
import org.deltaecore.suite.mapping.util.DEMappingIOUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import de.christophseidl.util.eclipse.ResourceUtil;
import de.christophseidl.util.ecore.EcoreIOUtil;
import de.darwinspl.feature.graphical.configurator.editor.DwFeatureModelConfiguratorEditor;
import eu.hyvar.feature.HyFeatureModel;
import eu.hyvar.feature.configuration.HyConfiguration;
import eu.hyvar.feature.exporter.hfm_exporter.HFMConfigurationExporter;
import eu.hyvar.feature.exporter.hfm_exporter.HFMExporter;
import eu.hyvar.feature.exporter.hfm_exporter.HFMMappingExporter;
import eu.hyvar.feature.mapping.HyMappingModel;
import eu.hyvar.feature.mapping.util.HyMappingModelUtil;
import eu.hyvar.feature.util.HyFeatureModelWellFormednessException;

public class DwDeriveVariantListener extends SelectionAdapter {

	private DwFeatureModelConfiguratorEditor configurator;
	private IFolder tempDeltaEcoreFolder;

	public DwDeriveVariantListener(DwFeatureModelConfiguratorEditor configurator) {
		super();
		this.configurator = configurator;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Shell shell = configurator.getShell();
		
		
		HyConfiguration configuration = configurator.getConfiguration();
		HyFeatureModel featureModel = configurator.getFeatureModel();
//		EcoreIOUtil.getFile(featureModel);		
		
		IFile mappingFile = ResourceUtil.getLocalFile(featureModel.eResource().getURI().toPlatformString(true));
		
		
		if(mappingFile == null || !mappingFile.exists()) {
			MessageDialog.openInformation(shell, "Could not find Mapping Model", "No Mapping Model with file extension "+HyMappingModelUtil.getMappingModelFileExtensionForConcreteSyntax()+" or "+ HyMappingModelUtil.getMappingModelFileExtensionForXmi()+" found!");
			return;
		}
		
		EObject loadedObject = EcoreIOUtil.loadAccompanyingModel(featureModel, HyMappingModelUtil.getMappingModelFileExtensionForConcreteSyntax(), HyMappingModelUtil.getMappingModelFileExtensionForXmi());			
		
		if(loadedObject == null || !(loadedObject instanceof HyMappingModel)) {
			MessageDialog.openInformation(shell, "Could not find Mapping Model", "No Mapping Model with file extension "+HyMappingModelUtil.getMappingModelFileExtensionForConcreteSyntax()+" or "+ HyMappingModelUtil.getMappingModelFileExtensionForXmi()+" found!");
			return;
		}
		
		
		HyMappingModel mapping = (HyMappingModel) loadedObject;

//		DwConfigurationVariantCreator variantCreator = new DwConfigurationVariantCreator();


		Date date = configurator.getCurrentSelectedDate();

		
		final String failureMessageTitle = "Variant Generation Failed!";
		
		
		// TODO Validate selected configuration
//		ValidateConfigurationResult validateConfigurationResult = doValidateConfiguration();
		
//		if (!validateConfigurationResult.getIsValid()) {
//			MessageDialog.openError(shell, failureMessageTitle, "The configuration is invalid: " + validateConfigurationResult.getErrorMessage());
//			return;
//		}
		
		try {
			
			DEGenerateVariantDialog dialog = new DEGenerateVariantDialog(shell);
			int result = dialog.open();
			
			if (result == Dialog.OK) {
				DEVariantGeneratorsComposite generatorsComposite = dialog.getGeneratorsComposite();
				DEVariantGenerator variantGenerator = generatorsComposite.getVariantGenerator();
				
				if (variantGenerator == null) {
					MessageDialog.openError(shell, failureMessageTitle, "No variant generator selected!");
					return;
				}
				
				DEVariantComposite variantComposite = dialog.getVariantComposite();
				IFolder variantFolder = variantComposite.getFolder();
				
				if (variantFolder == null) {
					MessageDialog.openError(shell, failureMessageTitle, "No variant folder selected!");
					return;
				}
				
				
				List<EObject> deltaEcoreModels = getDeltaEcoreModels(featureModel, configuration, mapping, date);
				
				DEConfiguration deConfiguration = null;
				
				for(EObject eObject : deltaEcoreModels) {
					if(eObject instanceof DEConfiguration) {
						deConfiguration = (DEConfiguration) eObject;
						break;
					}
				}
				
				if(deConfiguration == null) {
					throw new Exception("Could not translate configuration");
				}
				
				variantGenerator.createAndSaveVariantFromConfiguration(deConfiguration, variantFolder);
				
				// clean temporary files
				for(EObject eObject : deltaEcoreModels) {
					eObject.eResource().delete(null);
				}
				tempDeltaEcoreFolder.delete(true, null);
				
				
				// Output message
				IPath variantFolderPath = variantFolder.getFullPath();
				MessageDialog.openInformation(shell, "Variant Generation Completed", "Variant successfully generated in \"" + variantFolderPath.toOSString() + "\".");
			}
			
		} catch(Exception ex) {
			//Debug
			ex.printStackTrace();
			
			String message = "Variant could not be generated:\n\n" + ex.getMessage();
			MessageDialog.openError(shell, failureMessageTitle, message);
		}

	}
	
	private List<EObject> getDeltaEcoreModels(HyFeatureModel featureModel, HyConfiguration configuration, HyMappingModel mapping, Date date) {
		List<EObject> deltaEcoreModels = new ArrayList<EObject>(3);
		
		HFMExporter hfmExporter = new HFMExporter();
		DEFeatureModel deFeatureModel;
		try {
			deFeatureModel = hfmExporter.exportFeatureModel(featureModel, date);
			HFMConfigurationExporter configurationExporter = new HFMConfigurationExporter(hfmExporter.getFeatureMapping(), hfmExporter.getVersionMapping(), featureModel, deFeatureModel);
			DEConfiguration deConfiguration = configurationExporter.exportConfiguration(configuration, date);
			
			
			HFMMappingExporter mappingExporter = new HFMMappingExporter(hfmExporter.getFeatureMapping(), hfmExporter.getVersionMapping());
			DEMappingModel deMapping = mappingExporter.exportMappingModel(mapping, date);
			
			saveTemporaryDeltaEcoreFiles(EcoreIOUtil.getFile(featureModel.eResource()).getProject(), featureModel.eResource().getResourceSet(), deFeatureModel, deMapping, deConfiguration);
			
			deltaEcoreModels.add(deConfiguration);
			deltaEcoreModels.add(deMapping);
			deltaEcoreModels.add(deFeatureModel);
			
			return deltaEcoreModels;
		} catch (HyFeatureModelWellFormednessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return deltaEcoreModels;
		
	}
	
	
	private void saveTemporaryDeltaEcoreFiles(IProject p, ResourceSet resourceSet, DEFeatureModel featureModel, DEMappingModel mapping, DEConfiguration configuration) {
//		URI uriWithoutExtension = uri.trimFileExtension();
		
		if(!p.isOpen()) {
			try {
				p.open(null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tempDeltaEcoreFolder = p.getFolder(".tempDeltaEcoreFiles");
		
		String baseFileName = "tempDeltaEcoreModel";
		
		IFile featureModelFile = tempDeltaEcoreFolder.getFile(baseFileName+"."+DEFeatureIOUtil.getDefaultFileExtension());
		IFile mappingFile = tempDeltaEcoreFolder.getFile(baseFileName+"."+DEMappingIOUtil.getCurrentFileExtension());
		IFile configurationFile = tempDeltaEcoreFolder.getFile(baseFileName+"."+DEConfigurationIOUtil.getCurrentFileExtension());
		
		EcoreIOUtil.saveModelAs(featureModel, featureModelFile, resourceSet);
		EcoreIOUtil.saveModelAs(mapping, mappingFile, resourceSet);
		EcoreIOUtil.saveModelAs(configuration, configurationFile, resourceSet);
	}

}
