package an.gl.a;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import an.gl.acti;
import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_axis_xyz implements iglo{
	final private int nverts=6,nelems=7,nelembytes=4,stride=nelems*nelembytes,posoff=0,poslen=3,coloff=3,collen=4;
	@Override final public void iglo_load(){
		bv=ByteBuffer.allocateDirect(nverts*nelems*nelembytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		glo.xyzrgba(bv,0,0,0,1,0,0,1);// 0
		glo.xyzrgba(bv,1,0,0,1,0,0,1);// 1
		glo.xyzrgba(bv,0,0,0,0,1,0,1);// 2
		glo.xyzrgba(bv,0,1,0,0,1,0,1);// 3
		glo.xyzrgba(bv,0,0,0,0,0,1,1);// 4
		glo.xyzrgba(bv,0,0,1,0,0,1,1);// 5
		bv.position(0);
	}
	@Override final public void iglo_render(final windo w,final glob g){
		final float[]mvp=new float[16];
//		final float[]mvp=acti.matrix();
		Matrix.multiplyMM(mvp,0,w.matrix_world_view_projection(),0,g.matrix_model_world(),0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
//		acti.matrix_recycle(mvp);
		
		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		bv.position(coloff);
		GLES20.glVertexAttribPointer(shader.acol,collen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.acol);
		GLES20.glDisableVertexAttribArray(shader.atx);

//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
		GLES20.glDrawArrays(GLES20.GL_LINES,0,nverts);
//		GLES20.glDisableVertexAttribArray(shader.acol);
	}
	transient private FloatBuffer bv;

	public static a_axis_xyz shared_instance;
	private static final long serialVersionUID=1;
}