package scrollthief.model;

import com.jogamp.common.nio.Buffers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class OBJ {
	private String OBJModelPath; // The path to the Model File
    private ArrayList<float[]> vData = new ArrayList<float[]>(); // List of Vertex Coordinates
    private ArrayList<float[]> vtData = new ArrayList<float[]>(); // List of Texture Coordinates
    private ArrayList<float[]> vnData = new ArrayList<float[]>(); // List of Normal Coordinates
    private ArrayList<int[]> fv = new ArrayList<int[]>(); // Face Vertex Indices
    private ArrayList<int[]> ft = new ArrayList<int[]>(); // Face Texture Indices
    private ArrayList<int[]> fn = new ArrayList<int[]>(); // Face Normal Indices
    private FloatBuffer modeldata; // Buffer which will contain vertex data
    private int FaceFormat; // Format of the Faces Triangles or Quads
    private int FaceMultiplier; // Number of possible coordinates per face
    private int PolyCount = 0; // The Models Polygon Count
    private boolean init  = true;
    private int type;
//    private boolean isTransparent;
    
    private byte halftone[] = {
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,
    	    (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55,};
    
    public OBJ(String Modelpath) {
        OBJModelPath = Modelpath;
        LoadOBJModel(OBJModelPath);
        SetFaceRenderType();
        ConstructInterleavedArray();
        cleanup();
        
//        isTransparent = false;
    }
    
    private void LoadOBJModel(String ModelPath) {
        try {
            // Open a file handle and read the models data
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream (ModelPath), "UTF-8"));
            String  line = null;
            while((line = br.readLine()) != null) {
                if (line.startsWith("#")) { // Read Any Descriptor Data in the File
                    //System.out.println("Descriptor: "+line); //Uncomment to print out file descriptor data
                } else if (line.equals("")) {
                    //Ignore whitespace data
                } else if (line.startsWith("v ")) { // Read in Vertex Data
                    vData.add(ProcessData(line));
                } else if (line.startsWith("vt ")) { // Read Texture Coordinates
                    vtData.add(ProcessData(line));
                } else if (line.startsWith("vn ")) { // Read Normal Coordinates
                    vnData.add(ProcessData(line));
                } else if (line.startsWith("f ")) { // Read Face Data
                    ProcessfData(line);
                }
            }
            br.close();
        } catch(IOException e) {
            System.out.println("Failed to find or read OBJ: " + ModelPath);
            System.err.println(e);
        }
    }
    
    private float[] ProcessData(String read) {
        final String s[] = read.split("\\s+");
        return (ProcessFloatData(s)); //returns an array of processed float data
    }
    
    private float[] ProcessFloatData(String sdata[]) {
        float data[] = new float[sdata.length-1];
        for (int loop=0; loop < data.length; loop++) {
            data[loop] = Float.parseFloat(sdata[loop+1]);
        }
        return data; // return an array of floats
    }
    
    private void ProcessfData(String fread) {
        PolyCount++;
        String s[] = fread.split("\\s+");
        if (fread.contains("//")) { // Pattern is present if obj has only v and vn in face data
            for (int loop=1; loop < s.length; loop++) {
                s[loop] = s[loop].replaceAll("//","/0/"); //insert a zero for missing vt data
            }
        }
        ProcessfIntData(s); // Pass in face data
    }
    
    private void ProcessfIntData(String sdata[]) {
        int vdata[] = new int[sdata.length-1];
        int vtdata[] = new int[sdata.length-1];
        int vndata[] = new int[sdata.length-1];
        for (int loop = 1; loop < sdata.length; loop++) {
            String s = sdata[loop];
            String[] temp = s.split("/");
            vdata[loop-1] = Integer.valueOf(temp[0]); //always add vertex indices
            if (temp.length > 1) { // we have v and vt data
                vtdata[loop-1] = Integer.valueOf(temp[1]); // add in vt indices
            } else {
                vtdata[loop-1] = 0; // if no vt data is present fill in zeros
            }
            if (temp.length > 2) { // we have v, vt, and vn data
                vndata[loop-1] = Integer.valueOf(temp[2]); // add in vn indices
            } else {
                vndata[loop-1] = 0; //if no vn data is present fill in zeros
            }
        }
        fv.add(vdata);
        ft.add(vtdata);
        fn.add(vndata);
    }
    
    private void SetFaceRenderType() {
        final int temp [] = (int[]) fv.get(0);
        if ( temp.length == 3) { 
            FaceFormat = GL.GL_TRIANGLES; // The faces come in sets of 3 so we have triangular faces
            FaceMultiplier = 3;
        } else if (temp.length == 4) {
            FaceFormat = GL2.GL_QUADS; // The faces come in sets of 4 so we have quadrilateral faces
            FaceMultiplier = 4;
        } else {
        	//FaceMultiplier = 5;
            FaceFormat = GL2.GL_POLYGON; // Fall back to render as free form polygons
        }
    }
    
    private void ConstructInterleavedArray() {
        final int tv[] = (int[]) fv.get(0);
        final int tt[] = (int[]) ft.get(0);
        final int tn[] = (int[]) fn.get(0);
        // If a value of zero is found that it tells us we don't have that type of data
        if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] != 0)) {
            ConstructTNV(); //We have Vertex, 2D Texture, and Normal Data
            type = GL2.GL_T2F_N3F_V3F;
            // gl.glInterleavedArrays(GL2.GL_T2F_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] == 0)) {
            ConstructTV(); //We have just vertex and 2D texture Data
            type = GL2.GL_T2F_V3F;
            // gl.glInterleavedArrays(GL2.GL_T2F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] != 0)) {
            ConstructNV(); //We have just vertex and normal Data
            type = GL2.GL_N3F_V3F;
            // gl.glInterleavedArrays(GL2.GL_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] == 0)) {
            ConstructV();
            type = GL2.GL_V3F;
            // gl.glInterleavedArrays(GL2.GL_V3F, 0, modeldata);
        }
    }
    
    private void ConstructTNV() {
        int[] v, t, n;
        float tcoords[] = new float[2]; //Only T2F is supported in InterLeavedArrays!!
        float coords[] = new float[3];        
        int fbSize= PolyCount*(FaceMultiplier*8); // 3v Per Poly, 2vt Per Poly, 3vn Per Poly
        if (fbSize == 0)
        	fbSize= 48778 * 8;
        //System.out.println("fbsize: "+fbSize+", Polycount: "+PolyCount);
        modeldata = Buffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);     
        for (int oloop=0; oloop < fv.size(); oloop++) {
            v = (int[])(fv.get(oloop));
            t = (int[])(ft.get(oloop));
            n = (int[])(fn.get(oloop));
            for (int iloop=0; iloop < v.length; iloop++) {
                // Fill in the texture coordinate data
                for (int tloop=0; tloop < tcoords.length; tloop++) //Only T2F is supported in InterLeavedArrays!!
                    tcoords[tloop] = ((float[])vtData.get(t[iloop] - 1))[tloop];
                modeldata.put(tcoords);
                // Fill in the normal coordinate data
                for (int vnloop=0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[])vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                // Fill in the vertex coordinate data
                for (int vloop=0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[])vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0); 
    }
    
    private void ConstructTV() {
        int[] v, t;
        float tcoords[] = new float[2]; //Only T2F is supported in InterLeavedArrays!!
        float coords[] = new float[3];
        int fbSize= PolyCount*(FaceMultiplier*5); // 3v Per Poly, 2vt Per Poly
        modeldata = Buffers.newDirectFloatBuffer(fbSize);        
        modeldata.position(0);
        for (int oloop=0; oloop < fv.size(); oloop++) {
            v = (int[])(fv.get(oloop));
            t = (int[])(ft.get(oloop));
            for (int iloop=0; iloop < v.length; iloop++) {
                // Fill in the texture coordinate data
                for (int tloop=0; tloop < tcoords.length; tloop++) //Only T2F is supported in InterLeavedArrays!!
                    tcoords[tloop] = ((float[])vtData.get(t[iloop] - 1))[tloop];                
                modeldata.put(tcoords);
                // Fill in the vertex coordinate data
                for (int vloop=0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[])vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }
    
    private void ConstructNV() {
        int[] v, n;
        float coords[] = new float[3];
        int fbSize= PolyCount*(FaceMultiplier*6); // 3v Per Poly, 3vn Per Poly
        modeldata = Buffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop=0; oloop < fv.size(); oloop++) {
            v = (int[])(fv.get(oloop));
            n = (int[])(fn.get(oloop));
            for (int iloop=0; iloop < v.length; iloop++) {
                // Fill in the normal coordinate data
                for (int vnloop=0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[])vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                // Fill in the vertex coordinate data
                for (int vloop=0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[])vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }
    
    private void ConstructV() {
        int[] v;
        float coords[] = new float[3];
        int fbSize= PolyCount*(FaceMultiplier*3); // 3v Per Poly
        modeldata = Buffers.newDirectFloatBuffer(fbSize);
        modeldata.position(0);
        for (int oloop=0; oloop < fv.size(); oloop++) {
            v = (int[])(fv.get(oloop));
            for (int iloop=0; iloop < v.length; iloop++) {
                // Fill in the vertex coordinate data
                for (int vloop=0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[])vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }
       
    public void DrawModel(GL2 gl, boolean isTransparent) {
//        if (init) {
//            ConstructInterleavedArray();
//            cleanup();
//            init = false;
//        }
    	if(isTransparent) {
    		gl.glInterleavedArrays(type, 0, modeldata);
	        gl.glEnable(GL2.GL_CULL_FACE);
	        gl.glEnable(GL2.GL_POLYGON_STIPPLE);
	        gl.glCullFace(GL2.GL_BACK);
	        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
	        gl.glPolygonStipple(halftone, 0);
	        gl.glDrawArrays(FaceFormat, 0, PolyCount*FaceMultiplier);
	        gl.glDisable(GL2.GL_POLYGON_STIPPLE);
	        gl.glDisable(GL2.GL_CULL_FACE);
    	}
    	else {
	        gl.glInterleavedArrays(type, 0, modeldata);
	        gl.glEnable(GL2.GL_CULL_FACE);
	        gl.glCullFace(GL2.GL_BACK);
	        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
	        gl.glDrawArrays(FaceFormat, 0, PolyCount*FaceMultiplier);
	        gl.glDisable(GL2.GL_CULL_FACE);
    	}
    }
    
     public String Polycount() {
        String pc = Integer.toString(PolyCount);
        return pc;
    }
    
    private void cleanup() {
        vData.clear();
        vtData.clear();
        vnData.clear();
        fv.clear();
        ft.clear();
        fn.clear();
        //modeldata.clear();
        //System.gc();
    }
    
//    public void setIsTransparent(boolean isTransparent) {
//    	this.isTransparent = isTransparent;
//    }
//    
//    public boolean getIsTransparent() {
//    	return isTransparent;
//    }
}
