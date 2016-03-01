package an.gl.a;

import java.nio.FloatBuffer;

import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import an.gl.file.format_obj;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_obj_iglo implements iglo{
	/// override
	protected String base_path(){return "obj/low_poly_building_10_obj";}
	protected String object_file_path(){return "low_poly_building_10.obj";}
	protected String texture_path(){return null;}
	///
	
	final private int nelems=5,posoff=0,poslen=3,texoff=3,texlen=2;
	final private int elem_size_in_bytes=4,stride=nelems*elem_size_in_bytes;
	private int nverts;
	
	private int[]gltxid;
	transient private FloatBuffer bv;

	@Override final public void iglo_load()throws Throwable{
		final String bp=base_path();
		final String op=object_file_path();
		final String tp=texture_path();
		final format_obj fo=new format_obj(bp,op,tp);
		bv=fo.alloc_direct_xyzuv_float_buffer_trianglearray();
		nverts=bv.capacity()/nelems;
		
		final int textures=fo.texture_files.size();
		gltxid=new int[textures];
		if(textures>0){
			GLES20.glGenTextures(1,gltxid,0);
			int i=0;
			for(final String s:fo.texture_files){
				glo.texture_load(gltxid[i++],bp+"/"+s);
			}
		}
	}
	
	@Override final public void iglo_render(final windo w,final glob g){
		final float[]mvp=new float[16];
		Matrix.multiplyMM(mvp,0,w.matrix_world_view_projection(),0,g.matrix_model_world(),0);
		GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);

		bv.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,bv);
		GLES20.glEnableVertexAttribArray(shader.apos);
		
		if(gltxid.length>0){
			bv.position(texoff);
			GLES20.glVertexAttribPointer(shader.atx,texlen,GLES20.GL_FLOAT,false,stride,bv);
			GLES20.glEnableVertexAttribArray(shader.atx);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltxid[0]);
		}else{
			GLES20.glDisableVertexAttribArray(shader.atx);
		}
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,nverts);
	}

	public static a_obj_iglo shared_instance;
	private static final long serialVersionUID=1;
}