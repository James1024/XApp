package colin.com.module.camera;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.nio.ByteBuffer;

import colin.com.common.base.BaseActivity;
import colin.com.common.utils.SdcardUtils;

/**
 * @author wanglr
 * @date 2018/11/28
 */
public class MediaExtractorActivity extends BaseActivity {



    @Override
    public int getLayout() {
        return R.layout.layout_media_extractor;
    }

    @Override
    public void initView() {

    }

    /**
     * 抽取（ly.mp4）的视频文件不含音频
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void extracMedia() {
        MediaExtractor mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(SdcardUtils.getAppLocalPath()+"test1.mp4");
            //获取通道的个数
            int trackCount = mMediaExtractor.getTrackCount();
            int videoIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                //这个trackFormat可不得了可以获取视频的宽高，视频的通道（音频视频），获取视频的长。有的人用这个获取帧率我试过，有的手机行有的
                //不行，会报错。这是个坑，所以还是另求方式
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                String typeMimb = trackFormat.getString(MediaFormat.KEY_MIME);
                Log.e("---trackCount",trackCount+"");
                Log.e("---KEY_MIME",trackFormat.getString(MediaFormat.KEY_MIME)+"");
                Log.e("---KEY_LANGUAGE",trackFormat.getString(MediaFormat.KEY_LANGUAGE)+"");
                Log.e("---KEY_SAMPLE_RATE",trackFormat.getString(MediaFormat.KEY_SAMPLE_RATE)+"");
                Log.e("---KEY_CHANNEL_COUNT",trackFormat.getString(MediaFormat.KEY_CHANNEL_COUNT)+"");
                Log.e("---KEY_WIDTH",trackFormat.getInteger(MediaFormat.KEY_WIDTH)+"");
                Log.e("---KEY_HEIGHT",trackFormat.getInteger(MediaFormat.KEY_HEIGHT)+"");
                Log.e("---KEY_BIT_RATE",trackFormat.getString(MediaFormat.KEY_BIT_RATE)+"");
                Log.e("---KEY_FRAME_RATE",trackFormat.getInteger(MediaFormat.KEY_FRAME_RATE)+"");
                Log.e("---KEY_PCM_ENCODING",trackFormat.getString(MediaFormat.KEY_PCM_ENCODING)+"");
                if (typeMimb.startsWith("video/")) {
                    //这就获取了音频的信号通道了
                    videoIndex = i;
                    break;
                }
            }
            //设置音频通道信号
            mMediaExtractor.selectTrack(videoIndex);
            //再次拿到这个视频通道的format
            MediaFormat trackFormat = mMediaExtractor.getTrackFormat(videoIndex);
            //初始化视频合成器
            MediaMuxer mediaMuxer = new MediaMuxer(SdcardUtils.getAppLocalPath()+"test1_.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            //添加给合成器的通道
            mediaMuxer.addTrack(trackFormat);
            ByteBuffer allocate = ByteBuffer.allocate(500 * 1024);

            mediaMuxer.start();
            //获取视频的帧率
            long videoSametime = 0;
            mMediaExtractor.readSampleData(allocate, 0);
            //跳过I帧，要P帧（视频是由个别I帧和很多P帧组成）
            if (mMediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                mMediaExtractor.advance();
            }
            mMediaExtractor.readSampleData(allocate, 0);
            long firstTime = mMediaExtractor.getSampleTime();
            //下一帧
            mMediaExtractor.advance();

            mMediaExtractor.readSampleData(allocate, 0);
            long senondTime = mMediaExtractor.getSampleTime();
            videoSametime = Math.abs(senondTime - firstTime);
            //重新设置通道读取文件
            mMediaExtractor.unselectTrack(videoIndex);
            mMediaExtractor.selectTrack(videoIndex);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            while (true) {
                int readSamSize = mMediaExtractor.readSampleData(allocate, 0);
                if (readSamSize < 0) {
                    break;
                }
                mMediaExtractor.advance();
                bufferInfo.flags = mMediaExtractor.getSampleFlags();
                bufferInfo.size = readSamSize;
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += videoSametime;
                mediaMuxer.writeSampleData(videoIndex, allocate, bufferInfo);
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mMediaExtractor.release();
            Log.e("av", "success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //MediaExtractor 抽取音频不含视频
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void extracAudio() {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(SdcardUtils.getAppLocalPath()+"test.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            int audioIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                Log.e("---trackCount",trackCount+"");
                Log.e("---KEY_MIME",trackFormat.getString(MediaFormat.KEY_MIME)+"");
                Log.e("---KEY_LANGUAGE",trackFormat.getString(MediaFormat.KEY_LANGUAGE)+"");
//
                String type = trackFormat.getString(MediaFormat.KEY_MIME);
                if (type.startsWith("audio/")) {
                    audioIndex = i;
                }
            }
            mediaExtractor.selectTrack(audioIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex);
            MediaMuxer mediaMuxer = new MediaMuxer(SdcardUtils.getAppLocalPath() + "/test_.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudio = mediaMuxer.addTrack(trackFormat);
            mediaMuxer.start();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 500);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            long sampleTime = 0;
            mediaExtractor.readSampleData(buffer, 0);
            if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                mediaExtractor.advance();
            }
            mediaExtractor.readSampleData(buffer, 0);
            long firstTime = mediaExtractor.getSampleTime();
            mediaExtractor.advance();
//            mediaExtractor.readSampleData(buffer, 0);
            long secondeTime = mediaExtractor.getSampleTime();
            sampleTime = Math.abs(secondeTime - firstTime);

            mediaExtractor.unselectTrack(audioIndex);
            mediaExtractor.selectTrack(audioIndex);
            while (true) {
                int readSize = mediaExtractor.readSampleData(buffer, 0);
                if (readSize < 0) {
                    break;
                }
//                mediaExtractor.readSampleData(buffer, 0);
                mediaExtractor.advance();
                bufferInfo.size = readSize;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs = +sampleTime;
                mediaMuxer.writeSampleData(writeAudio, buffer, bufferInfo);
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            Log.e("av", "success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void combineVideo() {
        //用生成的out_ly.map4 和音频out_eat 合成一个新的eat_ly.mp4
        try {
            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(SdcardUtils.getAppLocalPath() + "/test1_.mp4");
            MediaFormat videoFormat = null;
            int videoTrackIndex = -1;
            int videoTrackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < videoTrackCount; i++) {
                videoFormat = videoExtractor.getTrackFormat(i);
                String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(SdcardUtils.getAppLocalPath() + "/test_.mp4");
            MediaFormat audioFormat = null;
            int audioTrackIndex = -1;
            int audioTrackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < audioTrackCount; i++) {
                audioFormat = audioExtractor.getTrackFormat(i);
                String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }

            videoExtractor.selectTrack(videoTrackIndex);
            audioExtractor.selectTrack(audioTrackIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            MediaMuxer mediaMuxer = new MediaMuxer(SdcardUtils.getAppLocalPath() + "/test_new.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            long sampleTime = 0;
            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long secondTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                long thirdTime = videoExtractor.getSampleTime();
                sampleTime = Math.abs(thirdTime - secondTime);
            }
            videoExtractor.unselectTrack(videoTrackIndex);
            videoExtractor.selectTrack(videoTrackIndex);



            long stampTimeAudio = 0;
            ByteBuffer byteBufferAudio = ByteBuffer.allocate(500 * 1024);
            //获取帧之间的间隔时间
            {
                audioExtractor.readSampleData(byteBufferAudio, 0);
                if (audioExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    audioExtractor.advance();
                }
                audioExtractor.readSampleData(byteBufferAudio, 0);
                long secondTime = audioExtractor.getSampleTime();
                audioExtractor.advance();
                long thirdTime = audioExtractor.getSampleTime();
                stampTimeAudio = Math.abs(thirdTime - secondTime);
                Log.e("fuck", stampTimeAudio + "");
            }

            audioExtractor.unselectTrack(audioTrackIndex);
            audioExtractor.selectTrack(audioTrackIndex);



            while (true) {
                int readVideoSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                if (readVideoSampleSize < 0) {
                    break;
                }
                videoBufferInfo.size = readVideoSampleSize;
                videoBufferInfo.presentationTimeUs += sampleTime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            while (true) {
                int readAudioSampleSize = audioExtractor.readSampleData(byteBufferAudio, 0);
                if (readAudioSampleSize < 0) {
                    break;
                }

                audioBufferInfo.size = readAudioSampleSize;
                audioBufferInfo.presentationTimeUs += stampTimeAudio;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBufferAudio, audioBufferInfo);
                audioExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            videoExtractor.release();
            audioExtractor.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void extracMedia(View view) {
        extracMedia();
    }

    public void extracAudio(View view) {
        extracAudio();
    }

    public void MediaMuxer(View view) {
        combineVideo();
    }
}
