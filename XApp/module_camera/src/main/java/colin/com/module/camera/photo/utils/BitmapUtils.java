package colin.com.module.camera.photo.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class BitmapUtils {

    private static final String TAG = "BitmapUtils";
    private static final int BLACK = 0xff000000;

    public static Bitmap loadBitmap(String path, int w, int h) {

        Options opts = new Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Config.RGB_565;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        } else if (width <= w && height <= h) {
            // 若是小图，不需要缩放
            return BitmapFactory.decodeFile(path);
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        Bitmap bm = Bitmap.createScaledBitmap(weak.get(), w, h, true);
        if (bm == null)
            return null;

        return getRotateBmIfNeedy(path, bm, (int) scaleWidth, (int) scaleHeight);
    }

    public static Bitmap generateVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), 600, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, int rotation, int targetWidth, int targetHeight) {
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Config.RGB_565);
        if (source == null)
            return targetBitmap;

        Canvas canvas = new Canvas(targetBitmap);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation, source.getWidth() / 2, source.getHeight() / 2);
        canvas.drawBitmap(source, matrix, new Paint());
        source.recycle();
        return targetBitmap;
    }

    public static Bitmap getRotateBmIfNeedy(String path, Bitmap bm, int targetWidth, int targetHeight) {

        int orientation = 0;

        try {
            ExifInterface ei = new ExifInterface(path);

            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            System.err.println(e);
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bm, 90, targetWidth, targetHeight);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bm, 180, targetWidth, targetHeight);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bm, 270, targetWidth, targetHeight);
            default:
                return bm;
            // etc.
        }
    }

    public static Bitmap getRotateBmIfNeedy(int orien, Bitmap bm, int targetWidth, int targetHeight) {
        switch (orien) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bm, 90, targetWidth, targetHeight);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bm, 180, targetWidth, targetHeight);
            default:
                return bm;
            // etc.
        }
    }

    /**
     * 压缩图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap getBitmapCompressed(Context context, int resId, float width, float height) {
        Options newOpts = new Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = height;//
        float ww = width;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeResource(context.getResources(), resId, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试

        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param context
     * @return
     */
    public static Bitmap getBitmapCompressed(Context context, String pathName, float width, float height) {
        Options newOpts = new Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = height;//
        float ww = width;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.RGB_565;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(pathName, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    /**
     * 按照指定宽高等比例加载位图到内存
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmap(byte[] data, int width, int height) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int xScale = opts.outWidth / width;
        int yScale = opts.outHeight / height;
        opts.inSampleSize = xScale > yScale ? xScale : yScale;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }

    public static Bitmap getBitmapCompressed(Bitmap bgimage, double newWidth) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    public static Bitmap getBitmapCompressed(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    @SuppressLint("NewApi")
    public static int getBytesCount(Bitmap bmp) {
        if (bmp == null)
            return -1;

        final int bytes;
        if (android.os.Build.VERSION.SDK_INT >= 12)
            bytes = bmp.getByteCount();
        else
            bytes = bmp.getWidth() * bmp.getHeight() * 4;

        return bytes;

    }

    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        Options options = new Options();
        options.inDither = false;
        options.inPreferredConfig = Config.RGB_565;
        Cursor cursor = cr.query(uri, new String[]{MediaStore.Video.Media._ID}, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)); // image
        // id
        // in
        // image
        // table.s

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, Images.Thumbnails.MINI_KIND, options);

        return bitmap;
    }

    public static Bitmap getImageThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        Options options = new Options();
        options.inDither = false;
        options.inPreferredConfig = Config.RGB_565;
        Cursor cursor = cr.query(uri, new String[]{MediaStore.Video.Media._ID}, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)); // image
        // id
        // in
        // 路径
        // int column_index =
        // cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // String filePath = cursor.getString(column_index);

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, Images.Thumbnails.MICRO_KIND, options);

        return bitmap;
    }

    public static boolean save(Bitmap bm, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);

            bm.compress(CompressFormat.JPEG, 100, out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean save(Bitmap bm, String filePath, int quality) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);

            bm.compress(CompressFormat.JPEG, quality, out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static File saveToFile(Bitmap bm, String fileName) {
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);

            bm.compress(CompressFormat.JPEG, 100, out);
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自定义相册保存时用
     *
     * @param data
     * @param fileName
     * @param angle
     * @return
     */
    public static Bitmap save(byte[] data, String fileName, int angle) {
        Options opts = new Options();
        opts.outWidth = 640;
        opts.outHeight = 640;
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        bm = rotaingImageView(angle, bm);
        save(bm, fileName);
        return bm;
    }

    public static boolean delete(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 旋转图片
     *
     * @param angle
     *
     * @param bitmap
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap getRotateBitmap(String path, Bitmap bm) {
        int degree = readPictureDegree(path);
        if (degree == 0) {
            return bm;
        } else {
            return rotaingImageView(degree, bm);
        }
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    // 2
    private static final int ww = 480;
    private static final int hh = 800;

    /**
     * 图片变圆角
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * save
     *
     * @param path
     * @param buffer
     * @return
     */
    public static int saveBitmap(String path, byte[] buffer) {
        int result = -1;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            out.close();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * 删除图片
     *
     * @param path
     */
    public static void deleteBitmap(String path) {
        File file = new File(path);
        if (file != null) {
            file.delete();
        }
    }

    /**
     * @param filePath
     * @return
     */
    public static Bitmap getdecodeBitmap(String filePath) {
        if (filePath == null) {
            return null;
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        int width = options.outWidth;
        int height = options.outHeight;
        float scale = 1f;
        if (width > ww && width > height) {
            scale = width / ww;
        } else if (height > hh && height > width) {
            scale = height / hh;
        } else {
            scale = 1;
        }

        options.inSampleSize = (int) scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    public static int saveBitmap(String path, Bitmap bitmap) {
        int result = -1;
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public static int[] getBitmapSize(String bitmapFileName) {
        Options newOpts = new Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFileName, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        return new int[]{w, h};
    }

    //使用BitmapFactory.Options的inSampleSize参数来缩放
    public static Bitmap resizeImage2(String path,
                                      int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
     * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
     *
     * @param maxBytes 压缩后的图像最大大小 单位为byte
     * @return
     */
    public final static Bitmap compressBitmap(Bitmap bitmap, long maxBytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 90, baos);
            byte[] bts = baos.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
            baos.close();
            return bmp;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @param isLand 是横屏吗
     * @return bitmap 图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap, boolean isLand) {

        //旋转图片 动作
        Matrix matrix = new Matrix();
        if (!isLand) {
            matrix.postRotate(angle);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

}
