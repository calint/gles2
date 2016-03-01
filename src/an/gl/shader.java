package an.gl;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.util.Log;
public class shader{
	public static int umvp;// model view projection matrix
	public static int apos;// position xyz
	public static int atx;// texture uv
	public static int acol;// color rgba
	public static int anml;// vertex normal x,y,z
	public static int umat4_model_world;
	public static int uvec3_ambient_light;
	
//	public static shader active_program;
	public static ArrayList<Class<? extends shader>>shader_classes;
//	=new ArrayList<>();
//	static{shader_classes.add(shader.class);}
	transient final public static List<shader>shaders=new ArrayList<shader>();
	private static int active_shader_index=0;
	public static shader get_active_shader(){return shaders.get(active_shader_index);}
	public static void active_shader_use(){get_active_shader().use();}
	public static void activate_shader(final Class<? extends shader>c){
		int i=0;
		for(final Class<? extends shader>sc:shader_classes){
			if(sc==c)break;
			i++;
		}
		active_shader_index=i;
	}
	public shader(){glload();}
	final public void glload(){
		prog=createProgram(shader_source_vertex_fragment());
		link();	
	}
	protected String[]shader_source_vertex_fragment(){
		final String vertex_shader_resource_path="shader_vertex.shad";
		final String fragment_shader_resource_path="shader_frag.shad";
		final String[]src=new String[]{acti.string_from_resource(vertex_shader_resource_path),acti.string_from_resource(fragment_shader_resource_path)};
		return src;
	}
	protected int prog;
	protected void link(){
		_umvp=uniform("umvp");
		_apos=attrib("apos");
		_atx=attrib("atx");
		_acol=attrib("acol");		
		_anml=attrib("anml");		
		_umat4_model_world=uniform("umat4_model_world");
		_uvec3_ambient_light=uniform("uvec3_ambient_light");
	}
	protected int _umvp;// model view projection matrix
	protected int _apos;// position xyz
	protected int _atx;// texture uv
	protected int _acol;// color rgba
	protected int _anml;// vertex normal x,y,z
	protected int _umat4_model_world;
	protected int _uvec3_ambient_light;
	protected void use(){
		GLES20.glUseProgram(prog);
		umvp=_umvp;
		apos=_apos;
		atx=_atx;
		acol=_acol;
		anml=_anml;
		umat4_model_world=_umat4_model_world;
		uvec3_ambient_light=_uvec3_ambient_light;
	}
	public String toString(){return getClass().getName()+" glprog "+prog;}
	//--
	private int createProgram(final String[]source_vertex_fragment){
		final int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,source_vertex_fragment[0]);
		final int pixelShader=loadShader(GLES20.GL_FRAGMENT_SHADER,source_vertex_fragment[1]);
		final int program=GLES20.glCreateProgram();
		if(program==0)throw new Error("cannot create program");
		GLES20.glAttachShader(program,vertexShader);
		checkglerror("glAttachShader vertex");
		GLES20.glAttachShader(program,pixelShader);
		checkglerror("glAttachShader pixel");
		GLES20.glLinkProgram(program);
		final int[]linkStatus=new int[1];
		GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linkStatus,0);
		if(linkStatus[0]!=GLES20.GL_TRUE)throw new Error(getClass().getName()+": cannot link program due to:\n"+GLES20.glGetProgramInfoLog(program));//GLES20.glDeleteProgram(program);
//		GLES20.glValidateProgram(program);
		return program;
	}
	private int loadShader(final int shaderType,final String source){
		final int shader=GLES20.glCreateShader(shaderType);
		if(shader==0)throw new Error("cannot create "+GLES20.glGetShaderInfoLog(shader));
		GLES20.glShaderSource(shader,source);
		GLES20.glCompileShader(shader);
		final int[]compiled=new int[1];
		GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
		if(compiled[0]==0)throw new Error(getClass().getName()+": cannot compile due to:\n"+GLES20.glGetShaderInfoLog(shader));//GLES20.glDeleteShader(shader);
		return shader;
	}
	final protected int attrib(final String nm){
		final int a=GLES20.glGetAttribLocation(prog,nm);
		if(a==-1)throw new Error(getClass().getName()+": cannot find attribute "+nm);
		return a;
	}
	final protected int uniform(final String nm){
		final int u=GLES20.glGetUniformLocation(prog,nm);
		if(u==-1)throw new Error(getClass().getName()+": cannot find uniform "+nm);
		return u;
	}
	private void checkglerror(final String op){
		int error;
		while((error=GLES20.glGetError())!=GLES20.GL_NO_ERROR){
			Log.e(shader.class.getName(),op+": glError "+error);
			throw new RuntimeException(op+": glError "+error);
		}
	}
	public static void activate_next(){
		active_shader_index=(active_shader_index+1)%shaders.size();
	}
}

