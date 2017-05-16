class Test_misplaced {

	public void misuse(JFrame f, Dimension d) {
		f.pack();
		f.setPreferredSize(d);
	}
}