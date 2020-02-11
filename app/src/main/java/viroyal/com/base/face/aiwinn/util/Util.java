package viroyal.com.base.face.aiwinn.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.aiwinn.facedetectsdk.common.ConfigLib;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Util {
//	private static String TAG = "Utils";
//	Context context;
//
//	public Util(Context context) {
//		this.context=context;
//	}
//
//	/**
//	 * 获取全部图片地址
//	 * @return
//	 */
//	public ArrayList<String> listAlldir(){
//		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		Uri uri = intent.getData();
//		ArrayList<String> list = new ArrayList<String>();
//		String[] proj ={MediaStore.Images.Media.DATA};
//		Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);//managedQuery(uri, proj, null, null, null);
//		while(cursor.moveToNext()){
//			String path =cursor.getString(0);
//			list.add(new File(path).getAbsolutePath());
//		}
//		return list;
//	}
//
//	public List<FileTraversal> LocalImgFileList(){
//		List<FileTraversal> data=new ArrayList<FileTraversal>();
//		String filename="";
//		List<String> allimglist=listAlldir();
//		List<String> retulist=new ArrayList<String>();
//		if (allimglist!=null) {
//			Set set = new TreeSet();
//			String[]str;
//			for (int i = 0; i < allimglist.size(); i++) {
//				retulist.add(getfileinfo(allimglist.get(i)));
//			}
//			for (int i = 0; i < retulist.size(); i++) {
//				set.add(retulist.get(i));
//			}
//			str= (String[]) set.toArray(new String[0]);
//			for (int i = 0; i < str.length; i++) {
//				filename=str[i];
//				FileTraversal ftl= new FileTraversal();
//				ftl.filename=filename;
//				data.add(ftl);
//			}
//
//			for (int i = 0; i < data.size(); i++) {
//				for (int j = 0; j < allimglist.size(); j++) {
//					if (data.get(i).filename.equals(getfileinfo(allimglist.get(j)))) {
//						data.get(i).filecontent.add(allimglist.get(j));
//					}
//				}
//			}
//		}
//		return data;
//	}
//
//	//显示原生图片尺寸大小
//	public Bitmap getPathBitmap(Uri imageFilePath, int dw, int dh)throws FileNotFoundException {
//		//获取屏幕的宽和高
//		/**
//		 * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
//		 * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds，将其设置为true
//		 * 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
//		 * 当我们设置这个值的时候，我们接着就可以从BitmapFactory.Options的outWidth和outHeight中获取到值
//		 */
//		BitmapFactory.Options op = new BitmapFactory.Options();
//		op.inJustDecodeBounds = true;
//		//由于使用了MediaStore存储，这里根据URI获取输入流的形式
//		Bitmap pic = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath),
//				null, op);
//
//		int wRatio = (int) Math.ceil(op.outWidth / (float) dw); //计算宽度比例
//		int hRatio = (int) Math.ceil(op.outHeight / (float) dh); //计算高度比例
//
//		/**
//		 * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。
//		 * 如果高和宽不是全都超出了屏幕，那么无需缩放。
//		 * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》
//		 * 这需要判断wRatio和hRatio的大小
//		 * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。
//		 * 缩放使用的还是inSampleSize变量
//		 */
//		if (wRatio > 1 && hRatio > 1) {
//			if (wRatio > hRatio) {
//				op.inSampleSize = wRatio;
//			} else {
//				op.inSampleSize = hRatio;
//			}
//		}
//		op.inJustDecodeBounds = false; //注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
//		pic = BitmapFactory.decodeStream(context.getContentResolver()
//				.openInputStream(imageFilePath), null, op);
//
//		return pic;
//	}
//
//	public String getfileinfo(String data){
//		String filename[]= data.split("/");
//		if (filename!=null) {
//			return filename[filename.length-2];
//		}
//		return null;
//	}
//
//	public void imgExcute(ImageView imageView, ImgCallBack icb, String... params){
//		LoadBitAsynk loadBitAsynk=new LoadBitAsynk(imageView,icb);
//		loadBitAsynk.execute(params);
//	}
//
//	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap> {
//
//		ImageView imageView;
//		ImgCallBack icb;
//
//		LoadBitAsynk(ImageView imageView, ImgCallBack icb){
//			this.imageView=imageView;
//			this.icb=icb;
//		}
//
//		@Override
//		protected Bitmap doInBackground(String... params) {
//			Bitmap bitmap=null;
//			try {
//				if (params!=null) {
//					for (int i = 0; i < params.length; i++) {
//						bitmap=getPathBitmap(Uri.fromFile(new File(params[i])), 200, 200);
//					}
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//
//			return bitmap;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			super.onPostExecute(result);
//			if (result!=null) {
////				imageView.setImageBitmap(result);
//				icb.resultImgCall(imageView, result);
//			}
//		}
//	}
//
//	public byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
//		long len = width * height;
//		// yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
//		byte[] yuv = new byte[(int) (len * 3 / 2)];  // 115 * 204 = 23460*3/2 =    35362
//		NuiLog.f("Pic", " yuv_length=" + yuv.length);
//		NuiLog.f("Pic", "aiwinn_yuv_width=" + width + ", height=" + height);
//		int y, u, v;
//		for (int i = 0; i < height; i++) {
//			for (int j = 0; j < width; j++) {
//				// 屏蔽ARGB的透明度值
//				int rgb = pixels[i * width + j] & 0x00FFFFFF;
//				// 像素的颜色顺序为bgr，移位运算。
//				int r = rgb & 0xFF;
//				int g = (rgb >> 8) & 0xFF;
//				int b = (rgb >> 16) & 0xFF;
//				// 套用公式
//				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
//				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
//				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
//				// rgb2yuv
//				// 调整
//				y = y < 16 ? 16 : (y > 255 ? 255 : y);
//				u = u < 0 ? 0 : (u > 255 ? 255 : u);
//				v = v < 0 ? 0 : (v > 255 ? 255 : v);
//				// 赋值
//				// NV12: YYYYYYYY UVUV     =>YUV420SP
//				// NV21: YYYYYYYY VUVU     =>YUV420SP
//				yuv[i * width + j] = (byte) y;
//				int uIndex = (int)(len + (i >> 1) * width + (j & ~1));
//				//  NuiLog.f("Pic", " yuv_length=" + yuv.length);
//				//  NuiLog.f("Pic", " uIndex =" + uIndex);
//				if((uIndex+1)< yuv.length){
//					yuv[uIndex + 0] = (byte) v;
//					yuv[uIndex + 1] = (byte) u;
//				}
////                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) v;
////                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) u;
//			}
//		}
//		NuiLog.f("Pic", " over_yuv_length=" + yuv.length);
//		return yuv;
//	}
//
//	// 获取YUV摄像头旋转角度
//	public static int getDegree() {
//		int degree = 0;
//		if (ConfigLib.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {  //横屏
//			degree = 0;
//		} else {   // 竖屏
//			if (ConfigLib.SwitchCameratype == 1) {  //前置摄像头  CAMERA_ID_FRONT
//				degree = 270;  //90;
//			} else {    //   98   //后置摄像头
//				degree = 90;
//			}
//		}
//		return degree;
//	}
//
//	//获取图片的缩略图,宽度和高度中较小的缩放到vMinWidth. 确保宽度和高度最小的都能覆盖到，
//	//比如图片是3000*2000，要缩放到 150*100, 那么vMinWidth=100;	 Context mContext,
//	public static ImageResult getScaledBitmap(String vPath, File mFile) {
//		ImageResult imageResult = new ImageResult();
//		NuiLog.f(TAG, "getScaledBitmap_Enter");
//		if ((null == vPath) && (mFile == null)) {
//			NuiLog.f(TAG, "路径为null");
//			return null;
//		}
//
//		NuiLog.f(TAG, "getScaledBitmap_Enter_vPath=" + vPath);
//		File file = null;// = new File(vPath);
//		if (vPath != null) {
//			file = new File(vPath);
//		} else {
//			file = mFile;
//			vPath = mFile.getAbsolutePath();
//		}
//
//		//如果不存在了，直接返回
//		if (!file.exists()) {
//			NuiLog.f(TAG, "文件不存在：path=" + vPath);
//			return null;
//		}
//
//		// 先获取图片的宽和高
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;   // 只是获取图片信息
//		BitmapFactory.decodeFile(vPath, options);
//		if (options.outWidth <= 0 || options.outHeight <= 0) {
//			NuiLog.f(TAG, "解析图片失败");
//			return null;
//		}
//		NuiLog.f(TAG, "原图大小：width:" + options.outWidth + ",height:" + options.outHeight + ",aiwinn_aiwinn_path=" + vPath);
////        int height0 = options.outHeight;
//		int tMinWidth = Math.min(options.outWidth, options.outHeight);
//
//		NuiLog.f(TAG, "aiwinn_thumbImg_tMinWidth=" + tMinWidth);
//		// 压缩图片，注意inSampleSize只能是2的整数次幂，如果不是的，话，向下取最近的2的整数次幂，例如3实际上会是2，7实际上会是4
//		options.inSampleSize = 1;  //Math.max(1, tMinWidth / vMinWidth);
//		NuiLog.f(TAG, "options_inSampleSize=" + options.inSampleSize);
//
//		//不能用Config.RGB_565
//		//options.inPreferredConfig = Config.RGB_565;
//		options.inDither = false;
//		options.inPurgeable = true;
//		options.inInputShareable = true;
//		options.inJustDecodeBounds = false;  // 获取图片的内容
//
//		try {
//			imageResult.image = BitmapFactory.decodeFile(vPath, options);
//		} catch (OutOfMemoryError e) {
//			NuiLog.f(TAG, "OutOfMemoryError, decodeFile失败   ");
//			return null;
//		}
//		if (null == imageResult.image) {
//			NuiLog.f(TAG, "decodeFile失败   ");
//			return null;
//		}
//		NuiLog.f(TAG, "thumbImgNow_size22=" + imageResult.image.getWidth() + "," + imageResult.image.getHeight());
//
//		int degree = readPictureDegree(vPath);
//		NuiLog.f(TAG, "aiwinn_degree=" + degree);
//		if (degree != 0) {
//			//	NuiLog.d(tag, "degree="+degree);
//			// 把图片旋转为正的方向
//			imageResult.image = rotateImage(degree, imageResult.image);
//		}
//		int wid = imageResult.image.getWidth();
//		int hgt = imageResult.image.getHeight();
//
//		//如果图片长，款大于这个上线就进行压缩
//		int MaxWidth = ConfigLib.picMaxWIDTH,  MaxHeight = ConfigLib.picMaxHEIGHT;
//		Bitmap mmthumbImgNow = null;
//		if ((wid > MaxWidth) || (hgt > MaxHeight)) {
//			//如果原图片最小宽度比预期最小高度大才进行缩小
//			float ratio = 0;
//			if (wid >= hgt) {
//				ratio = ((float) MaxWidth) / wid;
//			} else {
//				ratio = ((float) MaxHeight) / hgt;
//			}
//			//((float) vMinWidth) / tMinWidth;
//			NuiLog.f(TAG, "ratio_size = " + ratio);
//			Matrix matrix = new Matrix();
//			matrix.postScale(ratio, ratio);   // 缩放函数
//
//			mmthumbImgNow = Bitmap.createBitmap(imageResult.image, 0, 0, wid, hgt, matrix, true);
//			NuiLog.f(TAG, "ratio_size223= " + ratio);
//		} else {
//			mmthumbImgNow = Bitmap.createBitmap(imageResult.image);
//		}
//
//		//大图进行缩放 后提取特征值
//		MaxWidth = 2420;
//		MaxHeight = 2420;
//		Bitmap image2 = null;
//		if ((wid > MaxWidth) || (hgt > MaxHeight)) {
//			//如果原图片最小宽度比预期最小高度大才进行缩小
//			float ratio = 0;
//			if (wid >= hgt) {
//				ratio = ((float) MaxWidth) / wid;
//			} else {
//				ratio = ((float) MaxHeight) / hgt;
//			}
//			//((float) vMinWidth) / tMinWidth;
//			NuiLog.f(TAG, "ratio_size = " + ratio);
//			Matrix matrix = new Matrix();
//			matrix.postScale(ratio, ratio);   // 缩放函数
//			image2 = Bitmap.createBitmap(imageResult.image, 0, 0, wid, hgt, matrix, true);
////                if (!thumbImgNow.isRecycled() && thumbImgNow != mmthumbImgNow) {
////                    thumbImgNow.recycle();
////                    thumbImgNow = null;
////                }
//			NuiLog.f(TAG, "ratio_size223= " + ratio);
//			imageResult.image = image2;
//			NuiLog.f(TAG, "处理后：aiwinn_image2_width=" + image2.getWidth() + ", image2_height=" + image2.getHeight());
//		}
//
//		int scaleWigth = mmthumbImgNow.getWidth(), scaleHight = mmthumbImgNow.getHeight();
//		// 处理长 框都为 偶数；
//		if (scaleWigth % 2 != 0) {
//			scaleWigth = scaleWigth - 1;
//		}
//		if (scaleHight % 2 != 0) {
//			scaleHight = scaleHight - 1;
//		}
//		if (scaleWigth == scaleHight) {  // 长宽不能 相等
//			NuiLog.f(TAG, " ");
//			scaleHight = scaleHight - 4;
//		}
//
//		NuiLog.f(TAG, "偶数处理: Even_size_scaleWigth=" + scaleWigth + ", scaleHight=" + scaleHight);
//		Bitmap EvenScalePic = Bitmap.createBitmap(mmthumbImgNow, 0, 0, scaleWigth, scaleHight);
//		//注意 经过打印判断 EvenScalePic=mmthumbImgNow，有时为true，有时为false
////        if (!mmthumbImgNow.isRecycled() && mmthumbImgNow != EvenScalePic) {
////            mmthumbImgNow.recycle();
////        }
//
//		imageResult.scalePic = EvenScalePic;
//		NuiLog.f(TAG, "imageResult_widthpsScale1= " + imageResult.widthpsScale + ",imageResult_heightpsScale=" + imageResult.heightpsScale);
//		imageResult.widthpsScale = (float) imageResult.image.getWidth()/imageResult.scalePic.getWidth();
//		imageResult.heightpsScale = (float) imageResult.image.getHeight()/imageResult.scalePic.getHeight();
//		NuiLog.f(TAG, "imageResult_widthpsScale2= " + imageResult.widthpsScale + ",imageResult_heightpsScale=" + imageResult.heightpsScale);
//		return imageResult;
//	}
}
