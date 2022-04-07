package com.sssoft_lib.mobile.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sssoft_lib.mobile.activity.R;

public class SuccinctProgress {

	private static ProgressDialog pd;

	public static void showSuccinctProgress(Context context, String message,boolean isCanceledOnTouchOutside, boolean isCancelable) {

		pd = new ProgressDialog(context, R.style.mobile_succinctProgressDialog);
		pd.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		pd.setCancelable(isCancelable);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		View view = LayoutInflater.from(context).inflate(
				R.layout.succinct_progress_content, null);
		ImageView mProgressIcon = (ImageView) view
				.findViewById(R.id.progress_icon);
		mProgressIcon.setImageResource(R.drawable.icon_progress_style1);
		TextView mProgressMessage = (TextView) view
				.findViewById(R.id.progress_message);
		mProgressMessage.setText(message);
		new AnimationUtils();
		Animation jumpAnimation = AnimationUtils.loadAnimation(context,
				R.anim.succinct_animation);
		mProgressIcon.startAnimation(jumpAnimation);
		pd.getWindow().addFlags(5);

		if (context!=null && !((Activity) context).isFinishing()) {
			pd.show();
			pd.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
			pd.setContentView(view, params);
		}

	}

	
	public static boolean isShowing() {

		if (pd != null && pd.isShowing()) {

			return true;
		}
		return false;

	}

	
	public static void dismiss() {

		if (isShowing()) {

			pd.dismiss();
		}

	}
}
