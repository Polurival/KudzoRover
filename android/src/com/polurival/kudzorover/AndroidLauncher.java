package com.polurival.kudzorover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication {

	private RelativeLayout mainView;

	private AdView bannerView;
	private ViewGroup bannerContainer;
	private RelativeLayout.LayoutParams bannerParams;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // отображение на весь экран
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); // удаляем флаг, препятствующий развороту на весь экран на некоторых устройствах

		mainView = new RelativeLayout(this);
		setContentView(mainView);

		View gameView = initializeForView(new LunarRover(gameCallback)); // создаем окно игры, устанавливая пользовательский ввод и визуализацию через openGL
		mainView.addView(gameView);

		bannerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bannerContainer = new LinearLayout(this);

		mainView.addView(bannerContainer, bannerParams);
		bannerContainer.setVisibility(View.GONE);
	}

	private GameCallback gameCallback = new GameCallback() {
		@Override
		public void sendMessage(int message) {

			if (message == LunarRover.SHOW_BANNER) {
				AndroidLauncher.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});
			} else if (message == LunarRover.HIDE_BANNER) {
				AndroidLauncher.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});
			} else if (message == LunarRover.LOAD_INTERSTITIAL) {
				// вызов подгрузки межстраничного баннера

			} else if (message == LunarRover.SHOW_INTERSTITIAL) {
				// в отдельном потоке будем отображать баннер
				AndroidLauncher.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});
			} else if (message == LunarRover.OPEN_MARKET) {
				Uri uri = Uri.parse(getString(R.string.share_url));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			} else if (message == LunarRover.SHARE) {
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareTitle = getString(R.string.share_title);
				String shareBody = getString(R.string.share_body);
				String url = getString(R.string.share_url);

				String body = shareBody + url;

				sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
				sharingIntent.putExtra(Intent.EXTRA_TEXT, body);

				startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
			}
		}
	};
}
