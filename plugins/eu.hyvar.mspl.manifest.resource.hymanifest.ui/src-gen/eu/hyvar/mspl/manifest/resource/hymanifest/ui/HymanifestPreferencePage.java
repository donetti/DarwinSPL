/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.mspl.manifest.resource.hymanifest.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The root preference page.
 */
public class HymanifestPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	public void init(IWorkbench workbench) {
		setPreferenceStore(eu.hyvar.mspl.manifest.resource.hymanifest.ui.HymanifestUIPlugin.getDefault().getPreferenceStore());
		setDescription(eu.hyvar.mspl.manifest.resource.hymanifest.ui.HymanifestUIResourceBundle.ROOT_PREFERENCE_PAGE_DESCRIPTION);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite settingComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData gd;
		layout.numColumns= 1;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		gd = new GridData(GridData.BEGINNING);
		settingComposite.setLayout(layout);
		settingComposite.setLayoutData(gd);
		
		Link link = new Link(settingComposite, SWT.NONE);
		link.setText(eu.hyvar.mspl.manifest.resource.hymanifest.ui.HymanifestUIResourceBundle.ROOT_PREFERENCE_PAGE_TEXT);
		link.setSize(140, 40);
		link.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (e.text.startsWith("http")) Program.launch(e.text);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		return settingComposite;
	}
	
}
