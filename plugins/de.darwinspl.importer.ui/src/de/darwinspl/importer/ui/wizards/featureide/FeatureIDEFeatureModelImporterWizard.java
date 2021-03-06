package de.darwinspl.importer.ui.wizards.featureide;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.christophseidl.util.ecore.EcoreIOUtil;
import de.darwinspl.importer.FeatureModelConstraintsTuple;
import de.darwinspl.importer.featureide.FeatureIDEFeatureModelAndConstraintsImporter;
import de.darwinspl.importer.ui.wizards.DwFeatureModelWizardImportedFilePage;
import de.darwinspl.importer.ui.wizards.FileSelectionWizardPage;
import eu.hyvar.feature.HyFeatureModel;

public class FeatureIDEFeatureModelImporterWizard extends Wizard implements IImportWizard {

	protected FileSelectionWizardPage featureIdeModelFileSelectionWizardPage;
	protected DwFeatureModelWizardImportedFilePage dwFeatureModelWizardImportedFilePage;
	
	protected IStructuredSelection selection;
	protected IWorkbench workbench;
	
	public FeatureIDEFeatureModelImporterWizard() {
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		
		setWindowTitle("New Feature Model");
		
		featureIdeModelFileSelectionWizardPage = new FileSelectionWizardPage("FeatureIDE Feature Model Selection", "Please Select a FeatureIDE Feature Model for Import" , new String[] {"*.xml"}, "FeatureIDE Feature Model");
		dwFeatureModelWizardImportedFilePage = new DwFeatureModelWizardImportedFilePage("Select new File to store the imported Feature Model", selection);
	}

	@Override
	public boolean performFinish() {
		FeatureIDEFeatureModelAndConstraintsImporter importer = new FeatureIDEFeatureModelAndConstraintsImporter();
		
		FeatureModelConstraintsTuple tuple = importer.importFeatureModel(featureIdeModelFileSelectionWizardPage.getSelectedFilePath());
		HyFeatureModel featureModel = tuple.getFeatureModel();
		
		EcoreIOUtil.saveModelAs(featureModel, dwFeatureModelWizardImportedFilePage.getModelFile());
		
		System.out.println("FeatureIDE File:"+featureIdeModelFileSelectionWizardPage.getSelectedFilePath());
		System.out.println("DarwinSPL File:"+dwFeatureModelWizardImportedFilePage.getModelFile().getFullPath());
		return true;
	}

	@Override
    public String getWindowTitle() {
            return "DarwinSPL: Import FeatureIDE Feature Model";
    }

	@Override
	public void addPages() {
		addPage(featureIdeModelFileSelectionWizardPage);
		addPage(dwFeatureModelWizardImportedFilePage);
	}
}
