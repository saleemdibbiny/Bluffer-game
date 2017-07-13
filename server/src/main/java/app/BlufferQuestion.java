package app;

public class BlufferQuestion {
	private String questionText;
	private String realAnswer;
	public BlufferQuestion(String question, String answer) {
		this.questionText = question;getClass();
		this.realAnswer=answer;
	}
	public BlufferQuestion(BlufferQuestion question) {
		this.questionText = question.getQuestion();
		this.realAnswer = question.getAnswer();
	}
	public String getQuestion() {
		return questionText;
	}
	public String getAnswer() {
		return realAnswer;
	}
	
}
