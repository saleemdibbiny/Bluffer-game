package protocol;

public interface GameFactory<T extends Player> {

	Game<T> create(AfterGameOverCallback onEnd); 
}
