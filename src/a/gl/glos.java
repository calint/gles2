package a.gl;

import an.gl.glo;
import an.gl.iglo;
import b.a;
import b.xwriter;

public class glos extends a{
	@Override public void to(xwriter x)throws Throwable {
		for(final iglo g:glo.s){
			x.pl(g.toString());
		}
	}

	private static final long serialVersionUID = 1L;
}
