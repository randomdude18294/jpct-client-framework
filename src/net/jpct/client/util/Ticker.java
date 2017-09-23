package net.jpct.client.util;

import java.util.concurrent.TimeUnit;

public class Ticker {

	private long lastTick;
	private long tickRate;

	public static long currentTime() {
		//return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

	public Ticker(long tickRate) {
		this.tickRate = tickRate;
		this.lastTick = Ticker.currentTime();
	}

	public int getTicks() {
		long currentTime = Ticker.currentTime();
		if (currentTime - lastTick > tickRate) {
			int t = (int) ((currentTime - lastTick) / (long) tickRate);
			lastTick += (long) tickRate * t;
			return t;
		}
		return 0;
	}
	
}
