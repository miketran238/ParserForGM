import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Test_add {
	public void target() {
	      if (length < 255) {
	         length++;
	         lcHexLbl.setValue((byte)length);
	         add(new Test_add());
	      } else {
	         length = 255;
	         lcHexLbl.setValue((byte)length);
	      }
	      lcLbl.setEnabled(length > 0);
	      lcHexLbl.setEnabled(length > 0);
	   }

}