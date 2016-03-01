package b;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.channels.*;
import java.text.*;
import java.util.*;
final public class b{
	public final static String strenc="utf-8";
	public final static String q=" ڀ ";
	public final static String a=" ํ ";
	public final static int K=1024;
	public final static int M=K*K;
	public final static long G=K*M;
	public final static long T=K*G;
	public final static long P=K*T;
	public final static String pathsep="/";
	public static String hello="public domain server #1";
	public static String id=""+(int)Math.floor(Math.random()*10000);
	public static String root_dir=".";
	public static String server_port=ensure(System.getProperty("app.port"),"8888");
	public static boolean try_file=true;
	public static boolean try_rc=true;
	public static boolean thd_watch=false;
	public static boolean thread_pool=true;
	public static int thread_pool_size=128;
	public static long thread_pool_lftm=60*1000;
	public static boolean cache_uris=true;
	public static boolean cache_files=true;
	public static int cache_files_hashlen=K;
	public static int cache_files_maxsize=64*K;
	public static long cache_files_validate_dt=1000;
	public static int transfer_file_write_size=256*K;
	public static int io_buf_B=64*K;
	public static int chunk_B=4*K;
	public static int reqinbuf_B=4*K;
	public static String default_directory_file="index.html";
	public static String default_package_class="$";
	public static boolean gc_before_stats=false;
	public static int hash_size_session_values=32;
	public static int hash_size_sessions_store=4*K;
	public static String sessionfile="session.ser";
	public static boolean sessionfile_load=true;
	public static String sessions_dir="u";
	public static boolean cacheu_tofile=true;
	public static String cacheu_dir="/cache/";
	public static final String webobjpkg="a.";
	public static String datetimefmtstr="yyyy-MM-dd HH:mm:ss.sss";
	public static long resources_lastmod=0;
	public static boolean resources_enable_any_path=false;
	public static Set<String>resources_paths=new HashSet<String>(Arrays.asList("x.js","x.css"));
	public static boolean enable_upload=true;
//	public static boolean enable_ssl=false;
//	public static boolean enable_cluster=false;
	public static int max_pending_connections=20000;// when overrun causes SYN flood warning
	public static boolean tcpnodelay=true;
	public static boolean save_sessions_at_shutdown=false;
	public static long io_select_timeout_ms=0;
	
	public static long timeatload=System.currentTimeMillis();
	public static String timeatloadstrhtp=tolastmodstr(timeatload);
	public static PrintStream out=System.out;
	public static PrintStream err=System.err;
	private final static LinkedList<req>pending_req=new LinkedList<req>();
	public static boolean on;
	public static void main(final String[]args)throws Throwable{
		on=true;
//		System.out.println(hello);
		if(!class_init(b.class,args))return;
		resources_lastmod=System.currentTimeMillis();
		final ServerSocketChannel ssc=ServerSocketChannel.open();
		ssc.configureBlocking(false);
		final InetSocketAddress isa=new InetSocketAddress(Integer.parseInt(server_port));
		final ServerSocket ss=ssc.socket();
		ss.bind(isa,max_pending_connections);
		req.init_static();
		if(thd_watch)new thdwatch().start();
		final Selector sel=Selector.open();
		ssc.register(sel,SelectionKey.OP_ACCEPT);
		Runtime.getRuntime().addShutdownHook(new jvmsdh());
		while(on)try{
			sel.select(io_select_timeout_ms);
			thdwatch.iokeys=sel.keys().size();
			final Iterator<SelectionKey>it=sel.selectedKeys().iterator();
			if(!it.hasNext())continue;
			thdwatch.iosel++;
			while(it.hasNext()){
				thdwatch.ioevent++;
				final SelectionKey sk=it.next();
				it.remove();
				if(sk.isAcceptable()){
					thdwatch.iocon++;
					final req r=new req();
					r.sockch=ssc.accept();
					r.sockch.configureBlocking(false);
//					if(tcpnodelay)r.sockch.setOption(StandardSocketOptions.TCP_NODELAY,true);
					r.selkey=r.sockch.register(sel,0,r);
					read(r);
					continue;
				}
				sk.interestOps(0);
				final req r=(req)sk.attachment();
				if(sk.isReadable()){thdwatch.ioread++;read(r);continue;}
				if(sk.isWritable()){thdwatch.iowrite++;write(r);continue;}
				throw new IllegalStateException();
			}}catch(final Throwable e){
				log(e);
			}
	}
	private static void read(final req r)throws Throwable{
		if(r.is_sock()){
			if(r.is_sock_thread()){
				r.set_waiting_sock_thread_read();
				thread(r);
				return;
			}
			switch(r.sockread()){default:throw new Error();
			case read:r.selkey.interestOps(SelectionKey.OP_READ);return;
			case write:r.selkey.interestOps(SelectionKey.OP_WRITE);return;
			case close:r.close();thdwatch.socks--;return;
			case wait:r.selkey.interestOps(0);return;
			case noop:return;
			}
		}
		while(true)switch(r.parse()){default:throw new Error();
			case read:r.selkey.interestOps(SelectionKey.OP_READ);return;
			case write:r.selkey.interestOps(SelectionKey.OP_WRITE);return;
			case noop:return;
		}
	}
	private static void write(final req r)throws Throwable{
		if(r.is_sock()){
			if(r.is_sock_thread()){
				r.set_waiting_sock_thread_write();
				thread(r);
				return;
			}
			switch(r.sockwrite()){default:throw new Error();
			case read:r.selkey.interestOps(SelectionKey.OP_READ);break;
			case write:r.selkey.interestOps(SelectionKey.OP_WRITE);break;
			case close:r.close();thdwatch.socks--;break;
			}
			return;
		}
		if(r.is_waiting_write()){synchronized(r){r.notify();}return;}
		if(r.is_transfer()){
			if(!r.do_transfer()){r.selkey.interestOps(SelectionKey.OP_WRITE);return;}
			if(!r.is_connection_keepalive()){r.close();return;}
			if(r.is_buf_empty()){r.selkey.interestOps(SelectionKey.OP_READ);return;}
			read(r);//?? bug stackrain
			return;
		}
		if(r.is_waiting_run())
			thread(r);
		throw new Error();
	}
	static void thread(final req r){
		r.selkey.interestOps(0);//? must?
		if(!b.thread_pool||thdreq.all.size()<thread_pool_size){new thdreq(r);return;}
		synchronized(pending_req){pending_req.addLast(r);pending_req.notify();}
	}
	public static int cp(final InputStream in,final OutputStream out){//?. sts
		try{
			final byte[]buf=new byte[io_buf_B];
			int n=0;while(true){final int count=in.read(buf);if(count<=0)break;out.write(buf,0,count);n+=count;}
			return n;
		}catch(final Throwable t){throw new Error(t);}
	}
	public static int cp(final Reader in,final Writer out,final sts sts)throws Throwable{
		final char[]buf=new char[io_buf_B];
		int n=0;while(true){final int count=in.read(buf);if(count<=0)break;out.write(buf,0,count);n+=count;if(sts!=null)sts.setsts(Long.toString(n));}
		return n;
	}
	public static synchronized void log(final Throwable t){
		Throwable e=t;
		if(t instanceof InvocationTargetException)e=t.getCause();
		while(e.getCause()!=null)e=e.getCause();
		if(e instanceof java.nio.channels.CancelledKeyException)return;
		if(e instanceof java.nio.channels.ClosedChannelException)return;
		if(e instanceof java.io.IOException){
			if("Broken pipe".equals(e.getMessage()))return;
			if("Connection reset by peer".equals(e.getMessage()))return;
			if("An existing connection was forcibly closed by the remote host".equals(e.getMessage()))return;
		}
		b.err.println("\n\n"+b.stacktraceline(e));
	}
	public static path path(){return new path(new File(root_dir));}
	public static path path(final String path){if(path.contains(".."))throw new Error("illegalpath "+path);return new path(new File(root_dir,path));}
	static LinkedList<req>pendingreqls(){return pending_req;}

	private static long stats_last_t_ms;
	private static long stats_last_io_B;
	public static void stats_to(final OutputStream out)throws Throwable{
		final long t_ms=System.currentTimeMillis();
		final long dt_ms=t_ms-stats_last_t_ms;
		stats_last_t_ms=t_ms;
		final long total_io_B=thdwatch.input+thdwatch.output;
		final long dB=total_io_B-stats_last_io_B;
		stats_last_io_B=total_io_B;
		final float dBdt_s=dt_ms==0?0:dB*1000/dt_ms;
		final int throughput_qty;
		final String throughput_unit;
		if(dBdt_s==0){
			throughput_qty=0;
			throughput_unit="";
		}else if(dBdt_s>M){
			throughput_qty=(int)(dBdt_s/M+0.5f);
			throughput_unit=" MB/s";
		}else if(dBdt_s>K){
			throughput_qty=(int)(dBdt_s/K+0.5f);
			throughput_unit=" KB/s";
		}else{
			throughput_qty=(int)(dBdt_s);
			throughput_unit=" B/s";
		}
		final PrintStream ps=new PrintStream(out);
		ps.println(hello);
		ps.println("             time: "+tolastmodstr(t_ms));
		ps.println("             port: "+server_port);
		ps.println("            input: "+(thdwatch.input>>10)+" KB");
		ps.println("           output: "+(thdwatch.output>>10)+" KB");
		ps.println("       throughput: "+throughput_qty+throughput_unit);
		ps.println("         sessions: "+session.all().size());
		ps.println("        downloads: "+new File(root_dir).getCanonicalPath());
		ps.println("     sessions dir: "+new File(root_dir,sessions_dir).getCanonicalPath());
		ps.println("     cached files: "+(req.cachef_size()>>10)+" KB");
		ps.println("      cached uris: "+(req.cacheu_size()>>10)+" KB");
		ps.println("        classpath: "+System.getProperty("java.class.path"));
		final Runtime rt=Runtime.getRuntime();
		if(gc_before_stats)rt.gc();
		final long m1=rt.totalMemory();
		final long m2=rt.freeMemory();
		ps.println("         ram used: "+((m1-m2)>>10)+" KB");
		ps.println("         ram free: "+(m2>>10)+" KB");
		ps.println("          threads: "+thdreq.all.size());
		ps.println("            cores: "+Runtime.getRuntime().availableProcessors());
		ps.println("               id: "+id);
	}
	public static int rndint(final int from,final int tonotincl){return (int)(Math.random()*(tonotincl-from)+from);}
	public static String stacktrace(final Throwable e){final StringWriter sw=new StringWriter();final PrintWriter out=new PrintWriter(sw);e.printStackTrace(out);out.close();return sw.toString();}
	public static String stacktraceline(final Throwable e){return stacktrace(e).replace('\n',' ').replace('\r',' ').replaceAll("\\s+"," ").replaceAll(" at "," @ ");}
	public static String tolastmodstr(final long t){final SimpleDateFormat sdf=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");sdf.setTimeZone(TimeZone.getTimeZone("GMT"));return sdf.format(new Date(t));}
	public static String urldecode(final String s){try{return URLDecoder.decode(s,strenc);}catch(UnsupportedEncodingException e){throw new Error(e);}}
	public static String urlencode(final String s){try{return URLEncoder.encode(s,strenc);}catch(UnsupportedEncodingException e){throw new Error(e);}}
	public static String tostr(final Object object,final String def){return object==null?def:object.toString();}
	public static byte[]tobytes(final String v){try{return v.getBytes(strenc);}catch(UnsupportedEncodingException e){throw new Error(e);}}
	public static String sessionhref(final String sessionid){return sessions_dir+"/"+sessionid+"/";}
	public static boolean isempty(final String s){return s==null||s.length()==0;}
	public static String isempty(final String o,final String def){return isempty(o)?def:o;}
	public static Set<String>sessionsids(){return Collections.unmodifiableSet(session.all().keySet());}//?
	public static long sessionbits(final String sesid){//?
		//? file(system){sha1(sessionid),bits}
		if("".equals(sesid))return 2;
		return 0;
	}
	public static void class_printopts(final Class<?>cls)throws IllegalArgumentException,IllegalAccessException{
		for(final Field f:cls.getFields()){
			final Object o=f.get(null);
			out.print(f.getName());
			out.print("=");
			out.print(f.getType().getName());
			out.print("(");
			out.print(o==null?"":o.toString().replaceAll("\\n","\\\\n"));
			out.println(")");
		}
	}
	public static boolean class_init(final Class<?>cls,final String[]args)throws SecurityException,NoSuchFieldException,IllegalArgumentException,IllegalAccessException{
		if(args==null||args.length==0)return true;
		if("-1".equals(args[0])){class_printopts(cls);return false;}
		for(int i=0;i<args.length;i+=2){
			final String fldnm=args[i];
			final Field fld=cls.getField(fldnm);
			final String val=args[i+1];
			final Class<?>fldcls=fld.getType();
			if(fldcls.isAssignableFrom(String.class))fld.set(null,val);
			else if(fldcls.isAssignableFrom(int.class))fld.set(null,Integer.parseInt(val));
			else if(fldcls.isAssignableFrom(boolean.class))fld.set(null,"1".equals(val)||"true".equals(val)||"yes".equals(val)||"y".equals(val)?Boolean.TRUE:Boolean.FALSE);
			else if(fldcls.isAssignableFrom(long.class))fld.set(null,Long.parseLong(val));
		}
		return true;
	}
	static enum op{read,write,noop}
	private static String ensure(final String s,final String def){
		if(s==null||s.length()==0)return def;
		return s;
	}
}