package com.automic.actions;

import com.automic.exception.AutomicException;
import com.automic.util.ConsoleWriter;
import com.automic.util.SSHConnectionProperties;
import com.automic.util.SSHTaskTimeoutException;

/**
 * This action is used to Run Command in {@SSH}.
 *
 */
public class RunCommandAction extends AbstractSSHAction {

	protected static final int DEFAULT_MAX_OUTPUT_SIZE = 64;

	public RunCommandAction() {
		addOption("command", false, "Single Command To Execute");
	}

	@Override
	protected void executeSpecific() throws AutomicException {

		String command=getOptionValue("command");

		int returnCode = runCommand(command);
		
			String status = "SUCCESSFUL";
			if (returnCode != 0)
				status = "FAILED";
			ConsoleWriter.writeln("UC4RB_SSH_COMMAND_STATUS::=" + status);
		
	}

	
	public int runCommand(String command) throws AutomicException {

		int outputMaxSize = DEFAULT_MAX_OUTPUT_SIZE * 1024;
		final StringBuilder stdOut = new StringBuilder();
		final StringBuilder errOut = new StringBuilder();

		int returnValue = -1;
		try {
			final SSHConnectionProperties connectionProperties = createConnectionProperties();

			returnValue =executeSSHCommand(connectionProperties, command, stdOut, errOut, outputMaxSize);
			
				if (errOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("ERROR: " + errOut.toString());
				if (stdOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("OUTPUT: " + stdOut.toString());
			
			stdOut.setLength(0);
			errOut.setLength(0);

		} catch (final SSHTaskTimeoutException e) {
			throw new AutomicException("Command Execution Failed");
		} catch (final AutomicException e) {
			throw new AutomicException("Command Execution Failed");
		}

		return returnValue;
	}
}