package an.gl.a;

import java.io.InputStream;
import java.nio.FloatBuffer;

import an.gl.acti;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import an.gl.file.format_f3d;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_f3d_iglo implements iglo{
	/// override
	protected String object_file_path(){return "f3d/building.f3d";}
	///
	
	final private int nelems=9,posoff=0,poslen=3,coloff=3,collen=3,nmloff=6,nmllen=3;
	final private int elem_size_in_bytes=4,stride=nelems*elem_size_in_bytes;
	private int nverts;
	
	transient private FloatBuffer bv;

	@Override final public void iglo_load()throws Throwable{
		final String op=object_file_path();
		final InputStream is=acti.inputstream_from_asset(op);
		final format_f3d o=new format_f3d();
		o.read(is);
		is.close();
		o.rotate_vertices_about_y_axis(-90);
		bv=o.alloc_direct_xyzrgbnml_buffer_for_triangle_array();
		nverts=bv.capacity()/nelems;
	}
	
	@Override final public void iglo_render(final windo w,final glob g){
		final float[]mmw=g.matrix_model_world();
		GLES20.glUniformMatrix4fv(shader.umat4_model_world,1,false,mmw,0);
		final float[]mvp=new float[16];
		Matrix.multiplyMM(mvp,0,w.matrix_world_view_projection(),0,mmw,0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);
		
		final float a=glob.rad(acti.time_millis()*.01f);
//		System.out.println(a+"   "+acti.time_millis()*(float)Math.PI/180);
		final float s=glob.grid.size();
		final float[]lht=new float[]{s*(float)Math.cos(a),s,s*(float)Math.sin(a)};
		acti.vec_normalize(lht);
//		acti.vec_p(lht);
		GLES20.glUniform3fv(shader.uvec3_ambient_light,3,lht,0);
		
		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		bv.position(coloff);
		GLES20.glVertexAttribPointer(shader.acol,collen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.acol);
		bv.position(nmloff);
		GLES20.glVertexAttribPointer(shader.anml,nmllen,GLES20.GL_FLOAT,true,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.anml);
		
		GLES20.glDisableVertexAttribArray(shader.atx);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,nverts);
	}

	public static a_f3d_iglo shared_instance;
	private static final long serialVersionUID=1;
}