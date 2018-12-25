package colin.com.module.camera.photo.api;

import android.support.annotation.IntDef;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

@IntDef({CAMERA_FACING_BACK, CAMERA_FACING_FRONT})
public @interface Camera1Facing {
}
