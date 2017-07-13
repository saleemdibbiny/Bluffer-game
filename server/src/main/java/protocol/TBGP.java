package protocol;

import java.util.ArrayList;

import tokenizer.CommandMessage;

public class TBGP implements AsyncServerProtocol<CommandMessage> {
	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;
	private static TextBasedGameDatabase _database = TextBasedGameDatabase.getInstance();

	@Override
	public void processMessage(CommandMessage msg, ProtocolCallback<CommandMessage> callback) {
		if (this._connectionTerminated) {
			callback.call(null);
		} else if (isEnd(msg)) {
			this._shouldClose = true;
			_database.deleteNickIfExists(callback);
			callback.call(new CommandMessage("SYSMSG QUIT ACCEPTED"));
		} else {
			switch (msg.getCommand()) {
			default:
				callback.call(new CommandMessage("SYSMSG "+msg.getCommand()+" UNIDENTIFIED"));
				break;
			case "NICK": {
				boolean failed = false;
				String parameter = msg.getParameter();
				if (parameter.length() > 0)
					if (_database.addNicknameIfAvailable(parameter, callback))
						callback.call(new CommandMessage("SYSMSG NICK ACCEPTED"));
					else
						failed = true;
				else
					failed = true;

				if (failed)
					callback.call(new CommandMessage("SYSMSG NICK REJECTED"));
				break;
			}
			case "JOIN": {
				boolean failed = false;
				String nickname = _database.getNickname(callback);
				if (nickname != null) {
					String parameter = msg.getParameter();
					if (parameter.length() > 0) {
						if (_database.joinRoom(nickname, parameter)) {
							callback.call(new CommandMessage("SYSMSG JOIN ACCEPTED"));
						} else
							failed = true;
					} else
						failed = true;
				} else
					failed = true;
				if (failed)
					callback.call(new CommandMessage("SYSMSG JOIN REJECTED"));
				break;
			}
			case "MSG": {
				String nickname = _database.getNickname(callback);
				if (nickname != null) {
					String parameter = msg.getParameter();
					if (parameter.length() > 0) {
						ArrayList<ProtocolCallback<CommandMessage>> callbacks =_database
								.getCallbacksOfOthersInRoom(nickname);
						for(int i = 0; i<callbacks.size(); i++){
							callbacks.get(i).call(new CommandMessage("USRMSG " + nickname + ": " + parameter));
						}
						callback.call(new CommandMessage("SYSMSG MSG ACCEPTED"));
					} else {
						callback.call(new CommandMessage("SYSMSG MSG REJECTED"));
					}
				} else
					callback.call(new CommandMessage("SYSMSG MSG REJECTED"));
				break;
			}
			case "LISTGAMES": {
				boolean failed = false;
				String nickname = _database.getNickname(callback);
				String games = _database.getGames();
				if (nickname != null && games.length() > 0) {
					callback.call(new CommandMessage("SYSMSG LISTGAMES ACCEPTED " + games));
				} else
					failed = true;
				if (failed)
					callback.call(new CommandMessage("SYSMSG LISTGAMES REJECTED"));
				break;
			}
			case "STARTGAME": {
				boolean failed = false;
				String nickname = _database.getNickname(callback);
				String room = _database.getPlayerRoom(nickname);
				String parameter = msg.getParameter();
				if (nickname != null && !_database.isPlaying(nickname) && !_database.isInProgress(room)
						&& parameter.length() > 0) {
					Game<?> game = _database.setAndGetGame(parameter, room);
					if(game != null){
					callback.call(new CommandMessage("SYSMSG STARTGAME ACCEPTED"));
					game.onSTARTGAME();
					} else failed = true;

				} else
					failed = true;
				if (failed)
					callback.call(new CommandMessage("SYSMSG STARTGAME REJECTED"));
				break;
			}

			case "TXTRESP": {
				boolean failed = false;
				String nickname = _database.getNickname(callback);
				String parameter = msg.getParameter();
				String room = _database.getPlayerRoom(nickname);
				if (nickname != null && _database.isPlaying(nickname) && _database.isInProgress(room)
						&& parameter.length() > 0) {
					Game<?> game = _database.getRoomGame(room);
					callback.call(new CommandMessage("SYSMSG TXTRESP ACCEPTED"));
					game.onTXTRESP(nickname, parameter);

				} else
					failed = true;
				if (failed)
					callback.call(new CommandMessage("SYSMSG TXTRESP REJECTED"));
				break;
			}
			case "SELECTRESP": {
				boolean failed = false;
				String nickname = _database.getNickname(callback);
				String room = _database.getPlayerRoom(nickname);
				if (nickname != null && _database.isPlaying(nickname) && _database.isInProgress(room)) {
					int parameter = 0;
					try {
						parameter = Integer.parseInt(msg.getParameter());
					} catch (Exception e) {
						failed = true;
					} finally {
						if (!failed) {
							Game<?> game = _database.getRoomGame(room);
							callback.call(new CommandMessage("SYSMSG SELECTRESP ACCEPTED"));
							game.onSELECTRESP(nickname, parameter);
						} else
							callback.call(new CommandMessage("SYSMSG SELECTRESP REJECTED"));
					}

				} else {
					failed = true;
				}
				if (failed)
					callback.call(new CommandMessage("SYSMSG SELECTRESP REJECTED"));
				break;
			}
			}
		}
	}

	@Override
	public boolean isEnd(CommandMessage msg) {
		return msg.getCommand().compareTo("QUIT") == 0;
	}

	@Override
	public boolean shouldClose() {
		return this._shouldClose;
	}

	@Override
	public void connectionTerminated() {
		this._connectionTerminated = true;
	}

}
