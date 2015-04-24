package de.ovgu.featureide.core.roslaunch.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class RosLaunchConfigDelegator implements ILaunchConfigurationDelegate {

	static Process p = null;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String s = null;
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		MessageConsole console = null;
		for (IConsole c : conMan.getConsoles()) {
			if (c instanceof MessageConsole) {
				console = (MessageConsole) c;
				break;
			}
		}
		// no console found, so create a new one
		if (console == null) {
			console = new MessageConsole("Console", null);
			conMan.addConsoles(new IConsole[] { console });
		}
		MessageConsoleStream out = console.newMessageStream();
		String launchFileName = configuration.getAttribute("launch-file",
				"start.launch");

		try {
			if (p != null) {
				p.destroy();
			}
			p = Runtime.getRuntime().exec("roslaunch " + launchFileName);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			// read the output from the command
			out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				out.println(s);
			}

			// read any errors from the attempted command
			out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				out.println(s);
			}
			out.println("finished");
		} catch (IOException e) {
			out.println("exception happened - here's what I know: ");
			out.println(e.getMessage());
		}
	}
}
