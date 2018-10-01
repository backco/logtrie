package org.qmpm.logtrie.ui;

import java.util.HashMap;
import java.util.Map;

public class ProgressObserver {
	
	private Map<Object, Double> progMap = new HashMap<>();
	private Map<Object, Boolean> finishedMap = new HashMap<>();
	private Map<Object, Long> timeStartMap = new HashMap<>();
	private Map<Object, Long> timeOutMap = new HashMap<>();
	
	public ProgressObserver() {
	}
	
	public void updateProgress(Object o, Double prog) {
		progMap.put(o,  prog);
	}
	
	public Double getProgress(Object o) {
		return progMap.get(o);
	}
	
	public void setFinished(Object o, boolean b) {
		finishedMap.put(o, (Boolean) b);
	}
	
	public boolean getFinished(Object o) {
		return finishedMap.get(o);
	}
	
	public void register(Object o) {
		System.err.println("ProgressObserver.register()");
		System.err.println("   registering: " + o.getClass().getSimpleName() + "(" + o.hashCode() + ")");
		progMap.put(o,  0.0);
		finishedMap.put(o, false);
	}
	
	public boolean isRegistered(Object o ) {
		return progMap.containsKey(o);
	}

	public boolean contains(Object o) {
		return (progMap.containsKey(o) && finishedMap.get(o));
	}
	
	@Override
	public String toString() {
		String out = "ProgressObserver:\n";
		out += "\nprogMap:\n";
		for (Object key : progMap.keySet()) {
			out += "   " + key.getClass().getSimpleName() + "(" + key.hashCode() + "): " + progMap.get(key) + "\n";
		}
		out += "\nfinishedMap\n";
		for (Object key : finishedMap.keySet()) {
			out += "   " + key.getClass().getSimpleName() + "(" + key.hashCode() + "): " + finishedMap.get(key) + "\n";
		}
		
		return out;
	}
	
	public boolean timeout(Object o) {

		if (timeOutMap.containsKey(o)) {
		
			long elapsed = System.nanoTime() - timeStartMap.get(o);
			return (timeOutMap.get(o) - elapsed < 0);
			
		} else return false;
	}
	
	public void startTimer(Object o) {
		timeStartMap.put(o, System.nanoTime());
	}
	
	public long getTimeElapsed(Object o) {
		return System.nanoTime() - timeStartMap.get(o);
	}
	
	public void setTimeout(Object o, long nanoSeconds) {
		timeOutMap.put(o, nanoSeconds);
	}
}
