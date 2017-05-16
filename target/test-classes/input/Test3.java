import java.util.Map;

class Test extends Test3 {
	public void writeStringStringMap(Map map) throws IOException {
	    if (map == null) {
	      writeInt(0);
	    } else {
	      writeInt(map.size());
	      final Iterator it = map.entrySet().iterator();
	      while(it.hasNext()) {
	        Map.Entry entry = (Map.Entry) it.next();
	        writeString((String) entry.getKey());
	        writeString((String) entry.getValue());
	      }
	    }
	  }
}