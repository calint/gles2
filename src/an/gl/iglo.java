package an.gl;

import java.io.Serializable;

public interface iglo extends Serializable{
	void iglo_load()throws Throwable;
	void iglo_render(final windo cm,final glob host);
}