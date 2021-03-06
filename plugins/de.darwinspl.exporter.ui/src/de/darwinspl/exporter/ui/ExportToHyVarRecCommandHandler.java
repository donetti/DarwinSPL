package de.darwinspl.exporter.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.christophseidl.util.eclipse.ResourceUtil;
import de.christophseidl.util.eclipse.ui.SelectionUtil;
import de.christophseidl.util.ecore.EcoreIOUtil;
import de.darwinspl.feature.graphical.base.dialogs.DwDateDialog;
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
import eu.hyvar.preferences.HyProfile;
import eu.hyvar.preferences.util.HyPreferenceModelUtil;
import eu.hyvar.reconfigurator.input.exporter.HyVarRecExporter;

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
			try {
				if (featureModelFile.getContents().available() == 0) {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			modelFiles.add(featureModelFile);
		}

		IFile constraintModelFileConcreteSyntax = ResourceUtil.deriveFile(featureModelFile,
				HyConstraintUtil.getConstraintModelFileExtensionForConcreteSyntax());
		IFile constraintModelFileXmi = ResourceUtil.deriveFile(featureModelFile,
				HyConstraintUtil.getConstraintModelFileExtensionForXmi());
		IFile contextModelFile = ResourceUtil.deriveFile(featureModelFile,
				HyContextInformationUtil.getContextModelFileExtensionForConcreteSyntax());
		IFile validityModelFile = ResourceUtil.deriveFile(featureModelFile,
				HyValidityModelUtil.getValidityModelFileExtensionForConcreteSyntax());
		IFile preferenceModelFile = ResourceUtil.deriveFile(featureModelFile,
				HyPreferenceModelUtil.getPreferenceModelFileExtensionForXmi());
		IFile configurationFile = ResourceUtil.deriveFile(featureModelFile,
				HyConfigurationUtil.getConfigurationModelFileExtensionForXmi());
		IFile contextValueModelFile = ResourceUtil.deriveFile(featureModelFile,
				HyConfigurationUtil.getConfigurationModelFileExtensionForXmi());

		if (constraintModelFileConcreteSyntax != null && constraintModelFileConcreteSyntax.exists()) {
			try {
				if (constraintModelFileConcreteSyntax.getContents().available() != 0) {
					modelFiles.add(constraintModelFileConcreteSyntax);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} 
		else if(constraintModelFileXmi != null && constraintModelFileXmi.exists()) {
			try {
				if (constraintModelFileXmi.getContents().available() != 0) {
					modelFiles.add(constraintModelFileXmi);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (contextModelFile != null && contextModelFile.exists()) {
			try {
				if (contextModelFile.getContents().available() != 0) {
					modelFiles.add(contextModelFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (validityModelFile != null && validityModelFile.exists()) {
			try {
				if (validityModelFile.getContents().available() != 0) {
					modelFiles.add(validityModelFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (preferenceModelFile != null && preferenceModelFile.exists()) {
			try {
				if (preferenceModelFile.getContents().available() != 0) {
					modelFiles.add(preferenceModelFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (configurationFile != null && configurationFile.exists()) {
			try {
				if (configurationFile.getContents().available() != 0) {
					modelFiles.add(configurationFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (contextValueModelFile != null && contextValueModelFile.exists()) {
			try {
				if (contextValueModelFile.getContents().available() != 0) {
					modelFiles.add(contextValueModelFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		List<EObject> models = EcoreIOUtil.loadModels(modelFiles);

		HyFeatureModel featureModel = null;
		HyConstraintModel constraintModel = null;
		HyContextModel contextModel = null;
		HyValidityModel validityModel = null;
		HyProfile preferenceModel = null;
		HyConfiguration configuration = null;
		HyContextValueModel contextValueModel = null;

		for (EObject object : models) {
			if (object instanceof HyFeatureModel) {
				featureModel = (HyFeatureModel) object;
			} else if (object instanceof HyConstraintModel) {
				constraintModel = (HyConstraintModel) object;
			} else if (object instanceof HyContextModel) {
				contextModel = (HyContextModel) object;
			} else if (object instanceof HyValidityModel) {
				validityModel = (HyValidityModel) object;
			} else if (object instanceof HyProfile) {
				preferenceModel = (HyProfile) object;
			} else if (object instanceof HyConfiguration) {
				configuration = (HyConfiguration) object;
			} else if (object instanceof HyContextValueModel) {
				contextValueModel = (HyContextValueModel) object;
			}
		}

		HyVarRecExporter hyvarrecExporter = new HyVarRecExporter();

		Display display = Display.getDefault();
	    Shell shell = display.getActiveShell();

		DwDateDialog datePicker = new DwDateDialog(shell, new Date());
		datePicker.open();
		
		String hyVarRecString = hyvarrecExporter.exportSPL(contextModel, validityModel, featureModel,
				constraintModel, configuration, preferenceModel, contextValueModel, datePicker.getValue());

		String baseFileName = ResourceUtil.getBaseFilename(featureModelFile) + "_HyVarRecOutput";
		
		IFile hyVarRecOutputFile = ResourceUtil.getFileInSameContainer(featureModelFile,baseFileName+".json");
		
		if(hyVarRecOutputFile.exists()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			Date date = new Date();
			hyVarRecOutputFile = ResourceUtil.getFileInSameContainer(featureModelFile,baseFileName+"_"+dateFormat.format(date)+".json");
		}
		
		int counter = 1;
		while(hyVarRecOutputFile.exists()) {
			hyVarRecOutputFile = ResourceUtil.getFileInSameContainer(featureModelFile,baseFileName+"_"+counter+".json");
			counter++;
		}
		

		InputStream source = new ByteArrayInputStream(hyVarRecString.getBytes());
		try {
			hyVarRecOutputFile.create(source, IResource.FORCE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

}
