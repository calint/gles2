package a.ramvark;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import b.a;
import b.b;
import b.xwriter;
public abstract class itm extends a implements $.labeled{
	static final long serialVersionUID=1;
	public a pid,did;

	boolean notnew;
	a focus;
	a selref;
	a afterclosefocus;
	lst aftercloseaddtolist;
	private String label;
	
	protected itm(){try{
		for(final Field f:getClass().getDeclaredFields()){
			in annot=f.getAnnotation(in.class);
			if(annot==null)continue;
			if(f.getType()==agr.class){
				final agr rf=(agr)f.get(this);
				rf.cls=annot.itm();
				rf.owner=this;
				continue;
			}
			if(f.getType()==ref.class){
				final ref rf=(ref)f.get(this);
				rf.cls=annot.lst().getAnnotation(ls.class).cls();
				continue;
			}
			if(annot.type()==3){
				final lst ls=(lst)f.get(this);
				ls.owner=this;
				continue;
			}
		}
	}catch(Throwable t){throw new Error(t);}}
	final public void to(final xwriter x)throws Throwable{
		x.tag("span",id());
//		x.nl();
		if(notnew)
			x.p(" ").ax(this,"rn","⌾");
		x.p(" ").ax(this,"cl","••");
		focus=null;
		final LinkedList<Field>flds=new LinkedList<Field>();
		for(final Field f:getClass().getFields())//? fieldorderissue
			flds.addFirst(f);
		x.style();
		x.css("table.fm","background:#fff;margin:.5em 0 1em .5em");
		x.css("table.fm","box-shadow:0 0 .5em rgba(0,0,0,1);border-radius:0px");
		x.css("table.fm tr td","vertical-align:middle;padding:.25em 2em .25em 1em");
		x.css("table.fm tr td.lbl","vertical-align:baseline;text-align:right;padding:1em 0 1em 5em;font-weight:bold");
//		x.nl().css("table.fm tr:last-child","border-top:1px dotted green");
		x.css("input,select,textarea","padding:.5em;background:#fff;box-shadow:0 0 .5em rgba(0,0,0,.5)");
		x.css("input.ln","width:100%");
		x.css("input.nm","width:100%");
		x.css("textarea.ls","width:100%;height:100px");
		x.css("textarea.ed","width:64em;height:400px");
		x.css("input.nbr","text-align:right;width:5em");
		x.styleEnd();
		x.table("fm");
		for(final Field f:flds){
			final in annot=f.getAnnotation(in.class);
			if(annot==null)continue;
			x.nl().tr();
			final int t=annot.type();

			final a e=(a)f.get(this);
			if(focus==null)
				focus=e;

			if(t==4)
				x.td(2);
			else
				x.td("lbl").tago("label").attr("for",e.id()).tagoe().p(f.getName()).tage("label").td();
			
			try{getClass().getMethod("itm_"+f.getName()+"_in",xwriter.class).invoke(this,x);
				continue;
			}catch(final NoSuchMethodException ok){
			}catch(final InvocationTargetException e1){
				x.p(b.stacktraceline(e1.getTargetException()));
				continue;
			}
			if(f.getType()==ref.class){
				inputref(x,e);
				x.spc();
				final itm m=((ref)e).get();
				if(m==null)
					continue;
				x.p(m.toString()).spc().ax(this,"refclr "+f.getName(),"x");
				continue;
			}
			final Class<? extends lst>lscls=annot.lst();
			if(lscls!=lst.class){
				inputref(x,e);
				x.spc();
				if(!e.isempty()){
					final Class<? extends itm>ocls=lscls.getAnnotation(ls.class).cls();
					final itm m=cstore.load(ocls,e.toString());
					x.p(m.toString());
					x.spc().ax(this,"agrclr "+f.getName(),"x");
				}
				continue;
			}
			if(annot.itm()!=itm.class){
				inputagr(x,e);
				continue;
			}
			if(t==0){
				x.inputText(e,"ln",this,"sc");
				continue;
			}
			if(t==1){
				x.inputTextArea(e,"ls");
				continue;
			}
			if(t==3){//aggr many
//				((lst)e).owner=this;
				x.p(e);
				continue;
			}
			if(t==4){
				x.inputTextArea(e,"ed");
				continue;
			}
			if(t==5){
				x.inputText(e,"nbr",this,"sc");
				continue;
			}
		}
		x.nl().tr().td(2);
		x.nl().tableEnd().nl();
		
		x.nl().style();
		x.nl().css("ul.ac","margin-left:21px");
		x.nl().css("ul.ac li","display:inline;margin-left:.5em");
		x.nl().styleEnd();
		x.nl().ul("ac");
		x.li().ax(this,"sc","▣");
		x.li().ax(this,"sv","▢");
		x.ulEnd();
		x.br().br();

		if(focus!=null)
			x.focus(focus);
		
		x.tage("span");
	}
	//input aggr11
	final protected void inputagr(final xwriter x,final a e) throws Throwable{
		x.p("<a href=\"javascript:$x('").p(id()).p(" agr ").p(e.nm()).p("')\" id=").p(e.id()).p(">⌾</a>");
	}
	final public synchronized void x_agr(final xwriter x,final String s)throws Throwable{
		final Field f=getClass().getField(s);
		final agr ra=(agr)f.get(this);
		final itm m=ra.get();
		m.label=f.getName();
		m.afterclosefocus=ra;
		ev(x,this,m);
	}
	final public synchronized void x_agrclr(final xwriter x,final String s)throws Throwable{
		final Field f=getClass().getField(s);
		final agr ra=(agr)f.get(this);
		ra.rm();
		x.xuo(this);
		x.xfocus(ra);
	}
	//input ref
	final protected void inputref(final xwriter x,final a e) throws Throwable{
		x.p("<a href=\"javascript:$x('").p(id()).p(" ref ").p(e.nm()).p("')\" id=").p(e.id()).p(">⌾</a>");
	}
	//reference select
	final public synchronized void x_ref(final xwriter x,final String s)throws Throwable{
		final Field f=getClass().getField(s);
		final Class<? extends lst>clsls=f.getAnnotation(in.class).lst();
		final lst ls=clsls.newInstance();
		final ref rf=(ref)f.get(this);
		ls.seltrg=rf;
		final String q;
		final itm o=rf.get();
		if(o!=null){
			q=o.toString();
		}else
			q="";
		ls.qry.set(q);
		ls.label="select "+f.getName();
		ev(x,this,ls);
	}
	final public synchronized void x_refclr(final xwriter x,final String s)throws Throwable{
		final Field f=getClass().getField(s);
		final ref rf=(ref)f.get(this);
		rf.rm();
		x.xuo(this);
		x.xfocus(rf);
	}

	protected boolean validate(final xwriter x)throws Throwable{
		final LinkedList<Field>flds=new LinkedList<Field>();
		for(final Field f:getClass().getFields())
			flds.addFirst(f);
		for(final Field f:flds){
			final in annot=f.getAnnotation(in.class);
			if(annot==null)continue;
			if(annot.must()){
				final a e=(a)f.get(this);
				if(e.toString().trim().length()==0){
					x.xalert("enter "+f.getName());
					x.xfocus(e);
					return false;
				}
			}
		}
		return true;
	}
	
	//save
	final public synchronized void x_sv(final xwriter x,final String s)throws Throwable{
		if(!validate(x))return;
		onpresave(x);
		cstore.save(this);
		if(selref!=null)selref.set(did.toString());
		if(aftercloseaddtolist!=null)aftercloseaddtolist.set(aftercloseaddtolist.toString()+","+did);
	}
	//close
	final public synchronized void x_cl(final xwriter x,final String s)throws Throwable{
		ev(x,this,"cl");
	}
	//savevandvclose
	final public synchronized void x_sc(final xwriter x,final String s)throws Throwable{
		if(!validate(x))
			return;
		x_sv(x,null);
		if(selref!=null){
			ev(x,this,"cl2");
			x.xfocus(selref);
			return;
		}
		x_cl(x,null);
	}
	//rename
	public synchronized void x_rn(final xwriter x,final String s)throws Throwable{
		x.xalert("rename");
	}
	public String label(){return label!=null?label:toString();}
	
	//callbacks
	//protected void onnew()throws Throwable{}
	//protected void onafterload()throws Throwable{}
	protected void onpresave(final xwriter x)throws Throwable{}
	//protected void onpredelete()throws Throwable{}
	
////	public static interface ref{
////		// get may return null
////		public itm get()throws Throwable;
////		public boolean isnull();
////		public void ondelete();
////	}
////	public static interface agg extends ref{
////		// getcreate
////		public itm getc()throws Throwable;
////	}
//	public static interface aggm extends agg{
//		// visitor pattern (1:enter,2:up,0:default) 
//		public int apply(final cstore.visit cv);
//		// creates new
//		public itm mk();
//		// deletes did
//		public void rm(final String did);
//	}
//	public static interface refs extends agg{
//		// creates new item and adds it to list
//		public itm mk();
//		// adds did to list
//		public void add(final String did);
//		// removes did from list
//		public void rm(final String did);
//	}
	
	public static class ref extends a{
		static final long serialVersionUID=1;
		protected Class<? extends itm>cls;
		public itm get()throws Throwable{
			if(isempty())return null;
			return cstore.load(cls,toString());
		}
		final public boolean isnull(){return isempty();}
		public void rm(){clr();}
	}
	final public static class agr extends ref{
		static final long serialVersionUID=1;
		protected itm owner;
		public itm get()throws Throwable{
			final itm m=super.get();
			if(m!=null)return m;
			final itm mm=cstore.create(cls,owner);
			set(mm.did.toString());
			return mm;
		}
		public void rm(){
			try{cstore.delete(cls,toString());}catch(final Throwable t){throw new Error(t);}
			super.clr();
		}
		//? ondelete
//		public void ondelete()throws Throwable{rm();}
	}

}
