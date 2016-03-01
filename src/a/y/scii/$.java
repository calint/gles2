package a.y.scii;
import b.*;
public class $ extends a{
	public static class canvas{
		public char[][]ram;
		public canvas(final int width,final int height){
			ram=new char[height][width];
		}
		public void to(final xwriter x){
			for(char[]row:ram){
				for(char c:row){
					x.p(c);
				}
				x.nl();
			}
			x.nl();
		}
	}
	
	private canvas cnvs;
	@Override public void to(final xwriter x)throws Throwable {
		for(int y=0;y<cnvs.ram.length;y++){
			for(int c=0;c<cnvs.ram[c].length;c++){
				cnvs.ram[y][c]=(char)(Math.random()*512);
			}
		}
		cnvs.to(x);
		x.script().p("reload()").scriptEnd();
	}
	
	static final long serialVersionUID=1;
}

