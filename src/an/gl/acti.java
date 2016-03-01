package an.gl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import an.gl.annotations.initatinit;
import an.gl.annotations.initatinit_true;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import b.b;
final public class acti extends Activity implements Serializable,SensorEventListener{
	public static String initcls=System.getProperty(acti.class.getName(),"an.gl.a.$init");
	/// config at init
	public static boolean enable_game_server=true;
	public static boolean enable_web_server=true;
	public static boolean max_thread_priority=false;
	public static boolean save_state_to_file=false;
	public static String save_state_file_name=new File(Environment.getExternalStorageDirectory().getPath(),acti.class.getPackage().getName()+"/state.ser").toString();
	public static int default_screen_orientation=ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	public static interface keyboard extends Serializable{void keyboard_do(final int[]keys);}
	public static keyboard keyb;

	/// state
	public/*@readonly*/static sfx asfx;
	public/*@readonly*/static float touch_x,touch_y;
//	public/*@readonly*/static MotionEvent last_motion_event;
//	public/*@readonly*/static MotionEvent motion_event_at_touch;
	public/*@readonly*/static String status_line="--";

	public static cores cores;
	public static boolean on;
	/// activity interface
//	public acti(){System.out.println("acti new");}
	final @Override protected void onCreate(final Bundle bu){
		super.onCreate(bu);
		System.out.println("oncreate");
		on=true;
		aview=new view(getApplication());
		setContentView(aview);
		/// game server
		if(enable_game_server&&!net.on)new Thread(new Runnable(){@Override public void run(){
			while(on)
				try{
					net.main(new String[0]);
				}catch(final Throwable t){
					throw new Error(t);
				}
		}},net.class.getName()).start();//? how to stop
		/// web server
		if(enable_web_server&&!b.on)new Thread(new Runnable(){@Override public void run(){try{
//			b.server_port="8888";
			b.root_dir=new File(Environment.getExternalStorageDirectory().getPath(),"htp").getPath();
			b.thd_watch=false;
			b.thread_pool_size=8;
			b.save_sessions_at_shutdown=true;
			b.cacheu_tofile=false;
			b.main(new String[0]);
		}catch(final Throwable t){throw new Error(t);}}},b.class.getName()).start();//? how to stop

		sensors_oncreate();

		/// recreate state
		if(bu!=null){
			try{
				load(bu);
				post_load();
				return;
			}catch(final Throwable t){
				System.out.println(getClass().getName()+": could not load from bundle.");
			}
		}
		if(save_state_to_file){
			try{
				final File f=new File(save_state_file_name);
				if(f.exists()){
					load(f);
					post_load();
					return;
				}
			}catch(final Throwable t){
				System.out.println(getClass().getName()+": could not load from file "+save_state_file_name);
			}
		}
		try{
//			cores=new cores(2);
//			cores.run(new cores.code(){public void x()throws Throwable{
//				for(int i=0;i<10;i++){
//					System.out.println(this+"  "+i);
//					try{Thread.sleep(1000);}catch(InterruptedException ignored){return;}
//				}
//			}});
//			cores.run(new cores.code(){public void x()throws Throwable{
//				for(int i=0;i<10;i++){
//					System.out.println(this+"  "+i);
//					try{Thread.sleep(1000);}catch(InterruptedException ignored){return;}
//				}
//			}});
//			cores.run(new cores.code(){public void x()throws Throwable{
//				for(int i=0;i<10;i++){
//					System.out.println(this+"  "+i);
//					try{Thread.sleep(1000);}catch(InterruptedException ignored){return;}
//				}
//			}});
			classes_to_serialize_statics=new ArrayList<Class<?>>();
			classes_to_serialize_statics.add(glo.class);
			classes_to_serialize_statics.add(glob.class);
			classes_to_serialize_statics.add(glob.meters.class);
			classes_to_serialize_statics.add(acti.class);
			classes_to_serialize_statics.add(windo.class);
			classes_to_serialize_statics.add(shader.class);
//			shader.shader_classes=new ArrayList<>();
//			shader.shader_classes.add(shader.class);

			for(final Class<?>c:classes_to_serialize_statics){
				System.out.println("  initatinit: "+c);
				do_initatinit(c);
			}
			
			final Class<?>initcls=Class.forName(acti.initcls);
			initcls.getMethod("glos",(Class<?>[])null).invoke(null,(Object[])null);
			iglos_init_shared_instances();
			initcls.getMethod("make",(Class<?>[])null).invoke(null,(Object[])null);
			post_load();
		}catch(final Throwable t){
			System.out.println(getClass().getName()+": could not make using "+initcls+" due to "+t);
			throw new Error(t);
		}
	}
	private void post_load(){
		setRequestedOrientation(default_screen_orientation);
		if(max_thread_priority)Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}
	transient private view aview;
	final @Override protected void onResume(){
		System.out.println("onResume");
		super.onResume();
		aview.onResume();
		if(asfx!=null)throw new Error();//?
		asfx=new sfx();
		asfx.mute=true;
		asfx.start();
		sensors_onresume();
	}
	final @Override protected void onPause(){
		System.out.println("onPause");
		super.onPause();
		aview.onPause();
		asfx.on=false;
		asfx.interrupt();
		try{asfx.join();}catch(InterruptedException ignored){}
		asfx=null;
		sensors_onpause();
	}
	public/*readonly*/static ArrayList<Class<?>>classes_to_serialize_statics;
	final @Override protected void onSaveInstanceState(final Bundle b){try{
		super.onSaveInstanceState(b);
		System.out.println("onSaveInstanceState");
			
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		final ObjectOutputStream oos=new ObjectOutputStream(baos);
		for(final Class<?>c:classes_to_serialize_statics)
			serialize_statics(c.getName(),oos);
		oos.close();
		final byte[]serialized_state=baos.toByteArray();
		if(save_state_to_file){
			final File f=new File(save_state_file_name);
			f.getParentFile().mkdirs();
			final FileOutputStream os=new FileOutputStream(f);
			os.write(serialized_state);
			os.close();
		}
		b.putSerializable("oos",serialized_state);
	}catch(Throwable t){throw new Error(t);}}
	
	private void load(final byte[]oos)throws Throwable{
		final ByteArrayInputStream bais=new ByteArrayInputStream(oos);
		final ObjectInputStream ois=new ObjectInputStream(bais);
		for(final Class<?>c:classes_to_serialize_statics)
			deserialize_statics(c.getName(),ois);
//		deserialize_statics(glo.class.getName(),ois);
//		deserialize_statics(glob.class.getName(),ois);
//		deserialize_statics(glob.meters.class.getName(),ois);
//		deserialize_statics(acti.class.getName(),ois);
//		deserialize_statics(windo.class.getName(),ois);
//		deserialize_statics(shader.class.getName(),ois);
		ois.close();
		iglos_init_shared_instances();
	}

	private void load(final Bundle b){try{	
		final byte[]oos=(byte[])b.getSerializable("oos");
		load(oos);
		status_line="restored globo, "+glob.o.chlds_count()+" globs, "+glo.s.size()+" glos and "+shader.shader_classes.size()+" shaders";
		System.out.println(status_line);
		return;
	}catch(Throwable t){throw new Error(t);}}

	private void load(final File f){try{
		final long fs=f.length();
		final byte[]oos=new byte[(int)fs];//? 4G limit
		final FileInputStream fis=new FileInputStream(f);
		fis.read(oos);
		fis.close();
		load(oos);
		status_line="restored globo, "+glob.o.chlds_count()+" globs, "+glo.s.size()+" glos and "+shader.shader_classes.size()+" shaders";
		System.out.println(status_line);
		return;
	}catch(Throwable t){throw new Error(t);}}

	@Override protected void onDestroy() {
		super.onDestroy();
		on=false;
	}
	
	private void iglos_init_shared_instances(){
		/// clear shared_instances  (cancel differences from loading bundle,file or make)
		for(final iglo i:glo.s){try{
			final Field f=i.getClass().getField("shared_instance");
			f.set(null,null);
		}catch(Throwable t){throw new Error(t);}}

		for(final iglo i:glo.s){try{
//			System.out.println("   "+i.getClass().getName()+".shared_instance="+i);
//			System.out.println("  linking "+i.getClass().getName());
			final Field f=i.getClass().getField("shared_instance");
			final Object o=f.get(null);
//			System.out.println("  linking "+i.getClass().getName()+" "+o);
			if(o!=null)continue;
			f.set(null,i);
//			System.out.println("  linked "+i.getClass().getName()+" "+i);
		}catch(Throwable t){throw new Error(t);}}
	}
	
	public static void serialize_statics(final String classname,final ObjectOutputStream oos)throws Throwable{
		final Class<?>c=Class.forName(classname);
		for(final Field f:c.getDeclaredFields()){
			final int m=f.getModifiers();
			if(!Modifier.isStatic(m))continue;
			if(Modifier.isTransient(m))continue;
			if(Modifier.isFinal(m))continue;
			final String fn=f.getName();
			if("serialVersionUID".equals(fn))continue;
			final Class<?>t=f.getType();
			if(!Serializable.class.isInstance(t))continue;
			if(Modifier.isPrivate(m))
				f.setAccessible(true);
			final Serializable s=(Serializable)f.get(null);
			oos.writeObject(s);
		}
	}
	private static void do_initatinit(final Class<?>c)throws Throwable{
		for(final Field f:c.getDeclaredFields()){
			final int m=f.getModifiers();
			if(!Modifier.isStatic(m))continue;
			if(Modifier.isTransient(m))continue;
			if(Modifier.isFinal(m))continue;
			final String fn=f.getName();
			if("serialVersionUID".equals(fn))continue;
			final Annotation at=f.getAnnotation(initatinit_true.class);
			if(at!=null){
				final Class<?>t=f.getType();
				if(Modifier.isPrivate(m))
					f.setAccessible(true);
				final Serializable o=(Serializable)t.getConstructor(boolean.class).newInstance(true);
				f.set(null,o);
				return;
			}
			final Annotation a=f.getAnnotation(initatinit.class);
			if(a==null)continue;
			final Class<?>t=f.getType();
//			if(!Serializable.class.isInstance(t))throw new Error("notserializable "+t+" in field "+f+" in class "+c);
			if(Modifier.isPrivate(m))
				f.setAccessible(true);
			final Serializable o=(Serializable)t.newInstance();
			f.set(null,o);
		}
	}
	
	public static void deserialize_statics(final String classname,final ObjectInputStream ois){try{
		final Class<?>c=Class.forName(classname);
		for(final Field f:c.getDeclaredFields()){
			final int m=f.getModifiers();
			if(!Modifier.isStatic(m))continue;
			if(Modifier.isTransient(m))continue;
			if(Modifier.isFinal(m))continue;
			final String fn=f.getName();
			if("serialVersionUID".equals(fn))continue;
			final Class<?>t=f.getType();
			if(!Serializable.class.isInstance(t))continue;
			if(Modifier.isPrivate(m))
				f.setAccessible(true);
				
			final Serializable s=(Serializable)ois.readObject();
			f.set(null,s);
		}
	}catch(final Throwable t){throw new Error(t);}}

	final public int key_bit_volume_up=1;
	final public int key_bit_volume_down=2;
	final public int key_bit_back=4;
	private long render_hud_press_timestamp_ms=0;
	static public/*@readonly*/int fps;
	final @Override public boolean dispatchKeyEvent(final KeyEvent e){
		if(e.getRepeatCount()!=0)return true;
		final int keyCode=e.getKeyCode();
		if(keyCode==KeyEvent.KEYCODE_MENU){
			final int action=e.getAction();
			if(action==KeyEvent.ACTION_DOWN){
				windo.w.render_hud(true);
				render_hud_press_timestamp_ms=acti.time_millis();
			}
			else if(action==KeyEvent.ACTION_UP){
				final long dt_ms=acti.time_millis()-render_hud_press_timestamp_ms;
				if(dt_ms<3000){
					windo.w.render_hud(false);
				}
				vibrator_vibrate();
			}
		}else if(keyCode==KeyEvent.KEYCODE_BACK){
			final int action=e.getAction();
			if(action==KeyEvent.ACTION_DOWN){vibrator_vibrate(64);}
			else if(action==KeyEvent.ACTION_UP){
				shader.activate_next();
				asfx.mute=!asfx.mute;
				vibrator_vibrate(32,.5f,.2f);
			}
		}else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
			final int action=e.getAction();
			if(action==KeyEvent.ACTION_DOWN){keys[0]|=key_bit_volume_up;}
			else if(action==KeyEvent.ACTION_UP){keys[0]^=key_bit_volume_up;}
		}else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
			final int action=e.getAction();
			if(action==KeyEvent.ACTION_DOWN){keys[0]|=key_bit_volume_down;}
			else if(action==KeyEvent.ACTION_UP){keys[0]^=key_bit_volume_down;}
		}
//		System.out.println(" keys:"+keys+"   "+e);
		return true;
	}
	private static int[]keys=new int[1];
	final @Override public boolean dispatchTouchEvent(final MotionEvent e){
		final event_motion ev=new event_motion(e);
		if(e.getAction()==MotionEvent.ACTION_DOWN){
			event_motion_at_touch=ev;
		}
		touch_x=e.getX();
		touch_y=e.getY();
		event_motion_current=ev;
		System.out.println(event_motion_current);
		return true;
	}
	final public static class event_motion implements Serializable{
		public int action;
		public float pointers_xy[][];
		public event_motion(final MotionEvent e){
			action=e.getAction();
			final int c=e.getPointerCount();
			pointers_xy=new float[c][2];
			for(int i=0;i<pointers_xy.length;i++){
				pointers_xy[i][0]=e.getX(i);
				pointers_xy[i][1]=e.getY(i);
			}
		}
		public String toString(){
			final StringBuilder s=new StringBuilder();
			s.append("event_motion ").append(action).append(" ");
			for(int i=0;i<pointers_xy.length;i++){
				s.append(" p").append(i).append("=").append((int)pointers_xy[i][0]).append(" ").append((int)pointers_xy[i][1]).append("  ");
			}
			return s.toString();
		}
		
		public final int action_up=MotionEvent.ACTION_UP,action_down=MotionEvent.ACTION_DOWN;		
		private static final long serialVersionUID=1;
	}
	public static event_motion event_motion_at_touch;
	public static event_motion event_motion_current;
	
	/// matrix
//	private static transient ConcurrentLinkedQueue<float[]>matrix_pool=new ConcurrentLinkedQueue<>();
//	public static int matrix_count;
//	public static float[]matrix(){
//		float[]m=matrix_pool.poll();
//		if(m==null){
//			m=new float[16];
//			matrix_count++;
//		}
//		return m;
//	}
//	public static void matrix_recycle(final float[]matrix4x4){
//		matrix_pool.add(matrix4x4);
//	}

	/// vector math
	/// performs worse than new
//	private static transient ConcurrentLinkedQueue<float[]>vector_pool=new ConcurrentLinkedQueue<>();
//	public static int vector_count;
//	public static float[]vec(final float x,final float y,final float z){
//		float[]v=vector_pool.poll();
//		if(v==null){
//			v=new float[4];
//			vector_count++;
//		}
//		v[0]=x;v[1]=y;v[2]=z;v[3]=1;
//		return v;
//	}
//	public static void vec_recycle(final float[]vector4){
//		vector_pool.add(vector4);
//	}
	public static void vec_normalize(final float[]vector){
		final float magnitude_inv=(float)(1/Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]));
		vector[0]*=magnitude_inv;
		vector[1]*=magnitude_inv;
		vector[2]*=magnitude_inv;
	}
	public static float vec_dot(final float[]v1,final float[]v2){
		float res=0;
		for(int i=0;i<v1.length;i++)
			res+=v1[i]*v2[i];
		return res;
	}
	public static void vec_minus(final float[]result,final float[]p1,final float[]p2){
		result[0]=p1[0]-p2[0];
		result[1]=p1[1]-p2[1];
		result[2]=p1[2]-p2[2];
		result[3]=p1[3]-p2[3];
	}
	public static void vec_cross(float[]result,float[]p1,float[]p2){//? result can not be any p
		result[0]=p1[1]*p2[2]-p2[1]*p1[2];
		result[1]=p1[2]*p2[0]-p2[2]*p1[0];
		result[2]=p1[0]*p2[1]-p2[0]*p1[1];
	}
	public static float[]vec_normalize_projected(float x,float y){
		final float xn=x*2/windo.w.width()-1;
		final float yflip=windo.w.height()-y;
		final float yn=yflip*2/windo.w.height()-1;
		return new float[]{xn,yn,-1,1};
	}
	public static void vec_p(float[]v){
		for(final float f:v)
			System.out.print(f+"  ");
		System.out.println();
	}
	public static float[]vec_set(final float[]v,final float x,final float y,final float z){
		v[0]=x;v[1]=y;v[2]=z;v[3]=1;
		return v;
	}
	/// static interface
	public static void vibrator_vibrate(){vibrator_vibrate(128);}
	public static void vibrator_vibrate(final int len){vibrator_vibrate(len,5,40);}
	public static void vibrator_vibrate(final int len,final float vib_probability,final float pause_probability){
		final Vibrator v=(Vibrator)a.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(vibrator_generate_random_pattern(len,vib_probability,pause_probability),-1);
	}
	public static long[]vibrator_generate_random_pattern(final int len,final float vib_scale,final float pause_scale){
		final long[]p=new long[1+len*2];
		for(int i=1;i<p.length;){
			p[i++]=(int)(vib_scale*Math.random());
			p[i++]=(int)(pause_scale*Math.random());
		}
		return p;
	}
	public static InputStream inputstream_from_asset(final String path){
		try{return a.getApplication().getAssets().open(path);}catch(final IOException e){throw new Error(e);}
	}
	public static InputStream inputstream_from_resource(final int raw_resource_id){
		final InputStream is=a.getResources().openRawResource(raw_resource_id);
		return is;
	}
	public static Typeface font_load(final String path){
		return Typeface.createFromAsset(a.getAssets(),"fonts/"+path);
	}
//	public static Typeface font_load_from_resource(final String path){
//		return Typeface.createFromAsset(a.getAssets(),"fonts/"+path);
//	}
	public static String ip_address(){
		final WifiManager wifiManager=(WifiManager)a.getSystemService(WIFI_SERVICE);
		final WifiInfo wifiInfo=wifiManager.getConnectionInfo();
		final int ipAddress = wifiInfo.getIpAddress();
		final String ip=String.format(Locale.US,"%d.%d.%d.%d",(ipAddress&0xff),(ipAddress>>8&0xff),(ipAddress>>16&0xff),(ipAddress>>24&0xff));
		return ip;
	}
	public static String string_from_resource(final String path){try{
		final InputStream in=view.class.getResourceAsStream(path);
		final ByteArrayOutputStream out=new ByteArrayOutputStream(4096);
		if(in==null)return null;
		final byte[]buf=new byte[4096];
		while(true){final int count=in.read(buf);if(count<=0)break;out.write(buf,0,count);}
		return new String(out.toByteArray(),"utf8");
	}catch(final Throwable t){throw new Error(t);}}
	
	public static long time_millis(){
		return System.currentTimeMillis();
	}
	public static interface console extends Serializable{
		console p(final CharSequence cs);
		console nl();
		console pl(final CharSequence cs);
	}
	public static console con=new console(){
		public console nl(){System.out.println();return this;};
		public console p(final CharSequence cs){System.out.print(cs);return this;};
		public console pl(final CharSequence cs){System.out.println(cs);return this;};
		private static final long serialVersionUID=1;
	};
	/////////////////////////////



	/// app key handler
	static void handle_keys(){
		if(keyb==null)return;
		keyb.keyboard_do(keys);
	}

	
	
	
	// sensors
	private SensorManager sensors;
	private Sensor sensor_accel;
	private Sensor sensor_geomag;
	private Sensor sensor_grav;
	private Sensor sensor_gyro;
	private Sensor sensor_linacc;
	private Sensor sensor_rotvec;
	private Sensor sensor_light;
	private Sensor sensor_proxim;
	private Sensor sensor_ambient_temp;
	private Sensor sensor_pressure;
	private Sensor sensor_rel_humidity;
	private void sensors_oncreate(){
		sensors=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		final List<Sensor>deviceSensors=sensors.getSensorList(Sensor.TYPE_ALL);
//		System.out.println("sensors: "+deviceSensors);
		for(final Sensor s:deviceSensors){
			System.out.println("sensor type "+s.getType()+"  name:"+s.getName());
		}
//		sensor_light=sensors.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensor_geomag=sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		sensor_proxim=sensors.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//		sensor_accel=sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		sensor_grav=sensors.getDefaultSensor(Sensor.TYPE_GRAVITY);
//		sensor_gyro=sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//		sensor_linacc=sensors.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//		sensor_rotvec=sensors.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//		sensor_ambient_temp=sensors.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
//		sensor_pressure=sensors.getDefaultSensor(Sensor.TYPE_PRESSURE);
//		sensor_rel_humidity=sensors.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
	}
	private void sensors_onresume(){
		if(sensor_light!=null)sensors.registerListener(this, sensor_light,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_geomag!=null)sensors.registerListener(this, sensor_geomag,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_proxim!=null)sensors.registerListener(this, sensor_proxim,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_accel!=null)sensors.registerListener(this,sensor_accel,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_grav!=null)sensors.registerListener(this,sensor_grav,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_gyro!=null)sensors.registerListener(this,sensor_gyro,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_linacc!=null)sensors.registerListener(this,sensor_linacc,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_rotvec!=null)sensors.registerListener(this,sensor_rotvec,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_ambient_temp!=null)sensors.registerListener(this,sensor_ambient_temp,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_pressure!=null)sensors.registerListener(this,sensor_pressure,SensorManager.SENSOR_DELAY_NORMAL);
		if(sensor_rel_humidity!=null)sensors.registerListener(this,sensor_rel_humidity,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	// SensorEventListener
	private void sensors_onpause(){
		sensors.unregisterListener(this);
	}
	@Override public final void onAccuracyChanged(final Sensor sensor,final int accuracy){
		System.out.println("onaccuracychanged "+accuracy);
	}
	final static public class sensors_overview{
		public static float geomag_y;
	}
	@Override public final void onSensorChanged(final SensorEvent ev) {
		if(ev.sensor==sensor_geomag){
//			System.out.print("geomag: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
			if(geomag_sensor_connected_glob==null)
				return;
//			geomag_sensor_connected_glob.angle_y(geomag_sensor_connected_glob_amplifier_scalar*ev.values[0]);
			sensors_overview.geomag_y=ev.values[1];
			geomag_sensor_connected_glob.angle_z(geomag_sensor_connected_glob_amplifier_scalar*ev.values[1]);
			
//			acti.con.pl(""+ev.values[1]);
//			geomag_sensor_connected_glob.angle_x(geomag_sensor_connected_glob_amplifier_scalar*ev.values[2]);
		} else if(ev.sensor==sensor_proxim){
//			System.out.print("proxim: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		} else if(ev.sensor==sensor_accel){
//			System.out.print("accel: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_grav){
//			System.out.print("grav: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_gyro){
//			System.out.print("gyro: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_linacc){
//			System.out.print("linacc: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_rotvec){
//			System.out.print("rotvec: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_ambient_temp){
//			System.out.print("ambtemp: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_pressure){
//			System.out.print("preassure: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else if(ev.sensor==sensor_rel_humidity){
//			System.out.print("relhumidity: ");
//			for(float f:ev.values)System.out.print(f+"  ");
//			System.out.println();
		}else System.out.println("unknown event "+ev);
	}
	public static glob geomag_sensor_connected_glob;
	public static float geomag_sensor_connected_glob_amplifier_scalar;
	
	transient private static acti a;{a=this;}
	private static final long serialVersionUID = 1L;
	public static void vec_scale_xyz(final float[]result,final float[]v,final float scale){
		result[0]=v[0]*scale;
		result[1]=v[1]*scale;
		result[2]=v[2]*scale;
	}
}
