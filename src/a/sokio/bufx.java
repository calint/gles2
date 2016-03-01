package a.sokio;
import java.io.*;
import java.nio.*;
import java.util.*;
import b.*;
final class bufx{
	public bufx(){}
	public bufx(final byte[]ba/*takes*/){this.ba=ba;pagesize_B=ba.length;n++;}
	public bufx(final int pagesize_B){this.pagesize_B=pagesize_B;}
	public bufx put(final String s){return put(s.getBytes());}
	public bufx put(final byte[]b){return b(b,0,b.length);}
	public bufx b(final byte[]b,int off,int len){
		if(ba==null){
			ba=new byte[pagesize_B];
			i=0;n++;
		}
		final int cap=ba.length-i;
		if(cap>=len){
			System.arraycopy(b,off,ba,i,len);
			i+=len;
			return this;
		}
		System.arraycopy(b,off,ba,i,cap);
		len-=cap;
		off+=cap;
		lsb_add(ba);
		n++;
		ba=new byte[pagesize_B];//? incbalen
		while(len>ba.length){
			System.arraycopy(b,off,ba,0,ba.length);
			len-=ba.length;
			off+=ba.length;				
			lsb.add(ba);
			n++;
			ba=new byte[pagesize_B];
		}
		if(len!=0){
			System.arraycopy(b,off,ba,0,len);
			i=len;
			len-=len;
			off+=len;
		}
		return this;
	}
	public void clear(){ba=null;lsb=null;bboa=null;i=n=0;}
	public void to(final OutputStream os)throws Throwable{
		if(lsb!=null){
			final Iterator<byte[]>it=lsb.iterator();
			while(true){
				n--;
				if(n==0)break;
				os.write(it.next());
			}
		}
		os.write(ba,0,i);
	}
	public long send_start(final sockio sc)throws Throwable{
		bboa=new ByteBuffer[n];
		int x=0;
		bboa_rem=0;
		if(lsb!=null){
			final Iterator<byte[]>it=lsb.iterator();
			while(true){
				n--;
				if(n==0)break;
				final ByteBuffer bb=ByteBuffer.wrap(it.next());
				bboa[x++]=bb;
				bboa_rem+=bb.remaining();
			}
		}
		final ByteBuffer bb=ByteBuffer.wrap(ba,0,i);
		bboa[x++]=bb;
		bboa_rem+=bb.remaining();
		return send_resume(sc);
	}
	public long send_resume(final sockio sc)throws Throwable{
		while(true){
			final long c=sc.write(bboa);
			if(c==0)return 0;
			bboa_rem-=c;
			if(bboa_rem==0){clear();return c;}
		}
	}
	public boolean send_isdone(){return bboa_rem==0;}
	private void lsb_add(byte[]b){
		if(lsb==null)lsb=new ArrayList<byte[]>();
		lsb.add(b);
	}
	private List<byte[]>lsb;
	private byte[]ba;
	private int pagesize_B=1024;
	private int n,i;
	private ByteBuffer[]bboa;
	private long bboa_rem;
}
