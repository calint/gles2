package an.gl;

import java.io.Serializable;

import an.gl.annotations.readsonly;
import an.gl.annotations.takes;


final public class plane implements Serializable{
	public float[]origo;
	public float[]normal;
	public plane(final@takes float[]origo,final@takes float[]norm){
		this.origo=origo;
		this.normal=norm;
	}
	public plane(final@takes float[]origo,final@readsonly float[]p1,final@readsonly float[]p2){
		this.origo=origo;
		normal=new float[4];
		final float[]v1=new float[4];
		final float[]v2=new float[4];
		acti.vec_minus(v1,p1,origo);
		acti.vec_minus(v2,p2,origo);
		acti.vec_cross(normal,v1,v2);
		acti.vec_normalize(normal);
	}
	public int is_in_collision_with_sphere(final float x,final float y,final float z,final float r){
		final float dist=distance_to_point(x,y,z);
//		if(r!=0)System.out.println(x+"   "+origo[0]+"   "+dist+"   "+r);
		if(dist>=0&&dist>=r)return INFRONT;
		if(dist<0&&-r>dist)return BEHIND;
		return INTERSECTING;
	}
	public float distance_to_point(final float x,final float y,final float z){
//		final float[]p=new float[]{x,y,z,1};
//		final float[]v=new float[4];
//		acti.vec_minus(v,p,origo);
//		final float dist=acti.vec_dot(normal,v);
//		return dist;
		
		final float[]p=new float[]{x,y,z,1};
//		final float[]p=acti.vec(x,y,z);
		acti.vec_minus(p,p,origo);
		final float dist=acti.vec_dot(normal,p);
//		acti.vec_recycle(p);
		return dist;
}
//	public float distance_to_point(final float[]model_to_world_matrix,final float x,final float y,final float z){
//		final float[]point_in_world_coord=new float[4];
//		Matrix.multiplyMV(point_in_world_coord,0,model_to_world_matrix,0,origo,0);
//		final float[]normal_in_world_coord=new float[4];
//		Matrix.multiplyMV(normal_in_world_coord,0,model_to_world_matrix,0,norm,0);//? roation matrix only?
//		acti.vec_normalize(normal_in_world_coord);
//		final float[]p=new float[]{x,y,z,1};
//		final float[]v=new float[4];
//		acti.vec_minus(v,p,point_in_world_coord);
//		final float dist=acti.vec_dot(normal_in_world_coord,v);
//		return dist;
//	}
	public final static int BEHIND=1,INFRONT=2,INTERSECTING=3;
	private static final long serialVersionUID=1;
}
// .