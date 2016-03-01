package an.gl;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
final class view extends GLSurfaceView implements GLSurfaceView.Renderer{
	public view(final Context context){
		super(context);
		setEGLContextClientVersion(2);
//		setEGLConfigChooser(false);
		setRenderer(this);
		setPreserveEGLContextOnPause(true);
		setKeepScreenOn(true);
	}
	//-- GLSurfaceView.Renderer
	final @Override public void onSurfaceCreated(final GL10 unused,final EGLConfig config){
//		System.out.println("onSurfaceCreated");
		shader.shaders.clear();
		try{
			System.out.println(" loading shaders");
			for(final Class<? extends shader>cls:shader.shader_classes){
					System.out.println("    "+cls.getName());
					final shader s=cls.newInstance();
					shader.shaders.add(s);
			}
			System.out.println(" loading iglos");
			for(final iglo m:glo.s){
				System.out.println("    "+m.getClass().getName());
				m.iglo_load();
			}
		}catch(final Throwable t){
			throw new Error(t);
		}
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
//		System.out.println("onSurfaceCreated done");
	}
	final @Override public void onSurfaceChanged(final GL10 unused,final int width,final int height){
//		System.out.println("onsurfacechanged "+width+"x"+height);
		if(windo.w==null)return;
		windo.w.on_surfacechanged(width,height);
	}
	static private int fps_f;
	static private int fps_f0;
	static private long fps_t0;
	public static int fps_update_intervall_ms=100;
	private int frame;
	final @Override public void onDrawFrame(final GL10 unused){
		frame++;
//		System.out.println("onDrawFrame "+windo.w.frame);
		if(windo.w==null)return;
		glob.meters.before_render_new_frame();
		GLES20.glClearColor(.1f,.1f,.1f,1);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		glob.meters.globs_rend=0;
		shader.active_shader_use();
		/// grid update
		glob.meters.grids=1;/// including the root grid
		final long tt1=acti.time_millis();
		glob.grid.clear_for_new_frame();
		final long tt2=acti.time_millis();
		glob.meters.grids_clear_ms=(int)(tt2-tt1);
		glob.meters.grids_put_count=0;
		glob.meters.grids_put_skip_count=0;
		glob.grid.put_all(glob.o.chlds);
		final long tt3=acti.time_millis();
		glob.meters.grids_put_ms=(int)(tt3-tt2);
		glob.meters.grid_ms=(int)(tt3-tt1);
		glob.meters.grids_put_per_sec=glob.meters.grids_put_ms==0?0:glob.meters.grids_put_count*1000/glob.meters.grids_put_ms;
		/// render
		final long tt4=acti.time_millis();
		try{windo.w.render();}catch(Throwable t){throw new Error(t);}
		final long tt5=acti.time_millis();
		glob.meters.rend_ms=(int)(tt5-tt4);
		glob.after_render_new_frame(tt5);
		acti.handle_keys();
		glob.o.add_new_globs_to_children();
		glob.meters.coldet_bounding_spheres_check=0;
		glob.meters.collisions=0;
		glob.meters.bvol_world_coords_cache_update=0;
		glob.meters.bvol_check_collision=0;
		final long tt6=acti.time_millis();
		glob.grid.detect_collisions();
		final long tt7=acti.time_millis();
		glob.meters.collision_detection_ms=(int)(tt7-tt6);
		glob.meters.globs_update_already=0;
		glob.meters.grids_globs_update=0;
		glob.meters.globs_update=0;
		glob.grid.update_globs(frame);//? not same frame
		final long tt8=acti.time_millis();
		glob.meters.update_ms=(int)(tt8-tt7);
		fps_f++;
		final long t=acti.time_millis();
		final long dt=t-fps_t0;
		if(dt>fps_update_intervall_ms){
			final int df=fps_f-fps_f0;
			acti.fps=(int)(df*1000/dt);
			fps_f0=fps_f;
			fps_t0=t;
//			System.out.println("fps: "+fps);
		}
	}

//	try{
//		final PrintStream out=System.out;
//		final vintage pco=new vintage();
//		pco.x_l(null,null);
//		out.println(pco.sts);
//		pco.x_c(null,null);
//		out.println(pco.sts);
//		pco.x_r(null,null);
//		out.println(pco.sts);
//		
//		for(int i=0;i<30;i++){
//			pco.x_f(null,null);
//			out.println(pco.sts);
//		}
//	}catch(Throwable t){
//		t.printStackTrace();
//	}

}

