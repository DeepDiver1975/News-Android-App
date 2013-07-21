package de.luhmer.owncloudnewsreader;

import java.net.MalformedURLException;
import java.net.URL;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import de.luhmer.owncloudnewsreader.database.DatabaseConnection;
import de.luhmer.owncloudnewsreader.reader.owncloud.OwnCloudReaderMethods;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginDialogFragment extends SherlockDialogFragment {
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;
	private String mOc_root_path;
	private boolean mCbAllowAllSSL;
	
	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mOc_root_path_View;
	private CheckBox mCbAllowAllSSLView;
	
	//private View mLoginFormView;
	//private View mLoginStatusView;	
	//private TextView mLoginStatusMessageView;

	ProgressDialog mDialogLogin;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_signin, null);
        builder.setView(view)
        	/*
        	// Add action buttons
           .setPositiveButton(R.string.action_sign_in_short, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
            	   //attemptLogin();
               }
           })
           .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   LoginDialogFragment.this.getDialog().cancel();
               }
           })*/
           .setTitle(getString(R.string.action_sign_in_short));  
        
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUsername = mPrefs.getString(SettingsActivity.EDT_USERNAME_STRING, null);
        mPassword = mPrefs.getString(SettingsActivity.EDT_PASSWORD_STRING, null);
        mOc_root_path = mPrefs.getString(SettingsActivity.EDT_OWNCLOUDROOTPATH_STRING, null);
        mCbAllowAllSSL = mPrefs.getBoolean(SettingsActivity.CB_ALLOWALLSSLCERTIFICATES_STRING, false);
        
    	// Set up the login form.
 		//mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
 		mUsernameView = (EditText) view.findViewById(R.id.username);
 		mUsernameView.setText(mUsername);

 		mPasswordView = (EditText) view.findViewById(R.id.password);
 		mPasswordView.setText(mPassword); 		

 		mOc_root_path_View = (EditText) view.findViewById(R.id.edt_owncloudRootPath);
 		mOc_root_path_View.setText(mOc_root_path);
 		
 		mCbAllowAllSSLView = (CheckBox) view.findViewById(R.id.cb_AllowAllSSLCertificates);
 		mCbAllowAllSSLView.setChecked(mCbAllowAllSSL);
 		mCbAllowAllSSLView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				mPrefs.edit()
					.putBoolean(SettingsActivity.CB_ALLOWALLSSLCERTIFICATES_STRING, isChecked)
					.commit();
			}
		});
 		
 		
 		//mLoginFormView = view.findViewById(R.id.login_form);
 		//mLoginStatusView = view.findViewById(R.id.login_status);
 		//mLoginStatusMessageView = (TextView) view.findViewById(R.id.login_status_message);
        
 		view.findViewById(R.id.btn_signin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
 		
 		view.findViewById(R.id.btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LoginDialogFragment.this.getDialog().cancel();
					}
				});
 		
 		
        return builder.create();
    }
	
	private ProgressDialog BuildPendingDialogWhileLoggingIn()
	{
		ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle(getString(R.string.login_progress_signing_in));
        return pDialog;
	}
	
	
	/*
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_signin, container);
		
		// Set up the login form.
		//mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
		mUsernameView = (EditText) view.findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) view.findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.btn_signin || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mOc_root_path_View = (EditText) view.findViewById(R.id.edt_owncloudRootPath);
		mOc_root_path_View.setText(mOc_root_path);
		
		mLoginFormView = view.findViewById(R.id.login_form);
		mLoginStatusView = view.findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) view.findViewById(R.id.login_status_message);

		view.findViewById(R.id.btn_signin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		return view;
	}*/
	
	
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo_Dialog);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_signin);

		// Set up the login form.
		//mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.btn_signin || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mOc_root_path_View = (EditText) findViewById(R.id.edt_owncloudRootPath);
		mOc_root_path_View.setText(mOc_root_path);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.btn_signin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}*/

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);
		mOc_root_path_View.setError(null);
		
		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mOc_root_path = mOc_root_path_View.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}/* else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}*/
		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} /*else if (!mUsername.contains("@")) {
			mUsernameView.setError(getString(R.string.error_invalid_email));
			focusView = mUsernameView;
			cancel = true;
		}*/
		
		if (TextUtils.isEmpty(mOc_root_path)) {
			mOc_root_path_View.setError(getString(R.string.error_field_required));
			focusView = mOc_root_path_View;
			cancel = true;
		} else {
			try {
				URL url = new URL(mOc_root_path);
				if(!url.getProtocol().equals("https"))
					ShowAlertDialog(getString(R.string.login_dialog_title_security_warning),
							getString(R.string.login_dialog_text_security_warning), getActivity());
			} catch (MalformedURLException e) {
				mOc_root_path_View.setError(getString(R.string.error_invalid_url));
				focusView = mOc_root_path_View;
				cancel = true;
				//e.printStackTrace();
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();			
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			
			//mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			//showProgress(true);
			mAuthTask = new UserLoginTask(mUsername, mPassword, mOc_root_path);
			mAuthTask.execute((Void) null);
			
			mDialogLogin = BuildPendingDialogWhileLoggingIn();
     	   	mDialogLogin.show();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	/*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}*/

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
		String username;
		String password;
		String oc_root_path;
		String exception_message;
		
		public UserLoginTask(String username, String password, String oc_root_path) {
			this.username = username;
			this.password = password;
			this.oc_root_path = oc_root_path;
		}
		
		@Override
		protected Integer doInBackground(Void... params) {

			try {
				String _version = OwnCloudReaderMethods.GetVersionNumber(getActivity(), username, password, oc_root_path);
				if(_version != null)
				{
					_version = _version.replace(".", "");
					return  Integer.parseInt(_version);
				}
				
			} catch (Exception e) {
				exception_message = e.getLocalizedMessage();
				return -1;
			}
			
			//if(version_info > 1101)
			//	Sho
				
			return 0;
		}

		@Override
		protected void onPostExecute(final Integer versionCode) {
			mAuthTask = null;
			//showProgress(false);

			mDialogLogin.dismiss();
			
			if(versionCode == -1 && exception_message.equals("Value <!DOCTYPE of type java.lang.String cannot be converted to JSONObject")) {
				ShowAlertDialog(getString(R.string.login_dialog_title_error), getString(R.string.login_dialog_text_not_compatible), getActivity());
			} else if(versionCode == -1) {
				ShowAlertDialog(getString(R.string.login_dialog_title_error), exception_message, getActivity());
			} else if(versionCode == 0){
				ShowAlertDialog(getString(R.string.login_dialog_title_error), getString(R.string.login_dialog_text_something_went_wrong), getActivity());
			} else {
				//Reset Database
				DatabaseConnection dbConn = new DatabaseConnection(getActivity());
				dbConn.resetDatabase();
				dbConn.closeDatabase();
				
				//LoginFragment.this.dismiss();
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				Editor editor = mPrefs.edit();
				editor.putString(SettingsActivity.EDT_OWNCLOUDROOTPATH_STRING, oc_root_path);
				editor.putString(SettingsActivity.EDT_PASSWORD_STRING, password);
				editor.putString(SettingsActivity.EDT_USERNAME_STRING, username);
				editor.commit();
				
				LoginDialogFragment.this.getDialog().cancel();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}
	
	public static void ShowAlertDialog(String title, String text, Activity activity)
	{
		AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
		aDialog.setTitle(title);
		aDialog.setMessage(text);
		aDialog.setPositiveButton(activity.getString(android.R.string.ok) , null);
		aDialog.create().show();
	}
}
