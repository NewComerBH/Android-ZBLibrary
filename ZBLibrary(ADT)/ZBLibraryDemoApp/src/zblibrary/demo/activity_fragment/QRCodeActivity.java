/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zblibrary.demo.activity_fragment;

import zblibrary.demo.R;
import zblibrary.demo.model.User;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.util.ImageLoaderUtil;
import zuo.biao.library.util.Json;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

/**二维码界面Activity
 * @author Lemon
 */
public class QRCodeActivity extends BaseActivity implements OnClickListener, OnBottomDragListener {
	private static final String TAG = "QRCodeActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	/**启动这个Activity的Intent
	 * @param context
	 * @param contactBeanJsonString
	 * @return
	 */
	public static Intent createIntent(Context context, long userId) {
		return new Intent(context, QRCodeActivity.class).
				putExtra(INTENT_ID, userId);
	}

	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	@Override
	@NonNull
	public BaseActivity getActivity() {
		return this;
	}

	private long userId = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_activity, this);

		intent = getIntent();
		userId = intent.getLongExtra(INTENT_ID, userId);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initListener();
		//功能归类分区方法，必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	private ImageView ivQRCodeHead;
	private TextView tvQRCodeName;

	private ImageView ivQRCodeCode;
	private View ivQRCodeProgress;
	@Override
	public void initView() {//必须调用

		ivQRCodeHead = (ImageView) findViewById(R.id.ivQRCodeHead);
		tvQRCodeName = (TextView) findViewById(R.id.tvQRCodeName);

		ivQRCodeCode = (ImageView) findViewById(R.id.ivQRCodeCode);
		ivQRCodeProgress = findViewById(R.id.ivQRCodeProgress);
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	private User user;
	@Override
	public void initData() {//必须调用

		ivQRCodeProgress.setVisibility(View.VISIBLE);
		runThread(TAG + "initData", new Runnable() {

			@Override
			public void run() {

				user = CacheManager.getInstance().get(User.class, "" + userId);
				if (user == null) {
					user = new User(userId);
				}
				runUiThread(new Runnable() {
					@Override
					public void run() {
						ImageLoaderUtil.loadImage(ivQRCodeHead, user.getHead());
						tvQRCodeName.setText(StringUtil.getTrimedString(
								StringUtil.isNotEmpty(user.getName(), true)
								? user.getName() : user.getPhone()));
					}
				});

				setQRCode(user);
			}
		});

	}

	private Bitmap qRCodeBitmap;
	protected void setQRCode(User user) {
		if (user == null) {
			Log.e(TAG, "setQRCode  user == null" +
					" || StringUtil.isNotEmpty(user.getPhone(), true) == false >> return;");
			return;
		}

		try {
			qRCodeBitmap = EncodingHandler.createQRCode(Json.toJSONString(user)
					, (int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
		} catch (WriterException e) {
			e.printStackTrace();
			Log.e(TAG, "initData  try {Bitmap qrcode = EncodingHandler.createQRCode(contactJson, ivQRCodeCode.getWidth());" +
					" >> } catch (WriterException e) {" + e.getMessage());
		}

		runUiThread(new Runnable() {
			@Override
			public void run() {
					ivQRCodeProgress.setVisibility(View.GONE);
					ivQRCodeCode.setImageBitmap(qRCodeBitmap);						
			}
		});	
	}

	//data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//listener事件监听区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initListener() {//必须调用

		findViewById(R.id.tvQRCodeReturn).setOnClickListener(this);

	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {

			return;
		}

		finish();
	}

	//示例代码<<<<<<<<<<<<<<<<<<<
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvQRCodeReturn:
			onDragBottom(false);
			break;
		default:
			break;
		}
	}
	//示例代码>>>>>>>>>>>>>>>>>>>


	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	protected void onDestroy() {
		super.onDestroy();

		ivQRCodeProgress = null;
		ivQRCodeCode = null;
		user = null;

		if (qRCodeBitmap != null) {
			if (qRCodeBitmap.isRecycled() == false) {
				qRCodeBitmap.recycle();
			}
			qRCodeBitmap = null;
		}
		if (ivQRCodeCode != null) {
			ivQRCodeCode.setImageBitmap(null);
			ivQRCodeCode = null;
		}
	}


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//listener事件监听区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}