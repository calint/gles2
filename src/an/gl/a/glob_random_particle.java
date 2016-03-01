package an.gl.a;

import java.util.Set;

import an.gl.glob;

public class glob_random_particle extends glob{
	public glob_random_particle(final float max_displacement,final float max_velocity,final float max_daz,final float scale){
		xyz(rand(-max_displacement,max_displacement),rand(-max_displacement,max_displacement),0);
		angle_z(rand()*max_displacement);
		angle_z_delta_t(rand()*max_daz);
		dx_dy(rand()*max_velocity,rand()*max_velocity);
//		dz=(float)rand()*max_displacement;
		scale(scale);
		bounding_radius(scale);
		iglo(a_textured_circle.shared_instance);
		this.max_displacement=max_displacement;
	}
	public final static float rand(){return (float)Math.random();}
	public final static float rand(final float from_inclusive,final float to_exclusive){
		final float d=to_exclusive-from_inclusive;
		return (float)Math.random()*d+from_inclusive;
	}
	final @Override protected void on_update(){
		if(x()>max_displacement||x()<-max_displacement){dx(-.5f*dx());angle_z_delta_t(-angle_z_delta_t());}
		if(y()>max_displacement||y()<-max_displacement){dy(-.5f*dy());angle_z_delta_t(-angle_z_delta_t());}
//		if(z>1||z<-1)dz=-dz;
		
	}
	private float max_displacement;

	@Override protected void on_handle_collisions(final Set<glob>collisions){
		if(!collisions.isEmpty())
			for(final glob g:collisions)
				if(g instanceof glob_freq){
					detach();
					return;
				}
	}
	public static final long serialVersionUID=1;
}
