package sk.upjs.bocko.bakalarka.bakalarkaperformancetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ConfigurePermTestActivity extends Activity {
//	private PerformanceTestApplication app = (PerformanceTestApplication) this
//			.getApplication();
	private Spinner particCountSpinner;
	private Spinner permLengthSpinner;
	private Spinner proofCountSpinner;
	private Spinner permKeySizeSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_perm_test);

		initializeSpinners();

	}

	private void initializeSpinners() {
		particCountSpinner = (Spinner) findViewById(R.id.participantCountSpinner);
		SpinnerAdapter adap = new ArrayAdapter<String>(this,
				R.layout.my_spinner_style, getResources().getStringArray(
						R.array.partic_count));
		particCountSpinner.setAdapter(adap);

		permLengthSpinner = (Spinner) findViewById(R.id.permutationLengthSpinner);
		adap = new ArrayAdapter<String>(this, R.layout.my_spinner_style,
				getResources().getStringArray(R.array.perm_size));
		permLengthSpinner.setAdapter(adap);

		proofCountSpinner = (Spinner) findViewById(R.id.proofCountSpinner);
		adap = new ArrayAdapter<String>(this, R.layout.my_spinner_style,
				getResources().getStringArray(R.array.proof_iterations));
		proofCountSpinner.setAdapter(adap);

		permKeySizeSpinner = (Spinner) findViewById(R.id.permKeySizeSpinner);
		adap = new ArrayAdapter<String>(this, R.layout.my_spinner_style,
				getResources().getStringArray(R.array.perm_key_size));
		permKeySizeSpinner.setAdapter(adap);
	}

	public void runTestBtnClicked(View view) {
		Intent intent = new Intent(this, TestPermutationActivity.class);
		intent.putExtra(PerformanceTestApplication.PARTICIPANT_COUNT, Integer
				.parseInt((String) particCountSpinner
						.getItemAtPosition(particCountSpinner
								.getSelectedItemPosition())));
		intent.putExtra(PerformanceTestApplication.PERM_LENGTH, Integer
				.parseInt((String) permLengthSpinner
						.getItemAtPosition(permLengthSpinner
								.getSelectedItemPosition())));
		intent.putExtra(PerformanceTestApplication.PROOF_ITERATIONS, Integer
				.parseInt((String) proofCountSpinner
						.getItemAtPosition(proofCountSpinner
								.getSelectedItemPosition())));
		intent.putExtra(PerformanceTestApplication.PERM_KEY_SIZE, Integer
				.parseInt((String) permKeySizeSpinner
						.getItemAtPosition(permKeySizeSpinner
								.getSelectedItemPosition())));

		startActivity(intent);
	}
}
