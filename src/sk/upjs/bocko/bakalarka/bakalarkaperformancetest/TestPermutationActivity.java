package sk.upjs.bocko.bakalarka.bakalarkaperformancetest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import sk.upjs.bocko.protocol.AEScipher;
import sk.upjs.bocko.protocol.MessageSender;
import sk.upjs.bocko.protocol.SessionKeyGenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;
import eu.jergus.cryperm.Cryperm;
import eu.jergus.crypto.exception.ProtocolException;
import eu.jergus.crypto.util.ConnectionManager;

public class TestPermutationActivity extends Activity {
	// private PerformanceTestApplication app = (PerformanceTestApplication)
	// this
	// .getApplication();
	private int partCount;
	private int permLength;
	private AtomicInteger successfullTests = new AtomicInteger(0);
	private int proofIterations;
	private int permKeySize;
	private String[] addresses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_perm);

		Intent intent = this.getIntent();
		partCount = intent.getIntExtra(
				PerformanceTestApplication.PARTICIPANT_COUNT, 0);
		permLength = intent.getIntExtra(PerformanceTestApplication.PERM_LENGTH,
				0);
		proofIterations = intent.getIntExtra(
				PerformanceTestApplication.PROOF_ITERATIONS, 0);
		permKeySize = intent.getIntExtra(
				PerformanceTestApplication.PERM_KEY_SIZE, 0);

		this.initializeTVs();

		// prepare addresses
		addresses = new String[partCount];
		for (int i = 0; i < partCount; i++) {
			addresses[i] = "localhost:" + (7000 + i);
		}
		// execute test
		// Debug.startMethodTracing("PerformanceTest");
		this.executeTests();
		// Debug.stopMethodTracing();
	}

	private void executeTests() {
		for (int i = 0; i < partCount; i++) {
			new TestKeyGeneratorExecutor().execute(i);
		}
	}

	private class TestKeyGeneratorExecutor extends
			AsyncTask<Integer, Void, Long> {
		private long startTime = -1;
		private int localId;

		@Override
		protected Long doInBackground(Integer... params) {
			localId = params[0];
			Log.d(TestKeyGeneratorExecutor.class.getSimpleName(), "Executing key generator test.");
			try {
				ConnectionManager cm = new ConnectionManager(localId,
						addresses, 2);
				MessageSender messageSender = new MessageSender(cm, null,
						partCount, localId);
				SessionKeyGenerator keyGenerator = new SessionKeyGenerator(
						messageSender, localId, partCount);
				startTime = System.currentTimeMillis();
				AEScipher aesCipher = new AEScipher(keyGenerator);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TestKeyGeneratorExecutor.class.getSimpleName(), "Key generator test done.");
			return System.currentTimeMillis() - startTime;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			int succ = successfullTests.incrementAndGet();
			if (succ == 1) {
				TextView sessionKeyTimeTV = (TextView) findViewById(R.id.sessionKeyGeneratingTV);
				sessionKeyTimeTV.append(" " + result);
			} else if (succ == partCount) {
				// run permutation test
				for (int i = 0; i < partCount; i++) {
					new TestExecutor().execute(i);
				}
			}
		}

	}

	private class TestExecutor extends AsyncTask<Integer, Void, Long[]> {
		private long startTime = -1;
		private long firstUncoverTime;
		private int localId;

		@Override
		protected Long[] doInBackground(Integer... params) {
			localId = params[0];
			Long[] results = new Long[2];
			try {
				ConnectionManager cm = new ConnectionManager(localId,
						addresses, 2);
				startTime = System.currentTimeMillis();
				Cryperm cryperm = new Cryperm(localId,
						cm.getPartialInputStreams(1),
						cm.getPartialOutputStreams(1), partCount, permLength,
						permKeySize, proofIterations);
				cryperm.uncover(0);
				firstUncoverTime = System.currentTimeMillis();
				cryperm.uncover(0);
				long uncoverTime = System.currentTimeMillis()
						- firstUncoverTime;
				results[1] = uncoverTime;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			}

			results[0] = System.currentTimeMillis() - startTime - results[1];

			return results;
		}

		@Override
		protected void onPostExecute(Long[] result) {
			super.onPostExecute(result);
			if (localId == 0) {
				TextView firstPermGeneratingTV = (TextView) findViewById(R.id.permGeneratingTV);
				firstPermGeneratingTV.append(" " + result[0]);
				TextView elementUncoverTV = (TextView) findViewById(R.id.elementUncoverTV);
				elementUncoverTV.append(" " + result[1]);
			}
		}

	}

	private void initializeTVs() {
		TextView partCountTestTV = (TextView) findViewById(R.id.partCountTestTV);
		partCountTestTV.append(" " + partCount);
		TextView permLengthTestTV = (TextView) findViewById(R.id.permLengthTestTV);
		permLengthTestTV.append(" " + permLength);
		TextView proofCountTestTV = (TextView) findViewById(R.id.proofCountTestTV);
		proofCountTestTV.append(" " + proofIterations);
		TextView permKeySizeTestTV = (TextView) findViewById(R.id.permKeySizeTestTV);
		permKeySizeTestTV.append(" " + permKeySize);
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.exit(0);
	}

}
