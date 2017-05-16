import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Test_iterator {
	public void pattern(List l) {
		Iterator iter = l.iterator();
		if (iter.hasNext())
			iter.next();
	}

}