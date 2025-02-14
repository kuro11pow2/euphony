package co.euphony.rx;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import co.euphony.common.Constants;
import co.euphony.util.EuOption;

import static co.euphony.rx.EuPI.EuPITrigger.KEY_DOWN;
import static co.euphony.rx.EuPI.EuPITrigger.KEY_PRESSED;
import static co.euphony.rx.EuPI.EuPITrigger.KEY_UP;

public class EuRxManager {

	private final String LOG = "EuRxManager";

	private Thread mListenThread = null;
	private DetectRunner mDetectRunner = null;
	private EuPICallRunner mEuPICallRunner = null;

	public enum RxManagerStatus {
		RUNNING, STOP
	}

	private static final int RX_MODE = 1;
	private static final int DETECT_MODE = 2;
	private static final int EUPI_CALL_MODE = 3;

	private EuOption mOption;

	public EuRxManager() {
		mOption = EuOption.builder()
				.modeWith(EuOption.ModeType.DEFAULT)
				.encodingWith(EuOption.CodingType.BASE16)
				.modulationWith(EuOption.ModulationType.FSK)
				.build();
	}

	public EuRxManager(EuOption.ModeType mode) {
		mOption = EuOption.builder()
				.modeWith(mode)
				.build();
	}
	
	public boolean listen() {
		if(getStatus() != RxManagerStatus.RUNNING) {
			switch (mOption.getMode()) {
				case DEFAULT:
					mListenThread = new Thread(new RxRunner(), "RX");
					break;
				case EUPI: {
					if(mEuPICallRunner != null) {
						mListenThread = new Thread(mEuPICallRunner, "EUPI");
						Log.d(LOG, "Euphony : EuPICallRunner's EuPI Count : " + mEuPICallRunner.getEuPICount());
						break;
					}
					else {
						Log.d(LOG, "Euphony : EuPICallRunner is null");
						return false;
					}
				}
				default:
					Log.d(LOG, "Detect must have specific frequency value");
					return false;
			}
			mListenThread.start();
			return true;
		} else {
			return false;
		}
	}

	public boolean listen(int freq) {
		if(getStatus() != RxManagerStatus.RUNNING) {
			if(mOption.getMode() == EuOption.ModeType.DETECT) {
				mDetectRunner = new DetectRunner(freq);
				mListenThread = new Thread(mDetectRunner, "DETECT");
				mListenThread.start();
				return true;
			} else {
				Log.d(LOG, "Please use other listen function.");
				return false;
			}
		} else {
			return false;
		}
	}

	public void finish()
	{
		if(mListenThread != null) {
			mListenThread.interrupt();
		}
		
		mListenThread = null;
	}

	public void setOnWaveKeyPressed(int freq, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(freq, KEY_PRESSED, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public void setOnWaveKeyDown(int key, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(key, KEY_DOWN, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public void setOnWaveKeyUp(int key, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(key, KEY_UP, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public void setOnWaveKeyPressed(int freq, double threshold, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(freq, KEY_PRESSED, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(threshold, eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public void setOnWaveKeyDown(int key, double threshold, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(key, KEY_DOWN, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(threshold, eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public void setOnWaveKeyUp(int key, double threshold, EuPICallDetector iEuPICallDetector) {
		EuPI eupi = new EuPI(key, KEY_UP, iEuPICallDetector);

		if(mEuPICallRunner == null) {
			mEuPICallRunner = new EuPICallRunner(threshold, eupi);
		} else {
			mEuPICallRunner.addEuPI(eupi);
		}
	}

	public RxManagerStatus getStatus() {
		if(mListenThread != null) {
			switch (mListenThread.getState()) {
				case RUNNABLE:
					return RxManagerStatus.RUNNING;
				case NEW:
				case WAITING:
				case TIMED_WAITING:
				case BLOCKED:
				case TERMINATED:
				default:
					return RxManagerStatus.STOP;
			}
		} else
			return RxManagerStatus.STOP;
	}

	private AcousticSensor mAcousticSensor;
	
	public AcousticSensor getAcousticSensor() {
		return mAcousticSensor;
	}

	public void setAcousticSensor(AcousticSensor iAcousticSensor) {
		this.mAcousticSensor = iAcousticSensor;
	}
	
	private final Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(Message msg){			
			switch(msg.what){
				case RX_MODE:
					mAcousticSensor.notify(msg.obj + "");
					break;
				case DETECT_MODE:
					mFrequencyDetector.detect((float)msg.obj);
					break;
				case EUPI_CALL_MODE:
					EuPI eupi = (EuPI)msg.obj;
					eupi.getCallback().call();
					break;

			default:
				break;
			}
		}
	};

	public EuOption getOption() {
		return mOption;
	}

	public void setOption(EuOption mOption) {
		this.mOption = mOption;
	}
	
	private class RxRunner extends EuFreqObject implements Runnable{
		@Override
		public void run() 
		{
			while (!Thread.currentThread().isInterrupted()) {
				processFFT();
				if (this.getStarted())
					catchSingleData();
				else
					this.setStarted(checkStartPoint());

				if (this.getCompleted()) {
					Message msg = mHandler.obtainMessage();
					msg.what = RX_MODE;
					msg.obj = null;
					if(mOption.getCodingType() == EuOption.CodingType.BASE16) {
							msg.obj = EuDataDecoder.decodeStaticHexCharSource(getReceivedData());
					}
					this.setCompleted(false);
					mHandler.sendMessage(msg);
					destroyFFT();
					return;
				}
			}

			destroyFFT();
		}
	}

	private FrequencyDetector mFrequencyDetector;

	public FrequencyDetector getFrequencyDetector() {
		return mFrequencyDetector;
	}

	public void setFrequencyDetector(FrequencyDetector mFrequencyDetector) {
		this.mFrequencyDetector = mFrequencyDetector;
	}

	public void setFrequencyForDetect(int freq) {
		if(mOption.getMode() == EuOption.ModeType.DETECT) {
			if(mDetectRunner != null)
				mDetectRunner.setFrequency(freq);
		}
	}

	private class EuPICallRunner extends EuFreqObject implements Runnable {

		private double mThreshold = 0.0009;
		private final ArrayList<EuPI> EuPICallList = new ArrayList<>();

		EuPICallRunner(EuPI eupi) {
			addEuPI(eupi);
			Log.d(LOG, "Added " + eupi.getKey() + "(" + eupi.getFreqIndex() + ")");
		}

		EuPICallRunner(double threshold, EuPI eupi) {
			mThreshold = threshold;
			addEuPI(eupi);
			Log.d(LOG, "Added " + eupi.getKey() + "(" + eupi.getFreqIndex() + ")");
		}

		private int calculateFreqIndex(int freq) {
			double freqRatio = ((float)freq) / (float)Constants.HALF_SAMPLERATE;
			return (int) Math.round((freqRatio * (float) (Constants.FFT_SIZE >> 1)));
		}

		private boolean compareThreshold(float amp) {
			return amp > mThreshold;
		}

		public void addEuPI(EuPI eupi) {
			eupi.setFreqIndex(calculateFreqIndex(eupi.getKey()));
			EuPICallList.add(eupi);
		}

		public int getEuPICount() {
			return EuPICallList.size();
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				processFFT();

				float[] amp = {0, 0, 0};
				for(EuPI eupi : EuPICallList) {
					boolean isActable = false;
					switch(eupi.getTrigger()) {
						case KEY_DOWN: {
							if(eupi.getStatus() == EuPI.EuPIStatus.KEY_UP){
								isActable = true;
							}
						}
						break;
						case KEY_UP: {
							if(eupi.getStatus() != EuPI.EuPIStatus.KEY_UP) {
								isActable = true;
							}
						}
						break;
						case KEY_PRESSED: {
							isActable = true;
						}
						break;
					}

					int freqIndex = eupi.getFreqIndex();
					amp[0] = getSpectrumValue(freqIndex - 2);
					amp[1] = getSpectrumValue(freqIndex);
					amp[2] = getSpectrumValue(freqIndex + 2);

					if(isActable) {
						if(compareThreshold((amp[1] - (amp[0] + amp[2])/2))) {
							if(eupi.getTrigger() == KEY_DOWN || eupi.getTrigger() == KEY_PRESSED) {
								Message msg = mHandler.obtainMessage();
								msg.what = EUPI_CALL_MODE;
								msg.obj = eupi;
								mHandler.sendMessage(msg);
							}

							eupi.setStatus(EuPI.EuPIStatus.KEY_DOWN);
						} else {
							if(eupi.getTrigger() == KEY_UP) {
								Message msg = mHandler.obtainMessage();
								msg.what = EUPI_CALL_MODE;
								msg.obj = eupi;
								mHandler.sendMessage(msg);
							}

							eupi.setStatus(EuPI.EuPIStatus.KEY_UP);
						}
					} else {
						if(compareThreshold((amp[1] - (amp[0] + amp[2])/2))) {
							eupi.setStatus(EuPI.EuPIStatus.KEY_DOWN);
						} else {
							eupi.setStatus(EuPI.EuPIStatus.KEY_UP);
						}
					}

					Log.d(LOG, eupi.getKey() + "(" + eupi.getFreqIndex() + ")" + "'s Amplitude : " + amp[2]);
				}
			}

			destroyFFT();
		}
	}

	private class DetectRunner extends EuFreqObject implements Runnable {

		int mFrequency = 0;
		private int mFreqIndex = 0;
		DetectRunner(int freq) {
			setFrequency(freq);
		}

		public void setFrequency(int frequency) {
			mFrequency = frequency;
			mFreqIndex = ((int)((frequency / 22050.0) * Constants.FFT_SIZE) / 2) + 1;
			Log.d(LOG, "Frequency = " + mFrequency + ", mFreqIndex = " + mFreqIndex);
		}

		@Override
		public void run() {
			float previousAmp = 0;

			while (!Thread.currentThread().isInterrupted()) {
				processFFT();
				float amp = getSpectrumValue(mFreqIndex);

				if (previousAmp != amp) {
					Message msg = mHandler.obtainMessage();
					msg.what = DETECT_MODE;
					msg.obj = amp;
					mHandler.sendMessage(msg);
					previousAmp = amp;
				}
			}
			destroyFFT();
		}
	}
}
