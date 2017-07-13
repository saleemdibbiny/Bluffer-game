package protocol;

import tokenizer.CommandMessage;

public abstract class Player {
	private String _nickname;
	private ProtocolCallback<CommandMessage> _callback;

	public Player(String nickname, ProtocolCallback<CommandMessage> callback) {
		this._nickname = nickname;
		this._callback = callback;
	}

	public String getNickname() {
		return _nickname;
	}
	public ProtocolCallback<CommandMessage> getCallback() {
		return _callback;
	}
}
