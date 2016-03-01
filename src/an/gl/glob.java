package an.gl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import an.gl.annotations.readonly;
import an.gl.annotations.reference;
import android.opengl.Matrix;

public class glob implements Serializable{
	public static ArrayList<iglo>overlay_iglos;
	public static grid grid;

	public glob(final boolean root){if(!root)throw new Error();meters.globs++;}
	public glob(final@reference glob parent){this.parent=parent;parent.add(this);meters.globs++;}
	public glob(){this(o);}
	/// override
	protected void on_handle_collisions(final Set<glob>collisions){}
	protected void on_update(){}

	final public void detach(){
		if(parent==null){
			System.out.println("!! detach already detached: "+this);
			return;
		}
		parent.chlds.remove(this);
		this.parent=null;
		glob.meters.globs--;
	}

	private long render_frame;// used by renderer to not render same glob multiple times during same frame due to grid overlapping
	public String toString(){
		final StringBuilder sb=new StringBuilder();
		sb.append(getClass().getName()).append("[");
		if(name!=null&&name.length()!=0){
			sb.append(name);
		}else{
			sb.append(Integer.toString(hashCode(),16));
		}
		sb.append("] r=").append(bounding_radius).append(" glo=").append(glo).append(" x,y,z=(");
		sb.append(x).append(",");
		sb.append(y).append(",");
		sb.append(z).append(") az=");
		sb.append(az);
		return sb.toString();
	}
	private String name;final public String name(){return name;}final public glob name(final String name){this.name=name;return this;}
//	public static glob get(){return o;}
	
	final public void add(final glob o){
		chlds_new.add(o);
	}
	final void draw(final windo c,final long frame){
		if(this.render_frame==frame){
			glob.meters.globs_rend_already++;
			return;
		}
		this.render_frame=frame;
		meters.globs_rend++;
		if(glo!=null)glo.iglo_render(c,this);
//		glo=((lib)lib.lib).textured_circle;
		if(overlay_iglos!=null)
			for(final iglo g:overlay_iglos)
				g.iglo_render(c,this);
//		if(glo_bounding_sphere!=null)glo_bounding_sphere.gldraw(c,this);
//		if(glo_axis!=null)glo_axis.gldraw(c,this);
		for(final glob o:chlds)
			o.draw(c,frame);
	}
	private long update_frame;
	final public void update(final long frame){
		if(this.update_frame==frame){//? synch
			meters.globs_update_already++;
			return;
		}
		this.update_frame=frame;
		meters.globs_update++;
		on_handle_collisions(collisions);//? after all the updates, handle collisions?
		collisions.clear();
		x+=dt(dx);y+=dt(dy);z+=dt(dz);
		ax+=dt(dax);ay+=dt(day);az+=dt(daz);
		on_update();
		add_new_globs_to_children();
		for(final glob o:chlds){
			o.update(frame);
		}
	}

	
	final void add_new_globs_to_children(){
		chlds.addAll(chlds_new);//? sync on chlds_new, append to linked list
		chlds_new.clear();
	}
	final public glob parent(){return parent;}//@readonly 
	private glob parent;
	private iglo glo;final public glob iglo(final iglo g){glo=g;return this;}final public iglo iglo(){return glo;}
	private final LinkedList<glob>chlds_new=new LinkedList<glob>();
	final LinkedList<glob>chlds=new LinkedList<glob>();
//	public glob add(final glob g){chlds_new.add(g)
	public static interface code{public void x(final glob g)throws Throwable;}
	final public void chlds_for_each(final code c)throws Throwable{
		for(final glob g:chlds)
			c.x(g);
	}
	final public int chlds_count(){return chlds.size();}
	
	private float ax;final public float angle_x(){return ax;}final public glob angle_x(final float degrees){this.ax=degrees;return this;}
	private float ay;final public float angle_y(){return ay;}final public glob angle_y(final float degrees){this.ay=degrees;return this;}
	private float az;final public float angle_z(){return az;}final public glob angle_z(final float degrees){this.az=degrees;return this;}
	float x;final public float x(){return x;}final public glob x(final float x){this.x=x;return this;}
	float y;final public float y(){return y;}final public glob y(final float y){this.y=y;return this;}
	float z;final public float z(){return z;}final public glob z(final float z){this.z=z;return this;}
	final public glob xyz(final float x,final float y,final float z){this.x=x;this.y=y;this.z=z;return this;}
	final public glob xyz(final float[]v){this.x=v[0];this.y=v[1];this.z=v[2];return this;}
	private float sx=1,sy=1,sz=1;
	final public glob scale(final float s){sx=sy=sz=s;return this;}
	final public glob scale_xyz(final float x,final float y,final float z){sx=x;sy=y;sz=z;return this;}
	final public float scale_x(){return sx;}
	final public float scale_y(){return sy;}
	final public float scale_z(){return sz;}
	
	
	private float dx;final public float dx(){return dx;}final public glob dx(final float dx){this.dx=dx;return this;}
	private float dy;final public float dy(){return dy;}final public glob dy(final float dy){this.dy=dy;return this;}
	private float dz;final public float dz(){return dz;}final public glob dz(final float dz){this.dz=dz;return this;}
	final public glob dx_dy(final float dx,final float dy){this.dx=dx;this.dy=dy;return this;}
	private float dax;final public float angle_x_delta_t(){return dax;}final public glob angle_x_delta_t(final float degrees){this.dax=degrees;return this;}
	private float day;final public float angle_y_delta_t(){return day;}final public glob angle_y_delta_t(final float degrees){this.day=degrees;return this;}
	private float daz;final public float angle_z_delta_t(){return daz;}final public glob angle_z_delta_t(final float degrees){this.daz=degrees;return this;}
	final public glob angle_xyz_delta_t(final float dx,final float dy,final float dz){dax=dx;day=dy;daz=dz;return this;}
	float bounding_radius;
	final public glob bounding_radius(final float r){bounding_radius=r;bounding_radius2=r*r;return this;}
	final public float bounding_radius(){return bounding_radius;}
	private float bounding_radius2;

	private final float[]mtx_model_world=new float[16];
	private float px,py,pz,pax,pay,paz,psx,psy,psz;
//	public static grid grid;
	public @annotations.readonly
long matrix_model_world_updated_at_seq=-2;
	private boolean upd_mtx_local(){
		if(x==px&&y==py&&z==pz&&ax==pax&&ay==pay&&az==paz&&sx==psx&&sy==psy&&sz==psz)
			return false;
		Matrix.setIdentityM(mtx_model_world,0);
		Matrix.translateM(mtx_model_world,0,x,y,z);
		Matrix.rotateM(mtx_model_world,0,ax,1,0,0);
		Matrix.rotateM(mtx_model_world,0,ay,0,1,0);
		Matrix.rotateM(mtx_model_world,0,az,0,0,1);
		Matrix.scaleM(mtx_model_world,0,sx,sy,sz);
		px=x;py=y;pz=z;pax=ax;pay=ay;paz=az;psx=sx;psy=sy;psz=sz;
		matrix_model_world_updated_at_seq=update_frame;
		return true;
	}
	final public float[]matrix_model_world(){
		upd_mtx_local();
		//? checkifdirty
		return mtx_model_world;
	}
	public @readonly
    long rotation_matrix_updated_at_seq=-3;
	final public @readonly
    float[]rotation_matrix=new float[16];
	private boolean upd_local_rotation_matrix(){
		if(rotation_matrix_updated_at_seq==matrix_model_world_updated_at_seq)
			return false;
		Matrix.setIdentityM(rotation_matrix,0);
		Matrix.rotateM(rotation_matrix,0,ax,1,0,0);
		Matrix.rotateM(rotation_matrix,0,ay,0,1,0);
		Matrix.rotateM(rotation_matrix,0,az,0,0,1);
		rotation_matrix_updated_at_seq=matrix_model_world_updated_at_seq;
		return true;
	}
	final public float[]matrix_rotation_model_to_world(){
		upd_local_rotation_matrix();
		return rotation_matrix;
	}
	private final float sqrt_of_2=(float)Math.sqrt(2);
	final public glob recalc_bounding_radius_using_scale(){
		final float r=Math.max(Math.max(sx,sy),sz);
		if(is_sphere){
			bounding_radius(r);
			return this;
		}
//		final float len=(float)Math.sqrt(sx*sx+sy*sy+sz*sz);
		bounding_radius(r*sqrt_of_2);
		return this;
	}

	public static class state_for_renderer_while_update_modifies_object_state{
		public float x,y,z;
		public float r;
		public float[]to_world_matrix;
	}
	
	
	// dt
	private static long t0_ms;
	public static void after_render_new_frame(final long t_ms){
		long dt_ms=t_ms-t0_ms;
		if(dt_ms==0){dt_ms=1;System.out.println("abnormal dt "+dt_ms);}
		else if(dt_ms>500){dt_ms=1;System.out.println("abnormal dt "+dt_ms);}
//		dt_ms=1;
		dt_s=dt_ms/1000.f;
		t0_ms=t_ms;
	}
	//
//	public static Collection<glob>chlds_query_using_bounding_radius(final glob query_maker,final float x,final float y,final float z,final float radius){
//		final Collection<glob>ls=new ArrayList<glob>();
//		for(final glob g:o.chlds){
//			final float dx=g.x-x;
//			final float dy=g.y-y;
//			final float dz=g.z-z;
//			final float dist2=dx*dx+dy*dy+dz*dz;
//			final float dist=(float)Math.sqrt(dist2);//? notnecessary (radius+g.boundnig_radius)^2
////			System.out.println("dist: "+dist+"   range:"+(radius+g.bounding_radius));
//			if(dist>(radius+g.bounding_radius))
//				continue;
//			if(g==query_maker)
//				continue;
//			ls.add(g);
//		}
////		System.out.println(ls);
//		return ls;
//	}
	
	private static float dt_s;
	private static float rad_per_deg=(float)Math.PI/180;
	public static float dt(){return dt_s;}
	public static float dt(final float value_per_second){
//		return value_per_second*.2f;
			return value_per_second*dt_s;
	}
	public static float rad(final float deg){return deg*rad_per_deg;}
	
	public final static class meters{
		public static long time_ms;
		public static int globs;
		public static int globs_rend;
		public static int globs_update;
		public static int globs_update_already;
		public static int globs_rend_already;
		public static int globs_render_outside_viewpyr;
		public static int grids;
		public static int grids_rendered;
		public static int grids_allocated;
		public static int grids_clear_ms;
		public static int grids_put_ms;
		public static int grids_put_count;
		public static int grids_put_per_sec;
		public static int grids_globs_update;
		public static int grids_render_skipped;
		public static int grids_put_skip_count;
		public static int update_ms;
		public static int rend_ms;
		public static int grid_ms;
		public static int collisions;
		public static int collision_detection_ms;
		public static int coldet_bounding_spheres_check;
		public static int bvol_world_coords_cache_update;
		public static int bvol_check_collision;
		public static void before_render_new_frame(){
			time_ms=acti.time_millis();
		}
//		public static int grids_recycled_used;
//		public static int grids_recycled;
//		public static int grids_recycled_tried;
	}
	public static long time(){return meters.time_ms;}
	private boolean is_sphere=true;final public glob is_sphere(final boolean yes){is_sphere=yes;return this;}final public boolean is_sphere(){return is_sphere;}
//	public planes bounding_planes;
	bvol bounding_volume;final public bvol bounding_volume(){return bounding_volume;}final public glob bounding_volume(final bvol b){bounding_volume=b;return this;}
	final public glob bounding_volume(final planes p){bounding_volume(new bvol(p));return this;}
	public static Object[]check_collision(final glob a,final glob b){
		glob.meters.coldet_bounding_spheres_check++;
		final float dx=a.x-b.x;
		final float dy=a.y-b.y;
		final float dz=a.z-b.z;
		final float dist2=dx*dx+dy*dy+dz*dz;
		final float distance_margin=a.bounding_radius+b.bounding_radius;
		final float rarb2=distance_margin*distance_margin;
//		final float rarb2=a.bounding_radius2+a.bounding_radius*b.bounding_radius+b.bounding_radius2;
//		final float dist=(float)Math.sqrt(dist2);//? notnecessary (radius+g.boundnig_radius)^2
//		System.out.println("dist: "+dist+"   range:"+(radius+g.bounding_radius));
//		final float rarb=a.bounding_radius+b.bounding_radius;
//		if(dist>rarb)return null;
//		System.out.println(" check col: dist "+Math.sqrt(dist2)+"  margin "+a.bounding_radius+b.bounding_radius);
		if(dist2>rarb2)
//		if(Math.sqrt(dist2)>a.bounding_radius+b.bounding_radius)
			return null;
		if(a.is_sphere&&b.is_sphere)
			return new Object[]{'s',dist2};
		
		// check collision between sphere and plane
		if(a.is_sphere&&!b.is_sphere){
			if(b.bounding_volume==null)// b not a sphere and has no bounding planes, maybe a dot
				return new Object[]{'s',dist2};
			if(b.bounding_volume.is_sphere_inside_or_intersecting(b,a.x,a.y,a.z,a.bounding_radius))
				return new Object[]{'p'};
			return null;
		}else if(!a.is_sphere&&b.is_sphere){// b is sphere
			if(a.bounding_volume==null)// b not a sphere and has no bounding planes, maybe a dot
				return new Object[]{'s',dist2};
			if(a.bounding_volume.is_sphere_inside_or_intersecting(a,b.x,b.y,b.z,b.bounding_radius))
				return new Object[]{'p'};
			return null;
		}else{// none are sphere
			if(a.bounding_volume!=null&&b.bounding_volume!=null){
				if(bvol.check_collision(a,b))
					return new Object[]{'p'};
				return null;				
			}
			throw new Error(a+" or "+b+" has not bounding_planes");
		}
	}

	final public Collection<glob>chlds_query_using_bounding_radius(final float x,final float y,final float z,final float radius){
		final Collection<glob>ls=new ArrayList<glob>();
		final float radius2=radius;
		for(final glob g:chlds){
			final float dx=x-g.x;
			final float dy=y-g.y;
			final float dz=z-g.z;
			final float dist2=dx*dx+dy*dy+dz*dz;
			final float rarb2=g.bounding_radius2+g.bounding_radius*radius+radius2;
			if(dist2>rarb2)continue;
			ls.add(g);
		}
//		System.out.println(ls);
		return ls;
	}
	
	
	private static final long serialVersionUID=1;

	Set<glob>collisions=new HashSet<glob>();
//	final void collisions_add(final glob g){collisions.add(g);}
	
	
//	public static interface events{
//		void on_handle_collisions(final Set<glob>collisions);
//		void on_update();
//		void on_detach();
//		void on_attach();
//		void on_death();
//	}

	public static glob o;

	final public glob dxyz(final float[]xyz){dx=xyz[0];dy=xyz[1];dz=xyz[2];return this;}
	public static long timestamp_ms(){return glob.meters.time_ms;}
}
