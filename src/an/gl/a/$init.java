package an.gl.a;

import java.util.ArrayList;

import an.gl.a_grid;
import an.gl.a_grid_plane_xz;
import an.gl.acti;
import an.gl.acti.event_motion;
import an.gl.glo;
import an.gl.glob;
import an.gl.grid;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.view.MotionEvent;

final public class $init{
	public static void glos(){
		System.out.println("glos");
		// each iglo class has a shared_instance
		// the shared instance is set by acti.link_iglos() before make() is called 
//		glo.s.add(new glo());
		glo.s.add(new a_axis_xyz());
		glo.s.add(new a_bounding_circle());
		glo.s.add(new a_bounding_circle_plane_xz());
		glo.s.add(new a_bounding_box());
		glo.s.add(new a_bounding_box_plane_xz());
//		glo.s.add(new a_textured_circle());
		glo.s.add(new a_hud());
//		glo.s.add(new a_triangle());
		glo.s.add(new a_textured_square());
//		glo.s.add(new a_tiler());
		glo.s.add(new a_grid());
		glo.s.add(new a_grid_plane_xz());
//		glo.s.add(new a_obj_iglo());
//		glo.s.add(new a_f3d_iglo());
		glo.s.add(new a_building());
		glo.s.add(new a_recon());
		glo.s.add(new a_ufo());
		glo.s.add(new a_trace());
		glo.s.add(new a_explosion());
	}
	public static void make(){
		System.out.println("make");
//		acti.default_screen_orientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		glob.o=new glob(true);
//		glob.grid=new grid(32,0,32,0,0);// 1x1 grid
//		glob.grid=new grid(32,1,32,0,0);// 2x2 grid 
//		glob.grid=new grid(32,2,32,0,0);// 4x4 grid 
//		glob.grid=new grid(16,3,32,0,0);// 8x8 grid 
//		glob.grid=new grid(16,0,32,0,0);// 1x1 grid 
//		glob.grid=new grid(8,3,32,0,0);// 8x8 grid 
//		glob.grid=new grid(4,3,32,.25f,0,.25f);// 8x8 grids covering 8x8 units
//		glob.grid=new grid(4,3,32,0,0,0);// 8x8 grids covering 8x8 units
//		glob.grid=new grid(4,2,32,0,0,0);// 4x4 grids covering 8x8 units, 16 cores
		glob.grid=new grid(16,2,64,0,0,0);// 4x4 grids covering 32x32 units, 16 cores, max 64x64/2 colchecks
//		glob.grid=new grid(50,2,64,0,0,0);// 4x4 grids covering 8x8 units, 16 cores, max 64x64/2 colchecks

		shader.shader_classes=new ArrayList<Class<? extends shader>>();
		shader.shader_classes.add(shader.class);
		shader.shader_classes.add(shader_bo.class);
		shader.shader_classes.add(shader_zish.class);

//		acti.classes_to_serialize_statics.add(etc.class);

		windo.w=new windo();
		windo.w.clip(1,glob.grid.size()*3).perspective_projection(true).hud(a_hud.shared_instance).render_hud(true);
//		windo.w.xyz(0,1,3).dx(-.2f);
//		windo.w.xyz(0,0,3);
//		windo.w.lookat_xyz(0,0,0).xyz(0,4,.05f);
		windo.w.lookat_xyz(0,0,0).xyz(0,4,2);//.dy(1).dz(1);
//		windo.enable_cull_back_face=true;
//		windo.w.draw_grid_borders=false;
		
		glob.overlay_iglos=new ArrayList<iglo>();
		glob.overlay_iglos.add(a_axis_xyz.shared_instance);
		glob.overlay_iglos.add(a_bounding_circle.shared_instance);
		glob.overlay_iglos.add(a_bounding_circle_plane_xz.shared_instance);
		glob.overlay_iglos.add(a_bounding_box.shared_instance);
		glob.overlay_iglos.add(a_bounding_box_plane_xz.shared_instance);

		glob.o.scale(glob.grid.size()).angle_x(-90);
		glob.o.iglo(a_textured_square.shared_instance);


		final glob ufo=new glob_ufo().y(4).scale_xyz(1,.3f,1).angle_xyz_delta_t(0,36,0).recalc_bounding_radius_using_scale();
		final glob recon=new glob_recon();
		acti.geomag_sensor_connected_glob=recon;
//		acti.geomag_sensor_connected_glob=windo.w;
		acti.geomag_sensor_connected_glob_amplifier_scalar=1;

//		new glob_building(0,glob_building.scale,0);
		setup_town_scape();
		
//		setup_scene_2();
		
//		final float s=glob.grid.size();
//		final float dx=1;
//		float street_z=1;
//		for(float x=-s;x<=s;x+=dx){
//			new glob_building(x,street_z,.25f);
//		}
//		street_z-=2;
//		for(float x=-s;x<=s;x+=dx){
//			new glob_building(x,street_z,.5f);
//		}
//		street_z-=2;
//		for(float x=-s;x<=s;x+=dx){
//			new glob_building(x,street_z,1);
//		}
		
		
		acti.keyb=new acti.keyboard(){private static final long serialVersionUID=1;@Override public void keyboard_do(final int[]keys){
	//		System.out.println(" buttons:"+keys[0]);
			if((keys[0]&1)!=0){acti.asfx.frq+=glob.dt(50);}
			if((keys[0]&2)!=0){acti.asfx.frq-=glob.dt(50);}
			final event_motion me=acti.event_motion_current;
			if(me==null)return;
			acti.event_motion_current=null;
			final float scr_wi=windo.w.width();
			final float scr_hi=windo.w.height();
			for(final float[]xy:me.pointers_xy){
				final float x_screen=xy[0];
				final float y_screen=xy[1];
				final float x_norm=x_screen/scr_wi;
				final float y_norm=y_screen/scr_hi;
				final int tg=x_norm>.5f?0:1;/// left or right part of screen
				acti.asfx.tone_generators[tg].freq=(tg+1)*110.f*y_norm;
			}
			
			if(me.action==MotionEvent.ACTION_DOWN){
				fire=true;
			}else if(me.action==MotionEvent.ACTION_UP){
				fire=false;
			}
			if(fire){
				final long t=acti.time_millis();
				final long dt=t-fire_last_t_ms;
				if(dt<fire_rate_ms)return;
				fire_last_t_ms=t;
				for(final float[]xy:me.pointers_xy){
					final float x_screen=xy[0];
					final float y_screen=xy[1];
					final float[]p_world=windo.w.window_to_world_coord(x_screen,y_screen);
					final float[]origo=new float[]{windo.w.x(),windo.w.y(),windo.w.z(),1};
					final float[]velocity=new float[4];
					acti.vec_minus(velocity,p_world,origo);
//					acti.vec_scale();
					acti.vec_scale_xyz(velocity,velocity,glob_laser_velocity_scale);
					new glob_laser(p_world,velocity);//.angle_x(windo.w.angle_x()).angle_y(windo.w.angle_y()).angle_z(windo.w.angle_z());
				}
				return;
			}
//			if(1==1)return;
//			
//			
//			
//			
//			
//			
//			final float x=me.pointers_xy[0][0],y=me.pointers_xy[0][1];
//			
//			///? review
//			// screen to normalized clip space
//			final float xn=x*2/windo.w.width()-1;
//			final float yflip=windo.w.height()-y;
//			final float yn=yflip*2/windo.w.height()-1;
//			final float[]vec_normalized_projected=new float[]{xn,yn,-1,1};
//			final float[]vec_world=new float[4];
//			final float[]mtx_wvp_inv=windo.w.matrix_world_view_projection_inverted();
//			Matrix.multiplyMV(vec_world,0,mtx_wvp_inv,0,vec_normalized_projected,0);
//			vec_world[0]/=vec_world[3];
//			vec_world[1]/=vec_world[3];
//			vec_world[2]/=vec_world[3];
//			vec_world[3]=1;
//			if(windo.w.is_perspective_projection()){
//				final float z0=vec_world[2]+windo.w.clip_near();
//				vec_world[0]=vec_world[0]*z0;// x at z=0
//				vec_world[1]*=z0;// y at z=0
//				vec_world[2]=0;
//			}else{
//				vec_world[2]=0;
//			}
//			final float p=440*2;
//			acti.asfx.frq=p*vec_normalized_projected[1];
//			final float dist2=vec_world[0]*vec_world[0]+vec_world[1]*vec_world[1];
//			final float margin2=.5f*.5f;
//			if(dist2<margin2)
//				acti.asfx.mute=!acti.asfx.mute;
//			if(me.action==MotionEvent.ACTION_UP){
//				fling(vec_world,me,acti.event_motion_at_touch);
//			}else if(me.action==MotionEvent.ACTION_DOWN){
//				acti.asfx.frq=yn*p;
//			}
		}private boolean fire;};
	}
	private static void setup_scene_2() {
		glob.o.scale(glob.grid.size()).angle_x(-90);
		glob.o.iglo(a_textured_square.shared_instance);

		new glob_recon().x(-1);
		new glob_recon().x(-3);
		new glob_recon().x(-5);
		new glob_recon().x(-7);
		new glob_recon().x(-9);
		new glob_ufo().x(1);
		acti.geomag_sensor_connected_glob=new glob_recon();
//		acti.geomag_sensor_connected_glob=windo.w;
		acti.geomag_sensor_connected_glob_amplifier_scalar=1;

		setup_town_scape();
	}
	private static void setup_town_scape() {
		float x,z;
		final float s=glob.grid.size()/2;
		x=z=-s;
		final float step=2;
		final float max_height=4;
		while(z<s){
			x=-s;
			z+=step;
			if(z==0)continue;
			while(x<s){
				x+=step;
				if(x==0)continue;
				final int floors=(int)(max_height*Math.random());
				for(int i=0;i<floors;i++){
					new glob_building(x,glob_building.scale+glob_building.scale*2*i,z);//.angle_y_delta_t(i*45);
				}
			}
		}
	}
	private static void fling(final float[]world_coord,final acti.event_motion current,final acti.event_motion at_touch){
		if(current==null)
			return;
		if(at_touch==null)
			return;
		final float dx=fling_knob_x*(current.pointers_xy[0][0]-at_touch.pointers_xy[0][0]);
		final float dy=fling_knob_y*(current.pointers_xy[0][1]-at_touch.pointers_xy[0][1]);
		final float gravity=.1f;
		new glob_fling(world_coord,dx,dy);
	}
	private static long fire_last_t_ms;


	public static int score;
	public static float fling_knob_x=.02f,fling_knob_y=-.05f,bounce_factor=.7f;
	public static int fire_rate_ms=100;
	public static float glob_laser_velocity_scale=6;
}
