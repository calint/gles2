package an.gl.a;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import an.gl.acti;
import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.sfx.tone_generator;
import an.gl.shader;
import an.gl.windo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
public class a_hud implements iglo{
	final private int nelems=5,nelembytes=4,stride=nelems*nelembytes,posoff=0,poslen=3,texoff=3,texlen=2;
	private int nverts;
	@Override final public void iglo_load(){
		bv=glo.vertices_xyzuv_square();
		nverts=bv.capacity()/nelems;
		final int[]textures=new int[1];
		GLES20.glGenTextures(1,textures,0);
		gltx=textures[0];
		glo.texture_init(gltx);
		ttf_tini=acti.font_load("tini.ttf");//? cache in acti
	}
	public static long refresh_every_nth_ms=100;
	public float hud_y=-.5f;
	@Override final public void iglo_render(final windo cm,final glob host){
		final float[]mvp=new float[16];
		Matrix.setIdentityM(mvp,0);
		Matrix.translateM(mvp,0,0,hud_y,-1);// put hud infront
		Matrix.scaleM(mvp,0,1,bmp_ratio,0);// bmp_ratio not set first run ok
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);

		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		bv.position(texoff);
		GLES20.glVertexAttribPointer(shader.atx,texlen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.atx);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltx);
		if(glob.time()-gldraw_lastdraw>refresh_every_nth_ms){
			gldraw_lastdraw=glob.time();
			final Bitmap bmp=Bitmap.createBitmap(windo.w.width(),36,Bitmap.Config.ARGB_4444);
			bmp_ratio=(float)bmp.getHeight()/bmp.getWidth();
			final Canvas c=new Canvas(bmp);
			at_refresh(c);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bmp,0);
			bmp.recycle();
		}
		GLES20.glDisableVertexAttribArray(shader.acol);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,nverts);
	}
	private long gldraw_lastdraw;
	private float bmp_ratio=1;
	private static String num(final int n){return String.format("%02d",n);}
	private static String num3(final int n){return String.format("%03d",n);}
	protected void at_refresh(final Canvas c){
		c.drawColor(0,Mode.CLEAR);
//		bitmap.eraseColor(0x00808080);
//		final int argb=(0x80&0xFF)<<24;
//		canvas.drawColor(argb,Mode.DST_IN);
		final Paint tp=new Paint();
		tp.setTypeface(ttf_tini);
		tp.setTextSize(8);
		tp.setAntiAlias(true);
		final float x,y;
		if(acti.event_motion_at_touch==null){
			x=y=0;
		}else{
			x=acti.touch_x;
			y=acti.touch_y;
		}
		tp.setARGB(0xff,0x80,0,0x80);
		c.drawText("  x "+(int)x+" y "+(int)y+"  fps "+acti.fps+"  dt "+(int)(1000*glob.dt(1)),0,8,tp);
		tp.setTextSize(4);
		tp.setARGB(0xff,0,0,0x80);
		c.drawText("globs "+glob.meters.globs+"  grids "+glob.meters.grids+"  rend "+glob.meters.globs_rend+"/"+glob.meters.globs_rend_already+"  cull "+glob.meters.globs_render_outside_viewpyr+"  upd "+glob.meters.globs_update+"/"+glob.meters.globs_update_already+"  grdr "+glob.meters.grids_rendered+"/"+glob.meters.grids_render_skipped,0,14,tp);
		tp.setARGB(0xff,0,0x80,0x80);
		c.drawText(" [ "+num(glob.meters.grid_ms)+"  "+num(glob.meters.update_ms)+"  "+num(glob.meters.collision_detection_ms)+"  "+num(glob.meters.rend_ms)+" ]  [ "+num3(glob.meters.coldet_bounding_spheres_check)+"  "+num3(glob.meters.bvol_check_collision)+"  "+num3(glob.meters.collisions)+" ] [ "+num3(glob.meters.bvol_world_coords_cache_update)+" ] "+num3((glob.meters.grids_put_per_sec>>10))+" kglobs/s",0,18,tp);
		final StringBuilder sb=new StringBuilder();
		if(acti.asfx!=null){
			for(final tone_generator tg:acti.asfx.tone_generators){
				if(tg==null)continue;
				sb.append((int)tg.freq).append(" ");
			}
		}
		tp.setARGB(0xff,0,0x80,0);
		c.drawText("  puts "+glob.meters.grids_put_count+"/"+glob.meters.grids_put_skip_count+"  score "+$init.score+" sfx "+sb+"  geoy "+(int)acti.sensors_overview.geomag_y,0,22,tp);
		tp.setARGB(0xff,0x80,0,0x80);
		c.drawText(" "+acti.ip_address()+":"+an.gl.net.cfg.net_server_port+"  "+an.gl.net.status,0,30,tp);
		tp.setARGB(0xff,0,0x80,0x80);
		c.drawText("on port "+b.b.server_port+" web server sessions "+b.thdwatch.sessions+"  in/out "+(b.thdwatch.input>>10)+"/"+(b.thdwatch.output>>10)+" kb",0,34,tp);
	}
	transient private Typeface ttf_tini=acti.font_load("tini.ttf");
	transient private FloatBuffer bv;
	private int gltx;
	
	public static a_hud shared_instance;
	public static final long serialVersionUID=1;
}