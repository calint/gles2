package a.gl;

import java.lang.reflect.Field;

import an.gl.glob;
import b.a;
import b.xwriter;

public class meters extends a{
	@Override public void to(xwriter x)throws Throwable{
		for(final Field f:glob.meters.class.getFields()){
			x.p(f.getName()).p(": ").pl(f.get(null).toString());
		}
	}

	private static final long serialVersionUID = 1L;
}
