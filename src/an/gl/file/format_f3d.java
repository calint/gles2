package an.gl.file;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import an.gl.acti;
import an.gl.annotations.readonly;
import android.opengl.Matrix;


final public class format_f3d{
	public static void main(String[] args)throws Throwable{
		final format_f3d f=new format_f3d();
		f.read(new FileInputStream("ufo.f3d"));
		final FloatBuffer fb=f.alloc_direct_xyzrgbnml_buffer_for_triangle_array();
		System.out.println(fb);
	}
	public void read(final InputStream is){
		final Scanner sc=new Scanner(is);
		final int vertex_count=sc.nextInt();
		sc.nextLine();
//		System.out.println(vertex_count);
		for(int i=0;i<vertex_count;i++){
			final String l=sc.nextLine();
			final String[]s=l.split("\\s+");
			final vertex v=new vertex();
			v.x=Float.parseFloat(s[0]);
			v.y=Float.parseFloat(s[1]);
			v.z=Float.parseFloat(s[2]);
			vertices.add(v);
		}
		normalize_vertices();
		final int face_count=sc.nextInt();
		sc.nextLine();
		for(int i=0;i<face_count;i++){
			final String l=sc.nextLine();
			final String[]s=l.split("\\s+");
			final face f=new face();
			final int points=Integer.parseInt(s[0]);
			for(int j=1;j<=points;j++){
				final int ix=Integer.parseInt(s[j]);
				f.indices.add(ix);
			}
			f.rgb[0]=Integer.parseInt(s[points+1]);
			f.rgb[1]=Integer.parseInt(s[points+2]);
			f.rgb[2]=Integer.parseInt(s[points+3]);
			faces.add(f);
		}
	}
//	public FloatBuffer alloc_direct_xyzrgb_buffer_for_triangle_array(){
//		final int size_of_float_in_bytes=4;
//		int count=0;
//		for(final face f:faces){
//			count+=f.indices.size()-2;
//		}
//		count*=3;
////		final int count=faces.size()*3;// 3 xyzuv for each triangle
//		final FloatBuffer fb=ByteBuffer.allocateDirect(count*(3+3)*size_of_float_in_bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		for(final face f:faces){
//			final int c=f.indices.size();
//			final float r=f.rgb[0]/256.f;
//			final float g=f.rgb[1]/256.f;
//			final float b=f.rgb[2]/256.f;
//			if(c==3){
//				for(final int i:f.indices){
//					final vertex v=vertices.get(i);
//					fb.put(v.x).put(v.y).put(v.z).put(r).put(g).put(b);
////					System.out.println(i+"   "+v.x+"  "+v.y+"  "+v.z+"  ");
//				}
//			}else{ // make into triangles
//				final ArrayList<Integer>vi=f.indices;
//				final int v0=vi.get(0);
//				final vertex xyz0=vertices.get(v0);
//				for(int i=1;i<(c-1);i++){
//					final int v1=vi.get(i);
//					final vertex xyz1=vertices.get(v1);
//					final int v2=vi.get(i+1);
//					final vertex xyz2=vertices.get(v2);
//					fb.put(xyz0.x).put(xyz0.y).put(xyz0.z).put(r).put(g).put(b);
//					fb.put(xyz1.x).put(xyz1.y).put(xyz1.z).put(r).put(g).put(b);
//					fb.put(xyz2.x).put(xyz2.y).put(xyz2.z).put(r).put(g).put(b);
//				}
//			}
//		}
//		fb.position(0);
//		return fb;
//	}	
	public FloatBuffer alloc_direct_xyzrgbnml_buffer_for_triangle_array(){
		final int size_of_float_in_bytes=4;
		int count=0;
		for(final face f:faces){
			count+=f.indices.size()-2;
		}
		count*=3;
//		final int count=faces.size()*3;// 3 xyzuv for each triangle
		final FloatBuffer fb=ByteBuffer.allocateDirect(count*(3+3+3)*size_of_float_in_bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for(final face f:faces){
			final int c=f.indices.size();
			final float r=f.rgb[0]/256.f;
			final float g=f.rgb[1]/256.f;
			final float b=f.rgb[2]/256.f;
//			if(c==3){
//				for(final int i:f.indices){
//					final vertex v=vertices.get(i);
//					fb.put(v.x).put(v.y).put(v.z).put(r).put(g).put(b).put(n).put(m).put(l);
////					System.out.println(i+"   "+v.x+"  "+v.y+"  "+v.z+"  ");
//				}
//			}else{ // make into triangles
				final ArrayList<Integer>vi=f.indices;
				final int v0=vi.get(0);
				final vertex xyz0=vertices.get(v0);
				final float[]p0=new float[4];
				p0[0]=xyz0.x;p0[1]=xyz0.y;p0[2]=xyz0.z;p0[3]=1;
				
				for(int i=1;i<(c-1);i++){
					final int v1=vi.get(i);
					final vertex xyz1=vertices.get(v1);
					final int v2=vi.get(i+1);
					final vertex xyz2=vertices.get(v2);

					final float[]p1=new float[4];
					p1[0]=xyz1.x;p1[1]=xyz1.y;p1[2]=xyz1.z;p1[3]=1;
					final float[]p2=new float[4];
					p2[0]=xyz2.x;p2[1]=xyz2.y;p2[2]=xyz2.z;p2[3]=1;
					final float[]v01=new float[4];
					acti.vec_minus(v01,p1,p0);
					final float[]v02=new float[4];
					acti.vec_minus(v02,p2,p0);
					final float[]nml=new float[4];
					acti.vec_cross(nml,v02,v01);
//					acti.vec_cross(nml,v01,v02);
					acti.vec_normalize(nml);
					final float n=nml[0];
					final float m=nml[1];
					final float l=nml[2];
					
					fb.put(xyz2.x).put(xyz2.y).put(xyz2.z).put(r).put(g).put(b).put(n).put(m).put(l);
					fb.put(xyz1.x).put(xyz1.y).put(xyz1.z).put(r).put(g).put(b).put(n).put(m).put(l);
					fb.put(xyz0.x).put(xyz0.y).put(xyz0.z).put(r).put(g).put(b).put(n).put(m).put(l);
				}
//			}
		}
		fb.position(0);
		return fb;
	}	
	private void normalize_vertices(){
		float xmax,ymax,zmax;
		float xmin,ymin,zmin;
		xmax=ymax=zmax=Float.MIN_VALUE;
		xmin=ymin=zmin=Float.MAX_VALUE;
		for(final vertex v:vertices){
			final float x=v.x;
			final float y=v.y;
			final float z=v.z;
			if(x>xmax){xmax=x;}
			if(x<xmin){xmin=x;}
			if(y>ymax){ymax=y;}
			if(y<ymin){ymin=y;}
			if(z>zmax){zmax=z;}
			if(z<zmin){zmin=z;}
		}
		final float xdiv=1/(xmax-xmin);
		final float ydiv=1/(ymax-ymin);
		final float zdiv=1/(zmax-zmin);
		final float dx=(xmax-xmin)/2+xmin;
		final float dy=(ymax-ymin)/2+ymin;
		final float dz=(zmax-zmin)/2+zmin;
		for(final vertex v:vertices){
			v.x=(v.x-xmin)*xdiv*2-1;
			v.y=(v.y-ymin)*ydiv*2-1;
			v.z=(v.z-zmin)*zdiv*2-1;
		}
		scale_x=xdiv;scale_y=ydiv;scale_z=zdiv;
	}
	public void rotate_vertices_about_y_axis(final float deg){
		final float[]matrix=new float[16];
		Matrix.setIdentityM(matrix,0);
		Matrix.rotateM(matrix,0,deg,0,1,0);
		final float[]p=new float[4];
		final float[]pn=new float[4];
		for(final vertex v:vertices){
			p[0]=v.x;
			p[1]=v.y;
			p[2]=v.z;
			p[3]=1;
			Matrix.multiplyMV(pn,0,matrix,0,p,0);
			v.x=pn[0];
			v.y=pn[1];
			v.z=pn[2];
		}
	}
	public @readonly
    float scale_x,scale_y,scale_z;

	
	
	
	
	private static class face{
		ArrayList<Integer>indices=new ArrayList<Integer>();
		int[]rgb=new int[3];
	}
	private static class vertex{
		float x,y,z;
	}
	private ArrayList<vertex>vertices=new ArrayList<vertex>();
	private ArrayList<face>faces=new ArrayList<face>();
}
