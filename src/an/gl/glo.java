package an.gl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import an.gl.annotations.initatinit;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
public class glo implements iglo{
	private final int posoff=0,poslen=3,texoff=3,texlen=2,nelems=5,nelembytes=4,stride=nelems*nelembytes;
	private int nverts;
	final @Override public void iglo_load(){
//		System.out.println("glload "+this);
		vertices=make_vertices_buffer();
		nverts=vertices.capacity()/5;
		final int[]textures=new int[1];
		GLES20.glGenTextures(1,textures,0);
		gltx=textures[0];
		texture_load(gltx,get_texture_raw_resource_id());
	}
	final @Override public void iglo_render(final windo cm,final glob host){
		final float[]mvp=new float[16];
		Matrix.multiplyMM(mvp,0,cm.matrix_world_view_projection(),0,host.matrix_model_world(),0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);

		if(vertices==null)
			System.out.println(this+"   "+host);

		vertices.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,vertices);
		GLES20.glEnableVertexAttribArray(shader.apos);
		vertices.position(texoff);
		GLES20.glVertexAttribPointer(shader.atx,texlen,GLES20.GL_FLOAT,false,stride,vertices);
		GLES20.glEnableVertexAttribArray(shader.atx);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltx);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,nverts);
	}
	
	// to override
	protected FloatBuffer make_vertices_buffer(){return vertices_xyzuv_square();}
	protected int get_texture_raw_resource_id(){return R.raw.logo;}

	public static FloatBuffer vertices_xyzrgb_square(){
		final FloatBuffer fb=ByteBuffer.allocateDirect(4*6*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		xyzrgb(fb,-1,-1,0,1,1,1);
		xyzrgb(fb, 1,-1,0,1,1,1);
		xyzrgb(fb, 1, 1,0,1,1,1);
		xyzrgb(fb,-1, 1,0,1,1,1);
		fb.position(0);
		return fb;
	}
	public static FloatBuffer vertices_circle_xyzuv(final int nsegs) {
		final float[]xyzuv=new float[(2+nsegs)*5];// origo at xyzuv[0] and at the end
		final double dr=Math.PI*2/nsegs;
		double r=0;
		int i=5;// .origo at 0
		for(int k=0;k<nsegs;k++,r+=dr){
			final float x=(float)Math.cos(r);
			final float y=(float)Math.sin(r);
			final float z=0;
			xyzuv[i++]=x;
			xyzuv[i++]=y;
			xyzuv[i++]=z;
			xyzuv[i++]=x;
			xyzuv[i++]=-y;
		}
		xyzuv[i++]=xyzuv[5];
		xyzuv[i++]=xyzuv[6];
		xyzuv[i++]=xyzuv[7];
		xyzuv[i++]=xyzuv[8];
		xyzuv[i++]=xyzuv[9];
		return (FloatBuffer)ByteBuffer.allocateDirect(xyzuv.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(xyzuv).position(0);
	}
	public static FloatBuffer vertices_circle_xyzrgb(final int nsegs,final float r,final float g,final float b){
		final float[]xyzrgb=new float[(2+nsegs)*6];// origo at xyzrgb[0] and at the end
		final double dr=Math.PI*2/nsegs;
		double rr=0;
		int i=5;// .origo at 0
		for(int k=0;k<nsegs;k++,rr+=dr){
			final float x=(float)Math.cos(rr);
			final float y=(float)Math.sin(rr);
			final float z=0;
			xyzrgb[i++]=x;
			xyzrgb[i++]=y;
			xyzrgb[i++]=z;
			xyzrgb[i++]=r;
			xyzrgb[i++]=g;
			xyzrgb[i++]=b;
		}
		xyzrgb[i++]=xyzrgb[5];
		xyzrgb[i++]=xyzrgb[6];
		xyzrgb[i++]=xyzrgb[7];
		xyzrgb[i++]=xyzrgb[8];
		xyzrgb[i++]=xyzrgb[9];
		xyzrgb[i++]=xyzrgb[10];
		return (FloatBuffer)ByteBuffer.allocateDirect(xyzrgb.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(xyzrgb).position(0);
	}
	public static FloatBuffer vertices_circle_xyz(final int nsegs){
		final FloatBuffer fb=ByteBuffer.allocateDirect(nsegs*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			final double dr=Math.PI*2/nsegs;
			double r=0;
			for(int k=0;k<nsegs;k++,r+=dr){
				final float x=(float)Math.cos(r);
				final float y=(float)Math.sin(r);
				final float z=0;
				xyz(fb,x,y,z);
			}
			fb.position(0);
			return fb;
	}
	public static FloatBuffer vertices_circle_xyz_plane_xz(final int nsegs){
		final FloatBuffer fb=ByteBuffer.allocateDirect(nsegs*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			final double dr=Math.PI*2/nsegs;
			double r=0;
			for(int k=0;k<nsegs;k++,r+=dr){
				final float x=(float)Math.cos(r);
				final float y=0;
				final float z=(float)Math.sin(r);
				xyz(fb,x,y,z);
			}
			fb.position(0);
			return fb;
	}

	transient private FloatBuffer vertices;
	private int gltx;

	// utils
	public static void texture_load(final int gltx,final int texture_raw_resource_id){
		texture_init(gltx);
		final InputStream is=acti.inputstream_from_resource(texture_raw_resource_id);
		texture_load(gltx,is);
	}
	public static void texture_init(final int gltx){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltx);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
	}	
	public static void texture_load(final int gltx,final String asset_path){
		texture_init(gltx);
		final InputStream is=acti.inputstream_from_asset(asset_path);
//		final InputStream is=acti.get().getResources().openRawResource(texture_raw_resource_id);
		texture_load(gltx,is);
	}
	public static void texture_load(final int gltx,final InputStream is){
		final Bitmap bitmap;try{bitmap=BitmapFactory.decodeStream(is);}finally{try{is.close();}catch(IOException e){throw new Error(e);}}
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
		bitmap.recycle();
	}
	public static FloatBuffer vertices_xyzuv_square(){
		final FloatBuffer fb=ByteBuffer.allocateDirect(4*5*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		xyzuv(fb,-1,-1,0,0, 1);
		xyzuv(fb, 1,-1,0,1, 1);
		xyzuv(fb, 1, 1,0,1, 0);
		xyzuv(fb,-1, 1,0,0, 0);
		fb.position(0);
		return fb;
	}
	public static FloatBuffer vertices_xyzrgb_square_plane_xz(){
		final FloatBuffer fb=ByteBuffer.allocateDirect(4*6*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		xyzrgb(fb,-1,0,-1,1,1,1);
		xyzrgb(fb, 1,0,-1,1,1,1);
		xyzrgb(fb, 1,0, 1,1,1,1);
		xyzrgb(fb,-1,0, 1,1,1,1);
		fb.position(0);
		return fb;
	}
	public static void xyzrgb(final FloatBuffer fb,final float x,final float y,final float z,final float r,final float g,final float b){
		fb.put(x).put(y).put(z).put(r).put(g).put(b);
	}
	public static void xyzuv(final FloatBuffer fb,final float x,final float y,final float z,final float u,final float v){
		fb.put(x).put(y).put(z).put(u).put(v);
	}
	public static void xyz(final FloatBuffer fb,final float x,final float y,final float z){
		fb.put(x).put(y).put(z);
	}
	public static void xyzrgba(final FloatBuffer fb,final float x,final float y,final float z,final float r,final float g,final float b,final float a){
		fb.put(x).put(y).put(z).put(r).put(g).put(b).put(a);
	}
	
	public static @initatinit ArrayList<iglo>s;
	public static glo shared_instance;
	private static final long serialVersionUID = 1L;
}