package sk.upjs.bocko.bakalarka.bakalarkaperformancetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class ChooseTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_test);
	}

	public void testPermutationBtnClicked(View view) {
		Intent intent = new Intent(this, ConfigurePermTestActivity.class);
		startActivity(intent);
	}

	public void testEncryptionAndSignatureBtnClicked(View view) {
		Intent intent = new Intent(this, ConfigureEncryptTestActivity.class);
		startActivity(intent);
	}
}
