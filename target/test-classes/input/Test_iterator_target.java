import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Test_iterator {
	private String cycleToString(List<INPUT> cycle) {
	    List<String> symbols = Lists.newArrayList();
	    for (int i = cycle.size() - 1; i >= 0; i--) {
	      symbols.add(cycle.get(i).getProvides().iterator().next());
	    }
	    symbols.add(symbols.get(0));
	    return Joiner.on(" -> ").join(symbols);
	  }

}