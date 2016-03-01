package an.gl;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

final public class a_progressbar implements iglo{
	private int nverts;
	final private int nelems=5,nelembytes=4,posoff=0,poslen=3,stride=nelems*nelembytes;
	@Override final public void iglo_load(){
		bv=glo.vertices_xyzuv_square();
		nverts=bv.capacity()/nelems;
	}
	@Override final public void iglo_render(final windo cm,final glob host){
	}
	final public void draw(final float y,final float scale){
		final float[]mvp=new float[16];
		Matrix.setIdentityM(mvp,0);
		Matrix.translateM(mvp,0,0,y,0);
		Matrix.scaleM(mvp,0,1, scale,0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
//		GLES20.glDisableVertexAttribArray(shader.atx);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP,0,nverts);
	}
	transient private FloatBuffer bv;

	private static final long serialVersionUID=1;
}
