package an.gl.file;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import an.gl.acti;

final public class format_obj{
//	public static void main(final String[]args)throws Throwable{
//		final format_obj o=new format_obj(".",args[0]);
//		final FloatBuffer fb=o.alloc_direct_float_buffer_for_vertices_xyz();
//		System.out.println(fb);
//	}
//	
//	static public interface inputstream_from_path_provider{
//		InputStream inputstream_from_path(final String path)throws Throwable;
//	}
//	private inputstream_from_path_provider isp=new inputstream_from_path_provider(){@Override public InputStream inputstream_from_path(final String path)throws Throwable{
//			return new File(path);
//	}};
	public format_obj(final String work_dir,final String name,final String texture_path)throws Throwable{
//		final InputStream is_obj=acti.inputstream_from_asset(args[0]);
		final InputStream is=inputstream_from_path(work_dir+"/"+name);
		final Scanner sc=new Scanner(is);
		while(sc.hasNextLine()){
			final String line=sc.nextLine().trim();
			if(line.startsWith("#"))continue;
			if(line.isEmpty())continue;
			if(line.startsWith("mtllib ")){
				mtllib(work_dir,line);
				continue;
			}
			if(line.startsWith("v ")){
				v(line);
				continue;
			}
			if(line.startsWith("vn ")){
				vn(line);
				continue;
			}
			if(line.startsWith("vt ")){
				vt(line);
				continue;
			}
			if(line.startsWith("g ")){
				g(line);
				continue;
			}
			if(line.startsWith("usemtl ")){
				usemtl(line);
				continue;
			}
			if(line.startsWith("f ")){
				f(line);
				continue;
			}
//			System.out.println(line);
		}
		sc.close();
		normalize_vertices();
		texture_files_rebuild();
		if(texture_path!=null)
			texture_files.add(texture_path);
//		System.out.println(name);
//		System.out.println(vertices);
//		System.out.println(normals);
//		System.out.println(texture_coords);
//		System.out.println(faces);
	}
	private void texture_files_rebuild(){
		texture_files.clear();
		for(final face f:faces){
			if(f.material==null)
				continue;
			final Map<String,String>m=materials.get(f.material);
			if(m==null)
				continue;//? default texture
			final String texture_path=m.get("map_Ka");
			if(texture_path==null)
				continue;
			texture_files.add(texture_path);
		}
	}
	private void f(final String line){
		final String s=line.substring(line.indexOf(' ')+1);
		final face f=new face(s,use_material_on_face);
		faces.add(f);
	}
	public/*readonly*/String name;
	private String use_material_on_face;
	private void usemtl(final String line){
		final String[]split=line.split("\\s+");
		use_material_on_face=split[1];
	}
	private void g(final String line){
		final String[]split=line.split("\\s+");
		name=split[1];
	}
	private void v(final String line){
		final String[]split=line.split("\\s+");
		final float[]coord=new float[3];
		coord[0]=Float.parseFloat(split[1]);
		coord[1]=Float.parseFloat(split[2]);
		coord[2]=Float.parseFloat(split[3]);
		vertices.add(coord);
	}
	private void vn(final String line){
		final String[]split=line.split("\\s+");
		final float[]vector=new float[3];
		for(int i=0;i<vector.length;i++){
			vector[i]=Float.parseFloat(split[i+1]);
		}
		normals.add(vector);
	}
	private void vt(final String line){
		final String[]split=line.split("\\s+");
		final float[]uv=new float[2];
		uv[0]=Float.parseFloat(split[1]);
		uv[1]=-Float.parseFloat(split[2]);
		texture_coords.add(uv);
	}
	private InputStream inputstream_from_path(final String path)throws Throwable{
		return acti.inputstream_from_asset(path);
//		return new FileInputStream(path);
	}
	private void mtllib(final String work_dir,final String line)throws Throwable{
		final String path=line.substring(line.indexOf(' ')+1);
		final InputStream is=inputstream_from_path(work_dir+"/"+path);
		final Scanner sc=new Scanner(is);
		while(sc.hasNextLine()){
			final String l=sc.nextLine().trim();
			if(l.startsWith("#"))continue;
			if(l.isEmpty())continue;
			if(l.startsWith("newmtl ")){
				newmtl(l);
				continue;
			}
			if(current_material!=null){
				final int i=l.indexOf(' ');
				if(i==-1){System.out.println(getClass().getName()+"1");continue;}
				final String key=l.substring(0,i);
				final String value=l.substring(i+1);
				current_material.put(key,value);
				continue;
			}
		}
		sc.close();
//		System.out.println(current_material);
		current_material=null;
	}
	private void newmtl(final String line){
		final String name=line.substring(line.indexOf(' ')+1).trim();
		final Map<String,String>props=new HashMap<String, String>();
		materials.put(name,props);
		current_material=props;
//		System.out.println("new material "+newmtl);
	}
	
	
	public FloatBuffer alloc_direct_xyz_float_buffer_for_vertices(){
		final int size_of_float_in_bytes=4;
		final int count=vertices.size();
		final FloatBuffer fb=ByteBuffer.allocateDirect(3*count*size_of_float_in_bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for(final float[]f:vertices){
			fb.put(f[0]).put(f[1]).put(f[2]);
		}
		fb.position(0);
		return fb;
	}
	public FloatBuffer alloc_direct_xyz_float_buffer_linearray(){
		final int size_of_float_in_bytes=4;
		final int count=vertices.size();
		final FloatBuffer fb=ByteBuffer.allocateDirect(2*3*count*size_of_float_in_bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		final float d=.01f;
		for(final float[]f:vertices){
			fb.put(f[0]).put(f[1]).put(f[2]);
			fb.put(f[0]+d).put(f[1]+d).put(f[2]+d);
		}
		fb.position(0);
		return fb;
	}
	public FloatBuffer alloc_direct_xyzuv_float_buffer_trianglearray(){
		final int size_of_float_in_bytes=4;
		int count=0;
		for(final face f:faces){
			count+=f.vertex_indices.size()-2;
		}
		count*=3;
//		final int count=faces.size()*3;// 3 xyzuv for each triangle
		final FloatBuffer fb=ByteBuffer.allocateDirect(count*(3+2)*size_of_float_in_bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for(final face f:faces){
			final int c=f.vertex_indices.size();
			if(c==3){
				for(final vertex i:f.vertex_indices){
					final float[]xyz=vertices.get(i.index_coord);
					final float[]uv=texture_coords.get(i.index_texture);
					fb.put(xyz[0]).put(xyz[1]).put(xyz[2]);
					fb.put(uv[0]).put(uv[1]);
				}
			}else{ // make into triangles
				final ArrayList<vertex>vi=f.vertex_indices;
				final vertex v0=vi.get(0);
				final float[]xyz0=vertices.get(v0.index_coord);
				final float[]uv0=texture_coords.get(v0.index_texture);
				for(int i=1;i<(c-1);i++){
					final vertex v1=vi.get(i);
					final float[]xyz1=vertices.get(v1.index_coord);
					final float[]uv1=texture_coords.get(v1.index_texture);
					final vertex v2=vi.get(i+1);
					final float[]xyz2=vertices.get(v2.index_coord);
					final float[]uv2=texture_coords.get(v2.index_texture);
					fb.put(xyz0[0]).put(xyz0[1]).put(xyz0[2]);fb.put(uv0[0]).put(uv0[1]);
					fb.put(xyz1[0]).put(xyz1[1]).put(xyz1[2]);fb.put(uv1[0]).put(uv1[1]);
					fb.put(xyz2[0]).put(xyz2[1]).put(xyz2[2]);fb.put(uv2[0]).put(uv2[1]);
				}
			}
		}
		fb.position(0);
		return fb;
	}
	private void normalize_vertices(){
		float xmax,ymax,zmax;
		float xmin,ymin,zmin;
		xmax=ymax=zmax=Float.MIN_VALUE;
		xmin=ymin=zmin=Float.MAX_VALUE;
		for(final float[]p:vertices){
			final float x=p[0];
			final float y=p[1];
			final float z=p[2];
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
		for(final float[]p:vertices){
			p[0]=(p[0]-xmin)*xdiv*2-1;
			p[1]=(p[1]-ymin)*ydiv*2-1;
			p[2]=(p[2]-zmin)*zdiv*2-1;
		}
		scale_x=xdiv;scale_y=ydiv;scale_z=zdiv;
	}
	public float scale_x,scale_y,scale_z;
	
	private Map<String,String>current_material;
	public/*readonly*/Map<String,Map<String,String>>materials=new HashMap<String, Map<String, String>>();
	
	
	private List<float[]>vertices=new ArrayList<float[]>();
	private List<float[]>normals=new ArrayList<float[]>();
	private List<float[]>texture_coords=new ArrayList<float[]>();
	private List<face>faces=new ArrayList<face>();
	public Set<String>texture_files=new HashSet<String>();
	
	private static class vertex implements Serializable{
		public int index_coord=0;
		public int index_texture=0;
		public int index_normal=0;
		
		public vertex(final String s){// 12/16/4
			final String[]a=s.split("/");
			index_coord=Integer.parseInt(a[0])-1;
			if(a.length>1)index_texture=Integer.parseInt(a[1])-1;
			if(a.length>2)index_normal=Integer.parseInt(a[2])-1;
		}
		private static final long serialVersionUID=1;
	}
	private static class face implements Serializable{
		public final ArrayList<vertex>vertex_indices=new ArrayList<vertex>();
		public String material;

		public face(final String line,final String material){// 12/16/4 13/17/4 25/33/4
			this.material=material;
			final String[]s=line.split("\\s+");
			for(int i=0;i<s.length;i++)
				vertex_indices.add(new vertex(s[i]));
		}
		private static final long serialVersionUID=1;
	}	
}
