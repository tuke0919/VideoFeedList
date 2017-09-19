package com.ketu.video.Utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.ketu.video.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p/>
 * 功能 :视频链接
 *
 * @author ketu 时间 2017/9/19
 * @version 1.0
 */

public class VideoUtil {


    /**
     * 在线video的url
     */
    public static final String[] videoUrls = {

            "http://mediadownloads.mlb.com/mlbam/2009/05/09/mlbtv_tbabos_4494731_1m.mp4",
            "http://ht.cdn.turner.com/nba/big/games/warriors/2016/04/01/0021501136-bos-gsw-recap.nba_nba_1280x720.mp4",
            "http://ht.cdn.turner.com/nba/big/channels/nba_tv/2016/04/02/20160402-bop-warriors-celtics.nba_nba_1280x720.mp4",
            "http://rmrbtest-image.peopleapp.com/upload/video/201707/1499914158feea8c512f348b4a.mp4",

            "http://mediadownloads.mlb.com/mlbam/2009/05/09/mlbtv_tbabos_4494731_1m.mp4",
            "http://ht.cdn.turner.com/nba/big/games/warriors/2016/04/01/0021501136-bos-gsw-recap.nba_nba_1280x720.mp4",
            "http://ht.cdn.turner.com/nba/big/channels/nba_tv/2016/04/02/20160402-bop-warriors-celtics.nba_nba_1280x720.mp4",
            "http://rmrbtest-image.peopleapp.com/upload/video/201707/1499914158feea8c512f348b4a.mp4",

            "http://mediadownloads.mlb.com/mlbam/2009/05/09/mlbtv_tbabos_4494731_1m.mp4",
            "http://ht.cdn.turner.com/nba/big/games/warriors/2016/04/01/0021501136-bos-gsw-recap.nba_nba_1280x720.mp4",

    };
    public static String videopath = FileUtil.getVideoPath();

    /*video的缩略图url，正常情况下应该是请求后端接口，返回videourl和缩略图url*/
    public static final int[] videoThumbPics = {

            R.drawable.videofeed_11,
            R.drawable.videofeed_12,
            R.drawable.videofeed_13,
            R.drawable.videofeed_14,

            R.drawable.videofeed_11,
            R.drawable.videofeed_12,
            R.drawable.videofeed_13,
            R.drawable.videofeed_14,

            R.drawable.videofeed_11,
            R.drawable.videofeed_12


    };
    /**
     * 获取视频第一帧图片
     * @param videoPath
     * @return
     */
    public static Bitmap getFirstVideoFrame(String videoPath){

        Log.e("frame","截取图片开始时间:" + System.currentTimeMillis());

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        media.release();

       // Bitmap bitmap = createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);

        Log.e("frame","Bitmap的宽高：" + bitmap.getWidth()+"----"+bitmap.getHeight());
        Log.e("frame","截取图片结束时间:" + System.currentTimeMillis());

        return bitmap;
    }

    /**
     * 将bitmap保存到文件
     * @param bitmap
     * @param path
     */
    public static void saveBitmapToFile(Bitmap bitmap, String path) {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fout = null;
        try {
            file.createNewFile();
            fout = new FileOutputStream(file);
           /*将bitmap压缩到输出流,100表示质量不压缩*/
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fout != null) {
                    fout.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static Bitmap createVideoThumbnail(String filePath,int kind){
        Bitmap bitmap = null;
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {

                retriever.setDataSource(filePath);
                bitmap = retriever.getFrameAtTime(0);

            } catch (IllegalArgumentException ex) {
                // Assume this is a corrupt video file
            } catch (RuntimeException ex) {
                // Assume this is a corrupt video file.
            } finally {
                try {
                    retriever.release();
                } catch (RuntimeException ex) {
                    // Ignore failures while cleaning up.
                }
            }

            if (bitmap == null)
                return null;
            // Scale down the bitmap if it's too large.

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);

            if (max > 512) {

                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);

                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {

            bitmap = ThumbnailUtils.createVideoThumbnail(filePath,MediaStore.Images.Thumbnails.MICRO_KIND);
        }
        return bitmap;
    }


}
