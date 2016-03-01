package an.gl.a;

import java.util.Set;

import an.gl.glob;
import an.gl.planes;

public class glob_ufo extends glob{
	public glob_ufo(){
		iglo(a_ufo.shared_instance).is_sphere(false).bounding_volume(planes.cube).scale(.2f).recalc_bounding_radius_using_scale();
		y(.2f);
	}
	@Override protected void on_update(){
		final float x=x();
		final float xm=grid.size();
		if(x>xm){x(xm);dx(0);}
		if(x<-xm){x(-xm);dx(0);}
		
		final float v=1;
		final float a=glob.rad(r);
		final float c=(float)Math.cos(a);
		final float s=(float)Math.cos(a);
		dx(a*c);
		dz(a*s);
		angle_y(r);
		r+=dt(dr);
	}
	@Override protected void on_handle_collisions(final Set<glob>collisions){}

	private float r;
	public static float dr=36;
	
	private static final long serialVersionUID=1;
}
