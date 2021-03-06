package utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xzq.easy_zxing.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 常用设置
 *
 * @author yushan
 */
public class CommonUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取屏幕的宽、高
     *
     * @param context
     * @return
     */
    public static int[] getScreenWH(Context context) {
        WindowManager vmManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = vmManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    public static int getScreenDisPlay(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    /**
     * 为月、日补零
     *
     * @param x
     * @return
     */
    public static String FormatString(int x) {
        // TODO Auto-generated method stub
        String s = Integer.toString(x);
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 截取时间字符串
     *
     * @param time
     * @return 2015-02-01
     */
    public static String getSplitTimeStr(String time) {
        return time.substring(0, 10).toString().trim();
    }

    /**
     * 设置TextView 个别文字的颜色
     *
     * @param context
     * @param contentText
     * @param colorText
     * @param tv_info     contentText  完整字符串
     *                    colorText  要变颜色的字符串 【得到字符串要变色的String】
     *                    tv_info  TextView 控件
     */
    public static void setTextViewColor(Context context, String contentText, String colorText, TextView tv_info) {
        int fstart = contentText.indexOf(colorText);
        int fend = fstart + colorText.length();
        SpannableStringBuilder style = new SpannableStringBuilder(contentText);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.guide_theme_color)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_info.setText(style);
    }

    /**
     * 把图片保存到相册
     *  @param context
     * @param bmp
     */
    public static String saveImageToGallery(Context context, Bitmap bmp, String path) {
        // 首先保存图片
//        ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("保存中，请稍候...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        File appDir = new File(getSDCardPath() + path);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
            Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
//            progressDialog.dismiss();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
//            progressDialog.dismiss();
            return null;
        }
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }
    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    /**
     * 验证手机号码
     *
     * @param mobile mobile 手机号码
     *               <p>
     *               规则：telRegex = "[1][34578]\\d{9}
     *               "[1]"代表第1位为数字1
     *               "[34578]"代表第二位可以为3、4、5、7、8中的一个
     *               "\\d{9}" 代表后面是可以是0～9的数字，有9位
     */
    public static boolean validateMobliePhone(String mobile) {

        String telRegex = "[1][134578]\\d{9}";

        if (TextUtils.isEmpty(mobile))
            return false;
        else
            return mobile.matches(telRegex);

    }
    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
