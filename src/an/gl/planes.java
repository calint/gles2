package an.gl;

import java.io.Serializable;
import java.util.ArrayList;

final public class planes implements Serializable{
	public planes planes_add(final plane p){list.add(p);return this;}
	public boolean is_sphere_inside_or_intersecting(float x,float y,float z,float r){
		final int n=list.size();
		if(n==0)
			return true;
		for(final plane p:list){
			if(p.is_in_collision_with_sphere(x,y,z,r)==plane.INFRONT)
				return false;
		}
		return true;
	}
	public boolean is_dot_inside(final float x,final float y,final float z){
		final int n=list.size();
		if(n==0)
			return true;
		for(final plane p:list){
			final float dist=p.distance_to_point(x,y,z);
			if(dist>0)
				return false;
		}
		return true;
	}
	public ArrayList<plane>list=new ArrayList<plane>();
	public final static planes box=new planes();
	static{
		box.planes_add(new plane(new float[]{ -1, 0,0,1},new float[]{-1, 0,0,1}));// left
		box.planes_add(new plane(new float[]{  0, 1,0,1},new float[]{ 0, 1,0,1}));// top
		box.planes_add(new plane(new float[]{  1, 0,0,1},new float[]{ 1, 0,0,1}));// right
		box.planes_add(new plane(new float[]{  0,-1,0,1},new float[]{ 0,-1,0,1}));// bottom

		//. skip rotation matrix, use dot for origo and dot for normal endpoint
//		box.planes_add(new plane(new float[]{ -1, 0,0,1},new float[]{-2, 0,0,1}));// left
//		box.planes_add(new plane(new float[]{  0, 1,0,1},new float[]{ 0, 2,0,1}));// top
//		box.planes_add(new plane(new float[]{  1, 0,0,1},new float[]{ 2, 0,0,1}));// right
//		box.planes_add(new plane(new float[]{  0,-1,0,1},new float[]{ 0,-2,0,1}));// bottom
	}
	public final static planes cube=new planes();
	static{
		cube.planes_add(new plane(new float[]{ -1, 0,0,1},new float[]{-1, 0,0,1}));// left
		cube.planes_add(new plane(new float[]{  0, 1,0,1},new float[]{ 0, 1,0,1}));// top
		cube.planes_add(new plane(new float[]{  1, 0,0,1},new float[]{ 1, 0,0,1}));// right
		cube.planes_add(new plane(new float[]{  0,-1,0,1},new float[]{ 0,-1,0,1}));// bottom
		cube.planes_add(new plane(new float[]{  0,0,-1,1},new float[]{ 0,0,-1,1}));// backside
		cube.planes_add(new plane(new float[]{  0,0, 1,1},new float[]{ 0,0, 1,1}));// frontside
	}
	
	private static final long serialVersionUID=1;
}
