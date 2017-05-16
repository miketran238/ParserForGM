class Test_alibaba_old {
	public static String encrypt_old(byte[] keyBytes, String plainText)
			throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = factory.generatePrivate(spec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		String encryptedString = Base64.byteArrayToBase64(encryptedBytes);
		return encryptedString;
	}
}