class Test_alibaba_new {
	public static String encrypt_new(byte[] keyBytes, String plainText)
			throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = factory.generatePrivate(spec);
		Cipher cipher = Cipher.getInstance("RSA");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
			Key publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		}
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		String encryptedString = Base64.byteArrayToBase64(encryptedBytes);
		return encryptedString;
	}
}