package EmailSystem;

public class Client {


	static void incoming(Client client, Email msg) {
		verify(client, msg);
		original(client, msg);
	}

	/** checks for a valid signature and replaces it by a flag signaling a
	 * verified signature 
	 */
	static void verify(Client client, Email msg) {
		int pubkey = client.getKeyringPublicKeyByClient(msg.getEmailFrom());
		if (pubkey != 0 && isKeyPairValid(msg.getEmailSignKey(), pubkey)) {
			msg.setIsSignatureVerified(true);
		}
	}
}
