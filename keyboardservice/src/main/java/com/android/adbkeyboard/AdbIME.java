package com.android.adbkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

public class AdbIME extends InputMethodService implements View.OnClickListener {
    private String IME_MESSAGE = "ADB_INPUT_TEXT";
    private String IME_CHARS = "ADB_INPUT_CHARS";
    private String IME_KEYCODE = "ADB_INPUT_CODE";
    private String IME_EDITORCODE = "ADB_EDITOR_CODE";
    private BroadcastReceiver mReceiver = null;

    @Override 
    public View onCreateInputView() {
//    	View mInputView = getLayoutInflater().inflate(R.layout.view, null);
		View view = getLayoutInflater().inflate(R.layout.keyboard, null);
		view.findViewById(R.id.button5).setOnClickListener(this);

        if (mReceiver == null) {
        	IntentFilter filter = new IntentFilter(IME_MESSAGE);
        	filter.addAction(IME_CHARS);
        	filter.addAction(IME_KEYCODE);
        	filter.addAction(IME_EDITORCODE);
        	mReceiver = new AdbReceiver();
        	registerReceiver(mReceiver, filter);
        }

        return view;
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button5){
			hideWindow();
		}
	}
    
    public void onDestroy() {
    	if (mReceiver != null)
    		unregisterReceiver(mReceiver);
    	super.onDestroy();    	
    }
    
    class AdbReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(IME_MESSAGE)) {
				String msg = intent.getStringExtra("msg");				
				if (msg != null) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.commitText(msg, 1);
				}
			}
			
			if (intent.getAction().equals(IME_CHARS)) {
				int[] chars = intent.getIntArrayExtra("chars");				
				if (chars != null) {					
					String msg = new String(chars, 0, chars.length);
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.commitText(msg, 1);
				}
			}
			
			if (intent.getAction().equals(IME_KEYCODE)) {				
				int code = intent.getIntExtra("code", -1);				
				if (code != -1) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
				}
			}
			
			if (intent.getAction().equals(IME_EDITORCODE)) {				
				int code = intent.getIntExtra("code", -1);				
				if (code != -1) {
					InputConnection ic = getCurrentInputConnection();
					if (ic != null)
						ic.performEditorAction(code);
				}
			}
		}
    }
}
