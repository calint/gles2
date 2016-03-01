package a.gl;

import an.gl.glob;
import b.a;
import b.xwriter;

public class $ extends a{
	@Override public void to(final xwriter x)throws Throwable {
		x.pre();
		glob.o.chlds_for_each(new glob.code(){public void x(glob g)throws Throwable{
			x.pl(g.toString());
		}});
	}

	private static final long serialVersionUID = 1L;
}
