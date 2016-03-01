package an.gl;
import java.util.LinkedList;

final public class cores{
	final private LinkedList<core>free_cores=new LinkedList<core>();
	public cores(final int threads){
		for(int i=0;i<threads;i++){
			final core c=new core();
			c.start();
			free_cores.add(c);
		}
	}
	public void run(final code c){
		core cc=null;
		while(true){
			synchronized(free_cores){
				cc=free_cores.poll();
				if(cc!=null)break;
				try{free_cores.wait();}catch(final InterruptedException ignored){return;}
			}
		}
		cc.run(c);
	}
	final class core extends Thread{
		core(){super("core#");}
		public boolean on;
		@Override public void run(){
			on=true;
			while(on){
				synchronized(this){if(code==null)try{wait();}catch(final InterruptedException ignored){break;}}
				if(!on)break;
				if(code==null){System.out.println(">?1");continue;}
				try{code.x();}catch(final Throwable t){throw new Error(t);}
				code=null;
				synchronized(free_cores){//? concurrent que
					free_cores.add(this);
					free_cores.notify();
				}
			}
		}
		public void run(final code c){code=c;synchronized(this){notify();}}
		private code code;
	}
	public static interface code{public void x()throws Throwable;}
}
