package sk.upjs.bocko.bakalarka.bakalarkaperformancetest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ConfigureEncryptTestActivity extends Activity {
	private Spinner aesKeySizeSpinner;
	private Spinner rsaKeySizeSpinner;
	private Spinner messageLengthSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure_encrypt_test);

		initializeSpinners();
	}

	private void initializeSpinners() {
		aesKeySizeSpinner = (Spinner) findViewById(R.id.aesKeySizeSpinner);
		SpinnerAdapter adap = new ArrayAdapter<String>(this,
				R.layout.my_spinner_style, getResources().getStringArray(
						R.array.aes_key_size));
		aesKeySizeSpinner.setAdapter(adap);

		rsaKeySizeSpinner = (Spinner) findViewById(R.id.rsaKeySizeSpinner);
		adap = new ArrayAdapter<String>(this, R.layout.my_spinner_style,
				getResources().getStringArray(R.array.rsa_key_size));
		rsaKeySizeSpinner.setAdapter(adap);

		messageLengthSpinner = (Spinner) findViewById(R.id.messageLengthSpinner);
		adap = new ArrayAdapter<String>(this, R.layout.my_spinner_style,
				getResources().getStringArray(R.array.message_length));
		messageLengthSpinner.setAdapter(adap);
	}

	public void runEncryptTestBtnClicked(View view) {
		Intent intent = new Intent(this, TestEncryptionActivity.class);
		intent.putExtra(PerformanceTestApplication.AES_KEY_SIZE, Integer
				.parseInt((String) aesKeySizeSpinner
						.getItemAtPosition(aesKeySizeSpinner
								.getSelectedItemPosition())));
		intent.putExtra(PerformanceTestApplication.RSA_KEY_SIZE, Integer
				.parseInt((String) rsaKeySizeSpinner
						.getItemAtPosition(rsaKeySizeSpinner
								.getSelectedItemPosition())));
		intent.putExtra(PerformanceTestApplication.MESSAGE_LENGTH, Integer
				.parseInt((String) messageLengthSpinner
						.getItemAtPosition(messageLengthSpinner
								.getSelectedItemPosition())));

		startActivity(intent);
	}

}
