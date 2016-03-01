package a.budget;
import java.io.*;
import java.text.*;
import java.util.*;
import b.*;
//import static b.b.*;
final public class log extends a{
	static final long serialVersionUID=1;
	public a s;//item
	public a t;//total price
//	public a q;{q.set("1");}//qty
	public a d;{d.set(tostr(new Date()));}//date
	public a l;//output
	public a f;//filter
	public a fo;//filter rend
	public a ffr;//filter from
	public a fto;//filter to	
	final public void to(final xwriter x)throws Throwable{
		x.style();
		x.css("html","font-size:2em;padding-left:4em;line-height:1.5em");
		x.css(s,"border:1px dotted green;padding:.5em;padding:.5em;margin:.5em;border-radius:.5em");
		x.css(t,"width:4em;align:right;border:1px dotted green;padding:.5em;margin:.5em;border-radius:.5em");
//		x.css(q,"width:3em;border:1px dotted green;padding:.5em;margin:.5em;border-radius:.5em");
		x.css(d,"border:1px dotted green;padding:.5em;width:8em;padding:.5em;margin:.5em;border-radius:.5em");
		x.css(ffr,"border:1px dotted green;width:5em");
		x.css(fto,"border:1px dotted green;width:5em");
//		x.css(fo,"float:right");
		x.css("hr","color:black;height:.5em");
		x.css("table.log","border-top:3px double brown");
		x.css("table.log tr","border-bottom:1px dotted brown");
		x.css("table.log tr td","padding:.1em .5em .1em .5em;border-right:1px dotted brown");
		x.css("table.log tr td.q","text-align:right");
		x.css("table.log tr td.t","text-align:right");
		x.css("table.log tr td:last-of-type","border-right:0");
		x.css("table.log tr.total","font-weight:bold;border-bottom:0");
		x.styleEnd();
//		x.pl(getClass().toString());
//		x.nl();
		x.table().tr().td();
		x.p("⠸  item").inputText(s,null,this,"s").nl();
		x.focus(s);
		x.p("⠷ total").inputText(t,null,this,"s").nl();
//		x.p("   qty").inputText(q,null,this,"s").nl();
		x.p("⡹  date").inputText(d,null,this,"s").nl();
//		x.p("  time <input type=date name="+d.id()+" value=\""+d+"\">").inputText(d,null,this,"s").nl();
		x.nl().nl();
		
		x.td();
		
		x.el(fo);
		rend_filters(x);
		x.elend();
		x.hr();
		x.el(l);
		rend_log(x);
		x.elend();
		x.nl().p("ꖵ from ").inputText(ffr).p(" to ").inputText(fto).nl();
		x.tableEnd();
	}
	private void rend_filters(final xwriter x){
		final int i=f.toint();
		if(i==1)x.p(" today");else x.ax(this,"li 1"," today");
		if(i==5)x.p(" yesterday");else x.ax(this,"li 5"," yesterday");
		if(i==2)x.p(" this-week");else x.ax(this,"li 2"," this-week");
		if(i==3)x.p(" month");else x.ax(this,"li 3"," month");
		if(i==4)x.p(" year");else x.ax(this,"li 4"," year");
		if(i==0)x.p(" all");else x.ax(this,"li 0"," all");
	}
	private void rend_log(final xwriter x) throws IOException {
		final String fr=ffr.toString();
		final String to=fto.toString();
		final class gtot{int total;int lineno;}
		final gtot g=new gtot();
		final int datefieldlen=logdatefmt.length();
		x.table("log");
//		x.tr().td().p("date").td().p("total").td().p("qty").td().p("item").nl();
		path().to(new osnl(){final public void onnewline(final String line)throws Throwable{
			if(line.charAt(0)!=' ')return;
			g.lineno++;
			final String datestr=line.substring(1,datefieldlen+1);
			if(fr.length()!=0&&datestr.compareTo(fr)<0)return;
			if(to.length()!=0&&datestr.compareTo(to)>=0)return;
			final Scanner sc=new Scanner(line.substring(datefieldlen+2));
			final int total=sc.nextInt();
			g.total+=total;
//			final int qty=sc.nextInt();
//			x.tr().td().p(datestr).td("t").p(total).td("q").p(qty).td().pl(sc.nextLine().trim()).td().ax(log.this,"rm "+g.lineno,"x");
			x.tr().td().p(datestr).td("t").p(total).td().pl(sc.nextLine().trim()).td().ax(log.this,"rm "+g.lineno,"x");
			sc.close();
		}});
		x.tr("total").td().td("t").p(g.total).td().td();
		x.tableEnd();
	}
	final public void x_s(final xwriter x,final String st)throws Throwable{
//		path().append(" "+tologdatestr(parse(d.toString()))+" "+t.toint()+" "+q.toint()+" "+s,"\n");
		path().append(" "+tologdatestr(parse(d.toString()))+" "+t.toint()+" "+s,"\n");
		rend_log(x.xub(l,true,false));x.xube();
		x.xu(s.clr());
//		x.xu(q.set("1"));
		x.xfocus(s);
	}
	synchronized final public void x_li(final xwriter x,final String s)throws Throwable{
		f.set(s);
		final Date d=new Date();
		final Calendar cal=new GregorianCalendar();
		cal.setTime(d);
		cal.set(Calendar.HOUR,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		switch(Integer.parseInt(s)){
		case 1://today
			x.xu(ffr.set(tologdatestr(cal.getTime())));
			cal.add(Calendar.DATE,1);
			x.xu(fto.set(tologdatestr(cal.getTime())));
			break;
		case 2://this week
			cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
			x.xu(ffr.set(tologdatestr(cal.getTime())));
			cal.add(Calendar.WEEK_OF_MONTH,1);
			x.xu(fto.set(tologdatestr(cal.getTime())));
			break;
		case 3://this month
			cal.set(Calendar.DATE,1);
			x.xu(ffr.set(tologdatestr(cal.getTime())));
			cal.add(Calendar.MONTH,1);
			x.xu(fto.set(tologdatestr(cal.getTime())));
			break;
		case 4://this year
			cal.set(Calendar.MONTH,Calendar.JANUARY);
			cal.set(Calendar.DATE,1);
			x.xu(ffr.set(tologdatestr(cal.getTime())));
			cal.add(Calendar.YEAR,1);
			x.xu(fto.set(tologdatestr(cal.getTime())));
			break;
		case 5://yesterday
			x.xu(fto.set(tologdatestr(cal.getTime())));
			cal.add(Calendar.DATE,-1);
			x.xu(ffr.set(tologdatestr(cal.getTime())));
			break;
		case 0:
			x.xu(ffr.clr());
			x.xu(fto.clr());
			break;
		default:throw new Error(s);
		}
		rend_filters(x.xub(fo,true,false));x.xube();
		rend_log(x.xub(l,true,false));x.xube();
	}
	final public void x_rm(final xwriter x,final String s)throws Throwable{
		final int lineno=Integer.parseInt(s);
//		x.xalert(lineno+"");
		//? userandomaccess
		final path path_log=path();
		final path path_lognew=path_rm();
		final path path_logbak=path_bak();
		
		final InputStream is=path_log.fileinputstream();
		final Scanner sc=new Scanner(is);
		final OutputStream os=path_lognew.outputstream();
		final PrintStream ps=new PrintStream(os);
		int i=0;
		while(true){
			final String line=sc.nextLine();
			if(line.charAt(0)!=' ')continue;
			i++;
			if(i==lineno)break;
			ps.println(line);
		}
		while(sc.hasNextLine()){
			final String line=sc.nextLine();
			if(line.charAt(0)!=' ')continue;
			ps.println(line);
		}
		
		sc.close();
		ps.close();
//		is.close();
//		os.close();

		path_logbak.rm();//? log.bak.1
		if(!path_log.rename(path_logbak)){
			throw new Error("could not rename "+path_log+" to "+path_logbak);
		}
		if(!path_lognew.rename(path_log)){
			throw new Error("could not rename "+path_lognew+" to "+path_log);
		}
		rend_log(x.xub(l,true,false));x.xube();
	}
	private path path(){return req.get().session().path(getClass().getPackage().getName()).get("log");}
	private path path_rm(){return req.get().session().path(getClass().getPackage().getName()).get("log.rm");}
	private path path_bak(){return req.get().session().path(getClass().getPackage().getName()).get("log.bak");}
	final private static String inputdatefmt="yyyy-MM-dd";
	final private static String logdatefmt="yyyyMMdd";
	final private static Date parse(final String s){try{return new SimpleDateFormat(inputdatefmt).parse(s);}catch(final Throwable t){throw new Error(t);}}
//	final private static Date parse2(final String s){try{return new SimpleDateFormat(inputdate).parse(s);}catch(final Throwable t){return null;}}
	final private static String tostr(final Date d){return new SimpleDateFormat(inputdatefmt).format(d);}
	final private static String tologdatestr(final Date d){return new SimpleDateFormat(logdatefmt).format(d);}
}
