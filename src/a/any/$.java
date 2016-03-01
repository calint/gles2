package a.any;

import b.b;
import b.req;

public class $ extends list{
	public $(){
		final elroot l=new elroot(null,"el systems");
		l.add(new elclass(l,list.class));
//			l.add(new elclass(root,b.class));//? secure root path
		l.add(new elclass(l,req.class));
		l.add(new elpath(l,b.path()));
		root(l);
		path=root;
	}
	
	private static final long serialVersionUID=1;
}
