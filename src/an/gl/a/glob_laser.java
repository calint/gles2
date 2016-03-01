package an.gl.a;

import java.io.Serializable;
import java.util.Set;

import an.gl.glob;
import an.gl.windo;

public class glob_laser extends glob{
	public static float scale=.05f;

	public glob_laser(final float[]world_coord,final float[]velocity){
		iglo(a_triangle.shared_instance).scale(scale).recalc_bounding_radius_using_scale();
		xyz(world_coord).dxyz(velocity);
	}
	private float t_s;
	public static float lifetime_s=20;

	@Override protected void on_handle_collisions(final Set<glob>collisions){
		for(final glob g:collisions){
			if(g instanceof glob_laser)continue;
			if(g instanceof windo)continue;
			if(g instanceof glob_trace)continue;
			detach();
			new glob_explosion().xyz(x(),y(),z());
			return;
		}
	}
	@Override protected void on_update(){
		if(x()>grid.size()){detach();return;}
		if(x()<-grid.size()){detach();return;}
		if(z()>grid.size()){detach();return;}
		if(z()<-grid.size()){detach();return;}
		if(y()<0){detach();new glob_explosion().xyz(x(),y()+glob_explosion.scale,z());return;}
		t_s+=glob.dt(1);
		if(t_s>lifetime_s){
			detach();
			return;
		}
		tr.tick(this,glob.timestamp_ms());
	}
	private tracer tr=new tracer();
	
	final public static class tracer implements Serializable{
		private long t0_ms;
		public int trace_rate_ms=100; 
		public void tick(final glob g,final long t_ms){
			final long dt=t_ms-t0_ms;
			if(dt<trace_rate_ms)return;
			t0_ms=t_ms;//?? use dt trace_rate_ms
			new glob_trace().xyz(g.x(),g.y(),g.z());
		}
		private static final long serialVersionUID=1;
	}

	private static final long serialVersionUID=1;
}
