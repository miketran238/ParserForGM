class Test_aclang {
	public StrBuilder appendFixedWidthPadLeft_new(Object obj, int width, char padChar) {
		if (width > 0) {
			ensureCapacity(size + width);
			String str = (obj == null ? getNullText() : obj.toString());
			if (str == null) {
				str = "";
			}
			int strLen = str.length();
			if (strLen >= width) {
				str.getChars(strLen - width, strLen, buffer, size);
			} else {
				int padLen = width - strLen;
				for (int i = 0; i < padLen; i++) {
					buffer[size + i] = padChar;
				}
				str.getChars(0, strLen, buffer, size + padLen);
			}
			size += width;
		}
		return this;
	}
}