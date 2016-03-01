package an.gl.a;

import java.nio.FloatBuffer;

import an.gl.R;
import an.gl.glo;

public class a_triangle extends glo{
	@Override protected int get_texture_raw_resource_id(){return R.raw.robot;}
	@Override protected FloatBuffer make_vertices_buffer(){return glo.vertices_circle_xyzuv(3);}

	public static a_triangle shared_instance;
	public static final long serialVersionUID=1;
}
