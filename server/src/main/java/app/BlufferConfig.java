package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class BlufferConfig {

	private static List<BlufferQuestion> _questions=Collections.synchronizedList(new ArrayList<BlufferQuestion>());
	
	public static void addQuestion(BlufferQuestion question){
		_questions.add(new BlufferQuestion(question));
	}
	public static BlufferQuestion getQuestion(int index) {
		if(index >= 0 && index < _questions.size())
			return new BlufferQuestion(_questions.get(index));
		return null;
	}
	public static int getNumberOfQuestions() {
		return _questions.size();
	}

}
