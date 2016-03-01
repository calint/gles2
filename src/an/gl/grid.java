package an.gl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.opengl.Matrix;

final public class grid implements Serializable{
	public static boolean use_grid_recycling=true;
	public static int grids_in_initial_recycled_pool=256;
	public static boolean orientation_xz=true;
	
	private float x,y,z,size;
	final public float size(){return size;}
	private int threshhold_for_split,level;
	private final LinkedList<glob>globs=new LinkedList<glob>();//? lazyinit //? concurrentq
	private final LinkedList<grid>chlds=new LinkedList<grid>();//? lazyinit //? concurrentq
//	private final ConcurrentLinkedQueue<grid>chlds=new ConcurrentLinkedQueue<>();//? lazyinit //? concurrentq
//	
//	private transient final static ConcurrentLinkedQueue<grid>recycled_grids=new ConcurrentLinkedQueue<>();
//	static{
//		for(int i=0;i<grids_in_initial_recycled_pool;i++)
//			recycled_grids.add(new grid());
//	}
//	private static grid recycled_grids_new(final float x,final float y,final float z,final float size,final int threshhold_for_split,final int level){
//		glob.meters.grids_recycled_tried++;
//		grid g=recycled_grids.poll();
//		if(g==null){
//			g=new grid(x,y,z,size,threshhold_for_split,level);
//		}else{
//			glob.meters.grids_recycled_used++;
//			glob.meters.grids++;
//			g.x=x;
//			g.y=y;
//			g.z=z;
//			g.size=size;
//			g.threshhold_for_split=threshhold_for_split;
//			g.level=level;
//		}
//		return g;
//	}
//	void recycle(){
//		glob.meters.grids_recycled++;
//		globs.clear();
//		for(final grid g:chlds){
//			g.recycle();;
//		}
//		chlds.clear();
//		recycled_grids.add(this);
//	}
//	private grid(){
////		glob.meters.grids_allocated++;
//		glob.meters.grids++;
//	}
	public grid(final float size,final int level,final int threshhold_for_split,final float x,final float y,final float z){
//		this();
		glob.meters.grids++;
		this.size=size;
		this.level=level;
		this.threshhold_for_split=threshhold_for_split;
		this.x=x;this.y=y;this.z=z;
	}
	
	private grid(final float x,final float y,final float z,final float size,final int threshhold_for_split,final int level){
//		this();
		glob.meters.grids++;
		this.x=x;
		this.y=y;
		this.z=z;
		this.size=size;
		this.threshhold_for_split=threshhold_for_split;
		this.level=level;
	}
	
	void clear_for_new_frame(){// called on root grid
		/// keep globs, check if still in same grid, static scenery
		globs.clear();
//		for(final grid g:chlds)//? keep subgrids?
//			g.recycle();
		chlds.clear();
	}
	
	final static int outside=0,in_this_grid_only=1,overlapping_other_grid=2;
	int put(final glob g){
		glob.meters.grids_put_count++;
		final float r=g.bounding_radius;
		int in=0;
		if(g.x+r<x-size)return outside;/// to the left
		if(g.x-r>x+size)return outside;/// to the right
		if(g.x-r>x-size)in++;
		if(g.x+r<x+size)in++;
		if(!orientation_xz){
			if(g.y-r>y+size)return outside;/// above
			if(g.y+r<y-size)return outside;/// below
			if(g.y-r>y-size)in++;
			if(g.y+r<y+size)in++;
		}else{
			if(g.z-r>z+size)return outside;/// above
			if(g.z+r<z-size)return outside;/// below
			if(g.z-r>z-size)in++;
			if(g.z+r<z+size)in++;
		}
		final int res=in==4?in_this_grid_only:overlapping_other_grid;
		if(!chlds.isEmpty()){
			put_in_subgrids(g);
			return res;
		}
		globs.add(g);
		if(level==0){
			return res;
		}
		if(threshhold_for_split!=0&&globs.size()>threshhold_for_split){
			make_subgrids();
			for(final glob gl:globs){
				put_in_subgrids(gl);
			}
			globs.clear();
		}
		return res;
	}
	void put_all(final List<glob>ls){// to be called once on root node with a list of globs to skip a step 
		if(threshhold_for_split!=0&&ls.size()>threshhold_for_split){
			make_subgrids();
			for(final glob gl:ls){
				put_in_subgrids(gl);
			}
			return;
		}
		globs.addAll(ls);
	}
	void make_subgrids() {
		// split
		final float half=size/2;
		if(!orientation_xz){
			chlds.add(new grid(x-half,y+half,z,half,threshhold_for_split,level-1));/// top left
			chlds.add(new grid(x+half,y+half,z,half,threshhold_for_split,level-1));/// top right
			chlds.add(new grid(x+half,y-half,z,half,threshhold_for_split,level-1));/// bottom right
			chlds.add(new grid(x-half,y-half,z,half,threshhold_for_split,level-1));/// bottom left
		}else{
			chlds.add(new grid(x-half,y,z+half,half,threshhold_for_split,level-1));/// top left
			chlds.add(new grid(x+half,y,z+half,half,threshhold_for_split,level-1));/// top right
			chlds.add(new grid(x+half,y,z-half,half,threshhold_for_split,level-1));/// bottom right
			chlds.add(new grid(x-half,y,z-half,half,threshhold_for_split,level-1));/// bottom left
		}
	}
	private void put_in_subgrids(final glob g){
		for(final grid gr:chlds){
			final int res=gr.put(g);
			if(res==in_this_grid_only){
				glob.meters.grids_put_skip_count++;
				return;
			}
		}
		return;
	}
//	private boolean has_grids(){return !chlds.isEmpty();}
	void detect_collisions(){
		if(!chlds.isEmpty()){
			for(final grid g:chlds)
				g.detect_collisions();
			return;
		}
		for(final Iterator<glob>i1=globs.iterator();i1.hasNext();){
			final glob a=i1.next();
			for(final Iterator<glob>i2=globs.descendingIterator();i2.hasNext();){
				final glob b=i2.next();
				if(a==b)break;
//				glob.meters.coldet_bounding_spheres_check++;
				final Object[]res=glob.check_collision(a,b);
				if(res!=null){
					glob.meters.collisions++;
//					a.collisions_add(b);
//					b.collisions_add(a);
					a.collisions.add(b);
					b.collisions.add(a);
				}
			}
		}
	}
	void render_grid(final windo w){
		final float[]mvp=new float[16];
//		final float[]mvp=acti.matrix();
		Matrix.setIdentityM(mvp,0);
		Matrix.translateM(mvp,0,x,y,z);
		Matrix.scaleM(mvp,0,size,size,size);
		if(!orientation_xz){
			a_grid.shared_instance.gldraw(w,mvp);
		}else{
			a_grid_plane_xz.shared_instance.gldraw(w,mvp);
		}
//		acti.matrix_recycle(mvp);
		for(final grid g:chlds){
			g.render_grid(w);
		}
	}
	void render_globs(final windo w,final long frame,final planes bcp){
		final float r=(float)Math.sqrt(size*size+size*size);//? const
//		final float r=size;//?? bug not correct due to blindspots between grids
		if(!bcp.is_sphere_inside_or_intersecting(x,y,z,r)){
			glob.meters.grids_render_skipped++;
			return;
		}
		glob.meters.grids_rendered++;
		for(final glob g:globs){
			if(!bcp.is_sphere_inside_or_intersecting(g.x,g.y,g.z,g.bounding_radius)){
				glob.meters.globs_render_outside_viewpyr++;
				continue;
			}			
			g.draw(w,frame);
		}
		for(final grid gr:chlds){
			gr.render_globs(w,frame,bcp);
		}
	}
	void update_globs(final long frame){
		glob.meters.grids_globs_update++;
		for(final glob g:globs){
			g.update(frame);
		}
		for(final grid gr:chlds){
			gr.update_globs(frame);
		}
	}
//	public void render_recursively(){}
//	
//	public void recycle(){
//		if(!has_grids())return;
//		for(final grid g:subgrids)
//			g.recycle();
//		for(int i=0;i<subgrids.length;i++)
//			subgrids[i]=null;
//	}
//	public static grid new_grid(final float x,final float y,final float z,final float size,final int threshhold_for_split){return new grid(x,y,z,size,threshhold_for_split);}
//	private final static LinkedList<grid>recycled_grids=new LinkedList<>();

	private static final long serialVersionUID=1;
}
