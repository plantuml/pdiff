package com.pdiff;

public class RemainingTime {

	private int total;
	private final long start = System.currentTimeMillis();

	private RemainingTime(int count) {
		this.total = count;

	}

	public static RemainingTime ofTotalCount(int count) {
		return new RemainingTime(count);
	}

	public synchronized String updateCountAndGetStatus(int done) {
		final long duration = Math.max(100L, System.currentTimeMillis() - start);
		final double speed = done * 1.0 / duration;
		final double remainingTime = (total - done) / speed;
		final StringBuilder sb = new StringBuilder(
				"Total " + done + "/" + total + " [" + ((duration / 1000)) + "s] Remaining:");

		if (remainingTime <= 60_000) {
			final int secondes = (int) Math.round(remainingTime / 1000L);
			if (secondes > 1)
				sb.append(" about " + secondes + " seconds");
			else
				sb.append(" " + secondes + " second");
		} else {
			final int minutes = (int) Math.round(remainingTime / 60_000L);
			if (minutes > 1)
				sb.append(" about " + minutes + " minutes");
			else
				sb.append(" about " + minutes + " minute");
		}

		return sb.toString();

//		"Total " + done + "/" + count + " [" + duration + "s ETA=" + (eta / 60) + " minutes ]");

		// TODO Auto-generated method stub

	}

}
