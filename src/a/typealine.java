package a;
import static b.b.rndint;
import java.text.SimpleDateFormat;
import java.util.Date;
import b.a;
import b.req;
import b.xwriter;
public class typealine extends a{
	static final long serialVersionUID=1;
	public a q;
	public a s;
	public a b;
	public typealine(){upd();}
	public void to(final xwriter x)throws Throwable{
		x.title("typealine");
		x.style();
		x.css("input.line","border:0;width:32em;border-bottom:1px dotted grey;");
		x.css(".box","text-align:center;border:1px dotted blue;padding:7px;");
		x.css(".line","width:250px;");
		x.styleEnd();
		x.div("box");
		x.span(q).inputText(s,"line",this,"a").p(" ").axBgn(this,"a").span(b).axEnd();
		x.focus(s);
	}
	public void x_a(final xwriter x,final String p)throws Throwable{
		final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd---hh:mm:ss.SSS---");
		final String line=s.toString();
		req.get().session().path("log.txt").append(simpleDateFormat.format(new Date())+line,"\n");
		upd();
		s.clr();
		if(x==null)return;
		x.xtitle("typealine: "+line).xu(q).xu(s).xu(b).xfocus(s);
	}
	private void upd(){
		final long len=req.get().session().path("log.txt").size();
		if(len>0)
			q.set(" ํ "+len+" ڀ ");
		else
			q.set(" ڀ ");
		b.set(glyph_random());
	}
	private static final String glyphs="ᐖᐛツ";
	private static String glyph_random(){
		final int i=rndint(0,glyphs.length());
		return glyphs.substring(i,i+1);
	}
}
