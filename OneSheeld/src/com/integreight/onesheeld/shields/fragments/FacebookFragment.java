package com.integreight.onesheeld.shields.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.integreight.onesheeld.R;
import com.integreight.onesheeld.shields.controller.FacebookShield;
import com.integreight.onesheeld.shields.controller.FacebookShield.FacebookEventHandler;
import com.integreight.onesheeld.utils.ConnectionDetector;
import com.integreight.onesheeld.utils.OneShieldTextView;
import com.integreight.onesheeld.utils.ShieldFragmentParent;

public class FacebookFragment extends ShieldFragmentParent<FacebookFragment> {

	LinearLayout lastPostTextCont;
	TextView userNameTextView;
	Button facebookLogin;
	Button facebookLogout;
	Bundle savedInstanceState;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.facebook_shield_fragment_layout,
				container, false);
		setHasOptionsMenu(true);

		this.savedInstanceState = savedInstanceState;
		return v;

	}

	@Override
	public void onStart() {
		initializeFirmata();
		checkLogin();
		facebookLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loginToFacebook();
			}
		});
		facebookLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logoutFromFacebook();
			}
		});
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Session.getActiveSession().onActivityResult(getActivity(), requestCode,
				resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		lastPostTextCont = (LinearLayout) getView()
				.findViewById(R.id.postsCont);
		userNameTextView = (TextView) getView().findViewById(
				R.id.facebook_shield_username_textview);
		facebookLogin = (Button) getView().findViewById(R.id.login);
		facebookLogout = (Button) getView().findViewById(R.id.logout);
	}

	private FacebookEventHandler facebookEventHandler = new FacebookEventHandler() {

		@Override
		public void onRecievePost(final String post) {
			// TODO Auto-generated method stub
			if (canChangeUI()) {
				uiHandler.removeCallbacksAndMessages(null);
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						OneShieldTextView posty = (OneShieldTextView) getActivity()
								.getLayoutInflater().inflate(
										R.layout.facebook_post_item,
										lastPostTextCont, false);
						posty.setText(post);
						lastPostTextCont.addView(posty);
						((ScrollView) lastPostTextCont.getParent())
								.invalidate();
						Toast.makeText(getActivity(), "Posted on your wall!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}

		@Override
		public void onFacebookLoggedIn() {
			// TODO Auto-generated method stub
			if (canChangeUI()) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						buttonToLoggedIn();
						// getAppActivity()
						// .setSupportProgressBarIndeterminateVisibility(
						// false);
					}
				});
			}
		}

		@Override
		public void onFacebookError(final String error) {
			// TODO Auto-generated method stub
			if (canChangeUI()) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT)
								.show();
						// buttonToLoggedIn();
						// getAppActivity()
						// .setSupportProgressBarIndeterminateVisibility(
						// false);
					}
				});
			}
		}

	};

	private void initializeFirmata() {
		if ((getApplication().getRunningShields().get(getControllerTag())) == null)
			getApplication().getRunningShields().put(
					getControllerTag(),
					new FacebookShield(getActivity(), getControllerTag(), this,
							savedInstanceState));
		((FacebookShield) getApplication().getRunningShields().get(
				getControllerTag())).setShieldFragment(this);
		((FacebookShield) getApplication().getRunningShields().get(
				getControllerTag()))
				.setFacebookEventHandler(facebookEventHandler);
		checkLogin();
	}

	private void checkLogin() {
		if ((getApplication().getRunningShields().get(getControllerTag())) != null
				&& ((FacebookShield) getApplication().getRunningShields().get(
						getControllerTag())).isFacebookLoggedInAlready()) {
			buttonToLoggedIn();
		} else {
			buttonToLoggedOut();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.logout_from_facebook_menuitem:
			logoutFromFacebook();
			return true;
		case R.id.login_to_facebook_menuitem:
			loginToFacebook();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void logoutFromFacebook() {
		((FacebookShield) getApplication().getRunningShields().get(
				getControllerTag())).logoutFromFacebook();
		buttonToLoggedOut();
	}

	private void loginToFacebook() {
		if (ConnectionDetector.isConnectingToInternet(getActivity()))

			((FacebookShield) getApplication().getRunningShields().get(
					getControllerTag())).loginToFacebook();
		else
			Toast.makeText(getApplication().getApplicationContext(),
					"Please check your Internet connection and try again.",
					Toast.LENGTH_SHORT).show();
		// getAppActivity().setSupportProgressBarIndeterminateVisibility(true);
	}

	private void buttonToLoggedOut() {
		if (facebookLogout != null)
			facebookLogout.setVisibility(View.INVISIBLE);
		if (facebookLogin != null)
			facebookLogin.setVisibility(View.VISIBLE);
		if (userNameTextView != null)
			userNameTextView.setVisibility(View.INVISIBLE);
		if (lastPostTextCont != null) {
			lastPostTextCont.removeAllViews();
			lastPostTextCont.setVisibility(View.INVISIBLE);
		}
	}

	private void buttonToLoggedIn() {
		if (facebookLogin != null)
			facebookLogin.setVisibility(View.INVISIBLE);
		if (facebookLogout != null)
			facebookLogout.setVisibility(View.VISIBLE);
		if (userNameTextView != null)
			userNameTextView.setVisibility(View.VISIBLE);
		if (lastPostTextCont != null) {
			lastPostTextCont.removeAllViews();
			lastPostTextCont.setVisibility(View.VISIBLE);
		}
		userNameTextView.setText("Logged in as: "
				+ ((FacebookShield) getApplication().getRunningShields().get(
						getControllerTag())).getUsername());
	}

	@Override
	public void doOnServiceConnected() {
	}

}
