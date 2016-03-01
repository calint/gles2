package an.gl.a;

import java.util.Set;

import an.gl.glob;

public class glob_explosion extends glob{
	public static float scale=.3f;
	public static float lifetime_s=3;
	public static float scale_rate=.95f;

	public glob_explosion(){
		iglo(a_explosion.shared_instance).scale(scale).recalc_bounding_radius_using_scale();
//		xyz(world_coord).dxyz(velocity);
	}
	private float t_s;
	@Override protected void on_handle_collisions(final Set<glob>collisions){
//		for(final glob g:collisions){
//			if(g instanceof glob_explosion)continue;
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
//		if(y()<0){detach();return;}
		t_s+=glob.dt(1);
		if(t_s>lifetime_s){
			detach();
			return;
		}
		scale_xyz(scale_rate*scale_x(),scale_rate*scale_y(),scale_rate*scale_z()).recalc_bounding_radius_using_scale();
	}
	private static final long serialVersionUID=1;
}
