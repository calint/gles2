package an.gl.a;

import java.nio.FloatBuffer;

import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_bounding_circle implements iglo{
	final private int nelems=3,nelembytes=4,stride=nelems*nelembytes,posoff=0,poslen=3;
	@Override final public void iglo_load(){
		bv=glo.vertices_circle_xyz(16);
		nverts=bv.capacity()/nelems;
	}
	private int nverts;
	@Override final public void iglo_render(final windo w,final glob g){
		final float[]mvp=new float[16];
//		final float[]mvp=acti.matrix();
		Matrix.setIdentityM(mvp,0);
		Matrix.translateM(mvp,0,g.x(),g.y(),g.z());
		final float r=g.bounding_radius();
		Matrix.scaleM(mvp,0,r,r,r);
		Matrix.multiplyMM(mvp,0,w.matrix_world_view_projection(),0,mvp,0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
//		acti.matrix_recycle(mvp);
		
		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		GLES20.glDisableVertexAttribArray(shader.acol);		
		GLES20.glDisableVertexAttribArray(shader.atx);
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP,0,nverts);
	}
	transient private FloatBuffer bv;

	public static a_bounding_circle shared_instance;
	private static final long serialVersionUID=1;
}