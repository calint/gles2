package an.gl.a;

import an.gl.acti;
import an.gl.glob;

public class glob_tiler extends glob{{
	iglo(a_tiler.shared_instance);
}
	private long t0;
	private int tx,ty;
	private int a=1;
	protected void on_update(){
		final long t=acti.time_millis();
		final long dt=t-t0;
		if(dt<100)return;
		t0=t;
		tx++;
		if(tx>=a_tiler.shared_instance.tiles[ty].length){
			tx=0;
			ty++;
			if(ty>=a_tiler.shared_instance.tiles.length){
				ty=0;
				if(a==1)a=0;else a=1;
			}
		}
		a_tiler.shared_instance.tiles[ty][tx].index_in_tile_array=a;
	}
	
	public static final long serialVersionUID=1;
}
