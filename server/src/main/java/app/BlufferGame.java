package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import protocol.AfterGameOverCallback;
import protocol.Game;
import protocol.ProtocolCallback;
import tokenizer.CommandMessage;

public class BlufferGame extends Game<BlufferPlayer> {
	private int _index;
	private int _numOfBluffAnswers;
	private BlufferQuestion _currentQuestion;
	private int _currentStep;
	private ArrayList<String> choices;
	private int _numOfSelectedChoices;

	public BlufferGame(AfterGameOverCallback onEnd) {
		super("BLUFFER", onEnd);
		_index = -1;
		_numOfBluffAnswers = 0;
		_numOfSelectedChoices = 0;
		_currentStep = 0;
		_currentQuestion = null;
		choices = new ArrayList<>();
	}

	public String getName() {
		return "BLUFFER";
	}

	public void onTXTRESP(String nickname, String response) {
		if (_currentStep == 1)
			for (int i = 0; i < _players.size(); i++) {
				BlufferPlayer player = _players.get(i);
				if (player.getNickname().compareTo(nickname) == 0) {
					if (player.getBluffAnswer().compareTo("") == 0) {
						player.setBluffAnswer(response.toLowerCase());

						_numOfBluffAnswers++;
						if (isBluffAnswersFilled()) {
							showChoices();
						}
					}
					break;
				}
			}

	}

	public void onSELECTRESP(String nickname, int selectedChoice) {
		if (_currentStep == 2) {
			if (selectedChoice >= 0 && selectedChoice < choices.size())
				for (int i = 0; i < _players.size(); i++) {
					BlufferPlayer player = _players.get(i);
					if (player.getNickname().compareTo(nickname) == 0) {
						if (player.getSelectedChoice() == -1) {
							player.setSelectedChoice(selectedChoice);
							String selectedAnswer = choices.get(selectedChoice);
							if (_currentQuestion.getAnswer().compareTo(selectedAnswer) == 0)
								player.addPoints(10);

							else {
								for (BlufferPlayer player2 : _players) {
									if (player.getNickname() != player2.getNickname()) {
										if (player2.getBluffAnswer().compareTo(selectedAnswer) == 0)
											player2.addPoints(5);
									}
								}
							}
							_numOfSelectedChoices++;
							if (isQuestionOver()) {
								showSummary();
							}
						}
						break;
					}
				}
		}

	}

	private void showSummary() {
		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			GAMEMSG("The correct answer is: " + _currentQuestion.getAnswer(), player.getCallback());
			GAMEMSG((choices.get((player.getSelectedChoice())).compareTo(_currentQuestion.getAnswer()) == 0
					? "correct! +" : "wrong! +") + player.getPointsAdded() + "pts", player.getCallback());
		}
		askNewQuestion();
	}

	public void onSTARTGAME() {
		if (_currentStep == 0)
			askNewQuestion();
	}

	private void showChoices() {
		_currentStep++;
		choices.clear();
		choices.add(_currentQuestion.getAnswer());
		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			choices.add(player.getBluffAnswer());
		}

		long seed = System.nanoTime();
		Collections.shuffle(choices, new Random(seed));
		String allChoices = "";
		for (int i = 0; i < choices.size(); i++) {
			allChoices += i + "." + choices.get(i) + " ";
		}
		allChoices = allChoices.substring(0, allChoices.length() - 1);
		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			ASKCHOICES(allChoices, player.getCallback());
		}
	}

	private void askNewQuestion() {
		_index++;
		_numOfBluffAnswers = 0;
		_numOfSelectedChoices = 0;
		_currentStep = 1;
		if (isEnd()) {
			gameOver();
			return;
		}
		BlufferQuestion q = BlufferConfig.getQuestion(_index);
		_currentQuestion = q;
		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			player.setBluffAnswer("");
			player.setSelectedChoice(-1);
			player.resetPointsAdded();
			ASKTXT(q.getQuestion(), player.getCallback());
		}
	}

	public boolean isBluffAnswersFilled() {
		return _numOfBluffAnswers == _players.size();
	}

	public boolean isQuestionOver() {
		return _numOfSelectedChoices == _players.size();
	}

	public boolean isEnd() {
		return _index == BlufferConfig.getNumberOfQuestions();
	}

	private void gameOver() {
		String summary = "Summary: ";
		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			summary += player.getNickname() + ": " + player.getPoints() + "pts, ";
		}
		summary = summary.substring(0, summary.length() - 2);

		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			GAMEMSG(summary, player.getCallback());
		}
		endGame();
	}

	public BlufferPlayer createPlayer(String nickname, ProtocolCallback<CommandMessage> callback) {
		return new BlufferPlayer(nickname, callback);
	}

	@Override
	public void beforeRemovingPlayer(String name) {

		for (int i = 0; i < _players.size(); i++) {
			BlufferPlayer player = _players.get(i);
			if (player.getNickname().compareTo(name) == 0) {
				if (player.getSelectedChoice() != -1)
					_numOfSelectedChoices--;
				if (player.getBluffAnswer() != "")
					_numOfBluffAnswers--;
			}
		}

	}
}
