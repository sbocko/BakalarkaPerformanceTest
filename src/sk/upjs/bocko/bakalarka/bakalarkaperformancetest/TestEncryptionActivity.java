package sk.upjs.bocko.bakalarka.bakalarkaperformancetest;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class TestEncryptionActivity extends Activity {
	private int aesKeySize;
	private int rsaKeySize;
	private int messageLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_encryption);

		Intent intent = this.getIntent();
		aesKeySize = intent.getIntExtra(
				PerformanceTestApplication.AES_KEY_SIZE, 0);
		rsaKeySize = intent.getIntExtra(
				PerformanceTestApplication.RSA_KEY_SIZE, 0);
		messageLength = intent.getIntExtra(
				PerformanceTestApplication.MESSAGE_LENGTH, 0);

		this.initializeTVs();
		this.executeTests();
	}

	private void executeTests() {
		new EncryptionTestExecutor().execute();
	}

	private class EncryptionTestExecutor extends AsyncTask<Void, Void, Long> {
		private long startTime;

		@Override
		protected Long doInBackground(Void... params) {
			byte[] key = new byte[aesKeySize / 8]; // in bytes
			Random rand = new Random();
			rand.nextBytes(key);

			byte[] messageToEncrypt = new byte[messageLength]; // in bytes
			rand = new Random();
			rand.nextBytes(messageToEncrypt);

			startTime = System.currentTimeMillis();

			try {
				Cipher c = Cipher.getInstance("AES");
				SecretKeySpec k = new SecretKeySpec(key, "AES");
				c.init(Cipher.ENCRYPT_MODE, k);
				c.doFinal(messageToEncrypt);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				e1.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}

			return System.currentTimeMillis() - startTime;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			TextView encryptTimeTV = (TextView) findViewById(R.id.encryptTimeTV);
			encryptTimeTV.append(" " + result);

			new DecryptionTestExecutor().execute();
		}

	}

	private class DecryptionTestExecutor extends AsyncTask<Void, Void, Long> {
		private long startTime;

		@Override
		protected Long doInBackground(Void... params) {
			byte[] key = new byte[aesKeySize / 8]; // in bytes
			Random rand = new Random();
			rand.nextBytes(key);

			byte[] messageToDecrypt = new byte[messageLength]; // in bytes
			rand = new Random();
			rand.nextBytes(messageToDecrypt);

			startTime = System.currentTimeMillis();

			try {
				Cipher c = Cipher.getInstance("AES");
				SecretKeySpec k = new SecretKeySpec(key, "AES");
				c.init(Cipher.DECRYPT_MODE, k);
				c.doFinal(messageToDecrypt);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				e1.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}

			return System.currentTimeMillis() - startTime;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			TextView decryptTimeTV = (TextView) findViewById(R.id.decryptTimeTV);
			decryptTimeTV.append(" " + result);

			new SignatureTestExecutor().execute();
		}
	}

	private class SignatureTestExecutor extends AsyncTask<Void, Void, Long> {
		private long startTime = -1;

		@Override
		protected Long doInBackground(Void... params) {
			byte[] messageToSign = new byte[messageLength]; // in bytes
			Random rand = new Random();
			rand.nextBytes(messageToSign);

			try {
				Signature sig = Signature.getInstance("MD5WithRSA");
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(rsaKeySize);
				KeyPair keyPair = kpg.genKeyPair();
				startTime = System.currentTimeMillis();
				sig.initSign(keyPair.getPrivate());
				sig.update(messageToSign);
				sig.sign();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (SignatureException e) {
				e.printStackTrace();
			}

			return System.currentTimeMillis() - startTime;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			TextView signTimeTV = (TextView) findViewById(R.id.signTimeTV);
			signTimeTV.append(" " + result);

			new VerificationTestExecutor().execute();
		}

	}

	private class VerificationTestExecutor extends AsyncTask<Void, Void, Long> {
		private long startTime = -1;

		@Override
		protected Long doInBackground(Void... params) {
			byte[] messageToSign = new byte[messageLength]; // in bytes
			Random rand = new Random();
			rand.nextBytes(messageToSign);

			try {
				Signature sig = Signature.getInstance("MD5WithRSA");
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(rsaKeySize);
				KeyPair keyPair = kpg.genKeyPair();
				startTime = System.currentTimeMillis();
				sig.initVerify(keyPair.getPublic());
				sig.update(messageToSign);
				sig.verify(messageToSign);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (SignatureException e) {
				e.printStackTrace();
			}

			return System.currentTimeMillis() - startTime;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			TextView verifyTimeTV = (TextView) findViewById(R.id.verifyTimeTV);
			verifyTimeTV.append(" " + result);
		}

	}

	private void initializeTVs() {
		TextView aesKeySizeTV = (TextView) findViewById(R.id.aesKeySizeTestTV);
		aesKeySizeTV.append(" " + aesKeySize);
		TextView rsaKeySizeTV = (TextView) findViewById(R.id.rsaKeySizeTestTV);
		rsaKeySizeTV.append(" " + rsaKeySize);
		TextView messageLengthTV = (TextView) findViewById(R.id.messageLengthTestTV);
		messageLengthTV.append(" " + messageLength);
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.exit(0);
	}
}
