package an.gl.a;

import java.util.Set;

import an.gl.acti;
import an.gl.glob;

public class glob_fling extends glob{
	public static float scale=.05f;
	public glob_fling(){
		iglo(a_textured_circle.shared_instance).scale(scale).recalc_bounding_radius_using_scale();
	}
	public glob_fling(final float[]world_coord,final float dx,final float dy){
		this();
		xyz(world_coord).z(0).dx_dy(dx,dy).angle_z_delta_t((dx+dy)*100);
	}
	private float t_s;
	private float lifetime_s=5;
	final float gravity=.1f;

	@Override protected void on_handle_collisions(final Set<glob>collisions){
		for(final glob g:collisions){
			dy(-dy()*bounce_factor);
			dx(-dx()*bounce_factor);
//			if(g instanceof glob_truck){
//				$init.score++;
//				return;
//			}
			if(g.iglo()==a_triangle.shared_instance){
				$init.score++;
				if(g.parent()!=null)g.detach();
				return;
			}
		}
	}
	@Override protected void on_update(){
		t_s+=glob.dt(1);
		if(t_s>lifetime_s){
			detach();
			return;
		}
		acti.asfx.frq=y()*50+440;
		dy(dy()-gravity);
		if(y()<scale_y()){
			y(scale_y());//? split dt at line intersects plane
			dy(-dy()*bounce_factor);
		}
	}

	public static float bounce_factor=.7f;

	
	private static final long serialVersionUID=1;
}
