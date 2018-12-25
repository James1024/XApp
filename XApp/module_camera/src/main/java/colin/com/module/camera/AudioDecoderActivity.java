package colin.com.module.camera;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import colin.com.common.utils.SdcardUtils;

/**
 * @author wanglr
 * @date 2018/11/29
 */
public class AudioDecoderActivity extends Activity {

    protected static AudioDecoderThread mAudioDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_decoder);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioDecoder.stop();
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String SAMPLE = SdcardUtils.getAppLocalPath() + "temp.aac";

        public PlaceholderFragment() {
            mAudioDecoder = new AudioDecoderThread();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_audio_decoder, container, false);

            final Button btn = (Button) rootView.findViewById(R.id.play);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mAudioDecoder.startPlay(SAMPLE);
                }
            });
            return rootView;
        }
    }

}
