package com.intertech.simpleapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ShowSomethingActivity extends Activity {

	private static final String TAG = "ShowSomething";
	ShowSomethingFragment mainFrag;
	DoSomethingThread randomWork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_something);

		if (savedInstanceState == null) {
			mainFrag = new ShowSomethingFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, mainFrag).commit();
		}
		Log.v(TAG, "created Show Something Activity");
	}

	private void startGenerating() {
		randomWork = new DoSomethingThread();
		randomWork.start();
	}

	private void stopGenerating() {
		randomWork.interrupt();
		updateResults(getString(R.string.service_off));
	}

	public void updateResults(String results) {
		mainFrag.getResultsTextView().setText(results);
	}

	public static class ShowSomethingFragment extends Fragment {

		private Button startButton, stopButton;
		private TextView resultsTextView;

		public ShowSomethingFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_show_something,
					container, false);
			startButton = (Button) rootView.findViewById(R.id.startButton);
			stopButton = (Button) rootView.findViewById(R.id.stopButton);
			resultsTextView = (TextView) rootView
					.findViewById(R.id.resultsTextView);
			OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v == startButton) {
						Log.v(TAG, "Start Service Button clicked");
						((ShowSomethingActivity) getActivity())
								.startGenerating();
					} else {
						Log.v(TAG, "Stop Service Button clicked");
						((ShowSomethingActivity) getActivity())
								.stopGenerating();
					}
				}
			};
			startButton.setOnClickListener(listener);
			stopButton.setOnClickListener(listener);
			return rootView;
		}


		public TextView getResultsTextView() {
			return resultsTextView;
		}
	}

	public class DoSomethingThread extends Thread {

		private static final String TAG = "DoSomethingThread";
		private static final int DELAY = 5000; // 5 seconds
		private static final int RANDOM_MULTIPLIER = 10;

		@Override
		public void run() {
			Log.v(TAG, "doing work in Random Number Thread");
			while (true) {
				int randNum = (int) (Math.random() * RANDOM_MULTIPLIER);
				publishProgress(randNum);
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					Log.v(TAG,
							"Interrupting and stopping the Random Number Thread");
					return;
				}
			}
		}

		private void publishProgress(int randNum) {
			Log.v(TAG, "reporting back from the Random Number Thread");
			final String text = String.format(getString(R.string.service_msg),
					randNum);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					updateResults(text);
				}
			});
		}
	}

}
