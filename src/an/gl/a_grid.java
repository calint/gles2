package an.gl;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

final public class a_grid implements iglo{
	private int nverts;
	final private int nelems=5,nelembytes=4,posoff=0,poslen=3,stride=nelems*nelembytes;
	@Override final public void iglo_load(){
		bv=glo.vertices_xyzuv_square();
		nverts=bv.capacity()/nelems;
	}
	@Override final public void iglo_render(final windo cm,final glob host){
		final float[]mvp=new float[16];
		Matrix.setIdentityM(mvp,0);
		Matrix.translateM(mvp,0,host.x(),host.y(),host.z());
		Matrix.scaleM(mvp,0,host.scale_x(),host.scale_y(),host.scale_z());
		Matrix.multiplyMM(mvp,0,host.matrix_model_world(),0,mvp,0);
		Matrix.multiplyMM(mvp,0,cm.matrix_world_view_projection(),0,mvp,0);
		gldraw(cm,mvp);
	}
	final public void gldraw(final windo cm,final float[]matrix_model_world){
//		final float[]mvp=acti.matrix();
		final float[]mvp=new float[16];
		Matrix.multiplyMM(mvp,0,cm.matrix_world_view_projection(),0,matrix_model_world,0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
//		acti.matrix_recycle(mvp);
		
		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		GLES20.glDisableVertexAttribArray(shader.atx);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP,0,nverts);
	}
	transient private FloatBuffer bv;
	
	public static a_grid shared_instance;
	private static final long serialVersionUID=1;
}
