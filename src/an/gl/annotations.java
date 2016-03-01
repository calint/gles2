package an.gl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface annotations {
	public @Retention(RetentionPolicy.RUNTIME)@interface initatinit{}/// create default instance at init
	public @interface readonly{}/// property is read only when accessed from public interface
	public @interface readsonly{}/// method annotation, in c++  void f()const{}
	public @interface gives{}// object returned is recycled by the receiver
	public @interface takes{}// object is recycled by receiver of call
    public @interface copies{}// object is copied by call receiver
	public @interface reference{}
    public @interface aggregate{}
    public @interface lazyinit{}
    public @interface copyatchange{}

	
	/// special for glob.o which needs call to constructor glob(true)
	public @Retention(RetentionPolicy.RUNTIME)@interface initatinit_true{}
}
