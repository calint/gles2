package an.gl;
import java.io.Serializable;

import an.gl.annotations.readonly;
import an.gl.annotations.takes;
import android.opengl.Matrix;

final public class bvol implements Serializable{
	public @readonly
    planes planes;//. multiple voumes. example capsule+cylinder+capsule
	public @readonly
    planes planes_in_world_coords_cache;
	public @readonly
    long cached_at_update_seq=-1;
	public bvol(){}
	public bvol(final@takes planes p){this.planes=p;}
	public boolean is_sphere_inside_or_intersecting(final glob g,final float x,final float y,final float z,final float bounding_radius){
		upd_cache(g);
		return planes_in_world_coords_cache.is_sphere_inside_or_intersecting(x,y,z,bounding_radius);
	}
	public boolean is_dot_inside(glob g,float x,float y,float z){
		upd_cache(g);
		return planes_in_world_coords_cache.is_dot_inside(x,y,z);
	}

	private void upd_cache(final glob g){
		if(cached_at_update_seq!=g.matrix_model_world_updated_at_seq){//? g.upd_matrix() then check
			glob.meters.bvol_world_coords_cache_update++;
//			System.out.println("  bvol cache update for "+g);
			final float[]mtx_mw=g.matrix_model_world();
			cached_at_update_seq=g.matrix_model_world_updated_at_seq;
			planes_in_world_coords_cache=new planes();///? recycle
			for(final plane p:planes.list){
				/// transform to world coords
				final float[]origo=new float[4];
				final float[]normal=new float[4];
				Matrix.multiplyMV(origo,0,mtx_mw,0,p.origo,0);
				Matrix.multiplyMV(normal,0,g.matrix_rotation_model_to_world(),0,p.normal,0);
				planes_in_world_coords_cache.planes_add(new plane(origo,normal));
			}
		}
	}
	
	
	public static boolean check_collision(final glob a,final glob b){
		glob.meters.bvol_check_collision++;
		a.bounding_volume.upd_cache(a);
		b.bounding_volume.upd_cache(b);
		boolean res=false;//?! return true does not work?
		for(final plane pa:a.bounding_volume.planes_in_world_coords_cache.list){
			if(b.bounding_volume.is_dot_inside(b,pa.origo[0],pa.origo[1],pa.origo[2])){
//				System.out.println("  bvol col "+a+"  in  "+b);
//				return true;//?! return true does not work?
				res=true;
				break;
			}
		}
		if(res)return res;
		for(final plane pb:b.bounding_volume.planes_in_world_coords_cache.list){
			if(a.bounding_volume.is_dot_inside(a,pb.origo[0],pb.origo[1],pb.origo[2])){
//				System.out.println("  bvol col "+b+"  in  "+a);
				res=true;///?! return true here returns false? bug in vm?
				break;
//				return true;
			}
		}
		return res;
	}

	private static final long serialVersionUID=1;
}
