package fr.inria.aviz.physVizEval.jzy3d.stereo;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static javax.media.opengl.GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;

/**
 *
 */

public class FrameBufferObject {
    int id;
    public int colorId;
    int width;
    int height;

    public static FrameBufferObject create(GL2 gl, int w, int h, boolean depth) {
        int[] idArray = new int[1];

        // create a framebuffer object
        gl.glGenFramebuffers(1, idArray, 0);
        int fbo = idArray[0];
        gl.glBindFramebuffer(GL_FRAMEBUFFER, fbo); 

        // create a texture object
        gl.glGenTextures(1, idArray, 0);
        int fboTexture = idArray[0];

        gl.glBindTexture(GL_TEXTURE_2D, fboTexture);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        gl.glBindTexture(GL_TEXTURE_2D, 0);

        // Attach the texture to the frame buffer as the color attachment. This
        // will cause the results of rendering to the FBO to be written in the blur texture.
        gl.glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                fboTexture,
                0
        );

        if ( depth ) {
            // Create a 24-bit TEXTURE_SIZE x TEXTURE_SIZE depth buffer for the FBO.
            // We need this to get correct rendering results.
            gl.glGenRenderbuffers(1, idArray, 0);
            gl.glBindRenderbuffer(GL_RENDERBUFFER, idArray[0]);
            gl.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, w, h);
            gl.glBindRenderbuffer(GL_RENDERBUFFER, 0);

            // Attach the newly created depth buffer to the FBO.
            gl.glFramebufferRenderbuffer(
                    GL_FRAMEBUFFER,
                    GL_DEPTH_ATTACHMENT,
                    GL_RENDERBUFFER,
                    idArray[0]
            );
        }

        checkFramebufferStatus(gl);

        // switch back to window-system-provided framebuffer
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return new FrameBufferObject( fbo, fboTexture, w, h );

    }
    
    public void delete(GL2 gl) {
    	int[] idArray = new int[id];
    	gl.glDeleteFramebuffers(1, idArray, 0);
    	gl.glDeleteTextures(1, idArray, 0);
    	gl.glDeleteRenderbuffers(1, idArray, 0);
    }

    private static void checkFramebufferStatus(GL gl) {
        int status = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        switch(status) {
            case GL_FRAMEBUFFER_COMPLETE:
                // All good
                break;
            case GL_FRAMEBUFFER_UNSUPPORTED:
                throw new RuntimeException("Frame buffer extension not supported.");
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                throw new RuntimeException("Incomplete attachment");
            case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                throw new RuntimeException("Incomplete dimensions");
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                throw new RuntimeException("Incomplete drw buffer");
            //case GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT:
              //  throw new RuntimeException("Incomplete duplciate attachments");
            case GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                throw new RuntimeException("incomplete foramts");
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                throw new RuntimeException("incomplete attachments");
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                throw new RuntimeException("Incomplete read buffer");
            default:
                throw new RuntimeException("Logic error");
        }
    }

    private FrameBufferObject(int id, int colorId, int width, int height) {
        this.id = id;
        this.colorId = colorId;
        this.width = width;
        this.height = height;
    }

    public void bind(GL2 gl) {
        gl.glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void clear(GL2 gl) {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void resetViewport(GL2 gl) {
        gl.glViewport(0, 0, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}