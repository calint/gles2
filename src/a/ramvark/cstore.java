package a.ramvark;
import static b.b.strenc;
import static b.b.tobytes;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import b.a;
import b.b;
import b.cacheable;
import b.path;
import b.req;
import b.xwriter;
public final class cstore{
	final private static path root(final Class<? extends itm>cls){return req.get().session().path(cls.getName());}
	private static String mkdocid(){
		final SimpleDateFormat sdf=new SimpleDateFormat("yyMMdd-hhmmss.SSS-",Locale.US);
		final StringBuilder sb=new StringBuilder(b.id).append("-").append(sdf.format(new Date()));
		final String alf="0123456789abcdef";
		for(int n=0;n<8;n++)
			sb.append(alf.charAt(b.rndint(0,alf.length())));
		return sb.toString();
	}
	public static void delete(final Class<? extends itm>cls,final String did)throws Throwable{
		meters.deletes++;
		//final itm e=load(cls,did);
	//	e.onpredelete();
		root(cls).get(did).rm();
	}
	public static itm load(final Class<? extends itm>cls,final String did)throws Throwable{
		final path p=root(cls).get(did);
		if(!p.exists())
			return null;
		final itm e=cls.newInstance();
		load(e,p);
		return e;
	}
	public static itm create(final Class<? extends itm>cls,final itm owner)throws Throwable{
		meters.creates++;
		final itm e=cls.newInstance();
		if(owner!=null)e.pid.set(owner.did);
		final String docid=mkdocid();
		e.did.set(docid);
		//e.onnew();
		return e;
	}
	
	final private static byte[]bafieldsep=tobytes(":");
	final private static byte[]balinesep=tobytes("\n");
	final public static void save(final itm e)throws Throwable{
		meters.saves++;
		final String fn=e.did.toString();
		final Class<? extends itm>cls=e.getClass();
		final path file=root(cls).get(fn);
		final OutputStream os=file.outputstream(false);
		//head
		//. access list,summary,load,append,edit,save,remove
		//. time created,edited,deleted
		//. did,pid
		//. class
		//. name
		//. index,...
		os.write(tobytes(e.toString().replace('\n','\07')));//? e.to(os,enc)
		os.write(balinesep);
		//key value pairs
		for(final Field f:cls.getFields()){
			if(!a.class.isAssignableFrom(f.getType()))
				continue;
//			final in anot=f.getAnnotation(in.class);
//			if(anot!=null)
//				if(anot.type()==3)//dontwriteaggmanyfield
//					continue;
			final a m=(a)f.get(e);
			os.write(tobytes(f.getName()));//? f.getName().to(os,enc)
			os.write(bafieldsep);
			if(m!=null)
				os.write(tobytes(m.toString().replace('\n','\07')));//? m.to(os,enc)
			os.write(balinesep);
		}
		os.close();
		e.notnew=true;
	}
	final private static void load(final itm e,final path p)throws Throwable{
		meters.loads++;
		final Class<? extends itm>cls=e.getClass();
		final Reader re=new InputStreamReader(p.inputstream(),strenc);
		try{final StringBuilder sbname=new StringBuilder(256);
			final StringBuilder sbvalue=new StringBuilder(256);
			Field fld=null;
			int s=3;
			while(true){
				final int ch=re.read();
				if(ch==-1)
					break;
				switch(s){
				case 3://readfirstline
					if(ch==balinesep[0]){
						e.set(sbname.toString().trim().replace('\07','\n'));
						sbname.setLength(0);
						s=0;
					}else
						sbname.append((char)ch);
					break;
				case 0:
					if(ch==bafieldsep[0]){
						fld=cls.getField(sbname.toString().trim());
						sbvalue.setLength(0);
						s=1;
					}else
						sbname.append((char)ch);
					break;
				case 1:
					if(ch==balinesep[0]){
						final a ee=(a)fld.get(e);
						ee.set(sbvalue.toString().trim().replace('\07','\n'));
						sbname.setLength(0);
						s=0;
					}else
						sbvalue.append((char)ch);
					break;
				default:throw new Error();
				}
			}
		}finally{
			re.close();
		}
		e.notnew=true;
	}
	public static void foreach(final Class<? extends itm>cls,final itm owner,final String q,final visitor v)throws Throwable{
		meters.foreaches++;
		final String[]fns=root(cls).list();//?. stream
		for(final String fn:fns){
			final path p=root(cls).get(fn);
			final itm e=cls.newInstance();
			load(e,p);
			if(owner!=null){//?. implpartialload
				final String pid=e.pid.toString();
				final String did=owner.did.toString();
				if(!pid.equals(did))
					continue;
			}
			final String nm=e.toString();//?. queryfilename
			if(q!=null&&q.length()>0&&!nm.startsWith(q))
				continue;
			v.visit(e);
		}
	}
	public interface visitor{void visit(final itm e)throws Throwable;}
	public static class meters extends a implements cacheable{
		static final long serialVersionUID=1;
		public static long foreaches,creates,loads,saves,deletes;
		public void to(final xwriter x)throws Throwable{spclst(x);}
		public static void spclst(final xwriter x)throws Throwable{
			x.p(foreaches).spc();
			x.p(creates).spc();
			x.p(loads).spc();
			x.p(saves).spc();
			x.p(deletes);
		}

		// cache
		public String filetype(){return "ls";}
		public String contenttype(){return "plain/text";}
		private long t0;
		private static String t0d;
		private static long cachetimems=1000;
		public String lastmod(){//? called3timesinonereq
			final long t1=System.currentTimeMillis();
			if(t1-t0>cachetimems){
				t0=t1;
				t0d=new Date(t0).toString();
				return t0d;
			}
			return t0d;
		}
		public long lastmodupdms(){return 1000;}
		public boolean cacheforeachuser(){return false;}
	}
}
