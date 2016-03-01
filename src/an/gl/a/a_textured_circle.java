package an.gl.a;

import java.nio.FloatBuffer;

import an.gl.R;
import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_textured_circle implements iglo{
	private int nverts;
	final private int nelems=5,nelembytes=4,poslen=3,posoff=0,texlen=2,texoff=3,stride=nelems*nelembytes;
	@Override final public void iglo_load(){
		vb=glo.vertices_circle_xyzuv(5);
		nverts=vb.capacity()/nelems;
		final int[]textures=new int[1];
		GLES20.glGenTextures(1,textures,0);
		gltx=textures[0];
		glo.texture_load(gltx,R.raw.logo);
	}
	@Override final public void iglo_render(final windo cm,final glob host){
		final float[]mvp=new float[16];
//		final float[]mvp=acti.matrix();
		Matrix.multiplyMM(mvp,0,cm.matrix_world_view_projection(),0,host.matrix_model_world(),0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
//		acti.matrix_recycle(mvp);
		
		vb.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,vb);
		GLES20.glEnableVertexAttribArray(shader.apos);
		vb.position(texoff);
		GLES20.glVertexAttribPointer(shader.atx,texlen,GLES20.GL_FLOAT,false,stride,vb);
		GLES20.glEnableVertexAttribArray(shader.atx);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltx);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,nverts);
	}
	transient private FloatBuffer vb;
	private int gltx;

	public static a_textured_circle shared_instance;
	public static final long serialVersionUID=1;
}