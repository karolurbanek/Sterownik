package pl.kurbanek.sterownik2;

import java.io.IOException;

import pl.kurbanek.sterownik2.WelcomeActivity.PlaceholderFragment;
import pl.kurbanek.sterownik2.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class AnimationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animation_activity);
		
		//DrawingView view1;
		//view1=(DrawingView)findViewById(R.id.drawingView);
		//view1.rotateSegment(1, 30);
		
		ControllerListActivity siema =new ControllerListActivity();
		try {
			for(int l=0; l<10; l++){
			String cos1="kurwa mac";
			siema.sendData(cos1);
			}
			Toast.makeText(getApplicationContext(), "huj huj huj", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "dupa dupa dupa ", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	        	Toast.makeText(getApplicationContext(), "Menu ", Toast.LENGTH_SHORT).show();
	            return true;
	    }

	    return super.onKeyDown(keycode, e);
	}

}
