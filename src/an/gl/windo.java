package an.gl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

final public class windo extends glob{
	public static windo w;
	private boolean render_hud;final public boolean render_hud(){return render_hud;}final public glob render_hud(final boolean yes){render_hud=yes;return this;}
	public boolean render_globs=true;
	public boolean draw_grid_borders=true;
	
	private iglo hud;public windo hud(final iglo h){hud=h;return this;}
	private float[]mtx_proj=new float[16];
	private float[]mtx_wv=new float[16];
	private float[]mtx_wvp=new float[16];
	private float[]mtx_wvp_inv=new float[16];
	final public/*@ReadOnly*/float[]matrix_world_view_projection()/*@ReadOnly*/{return mtx_wvp;}
	private float clip_near=1;final public float clip_near(){return clip_near;}
	private float clip_far=16;
	final public windo clip(final float near,final float far){clip_near=near;clip_far=far;return this;}
	private float ratio=1;
	private int wi,hi;
	final public int width(){return wi;}
	final public int height(){return hi;}
	private boolean perspective_projection;// used by pick(screenx,screeny)
	public windo perspective_projection(final boolean b){perspective_projection=b;return this;}
	final public boolean is_perspective_projection(){return perspective_projection;}
	
	final void on_surfacechanged(final int width,final int height){
		if(wi==width&&hi==height)return;
		wi=width;
		hi=height;
		ratio=(float)width/height;
		if(perspective_projection){
			Matrix.perspectiveM(mtx_proj,0,45,ratio,clip_near,clip_far);
//			Matrix.frustumM(mtx_proj,0,-ratio,ratio,-1,1,clip_near,clip_far);
		}else{
			Matrix.orthoM(mtx_proj,0,-ratio,ratio,-1,1,clip_near,clip_far);
		}
		GLES20.glViewport(0,0,width,height);
		GLES20.glEnable(GL10.GL_CULL_FACE);
//		GLES20.glCullFace(GL10.GL_BACK);
	}
	private float lookat_x,lookat_y,lookat_z;
	final public windo lookat_xyz(final float x,final float y,final float z){
		lookat_x=x;lookat_y=y;lookat_z=z;
		return this;
	}
	private float r;
	public static float dr=360/60;// 1 rotation/minute
	final public void render()/*@ReadOnly*/throws Throwable{
		frame++;
//		lookat_x=x();
//		lookat_y=y();
//		Matrix.setLookAtM(mtx_wv,0,x(),y(),z(),lookat_x,lookat_y,lookat_z,0,1,0);

		final glob la=acti.geomag_sensor_connected_glob;
		
//		x(la.x());
		final float cx=grid.size()*(float)Math.cos(r*Math.PI/180);
		final float cz=grid.size()*(float)Math.sin(r*Math.PI/180);
		r+=glob.dt(dr);
		xyz(cx,y(),cz);
		if(la!=null)lookat_xyz(la.x(),la.y(),la.z());
		
//		lookat_x=acti.geomag_sensor_connected_glob.x();
//		lookat_y=acti.geomag_sensor_connected_glob.y();
//		lookat_z=acti.geomag_sensor_connected_glob.z();
		Matrix.setLookAtM(mtx_wv,0,x(),y(),z(),lookat_x,lookat_y,lookat_z,0,1,0);
		Matrix.multiplyMM(mtx_wvp,0,mtx_proj,0,mtx_wv,0);
		final float[]wvp_inv=matrix_world_view_projection_inverted();
		final float[]viewer_origo=new float[]{x,y,z,1};
		final planes bcp=new planes();
		if(perspective_projection){
			final float[]point_top_left=acti.vec_normalize_projected(0,0);// put screen coords 0,0 in world coords
			final float[]point_top_left_world_coords=new float[4];
			Matrix.multiplyMV(point_top_left_world_coords,0,wvp_inv,0,point_top_left,0);//
			final float[]point_bottom_left=acti.vec_normalize_projected(0,hi);// screen coords 0,height in world coords
			final float[]point_bottom_left_world_coords=new float[4];
			Matrix.multiplyMV(point_bottom_left_world_coords,0,wvp_inv,0,point_bottom_left,0);
			
			final plane p=new plane(viewer_origo,point_top_left_world_coords,point_bottom_left_world_coords);/// left hand cull plane
			bcp.planes_add(p);
			
			final float[]point_top_right=acti.vec_normalize_projected(wi,0);
			final float[]point_top_right_world_coords=new float[4];
			Matrix.multiplyMV(point_top_right_world_coords,0,wvp_inv,0,point_top_right,0);//
			final float[]point_bottom_right=acti.vec_normalize_projected(wi,hi);
			final float[]point_bottom_right_world_coords=new float[4];
			Matrix.multiplyMV(point_bottom_right_world_coords,0,wvp_inv,0,point_bottom_right,0);
			
			final plane p1=new plane(viewer_origo,point_bottom_right_world_coords,point_top_right_world_coords);/// right hand cull plane
			bcp.planes_add(p1);
			
			
		}else{//?? special case only
			bcp.planes_add(new plane(new float[]{x()-ratio,  0,0,1},new float[]{-1, 0,0,1}));// left cutoff plane (from windo seen)
			bcp.planes_add(new plane(new float[]{x()+ratio,  0,0,1},new float[]{ 1, 0,0,1}));// right
			bcp.planes_add(new plane(new float[]{      0,y()-1,0,1},new float[]{ 0,-1,0,1}));// top
			bcp.planes_add(new plane(new float[]{      0,y()+1,0,1},new float[]{ 0, 1,0,1}));// bottom
		}
		
		glob.meters.grids_rendered=0;
		glob.meters.globs_rend_already=0;
		glob.meters.grids_render_skipped=0;
		glob.meters.globs_rend=0;
		glob.meters.globs_render_outside_viewpyr=0;
		final iglo gi=glob.o.iglo();
		if(gi!=null)gi.iglo_render(this,glob.o);
		if(draw_grid_borders)glob.grid.render_grid(this);// draws grid borders
		glob.grid.render_globs(this,frame,bcp);// draws grid content
		if(render_hud&&hud!=null)hud.iglo_render(this,null);//? list
	}
	final public float[]window_to_world_coord(final float x,final float y){
		final float xn=x*2/wi-1;
		final float yflip=hi-y;
		final float yn=yflip*2/hi-1;
		final float[]vec_normalized_projected=new float[]{xn,yn,-1,1};
		final float[]vec_world=new float[4];
		final float[]mtx_wvp_inv=matrix_world_view_projection_inverted();
		Matrix.multiplyMV(vec_world,0,mtx_wvp_inv,0,vec_normalized_projected,0);
		return vec_world;
	}
	private long frame;
	private long mtx_wvp_inv_update_frame;
//	public static boolean enable_cull_back_face;//? upto shader
	// to not have to make the inverse matrix at every frame and once per frame
	final public@annotations.readonly
    float[]matrix_world_view_projection_inverted()/*@ReadOnly*/{
		if(frame==mtx_wvp_inv_update_frame)
			return mtx_wvp_inv;
		mtx_wvp_inv_update_frame=frame;
		Matrix.invertM(mtx_wvp_inv,0,mtx_wvp,0);
		return mtx_wvp_inv;
	}
	protected void on_update(){
		final float a=glob.grid.size();
//		System.out.println(a+"   "+x()+"   "+y()+"   "+z());
		if(x()>a){dx(-dx());x(a);}
		if(x()<-a){dx(-dx());x(-a);}
		if(y()>a){dy(-dy());y(a);}
		if(y()<-a){dy(-dy());y(-a);}
		if(z()>a){dz(-dz());z(a);}
		if(z()<-a){dz(-dz());z(-a);}
	}
	private static final long serialVersionUID=1;
}
