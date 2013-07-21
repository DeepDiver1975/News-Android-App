package de.luhmer.owncloudnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.luhmer.owncloudnewsreader.helper.MenuUtilsSherlockFragmentActivity;
import de.luhmer.owncloudnewsreader.helper.ThemeChooser;

/**
 * An activity representing a single NewsReader detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * {@link NewsReaderListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link NewsReaderDetailFragment}.
 */
public class NewsReaderDetailActivity extends MenuUtilsSherlockFragmentActivity {

	public static final String FOLDER_ID = "FOLDER_ID";
	public static final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
	public static final String ITEM_ID = "ITEM_ID";
	public static final String TITEL = "TITEL";
	protected static final String TAG = "NewsReaderDetailActivity";
	
	
	String titel;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		ThemeChooser.chooseTheme(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsreader_detail);

		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			
			Intent intent = getIntent();
			titel = "Name Missing";
			
			String idFeed = null;
			String idFolder = null;
			
			if(intent.hasExtra(SUBSCRIPTION_ID))
				idFeed = intent.getExtras().getString(SUBSCRIPTION_ID);
			if(intent.hasExtra(FOLDER_ID))
				idFolder = intent.getExtras().getString(FOLDER_ID);			
			if(intent.hasExtra(TITEL))
				titel = intent.getExtras().getString(TITEL);
			
			//getSupportActionBar().setTitle(titel);
			
			
			
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(
					NewsReaderDetailFragment.ARG_ITEM_ID,
					getIntent().getStringExtra(
							NewsReaderDetailFragment.ARG_ITEM_ID));			
			
			if(idFeed != null)
				arguments.putString(SUBSCRIPTION_ID, idFeed);
			if(idFolder != null)
				arguments.putString(FOLDER_ID, idFolder);
			
			arguments.putString(TITEL, titel);
			NewsReaderDetailFragment fragment = new NewsReaderDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.newsreader_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.subscription, menu);
		
		super.onCreateOptionsMenu(menu, getSupportMenuInflater(), true, this);
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//if (requestCode == 1) {
			if(resultCode == RESULT_OK){      
				int pos = data.getIntExtra("POS", 0);				
				UpdateListViewAndScrollToPos(this, pos);
			}
			if (resultCode == RESULT_CANCELED) {    
				//Write your code on no result return 
			}
		//}
	}
	

    //@TargetApi(Build.VERSION_CODES.FROYO)
	public static void UpdateListViewAndScrollToPos(FragmentActivity act, int pos)
    {
        ((NewsReaderDetailFragment) act.getSupportFragmentManager().findFragmentById(R.id.newsreader_detail_container)).getLvAdapter().notifyDataSetChanged();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        	//((NewsReaderDetailFragment) act.getSupportFragmentManager().findFragmentById(R.id.newsreader_detail_container)).getListView().smoothScrollToPosition(pos);
        //else
        	((NewsReaderDetailFragment) act.getSupportFragmentManager().findFragmentById(R.id.newsreader_detail_container)).getListView().setSelection(pos);
        
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = super.onOptionsItemSelected(item, this);
		if(!handled)
		{
			switch (item.getItemId()) {
				case android.R.id.home:
					// This ID represents the Home or Up button. In the case of this
					// activity, the Up button is shown. Use NavUtils to allow users
					// to navigate up one level in the application structure. For
					// more details, see the Navigation pattern on Android Design:
					//
					// http://developer.android.com/design/patterns/navigation.html#up-vs-back
					//
					NavUtils.navigateUpTo(this, new Intent(this,
							NewsReaderListActivity.class));
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
