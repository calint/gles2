package an.gl.a;

import java.util.Set;

import an.gl.glob;

public class glob_trace extends glob{
	public static float scale=.1f;
	public static float scale_rate=.95f;
	public static float lifetime_s=3;

	public glob_trace(){
		iglo(a_trace.shared_instance).scale(scale).recalc_bounding_radius_using_scale();
	}
	@Override protected void on_handle_collisions(final Set<glob>collisions){
//		for(final glob g:collisions){
//			if(g instanceof glob_trace)continue;
//			if(g instanceof windo)continue;
//			detach();
//			return;
//		}
	}
	@Override protected void on_update(){
		if(x()>grid.size()){detach();return;}
		if(x()<-grid.size()){detach();return;}
		if(z()>grid.size()){detach();return;}
		if(z()<-grid.size()){detach();return;}
		if(y()<0){detach();return;}
		t_s+=glob.dt(1);
		if(t_s>lifetime_s){
			detach();
			return;
		}
		scale_xyz(scale_rate*scale_x(),scale_rate*scale_y(),scale_rate*scale_z()).recalc_bounding_radius_using_scale();
	}
	
	private float t_s;

	private static final long serialVersionUID=1;
}
