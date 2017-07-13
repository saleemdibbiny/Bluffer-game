package tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMessage implements Message<CommandMessage> {
	private final String command;
	private final String parameter;

	public CommandMessage(String message) {
		String[] data = message.split(" ");
		this.command = data[0];
		if(data.length > 1)
		this.parameter = message.substring(command.length() + 1);
		else this.parameter="";
	}

	public String getCommand() {
		return command;
	}

	public String getParameter() {
		return parameter;
	}

	@Override
	public String toString() {
		return command + " " + parameter;
	}

	@Override
	public boolean equals(Object other) {
		return this.equals(other);
	}
}
