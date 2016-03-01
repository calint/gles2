package an.gl.a;

import an.gl.glob;

public class bouncingglob extends glob{
	@Override protected void on_update() {
		if(x()>1||x()<-1)dx(-dx());
		if(y()>1||y()<-1)dy(-dy());
	}
	public static final long serialVersionUID=1;
}
