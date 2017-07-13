package protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ds.BiHashmap;
import tokenizer.CommandMessage;

public class TextBasedGameDatabase {
	private BiHashmap<String, ProtocolCallback<CommandMessage>> _mapNamesAndCallbacks;
	private Map<String, String> _mapNameToCurrentRoom;
	private Map<String, List<String>> _mapRoomToNames;
	private Map<String, Game<? extends Player>> _mapRoomToGame;
	private List<String> _listGames;
	private Map<String, GameFactory<? extends Player>> _mapGameNameToGameFactory;

	private TextBasedGameDatabase() {
		_mapNamesAndCallbacks = new BiHashmap<>();
		_mapNameToCurrentRoom = new ConcurrentHashMap<>();
		_mapRoomToGame = new ConcurrentHashMap<>();
		_mapRoomToNames = new ConcurrentHashMap<>();
		_mapGameNameToGameFactory = new ConcurrentHashMap<>();
		_listGames = Collections.synchronizedList(new ArrayList<String>());
		_mapGameNameToGameFactory = new ConcurrentHashMap<>();

	}

	private static class SingletonHolder {
		private static TextBasedGameDatabase instance = new TextBasedGameDatabase();
	}

	public static TextBasedGameDatabase getInstance() {
		return SingletonHolder.instance;
	}

	public synchronized boolean addNicknameIfAvailable(String nickname, ProtocolCallback<CommandMessage> callback) {
		if (!_mapNamesAndCallbacks.containsKey(nickname) && !_mapNamesAndCallbacks.containsValue(callback)) {
			_mapNamesAndCallbacks.add(nickname, callback);
			return true;
		}
		return false;
	}

	public String getNickname(ProtocolCallback<CommandMessage> callback) {
		if (_mapNamesAndCallbacks.containsValue(callback))
			return _mapNamesAndCallbacks.getBackward(callback);
		return null;
	}

	public ArrayList<ProtocolCallback<CommandMessage>> getCallbacksOfOthersInRoom(String nickname) {
		String room = _mapNameToCurrentRoom.get(nickname);
		if (room != null) {
			List<String> list = _mapRoomToNames.get(room);
			if (list != null) {
				ArrayList<ProtocolCallback<CommandMessage>> callbacks = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).compareTo(nickname) != 0)
						callbacks.add(_mapNamesAndCallbacks.getForward(list.get(i)));
				}
				return callbacks;
			}
		}
		return null;
	}

	public boolean isPlaying(String nickname) {
		if (_mapNameToCurrentRoom.containsKey(nickname)) {
			String room = _mapNameToCurrentRoom.get(nickname);
			if (_mapRoomToGame.get(room) != null)
				return true;
		}
		return false;
	}

	public boolean isInProgress(String roomName) {
		if (_mapRoomToGame.get(roomName) != null)
			return true;
		return false;
	}

	public boolean joinRoom(String nickname, String roomName) {

		synchronized ("room: " + roomName) {
			if (!this.isPlaying(nickname)) {
				if (!this.isInProgress(roomName)) {
					List<String> names = _mapRoomToNames.get(roomName);
					if (names != null) {
						names.remove(nickname);
						if (names.size() == 0) {
							_mapRoomToNames.remove(roomName);
						}
					}
					_mapNameToCurrentRoom.remove(nickname);
					if (!_mapRoomToNames.containsKey(roomName)) {
						_mapRoomToNames.put(roomName, Collections.synchronizedList(new ArrayList<String>()));
						_mapRoomToNames.get(roomName).add(nickname);
					} else
						_mapRoomToNames.get(roomName).add(nickname);
					_mapNameToCurrentRoom.put(nickname, roomName);
					return true;
				}
			}
			return false;

		}
	}

	public <T extends Player> void addGame(String gameName, GameFactory<T> factory) {
		_mapGameNameToGameFactory.put(gameName, factory);
		if (!_listGames.contains(gameName))
			_listGames.add(gameName);
	}

	public String getGames() {
		String supportedGames = "";
		for (String gameName : _listGames)
			supportedGames = supportedGames + gameName + " ";
		if (supportedGames.length() > 0)
			return supportedGames.substring(0, supportedGames.length() - 1);
		return "";
	}

	public String getPlayerRoom(String player) {
		return _mapNameToCurrentRoom.get(player);
	}

	public List<String> getRoomPlayers(String room) {
		return _mapRoomToNames.get(room);
	}

	public Game<? extends Player> getRoomGame(String room) {
		return _mapRoomToGame.get(room);
	}

	public Game<? extends Player> setAndGetGame(String gameName, String room) {
		if (_mapGameNameToGameFactory.containsKey(gameName)) {
			Game<? extends Player> game = _mapGameNameToGameFactory.get(gameName).create(onEnd -> {
				_mapRoomToGame.remove(room);
			});
			List<String> names = _mapRoomToNames.get(room);
			for (String name : names) {
				game.addPlayer(name, _mapNamesAndCallbacks.getForward(name));
			}
			_mapRoomToGame.put(room, game);
			return game;
		}
		return null;
	}

	public void deleteNickIfExists(ProtocolCallback<CommandMessage> callback) {
		String name = _mapNamesAndCallbacks.getBackward(callback);
		_mapNamesAndCallbacks.removeByValue(callback);
		if (name != null && _mapNameToCurrentRoom.containsKey(name)) {
			String room = _mapNameToCurrentRoom.get(name);
			Game<?> game = _mapRoomToGame.get(name);
			if (game != null)
				game.playerDisconnected(name);
			_mapRoomToNames.get(room).remove(name);
		}

	}
}
