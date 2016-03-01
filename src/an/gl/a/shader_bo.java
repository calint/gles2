package an.gl.a;

import an.gl.acti;
import an.gl.shader;
import android.opengl.GLES20;

final public class shader_bo extends shader{
	@Override protected String[]shader_source_vertex_fragment() {
		return new String[]{acti.string_from_resource("a/shader_bo_vertex.shad"),acti.string_from_resource("a/shader_bo_frag.shad")};
	}
	public void link(){
		_umvp=uniform("umvp");
		_apos=attrib("apos");
	}
	public void use(){
		GLES20.glUseProgram(prog);
		umvp=_umvp;
		apos=_apos;
		acol=0;
		atx=0;
	}
}
