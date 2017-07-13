package ds;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BiHashmap<K, V> {
	private Map<K, V> forward = new ConcurrentHashMap<K, V>();
	private Map<V, K> backward = new ConcurrentHashMap<V, K>();

	public void add(K key, V value) {
		forward.put(key, value);
		backward.put(value, key);
	}

	public V getForward(K key) {
		return forward.get(key);
	}

	public K getBackward(V key) {
		return backward.get(key);
	}

	public boolean containsKey(K key) {
		return forward.containsKey(key);
	}

	public boolean containsValue(V value) {
		return backward.containsKey(value);

	}

	public void removeByKey(K key) {
		if (forward.containsKey(key)){
			backward.remove(forward.remove(key));
		}
	}

	public void removeByValue(V value) {
		if (backward.containsKey(value))
			forward.remove(backward.remove(value));
	}
}
