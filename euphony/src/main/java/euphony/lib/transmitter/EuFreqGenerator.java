package euphony.lib.transmitter;

import euphony.lib.util.EuOption;

public class EuFreqGenerator {
	
	// FIXED ACOUSTIC DATA
	private final double PI = Math.PI;
	private final double PI2 = PI * 2;

	public enum CrossfadeType {
		FRONT, END, BOTH
	}
	
	// Member for Frequency point
	// DEFAULT DEFINITION 
	private short[] mZeroSource;
	private int mBufferSize;
	private int mSampleRate;

	private int mCpIndex;
	private double mCpLastTheta;

	private EuOption mTxOption;

	public EuFreqGenerator() {
		mTxOption = new EuOption();
		initialize();
	}

	public EuFreqGenerator(EuOption option) {
		mTxOption = option;
		initialize();
	}

	private void initialize() {
		mBufferSize = mTxOption.getBufferSize();
		mSampleRate = mTxOption.getSampleRate();
		mZeroSource = new short[mBufferSize];

		mCpIndex = 0;
		mCpLastTheta = 0;
	}
	
	public short[] makeStaticFrequency(int freq, int degree)
    {
    	double[] double_source = new double[mBufferSize];
    	short[] source = new short[mBufferSize];
        double time, phase;
        
        for(int i = 0; i < mBufferSize; i++)
        {
        	time = (double)i / (double)mSampleRate;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);        	
        }
        
        return source;
    }

    public short[] makeFrequencyWithCP(int freq) {
		double[] double_source = new double[mBufferSize];
		short[] source = new short[mBufferSize];

		double x = PI2 * freq;
		double thetaDiff = x * (mCpIndex / mSampleRate) - mCpLastTheta;

		int bufferIdx = 0;
		double theta = 0;
		int i = mCpIndex;
		int bufferSize = mCpIndex + mBufferSize;
		for(; i < bufferSize; i++) {
			theta = x * i / mSampleRate - thetaDiff;
			double_source[bufferIdx] = Math.sin(theta);
			source[bufferIdx] = (short)(32767 * double_source[bufferIdx]);
			bufferIdx++;
		}

		mCpIndex = i;
		mCpLastTheta = x * mCpIndex / mSampleRate - thetaDiff;

		return source;
	}
    
    public void euMakeFrequency(short[] source, int freq)
    {
    	source = makeStaticFrequency(freq, 0);
    }
    
    public short[] makeFrequencyWithCrossFade(int freq)
    {
    	return applyCrossFade(makeStaticFrequency(freq, 0), CrossfadeType.BOTH);
    }

	public short[] makeFrequencyWithValue(int value) {
		return applyCrossFade(makeStaticFrequency(mTxOption.getControlPoint() + mTxOption.getDataInterval() * value, 0), CrossfadeType.BOTH);
	}
    
    public short[] applyCrossFade(short[] source, CrossfadeType type)
    {
    	double mini_window;
    	int fade_section = mTxOption.getFadeRange();
		for(int i = 0; i < fade_section; i++)
    	{
    		mini_window = (double)i / (double)fade_section;
    		switch(type) {
				case FRONT:
					source[i] *= mini_window;
					break;
				case END:
					source[mBufferSize-1-i] *= mini_window;
					break;
				case BOTH:
					source[i] *= mini_window;
					source[mBufferSize-1-i] *= mini_window;
					break;
			}
    	}
    	
    	return source;
    }   
    
    public short[] appendRawData(short[] src, short[] objective)
    {
    	int SRC_LENGTH, TOTAL_LENGTH;
    	if(src == null)
    		SRC_LENGTH = 0;
    	else
    		SRC_LENGTH = src.length;
    	TOTAL_LENGTH = SRC_LENGTH + objective.length;
    		
    	short[] dest = new short[TOTAL_LENGTH];

    	for(int i = 0; i < SRC_LENGTH; i++)
    		dest[i] = src[i];
    	for(int i = SRC_LENGTH; i < TOTAL_LENGTH; i++)
    		dest[i] = objective[i - SRC_LENGTH];
    	
    	return dest;
    }
    
    public short[] euLinkRawData(short[]... sources)
    {
		final int bufferSize = mTxOption.getBufferSize();
    	short[] dest = new short[sources.length * bufferSize];
    	
    	for(int i = 0; i < sources.length; i++)
    		for(int j = 0; j < sources[i].length; j++)
    			dest[j + i * bufferSize] = sources[i][j];
    	
    	return dest;
    }
    
    public short[] euLinkRawData(boolean isCrossfaded, short[]... sources)
    {
		final int bufferSize = mTxOption.getBufferSize();
    	short[] dest = new short[sources.length * bufferSize];
    	for(int i = 0; i < sources.length; i++)
    	{
    		if(isCrossfaded)
    			sources[i] = applyCrossFade(sources[i], CrossfadeType.BOTH);
    		
    		for(int j = 0; j < sources[i].length; j++)
    				dest[j + i * bufferSize] = sources[i][j];
    	}
    	
    	return dest;
    }
    
    public short[] mixingRawData(short[]... sources)
    {
    	short[] dest = sources[0].clone();
    	
    	for(int i = 1; i < sources.length; i++)
    	{
    		for(int j = 0; j < sources[i].length; j++){
    			dest[j] = (short) (((int)dest[j] + (int)sources[i][j])/2);
    		}
    	}
		return dest;
    }
    
    public short[] euMakeMaximumVolume(short[] source)
	 {
	    	int max = 0;
	    	//SCAN FOR VOLUME UP
		 	for (short i1 : source)
		 		if (max < Math.abs(i1))
		 			max = i1;
	    	if(32767 == max) 
	    		return source;
	    	
	    	for(int i = 0; i < source.length; i++)
	    		source[i] *= 32767/max;
	    	
	    	return source;
	 }
    
	public short[] getZeroSource() {
		return mZeroSource;
	}
}

