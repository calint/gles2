package an.gl.a;

import java.nio.FloatBuffer;

import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_bounding_box_plane_xz implements iglo{
	final private int posoff=0,poslen=3,coloff=3,collen=3,nelems=6,nelembytes=4,stride=nelems*nelembytes;
	@Override final public void iglo_load(){
		bv=glo.vertices_xyzrgb_square_plane_xz();
		nverts=bv.capacity()/nelems;
	}
	private int nverts;
	@Override final public void iglo_render(final windo w,final glob g){
		if(g.is_sphere())return;
		final float[]mvp=new float[16];
//		final float[]mvp=acti.matrix();
		Matrix.setIdentityM(mvp,0);
//		Matrix.translateM(mvp,0,g.x(),g.y(),g.z());
//		Matrix.scaleM(mvp,0,g.scale_x(),g.scale_y(),g.scale_z());
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
//		
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glEnableVertexAttribArray(shader.atx);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP,0,nverts);
	}
	transient private FloatBuffer bv;

	public static a_bounding_box_plane_xz shared_instance;
	private static final long serialVersionUID=1;
}