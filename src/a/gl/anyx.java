package a.gl;

import a.any.elclass;
import a.any.elpath;
import a.any.elroot;
import a.any.list;
import an.gl.acti;
import an.gl.glo;
import an.gl.glob;
import an.gl.grid;
import an.gl.net;
import an.gl.sfx;
import an.gl.shader;
import an.gl.windo;
import b.b;
import b.req;

public class anyx extends list {
	public anyx(){
		final elroot l=new elroot(null,"gles2 - config");
		l.add(new elclass(l,acti.class));
		l.add(new elclass(l,glo.class));
		l.add(new elclass(l,glob.class));
		l.add(new elclass(l,grid.class));
		l.add(new elclass(l,net.class));
		l.add(new elclass(l,sfx.class));
		l.add(new elclass(l,shader.class));
		l.add(new elclass(l,windo.class));
		l.add(new elclass(l,list.class));
		l.add(new elclass(l,req.class));
		l.add(new elpath(l,b.path()));
		root(l);
		path = root;
	}

	private static final long serialVersionUID = 1;
}
