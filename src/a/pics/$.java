package a.pics;
import b.a;
import b.path;
import b.req;
import b.xwriter;
public class $ extends a{static final long serialVersionUID=1;
	public void to(final xwriter x)throws Throwable{
		x.el(this).style(this,"img","vertical-align:top");
		final req r=req.get();
		final path p=r.session().path(r.query());
		p.apply(new path.visitor(){public boolean visit(final path p){
			final String s=p.uri();
			if(!p.type().equals("jpg")&&!s.equals("png"))
				return false;
			x.tago("img").attr("src",s).tagoe();
			return false;
		}});
		x.elend();
	}
}
