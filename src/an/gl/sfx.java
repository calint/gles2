package an.gl;

import java.io.Serializable;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

final public class sfx extends Thread{
	public float frq=440;
	public boolean mute;
	public boolean on;
	public sfx(){super(sfx.class.getName());}
	@Override public void run(){
		on=true;
		final int buffsize=AudioTrack.getMinBufferSize(sample_rate_freq,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
		final AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,sample_rate_freq,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,buffsize,AudioTrack.MODE_STREAM);
		final short[]samples=new short[buffsize];
		audioTrack.play();
		for(int i=0;i<tone_generators.length;i++){
			final tone_generator g=new tone_generator();
			g.freq=110*i;
			tone_generators[i]=g;
		}
		while(on){
			for(int i=0;i<buffsize;i++){
				if(mute){samples[i]=0;continue;}
				double acc=0;
				for(final tone_generator g:tone_generators){
					acc+=g.next_sample();
				}
				samples[i]=(short)(acc/tone_generators.length);
			}
			audioTrack.write(samples,0,buffsize);
		}
		audioTrack.release();
	}
	public final tone_generator[]tone_generators=new tone_generator[4];
	
	
	static final int sample_rate_freq=44100;
	static final float twopi=(float)(8.*Math.atan(1.));
	
	public static class tone_generator implements Serializable{
		public float amp=10000;
		public float freq=110;
		public float ph;
		public float next_sample(){
			final float s=amp*(float)Math.sin(ph);
			ph+=twopi*freq/sample_rate_freq;
			return s;
		}

		private static final long serialVersionUID=1;
	}
}
