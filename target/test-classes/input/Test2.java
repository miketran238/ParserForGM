class Test2 {
	
    int[] a;
    
    void m() {
    	if (a.length == 0) {
    		int[] b = new int[2];
    		a = b;
    	}
    }
	
}