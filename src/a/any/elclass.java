package a.any;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import a.any.list.el;
import a.any.list.ui_action;
import b.a;
import b.xwriter;

final public class elclass implements el,el.actions{
		private el pt;
		private Class<?>cls;
		public elclass(final el parent,final Class<?>c){pt=parent;cls=c;}
		@Override public el parent(){return pt;}
		@Override public String name(){return cls.getName().substring(cls.getName().indexOf(' ')+1);}
		@Override public String fullpath(){return cls.getName();}
		@Override public boolean isfile(){return false;}
		@Override public boolean isdir(){return true;}
		@Override public List<String>list(){
			final ArrayList<String>ls=new ArrayList<String>();
			for(final Field f:cls.getFields()){
				final int m=f.getModifiers();
				if(!Modifier.isStatic(m))continue;
				if(Modifier.isFinal(m))continue;
				ls.add(f.getName());
			}
			return ls;
		}
		@Override public List<String>list(final String query){
			final ArrayList<String>ls=new ArrayList<String>();
			for(final Field f:cls.getFields()){
				final int m=f.getModifiers();
				if(!Modifier.isStatic(m))continue;
				if(Modifier.isFinal(m))continue;
				final String nm=f.getName();
				if(!nm.startsWith(query))continue;
				ls.add(f.getName());
			}
			return ls;
		}
		@Override public el get(String name){try{return new elclassfield(this,cls.getDeclaredField(name));}catch(Throwable t){throw new Error(t);}}
		@Override public long size(){return 0;}
		@Override public long lastmod(){return 0;}
		@Override public String uri(){return null;}
		@Override public boolean exists(){return true;}
		@Override public void append(String cs){throw new UnsupportedOperationException();}
		@Override public boolean rm(){throw new UnsupportedOperationException();}
		@Override public OutputStream outputstream(){throw new UnsupportedOperationException();}
		@Override public InputStream inputstream(){throw new UnsupportedOperationException();}
		
		// el.actions
		@Override public List<a>actions(){
			final List<a>ls=new ArrayList<a>();
			for(final Method m:cls.getMethods()){
				final int mo=m.getModifiers();
				final Annotation a=m.getAnnotation(ui_action.class);
				if(a==null)continue;
//				x.ax(this,"",m.getName()).pl(" ::");
				ls.add(new classmethodcaller(m));
			}
			return ls;
		}
		
		public final static class classmethodcaller extends a{
			final private Method m;
			public classmethodcaller(final Method m){this.m=m;}
			@Override public void to(xwriter x) throws Throwable{
				x.ax(this,"",m.getName());
			}
			public void x_(xwriter x,String s)throws Throwable{
				m.invoke(null,x,s);
			}
			
			private static final long serialVersionUID=1;
		}
	}