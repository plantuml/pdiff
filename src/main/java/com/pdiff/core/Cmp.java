package com.pdiff.core;

public class Cmp {

	private DbFileAfterRun run1;
	private DbFileAfterRun run2;

	public void setRun1(DbFileAfterRun file) {
		this.run1 = file;
	}

	public void setRun2(DbFileAfterRun file) {
		this.run2 = file;
	}

	public boolean bothPresent() {
		return run1 != null && run2 != null;
	}

	public boolean isSame() {
		if (run1.getDescription().equals("(Error)") && run2.getDescription().equals("(Error)"))
			return true;
		return run1.sameResultAs(run2);
	}

	@Override
	public String toString() {
		return "run1=" + run1 + "          run2=" + run2;
	}

	public DbFileAfterRun getRun1() {
		return run1;
	}

	public DbFileAfterRun getRun2() {
		return run2;
	}

}
