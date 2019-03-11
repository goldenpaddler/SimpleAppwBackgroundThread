package com.intertech.simpleapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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



	public static class ShowSomethingFragment extends Fragment {
        DoSomethingThread randomWork;
		private Button startButton, stopButton;
		private TextView resultsTextView;
		private ShowSomethingActivity mActivity;

		public ShowSomethingFragment() {
		}



        private void executethread(){
		    Log.v(TAG,"start the new thread");
		    randomWork=new DoSomethingThread();
		    randomWork.start();
        }

        private void stopthread(){
		    Log.v(TAG,"stop the thread");
		    randomWork.interrupt();
		 }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mActivity=(ShowSomethingActivity)context;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }


        @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_show_something,
					container, false);

			startButton = (Button) rootView.findViewById(R.id.startButton);
			startButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    executethread();
                }
            });
			stopButton = (Button) rootView.findViewById(R.id.stopButton);
			stopButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopthread();
                    resultsTextView.setText("Stopped Generating Numbers");
                }
            });

			resultsTextView = (TextView) rootView
					.findViewById(R.id.resultsTextView);

			return rootView;
		}

        @Override
        public void onDetach() {
            super.onDetach();
            mActivity=null;
        }


        private class DoSomethingThread extends Thread {
            private static final String TAG = "DoSomethingThread";
            private static final int DELAY = 2500; // 5 seconds
            private static final int RANDOM_MULTIPLIER = 10;

            @Override
            public void run() {
                Log.v(TAG, "doing work in Random Number Thread");
                while (!isInterrupted()) {
                    int randNum = (int) (Math.random() * RANDOM_MULTIPLIER);
                    publishProgress(randNum);
                    try {
                        Thread.sleep(DELAY);
                    }catch (InterruptedException e) {
                        Log.v(TAG,"Interrupting and stopping the Random Number Thread");
                        return;
                    }
                }
            }

            private void publishProgress(int randNum) {
                Log.v(TAG, "reporting back from the Random Number Thread");
                final String text = String.format(getString(R.string.service_msg),
                        randNum);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {                        {
                            Log.v(TAG,"Update Generated number " + text.toString());
                            resultsTextView.setText(text);
                        }
                    }
                });
            }
        }
	}
}
