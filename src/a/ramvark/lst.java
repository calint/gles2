package a.ramvark;
import b.a;
import b.b;
import b.xwriter;
public abstract class lst extends a implements $.labeled{
	static final long serialVersionUID=1;
	public a qry;
	String label;
	a seltrg;
	itm owner;
	private String frstnm;
	private String frstid;
	public String label(){
		if(label!=null)
			return label;
		final String cn=getClass().getName();
		final int i=cn.lastIndexOf('.');
		return i==-1?cn:cn.substring(i+1);
	}
	final public void to(final xwriter x)throws Throwable{
		x.el(this);
		frstnm=null;
		frstid=null;
		rendhead(x);
		foreach(qry.toString(),new cstore.visitor(){public void visit(final itm e){
			if(frstnm==null){
				rendlisthead(x);
				frstid=e.did.toString();
				frstnm=e.toString();
			}
			try{rendrow(x,e);}catch(final Throwable t){x.p(b.stacktraceline(t));}				
		}});
		if(frstnm!=null)
			rendlistfoot(x);
		rendfoot(x);
		x.elend();
	}
	public void foreach(final String q,final cstore.visitor cv)throws Throwable{
		if(owner==null){
			cstore.foreach(itmcls(),owner,qry.toString(),cv);
			return;
		}
//		final Scanner sc=new Scanner(toString());
		final String[]sa=toString().split(",");
		final Class<? extends itm>cls=itmcls();
		for(final String s:sa){
			if(s.length()==0)
				continue;
			final itm e=cstore.load(cls,s);
			if(e==null){
				b.log(new Error("item not found "+cls+" "+s));
				continue;
			}
			if(q!=null&&!e.toString().startsWith(q))
				continue;
			cv.visit(e);
		}
	}
	
	// rend helpers
	protected void renddelax(final xwriter x,final itm e,final String txt){
		x.ax(this,"dl "+e.did,txt);
	}
	protected void rendldax(final xwriter x,final itm e){
		x.ax(this,"ld "+e.did,e.toString());
	}
	
	//rend
	protected void rendhead(final xwriter x){
//		x.nl();
//		x.tag("span",this);
		x.spc().ax(this,"cr","⌾");
		x.spc().style("input.q","width:20em;background:#fff;padding:.5em 2em .5em 1em;border:0px dotted #020;box-shadow:0 0 .5em rgba(0,0,0,.5);border-radius:0em");			
		x.inputax(qry,"q",this,"ch","sl");
		x.focus(qry);
	}
	protected void rendlisthead(final xwriter x){x.nl();}
	protected void rendrow(final xwriter x,final itm e)throws Throwable{
		rendldax(x,e);
		x.p(" ");
		renddelax(x,e);
		x.nl();
	}
	protected void rendlistfoot(final xwriter x){}
	protected void rendfoot(final xwriter x){
//		x.tagEnd("span");
	}

	//delete
	final public synchronized void x_dl(final xwriter x,final String s)throws Throwable{
		final String did=s;
		cstore.delete(itmcls(),did);
		if(owner!=null){
			final String dide=","+did;
			final String ix=toString();
			final int i=ix.indexOf(dide);
			if(i==-1)
				throw new Error(did+" not found in "+id());
			final String ss=ix.substring(0,i)+ix.substring(i+dide.length());
			set(ss);
		}
			
		x.xuo(this);
		x.xfocus(qry);
	}
	private Class<? extends itm>itmcls(){
		final ls annot=getClass().getAnnotation(ls.class);
		if(annot==null)throw new Error();
		final Class<? extends itm>cls=annot.cls();
		if(cls==null)throw new Error();
		return cls;
	}
	//load
	final public synchronized void x_ld(final xwriter x,final String s)throws Throwable{
		if(seltrg!=null){
			seltrg.set(s);
			ev(x,this,"cl");
			x.xfocus(seltrg);
			return;
		}
		final itm e=cstore.load(itmcls(),s);
		ev(x,this,e);
	}
	
	//ax qry change
	public synchronized void x_ch(final xwriter x,final String s)throws Throwable{
		x.xuo(this);
		x.xfocus(qry);
	}
	//ax qry sel
	public synchronized void x_sl(final xwriter x,final String s)throws Throwable{
		if(frstnm==null){
			x_cr(x,s);
			return;
		}
		if(seltrg!=null){
			seltrg.set(frstid);
			ev(x,this,"cl");
			x.xfocus(seltrg);
			return;
		}
		final Class<? extends itm>ocls=getClass().getAnnotation(ls.class).cls();
		final itm e=cstore.load(ocls,frstid);
		e.afterclosefocus=qry;
		ev(x,this,e);
	}

	//create
	public synchronized void x_cr(final xwriter x,final String s)throws Throwable{
		final String q=qry.toString().trim();
		if(q.length()==0){
			x.xalert("enter name");
			x.xfocus(qry);
			return;
		}
		if(q.indexOf('/')!=-1){
			x.xalert("character / not allowed in name");
			x.xfocus(qry);
			return;			
		}
		final Class<? extends itm>cls=getClass().getAnnotation(ls.class).cls();
		final itm e=cstore.create(cls,owner);
		e.set(q);
		e.selref=seltrg;
		e.afterclosefocus=qry;
		if(owner!=null)
			e.aftercloseaddtolist=this;
		ev(x,this,e);
	}
	// rend helpers
	protected void renddelax(final xwriter x,final itm e){
		x.ax(this,"dl "+e.did,"⌫");
	}
}
