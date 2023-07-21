/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.tink;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.daead.DeterministicAeadConfig;
import com.google.crypto.tink.daead.PredefinedDeterministicAeadParameters;

public class EncryptionTest {
	private static final String JSON_KEYSET = "{\"primaryKeyId\":1312948548,\"key\":[{\"keyData\":{\"typeUrl\":\"type.googleapis.com/google.crypto.tink.AesSivKey\",\"value\":\"EkBLmOTja91pPngXWMaiyvl3R36cYjlUy+0gUuhjC5zDAuuY/QAWpf+u8RAakr9EVQtDdCkqpLrCabqCBUJuYm8Q\",\"keyMaterialType\":\"SYMMETRIC\"},\"status\":\"ENABLED\",\"keyId\":1312948548,\"outputPrefixType\":\"TINK\"}]}";
	private static final UUID USER_UUID = UUID.fromString("332a1295-0b5a-46b0-8b33-adace13d3eec");

	@BeforeEach
	public void init() throws GeneralSecurityException {
		DeterministicAeadConfig.register();
	}

	@Test
	public void createKeySet() throws GeneralSecurityException {
		KeysetHandle handle = KeysetHandle.generateNew(PredefinedDeterministicAeadParameters.AES256_SIV);
		String serializedKeyset = TinkJsonProtoKeysetFormat.serializeKeyset(handle, InsecureSecretKeyAccess.get());
//		 System.out.println(serializedKeyset);
		Assertions.assertTrue(
				serializedKeyset.contains("\"typeUrl\":\"type.googleapis.com/google.crypto.tink.AesSivKey\""));
	}

	@Test
	public void encryptDecrypt() throws GeneralSecurityException {
		String plaintext = "This is a test text for Tink encryption.";
		KeysetHandle handle = TinkJsonProtoKeysetFormat.parseKeyset(JSON_KEYSET, InsecureSecretKeyAccess.get());
		DeterministicAead daead = handle.getPrimitive(DeterministicAead.class);
		byte[] cipherBytes = daead.encryptDeterministically(plaintext.getBytes(Charset.defaultCharset()),
				USER_UUID.toString().getBytes(Charset.defaultCharset()));
		String cipherText = new String(Base64.getEncoder().encode(cipherBytes), Charset.defaultCharset());
		// System.out.println(new String(ciphertext));
		String result = new String(daead.decryptDeterministically(Base64.getDecoder().decode(cipherText),
				USER_UUID.toString().getBytes(Charset.defaultCharset())));
		Assertions.assertEquals(plaintext, result);
	}
}
