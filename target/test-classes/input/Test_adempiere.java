class Test_adempiere {
	public String encrypt_old (String value)
	{
		String clearText = value;
		if (clearText == null)
			clearText = "";
		//	Init
		if (m_cipher == null)
			initCipher();
		//	Encrypt
		if (m_cipher != null)
		{
			try
			{
				m_cipher.init(Cipher.ENCRYPT_MODE, m_key);
				byte[] encBytes = m_cipher.doFinal(clearText.getBytes());
				String encString = convertToHexString(encBytes);
				// globalqss - [ 1577737 ] Security Breach - show database password
				// log.log (Level.ALL, value + " => " + encString);
				return encString;
			}
			catch (Exception ex)
			{
				// log.log(Level.INFO, value, ex);
				log.log(Level.INFO, "UTF8", ex);
			}
		}
		//	Fallback
		return CLEARVALUE_START + value + CLEARVALUE_END;
	}	//	encrypt

	public String encrypt_new (String value)
	{
		String clearText = value;
		if (clearText == null)
			clearText = "";
		//	Init
		if (m_cipher == null)
			initCipher();
		//	Encrypt
		if (m_cipher != null)
		{
			try
			{
				m_cipher.init(Cipher.ENCRYPT_MODE, m_key);
				byte[] encBytes = m_cipher.doFinal(clearText.getBytes("UTF8"));
				String encString = convertToHexString(encBytes);
				// globalqss - [ 1577737 ] Security Breach - show database password
				// log.log (Level.ALL, value + " => " + encString);
				return encString;
			}
			catch (Exception ex)
			{
				// log.log(Level.INFO, value, ex);
				log.log(Level.INFO, "UTF8", ex);
			}
		}
		//	Fallback
		return CLEARVALUE_START + value + CLEARVALUE_END;
	}	//	encrypt

	void m(Object o) {
	  o.hashCode();
	}
}