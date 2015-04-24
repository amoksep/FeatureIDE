package de.ovgu.featureide.core.roslaunch.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class RosLaunchTab extends AbstractLaunchConfigurationTab {
	private Text fNameText;
	public static String LUNCH_FILE = "launch-file";

	@Override
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, 1, 1,
				GridData.FILL_HORIZONTAL);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(getControl(), "this is help system");
		Composite namecomp = SWTFactory.createComposite(comp, comp.getFont(),
				4, 1, GridData.FILL_HORIZONTAL, 0, 0);
		SWTFactory.createLabel(namecomp, "Launch File", 1);
		fNameText = SWTFactory.createSingleText(namecomp, 1);
//		fNameText.a
		fNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				RosLaunchTab.this.updateLaunchConfigurationDialog();
			}
		});
		Button button = SWTFactory.createPushButton(namecomp, "Browse...", null);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedPath = openFileDialog();
				if (selectedPath != null) {
					fNameText.setText(selectedPath);
				}
			}
		});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LUNCH_FILE, (String) null);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fNameText.setText(configuration.getAttribute(LUNCH_FILE,
					"start.lauch"));
		} catch (CoreException CE) {
			fNameText.setText("");
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LUNCH_FILE, fNameText.getText());
	}

	@Override
	public String getName() {
		return "RosLaunch";
	}

	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		if (fNameText.getText().isEmpty()) {
			setErrorMessage("no launch file set");
			return false;
		} else {
			return true;
		}
	}
	private String openFileDialog() {
		FileDialog dialog = new FileDialog(getShell(), SWT.MULTI);
		dialog.setText("Launch File");
		dialog.setFilterExtensions(new String [] {"*.launch"});
		dialog.setFilterNames(new String[]{ "Launch *.launch"});
		
		return dialog.open();
	}
}
