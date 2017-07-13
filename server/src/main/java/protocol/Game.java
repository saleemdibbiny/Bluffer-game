package protocol;

import java.util.ArrayList;
import java.util.List;

import tokenizer.CommandMessage;

public abstract class Game<T extends Player> {
	protected List<T> _players;
	private String _name;
	private AfterGameOverCallback _afterGameOverCallback;

	public Game(String name, AfterGameOverCallback onEnd) {
		this._name = name;
		this._players = new ArrayList<>();
		this._afterGameOverCallback = onEnd;
	}

	public void endGame() {
		_afterGameOverCallback.call(_name);
	}

	public String getName() {
		return _name;
	}

	public void ASKTXT(String parameter, ProtocolCallback<CommandMessage> callback) {
		callback.call(new CommandMessage("ASKTXT " + parameter));
	}

	public void ASKCHOICES(String parameter, ProtocolCallback<CommandMessage> callback) {
		callback.call(new CommandMessage("ASKCHOICES " + parameter));
	}

	public void GAMEMSG(String parameter, ProtocolCallback<CommandMessage> callback) {
		callback.call(new CommandMessage("GAMEMSG " + parameter));
	}

	public void addPlayer(String nickname,ProtocolCallback<CommandMessage> callback) {
		_players.add(this.createPlayer(nickname, callback));
	}
	public abstract T createPlayer(String nickname,ProtocolCallback<CommandMessage> callback);

	public abstract void onTXTRESP(String nickname, String response);

	public abstract void onSELECTRESP(String nickname, int selectedChoice);

	public abstract void onSTARTGAME();

	public void playerDisconnected(String name) {
		beforeRemovingPlayer(name);
		for(int i = 0; i<_players.size(); i++){
			if(_players.get(i).getNickname().compareTo(name)==0){
				_players.remove(i);
				break;
			}
		}
		
	}
	public abstract void beforeRemovingPlayer(String name);
}
