package a.ramvark.ab;
import a.ramvark.in;
import a.ramvark.itm;
import b.a;
import b.xwriter;
public class puppet extends itm{static final long serialVersionUID=1;
	@in(type=3)public expenses expenses;
	@in(type=3)public items items;
	@in(itm=content.class)public agr content;
	@in(lst=puppets.class)public ref father;
	@in(lst=puppets.class)public ref mother;
	@in public a email;	
	@in public a phone;	
	
	public final void itm_content_in(final xwriter x)throws Throwable{
		inputagr(x,content);
		x.spc();
		if(!content.isnull()){
			final content m=(content)content.get();
			final String s=m.body.toString();
			final int len=256;
			final String ss=s.length()<len?s:(s.substring(0,len)+"...");
			x.p(ss);
			x.spc().ax(this,"agrclr "+content.nm(),"x");
		}
	}
}
