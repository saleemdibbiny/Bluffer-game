package protocol;

import tokenizer.CommandMessage;

/**
 * a simple implementation of the server protocol interface
 */
public class EchoProtocol implements AsyncServerProtocol<CommandMessage> {

	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;

	/**
	 * processes a message<BR>
	 * this simple interface prints the message to the screen, then composes a simple
	 * reply and sends it back to the client
	 *
	 * @param msg the message to process
	 * @return the reply that should be sent to the client, or null if no reply needed
	 */
	@Override
	public void processMessage(CommandMessage msg, ProtocolCallback<CommandMessage> callback) {        
		if (this._connectionTerminated) {
			callback.call(null);
		}
		else
		if (this.isEnd(msg)) {
			this._shouldClose = true;
			callback.call(new CommandMessage("Ok, bye bye"));
		}
		else
		callback.call(new CommandMessage("Your message \"" + msg + "\" has been received"));
	}

	/**
	 * detetmine whether the given message is the termination message
	 *
	 * @param msg the message to examine
	 * @return false - this simple protocol doesn't allow termination...
	 */
	@Override
	public boolean isEnd(CommandMessage msg) {
		return msg.equals("bye");
	}

	/**
	 * Is the protocol in a closing state?.
	 * When a protocol is in a closing state, it's handler should write out all pending data, 
	 * and close the connection.
	 * @return true if the protocol is in closing state.
	 */
	@Override
	public boolean shouldClose() {
		return this._shouldClose;
	}

	/**
	 * Indicate to the protocol that the client disconnected.
	 */
	@Override
	public void connectionTerminated() {
		this._connectionTerminated = true;
	}

}
