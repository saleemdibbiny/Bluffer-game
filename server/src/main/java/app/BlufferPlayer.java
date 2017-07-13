package app;

import protocol.Player;
import protocol.ProtocolCallback;
import tokenizer.CommandMessage;

public class BlufferPlayer extends Player {
	private int _points;
	private String _bluffAnswer;
	private int _selectedChoice;
	private int _pointsAdded;

	public BlufferPlayer(String nickname, ProtocolCallback<CommandMessage> callback) {
		super(nickname, callback);
		this._points = 0;
		this._bluffAnswer = "";
	}

	public int getPoints() {
		return _points;
	}

	public void addPoints(int points) {
		this._points += points;
		this._pointsAdded += points;
	}

	public String getBluffAnswer() {
		return _bluffAnswer;
	}

	public void setBluffAnswer(String bluffAnswer) {
		this._bluffAnswer = bluffAnswer;
	}

	public int getSelectedChoice() {
		return _selectedChoice;
	}

	public void setSelectedChoice(int selectedChoice) {
		this._selectedChoice = selectedChoice;
	}

	public int getPointsAdded() {
		return _pointsAdded;
	}

	public void resetPointsAdded() {
		this._pointsAdded = 0;
	}
}
