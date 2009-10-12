package com.google.code.geobeagle.activity.prox;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class AnimatorThread {

	//////////////////////////////////////////////////////////////
	// PRIVATE MEMBERS

	/** Handle to the surface manager object we interact with */
	private SurfaceHolder mSurfaceHolder;

	/** If the thread should be running */
	private boolean mShouldRun = false;
	private boolean mIsRunning = false;

	private Thread mThread;
	private ProximityPainter mWorld;
	
	/** Time in milliseconds of the start of this cycle */
	private long mCurrentTimeMillis = 0;
	/** Time in seconds between the last and this cycle */
	private double mCurrentTickDelta = 0;
	/** If non-zero, AdvanceTime will not be called until this time (msec) */
	private long mResumeAtTime = 0;
	
	void updateTime() {
		long now = System.currentTimeMillis();
		assert (now >= mCurrentTimeMillis);

		mCurrentTickDelta = (now - mCurrentTimeMillis) / 1000.0;
		mCurrentTimeMillis = now;
		if (mCurrentTickDelta > 0.3) {
			Log.w(this.getClass().getName(), "Elapsed time " + mCurrentTickDelta +
			      " capped at 0.3 sec");
			mCurrentTickDelta = 0.3;
		}
	}
	
	private class RealThread extends Thread {
		public void run() {
			mIsRunning = true;
			while (mShouldRun) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					//assert (c != null);  //TODO: Remove 'if' when not needed anymore
					if (c == null) {
						Log.w(this.getClass().getName(), 
						      "run(): lockCanvas returned null");
						continue;
					}
					synchronized (mSurfaceHolder) {
	                    updateTime();
	                    if (mCurrentTimeMillis >= mResumeAtTime) {
	                        mWorld.advanceTime(mCurrentTickDelta);
	                    }
						mWorld.draw(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
			mIsRunning = false;
		}
	}

	private double mSecsPerTick = 0;

	private long mLastTouchEvent = 0;
	
	private int mNextTickNo = 0;

	private class TimingThread extends Thread {
		private int mRemainingRuns;
		public TimingThread(int remainingRuns) {
			mRemainingRuns = remainingRuns;
		}
		public void run() {
			mIsRunning = true;
			int totalTicksCount = mRemainingRuns;
			long mStartTimeMillis = SystemClock.uptimeMillis();
			while (mShouldRun && mRemainingRuns > 0) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					if (c == null) {
						Log.w("GeoBeagle", 
						      "run(): lockCanvas returned null");
						continue;
					}
					synchronized (mSurfaceHolder) {
						mWorld.advanceTime(mSecsPerTick);
						mWorld.draw(c);
						mRemainingRuns -= 1;
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
			mIsRunning = false;
			long duration = SystemClock.uptimeMillis() - mStartTimeMillis;
			int ticksDone = totalTicksCount - mRemainingRuns;
			if (ticksDone == 1) {
				Log.d("GeoBeagle", "Iterated tick #" + mNextTickNo + 
						" (@" + mSecsPerTick + "s)");
			} else if (ticksDone > 0) {
				Log.d("GeoBeagle", "Iterated " + ticksDone + 
						" ticks (@" + mSecsPerTick + "s) in " +
						duration + "ms => " + (ticksDone*1000/duration) + "fps");
			}
			mNextTickNo += ticksDone;
		}
	}
	
	//////////////////////////////////////////////////////////////
	// PUBLIC MEMBERS
	
	public AnimatorThread(SurfaceHolder surfaceHolder, 
	                      ProximityPainter painter) {
		mSurfaceHolder = surfaceHolder;
		mWorld = painter;
	}
	
	public void start() {
		if (mShouldRun)
			return;

		Log.d("GeoBeagle", "AnimatorThread.start()");

		mCurrentTimeMillis = System.currentTimeMillis() - 1;
		mResumeAtTime = System.currentTimeMillis() + 200;
		
		mShouldRun = true;
		if (!mIsRunning) {
			if (mThread == null)
				mThread = new RealThread();
			mThread.start();
		}
	}
		
	/** Run the game loop a specific number of times, for testing purposes. */
	public void setupTiming(int nrOfTicks, double secondsPerTick) {
		assert (!mShouldRun && !mIsRunning);
		assert (nrOfTicks > 0);
		assert (secondsPerTick > 0);
		Log.d("GeoBeagle", "setupTiming(" + secondsPerTick +
				  " sec/tick, ticks=" + nrOfTicks + ")");
		mResumeAtTime = 0;
		//mShouldRun = true;
		mSecsPerTick = secondsPerTick;
		mThread = new TimingThread(nrOfTicks);
		//mThread.start();
	}
	
	public void stop() {
		if (!mIsRunning)
			return;
		boolean retry = true;
		mShouldRun = false;
		while (retry) {
			try {
				mThread.join();
				retry = false;
				mThread = null;
			} catch (InterruptedException e) {
			}
		}
	}
}

