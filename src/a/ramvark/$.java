package a.ramvark;
import java.util.LinkedList;

import a.ramvark.cstore.meters;
import b.a;
import b.xwriter;
final public class $ extends a{
	static final long serialVersionUID=1;
	public static String homepageclass="a.ramvark.ab.puppets";
	final LinkedList<a>stk=new LinkedList<a>();
	public a el;
	public a sts;
	{try{stk.add((a)Class.forName(homepageclass).newInstance());}catch(Throwable t){throw new Error(t);}attach(stk.getLast(),"el");}
	public void to(final xwriter x)throws Throwable{
		x.el(this);
		x.style();
		x.css("body","margin:0em 0 0 2em;color:#000;font:monospace;font-size:5mm");
		x.css("span.a","font-weight:bold");
		x.css("a","-webkit-transition: all .4s ease;");
		x.css("a","-moz-transition: all .4s ease;");
		x.css("a","-o-transition: all .4s ease;");
		x.css("a","transition: all .4s ease;");
		x.css("a:focus,a:hover,a:active","color:red;text-shadow:0px 0px 1px rgba(0, 0, 0, 0.5);");
		x.styleEnd();
		x.p("ramvark");
		final a last=stk.getLast();
		for(final a e:stk){
			x.p(" : ");
			if(e==last)
				x.tago("span").attr("class","a").tagoe();
			if(e instanceof labeled){
				x.p(((labeled)e).label());
			}else
				x.p(e.toString());
			if(e==last)
				x.tage("span");
		}
		x.p(el);
		x.br();
		meters.spclst(x);
		x.nl().elend();
//		x.nl().script().p("$('"+$el.id()+"').style.position='absolute';$('"+$el.id()+"').style.left='120px';").scriptEnd();
	}
	protected void ev(final xwriter x,final a from,final Object o)throws Throwable{
		if("cl".equals(o)){
			final a cl=stk.removeLast();
			attach(stk.getLast(),"el");
			x.xuo(this);
			//? x.focus in axjs workaround
			if(el instanceof lst)
				x.xfocus(((lst)el).qry);
			if(cl instanceof itm){
				final itm m=(itm)cl;
				if(m.afterclosefocus!=null){
					x.xfocus(m.afterclosefocus);
					m.afterclosefocus=null;
				}
			}			
		}else if("cl2".equals(o)){
			stk.removeLast();
			stk.removeLast();
			attach(stk.getLast(),"el");
			x.xuo(this);
				//? x.focus in axjs workaround
			if(el instanceof lst)
				x.xfocus(((lst)el).qry);
		}else if(o instanceof itm){
			stk.add((a)o);
			attach(stk.getLast(),"el");
			x.xuo(this);
			x.xfocus(((itm)el).focus);
		}else if(o instanceof lst){
			stk.add((a)o);
			attach(stk.getLast(),"el");
			x.xuo(this);
			x.xfocus(((lst)el).qry);
		}else super.ev(x,from,o);
	}
	interface labeled{String label();}
}
