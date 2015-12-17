/*
 *   SoftSqueeze Copyright (c) 2004 Richard Titmuss
 *
 *   This file is part of SoftSqueeze.
 *
 *   SoftSqueeze is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   SoftSqueeze is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SoftSqueeze; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.georgegalt.squeezeme.audio;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fill AudioBuffer from an InputStream. 
 * 
 * @author Richard Titmuss
 */
public class AudioStream implements Runnable {
	private static final String TAG = "AudioStream.Java";

	private static final int DEFAULT_BUFFER_SIZE = 128000;

	private static int audioStreamCount = 0;

	private InputStream audioStream;
    
    private AudioBuffer audioBuffer;
    
    private Object lock = new Object();
    
    private boolean playing = true;
    
	/*
	 * ICY meta data interval
	 */
	private int metaint = -1;
	
	/*
	 * Bytes to read from stream until next meta data interval
	 */
	private int bytesUntilMetadata = -1;

    
    public AudioStream(InputStream audioStream, AudioBuffer audioBuffer, int metaint) {
        this.audioStream = audioStream;
        this.audioBuffer = audioBuffer;
        this.metaint = metaint;
        
        if (metaint > 0)
            bytesUntilMetadata = metaint;

        Thread t = new Thread(this, "AudioStream-"+(audioStreamCount++));
        t.setDaemon(true);
        t.start();
    }

    public boolean isOpen() {
        return playing;
    }
    
    /**
     * Stop the audioStream.
     */
    public void stop() {
        synchronized (lock) {
            playing = false;
            lock.notifyAll();
        }
    }
    
    public void run() {
        Log.d(TAG, "audio stream started");
		
        try {            
            int n = 0;
    		byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
    		
            while (n >= 0 && playing) {
                if (bytesUntilMetadata > 0) {
                    n = audioStream.read(buf, 0, bytesUntilMetadata);
                    bytesUntilMetadata -= n;
                }
                else if (bytesUntilMetadata == 0) {
                    bytesUntilMetadata = metaint;
                    
                    int blockCount = audioStream.read();
                    if (blockCount == 0)
                        continue;
                    
                    int blockSize = blockCount * 16;
                    n = 0;
                    while (n < blockSize)
                        n += audioStream.read(buf, 0, blockSize - n);
                    
                    String metadata = new String(buf, 0, blockSize);
                    Log.d(TAG, "ICY METADATA:" + metadata);
                    audioBuffer.addReadEvent(0, new AudioEvent(audioBuffer,
                            AudioEvent.BUFFER_METADATA, metadata));                    
                    continue;
                }
                else {
                    // no metadata
                    
                    // FIXME first time sync to mp3 frame
                    
                    n = audioStream.read(buf, 0, buf.length);
                    if (n == -1)
                        break;
                }
                
                n = audioBuffer.write(buf, 0, n);
            }

        } catch (Exception e) {
            // usually socket closed
            Log.d(TAG, "Exception in AudioStream", e);
        }
        try {
            playing = false;
            audioBuffer.close();
            Log.d(TAG, "audio stream closed");
        } catch (IOException e1) {
        }        
    }
}
