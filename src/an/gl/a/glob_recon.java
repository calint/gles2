package an.gl.a;

import java.util.Set;

import an.gl.acti;
import an.gl.glob;
import an.gl.planes;
import android.opengl.Matrix;

public class glob_recon extends glob{
	public glob_recon(){
		iglo(a_recon.shared_instance).is_sphere(false).bounding_volume(planes.cube).scale(.2f).recalc_bounding_radius_using_scale();
		y(.2f);
	}
	@Override protected void on_update(){
		dx(-angle_z()*.1f);
		final float x=x();
		final float xm=grid.size();
		if(x>xm){x(xm);dx(0);}
		if(x<-xm){x(-xm);dx(0);}
	}
	@Override protected void on_handle_collisions(final Set<glob>collisions){
		if(collisions.isEmpty())return;
		final long tnow=acti.time_millis();
		final long dt=tnow-t;
		if(dt<500)return;
		t=tnow;
		final float[]v=new float[]{0,2,0,1};
		final float[]mtx=matrix_model_world();
		Matrix.multiplyMV(v,0,mtx,0,v,0);
		final float[]p=new float[]{x(),y(),z(),1};
		final float[]vp=new float[4];
		acti.vec_minus(vp,v,p);
		new glob_fling(v,vp[0]*dx_dy_amplifier_scalar,vp[1]*dx_dy_amplifier_scalar);
	}
	private long t;

	public static float dx_dy_amplifier_scalar=8;
	private static final long serialVersionUID=1;
}
