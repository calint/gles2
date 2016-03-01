package an.gl.a;

import java.io.Serializable;
import java.nio.FloatBuffer;

import an.gl.R;
import an.gl.glo;
import an.gl.glob;
import an.gl.iglo;
import an.gl.shader;
import an.gl.windo;
import android.opengl.GLES20;
import android.opengl.Matrix;
public class a_tiler implements iglo{
	private int nverts;
	final private int nelems=5,nelembytes=4,posoff=0,poslen=3,texoff=3,texlen=2,stride=nelems*nelembytes;
	public int[]txrawid=new int[]{R.raw.logo,R.raw.robot};
	private int[]gltxa=new int[txrawid.length];
	public tile[][]tiles=new tile[8][8];{for(int r=0;r<tiles.length;r++)for(int c=0;c<tiles.length;c++)tiles[r][c]=new tile();}
//	for(tile[]t:tiles)for(tile tt:t)=new tile;
	@Override final public void iglo_load(){
		tiles[0][1].index_in_tile_array=1;
		tiles[0][3].index_in_tile_array=1;
		tiles[0][5].index_in_tile_array=1;
		tiles[0][7].index_in_tile_array=1;
		
		vb=glo.vertices_xyzuv_square();
		nverts=vb.capacity()/nelems;
		GLES20.glGenTextures(gltxa.length,gltxa,0);
		for(int i=0;i<gltxa.length;i++)
			glo.texture_load(gltxa[i],txrawid[i]);
	}

	@Override final public void iglo_render(final windo cm,final glob host){
		final float[]mvp=new float[16];
		Matrix.setIdentityM(mvp,0);
		final float ratio=(float)windo.w.height()/windo.w.width();
		Matrix.scaleM(mvp,0,.1f*ratio,.1f,0);
		Matrix.translateM(mvp,0,-tiles[0].length,+tiles.length,0);

		vb.position(posoff);
		GLES20.glVertexAttribPointer(shader.apos,poslen,GLES20.GL_FLOAT,false,stride,vb);
		GLES20.glEnableVertexAttribArray(shader.apos);
		vb.position(texoff);
		GLES20.glVertexAttribPointer(shader.atx,texlen,GLES20.GL_FLOAT,false,stride,vb);
		GLES20.glEnableVertexAttribArray(shader.atx);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		for(int row=0;row<tiles.length;row++){
			for(int col=0;col<tiles[row].length;col++){
				final tile tile=tiles[row][col];
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,gltxa[tile.index_in_tile_array]);
				GLES20.glUniformMatrix4fv(shader.umvp,1,false,mvp,0);				
				GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,nverts);
				Matrix.translateM(mvp,0,2,0,0);
			}
			Matrix.translateM(mvp,0,-tiles[0].length*2,-2,0);
		}
	}
	transient private FloatBuffer vb;

	public static final class tile implements Serializable{private static final long serialVersionUID=1;
		public int index_in_tile_array;
	}

	public static a_tiler shared_instance;
	public static final long serialVersionUID=1;
}