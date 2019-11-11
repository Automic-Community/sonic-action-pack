package com.automic.actions;

import com.automic.exception.AutomicException;
import com.automic.util.CommonUtil;
import com.automic.util.ConsoleWriter;
import com.automic.util.SSHConnectionProperties;

/**
 * This action is used to Run Command in {@SSH}.
 *
 */
public class RunCommandAction extends AbstractSSHAction {

	protected static final int DEFAULT_MAX_OUTPUT_SIZE = 128;
	protected String standardOutputFilePath = null;

	public RunCommandAction() {
		addOption("command", true, "Single Command To Execute");
		addOption("outfile", false, "Output File Path");
		addOption("flag", false, "Flag");
	}

	@Override
	protected void executeSpecific() throws AutomicException {

		String command=getOptionValue("command");
		String outputFile = getOptionValue("outfile");
		boolean flag = CommonUtil.convert2Bool(getOptionValue("flag"));
		if (CommonUtil.checkNotEmpty(outputFile)) {
			standardOutputFilePath = outputFile;
		}


		int returnCode = runCommand(command,flag);
		
			String status = "SUCCESSFUL";
			if (returnCode != 0)
				status = "FAILED";
			ConsoleWriter.writeln("UC4RB_SSH_COMMAND_STATUS::=" + status);
		
	}

	
	public int runCommand(String command, boolean flag) throws AutomicException {

		int outputMaxSize = DEFAULT_MAX_OUTPUT_SIZE * 1024;
		final StringBuilder stdOut = new StringBuilder();
		final StringBuilder errOut = new StringBuilder();

		int returnValue = -1;
		try {
			final SSHConnectionProperties connectionProperties = createConnectionProperties();

			returnValue =executeSSHCommand(connectionProperties, command, stdOut, errOut, outputMaxSize, standardOutputFilePath);
			if(!flag) {
				if (errOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("ERROR: " + errOut.toString());
				if (stdOut.toString().trim().length() != 0)
					ConsoleWriter.writeln("OUTPUT: " + stdOut.toString());
			}
			stdOut.setLength(0);
			errOut.setLength(0);
			

		} catch (final AutomicException e) {
			ConsoleWriter.writeln(e);
			throw new AutomicException("Command Execution Failed");
		} 
		return returnValue;
	}
}