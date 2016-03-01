package an.gl.a;
import java.util.Set;

import an.gl.acti;
import an.gl.glob;
public class glob_freq extends bouncingglob{
	public static float scale=.2f;
	public glob_freq(){
		iglo(a_triangle.shared_instance);
		scale(scale);
		bounding_radius(scale);
	}
	@Override protected void on_update(){
		super.on_update();
		if(acti.asfx==null)return;
		angle_z(p*acti.asfx.frq);
	}
	@Override protected void on_handle_collisions(final Set<glob>collisions){
		if(!collisions.isEmpty())
			for(final glob g:collisions)
				if(g instanceof glob_freq){
					if(parent()!=null)detach();
					return;
				}
	}
	public float p=1.f;
	
	public static final long serialVersionUID=1;
}
