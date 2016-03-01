package an.gl.a;

import an.gl.acti;
import an.gl.shader;
import android.opengl.GLES20;

final public class shader_zish extends shader{
	@Override protected String[]shader_source_vertex_fragment() {
		return new String[]{acti.string_from_resource("a/shader_zish_vertex.shad"),acti.string_from_resource("a/shader_zish_frag.shad")};
	}
	public void link(){
		_umvp=uniform("umvp");
		_apos=attrib("apos");
		_acol=attrib("acol");
		_anml=attrib("anml");
	}
	public void use(){
		GLES20.glUseProgram(prog);
		umvp=_umvp;
		apos=_apos;
		acol=_acol;
		atx=0;
		anml=_anml;
	}
}
