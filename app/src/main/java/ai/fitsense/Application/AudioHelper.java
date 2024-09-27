package ai.fitsense.Application;

import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioHelper {
    private MediaRecorder mediaRecorder;
    private String audioFilePath;

    public void startRecording(String filePath) {
        audioFilePath = filePath;
        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }

    public void stopRecording() {
          if (mediaRecorder != null) {
              mediaRecorder.stop();
              mediaRecorder.release();
              mediaRecorder = null;
          }

    }

    public int getAudioLevel() {
        if (mediaRecorder != null) {
            return mediaRecorder.getMaxAmplitude(); // Returns the max amplitude since last call
        } else {
            return 0; // No recording is happening
        }
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }


    public String getBase64Audio() {
        Log.e("AudioHelper", "Getting base64 audio");
        try {
            File file = new File(audioFilePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            fis.close();
            Log.i("AudioHelper", "Base64 audio: " + Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

