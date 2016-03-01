package an.gl.a;

import java.util.Set;

import an.gl.glob;
import an.gl.planes;

public class glob_building extends glob{
	public static float scale=.5f;
	public static float health=5;
	
	public glob_building(final float x,final float y,final float z){
		iglo(a_building.shared_instance).is_sphere(false).bounding_volume(planes.cube).scale(scale).recalc_bounding_radius_using_scale();//bounding_radius(scale);
		xyz(x,y,z);
	}
	@Override protected void on_handle_collisions(final Set<glob>collisions){
		for(final glob g:collisions){
			if(g instanceof glob_explosion){hlth-=dt(1);if(hlth<0){detach();return;}}
		}
	}

	private float hlth=health;
	private static final long serialVersionUID = 1L;
}
