package colin.com.module.camera;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import colin.com.common.base.BaseActivity;
import colin.com.common.utils.SdcardUtils;

/**
 * @author wanglr
 * @date 2018/11/27
 */
public class AudioRecordActivity extends BaseActivity {
    AudioRecord audioRecord;
    File parent;
    boolean isRecording;
    int  audioSource,frequency,channelConfig,audioFormat,recordBufSize;
    AudioTrack audioTrack;


    @Override
    public int getLayout() {
        return R.layout.layout_audiorecord;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 0);

        }
        initRecord();
    }


    private void initRecord() {
        //指定音频源
        audioSource = MediaRecorder.AudioSource.MIC;
        //指定采样率(MediaRecoder 的采样率通常是8000Hz CD的通常是44100Hz 不同的Android手机硬件将能够以不同的采样率进行采样。其中11025是一个常见的采样率)
        frequency = 44100;
        //指定捕获音频的通道数目.在AudioFormat类中指定用于此的常量
         channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
        //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
         audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        recordBufSize = AudioRecord.getMinBufferSize(frequency, channelConfig, audioFormat);


        //int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
        //            int bufferSizeInBytes
        audioRecord = new AudioRecord(audioSource, frequency, channelConfig, audioFormat, recordBufSize);

        parent = new File(SdcardUtils.getAppLocalPath());
        if (!parent.exists()) {
            parent.mkdirs();
        }

    }

    public void StartRecord(View view) {

        getAudio();

    }

    /**
     * 获取录取的音频,并且写入文件
     */
    private void getAudio() {
        isRecording = true;

        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(parent, "audio.pcm");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataOutputStream outputStream = null;
                try {
                    outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    byte[] buffer = new byte[recordBufSize];
                    //开始录音
                    audioRecord.startRecording();
                    int r = 0;
                    while (isRecording) {
                        int readResult = audioRecord.read(buffer, 0, recordBufSize);
                        for (int i = 0; i < readResult; i++) {
                            outputStream.write(buffer[i]);
                        }
                        r++;
                        Log.e("avPcm", "录制中....");
                    }
                    audioRecord.stop();
                    audioRecord.release();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void playPcm() {
        DataInputStream dis = null;
        File file = new File(parent, "audio.pcm");
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfig, audioFormat);
            audioTrack = new AudioTrack(audioSource, frequency, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
            byte[] datas = new byte[bufferSize];
            audioTrack.play();
            while (true) {
                int i = 0;
                try {
                    while (dis.available() > 0 && i < datas.length) {
                        datas[i] = dis.readByte();
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                audioTrack.write(datas, 0, datas.length);
                //表示读取完了
                if (i != bufferSize) {
                    audioTrack.stop();
                    audioTrack.release();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void convertWaveFile() {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = frequency;
        int channels = channelConfig;
        long byteRate = 16 * frequency * channels / 8;
        byte[] data = new byte[recordBufSize];
        try {
            File filePcm = new File(parent, "audio.pcm");
            File fileWav = new File(parent, "audio.wav");
            in = new FileInputStream(filePcm);
            out = new FileOutputStream(fileWav);
            //视频源的总长度
            totalAudioLen = in.getChannel().size();
            //由于不包括RIFF和WAV
            totalDataLen = totalAudioLen + 36;

            //先写入头文件
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                //再写入数据源
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        try {
            out.write(header, 0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void StopRecord(View view) {
        isRecording=false;
    }

    public void PlayRecord(View view) {
        playPcm();
    }


    public void convertWaveFile(View view) {
        convertWaveFile();
    }


    public void convertAACFile(View view) {
        convertWaveFile();
    }

    public void playWav(View view) {
        playWav();
    }
    DataInputStream dis;
    private void playWav() {
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(frequency, channelConfig, audioFormat);
        audioTrack = new AudioTrack(audioSource, frequency, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
        File fileWav = new File(parent, "audio.wav");
        try {
            dis = new DataInputStream(new FileInputStream(fileWav));
            readWavHeader(dis);
            new Thread(ReadDataRunnable).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable ReadDataRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[1024 * 2];
            while (readData(buffer, 0, buffer.length) > 0) {
                if (audioTrack.write(buffer, 0, buffer.length) != buffer.length) {
                }

                audioTrack.play();
            }
            audioTrack.stop();
            audioTrack.release();
            try {
                if (dis != null) {
                    dis.close();
                    dis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };



    public int readData(byte[] buffer, int offset, int count) {
        try {
            int nbytes = dis.read(buffer, offset, count);
            if (nbytes == -1) {
                return 0;
            }
            return nbytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void readWavHeader(DataInputStream dis) {
        try {
            byte[] byteIntValue = new byte[4];
            byte[] byteShortValue = new byte[2];
            //读取四个
            String mChunkID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "mChunkID:" + mChunkID);
            dis.read(byteIntValue);
            int chunkSize = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "chunkSize:" + chunkSize);
            String format = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "format:" + format);
            String subchunk1ID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "subchunk1ID:" + subchunk1ID);
            dis.read(byteIntValue);
            int subchunk1Size = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "subchunk1Size:" + subchunk1Size);
            dis.read(byteShortValue);
            short audioFormat = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "audioFormat:" + audioFormat);
            dis.read(byteShortValue);
            short numChannels = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "numChannels:" + numChannels);
            dis.read(byteIntValue);
            int sampleRate = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "sampleRate:" + sampleRate);
            dis.read(byteIntValue);
            int byteRate = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "byteRate:" + byteRate);
            dis.read(byteShortValue);
            short blockAlign = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "blockAlign:" + blockAlign);
            dis.read(byteShortValue);
            short btsPerSample = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "btsPerSample:" + btsPerSample);
            String subchunk2ID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "subchunk2ID:" + subchunk2ID);
            dis.read(byteIntValue);
            int subchunk2Size = byteArrayToInt(byteIntValue);
            Log.e("subchunk2Size", "subchunk2Size:" + subchunk2Size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int byteArrayToInt(byte[] byteIntValue) {

        return ByteBuffer.wrap(byteIntValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }


    private short byteArrayToShort(byte[] byteShortValue) {

        return ByteBuffer.wrap(byteShortValue).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }


    public void pcmToAac(View view) {
        pcmToAac(MediaFormat.MIMETYPE_AUDIO_AAC,4,channelConfig);
    }

    private void pcmToAac(String mineType, int sampleRate, int channelConfig){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = frequency;
        int channels = channelConfig;
        long byteRate = 16 * frequency * channels / 8;
        byte[] data = new byte[recordBufSize];
        try {
            File filePcm = new File(parent, "audio.pcm");
            File fileAac = new File(parent, "audio.aac");
            in = new FileInputStream(filePcm);
            out = new FileOutputStream(fileAac);
            //视频源的总长度
            totalAudioLen = in.getChannel().size();
            //由于不包括RIFF和WAV
            totalDataLen = totalAudioLen + 36;

            //先写入头文件
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            //在写sample数据
            MediaCodec mAudioEncodec = MediaCodec.createEncoderByType(mineType);
            MediaFormat audioFormat = MediaFormat.createAudioFormat(mineType, sampleRate, channelConfig);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);
            mAudioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            MediaCodec.BufferInfo mAudioBuffInfo = new MediaCodec.BufferInfo();
            mAudioEncodec.start();
            mAudioEncodec.dequeueOutputBuffer(mAudioBuffInfo,100);
            mAudioEncodec.flush();




            while (in.read(data) != -1) {
                //再写入数据源
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private void WriteAacFileHeader(FileOutputStream out, int samplerate,int packetLen) {
        byte[] header = new byte[44];
        int profile = 2; // AAC LC
        int freqIdx = samplerate; // samplerate
        int chanCfg = 2; // CPE
        header[0] = (byte) 0xFF; // 0xFFF(12bit) 这里只取了8位，所以还差4位放到下一个里面
        header[1] = (byte) 0xF9; // 第一个t位放F
        header[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        header[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        header[4] = (byte) ((packetLen & 0x7FF) >> 3);
        header[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        header[6] = (byte) 0xFC;
        try {
            out.write(header, 0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }











}
