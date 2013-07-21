package de.luhmer.owncloudnewsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.BlockingExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import de.luhmer.owncloudnewsreader.database.DatabaseConnection;
import de.luhmer.owncloudnewsreader.helper.MenuUtilsSherlockFragmentActivity;
import de.luhmer.owncloudnewsreader.helper.ThemeChooser;
import de.luhmer.owncloudnewsreader.reader.IReader;
import de.luhmer.owncloudnewsreader.services.DownloadImagesService;

/**
 * An activity representing a list of NewsReader. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NewsReaderDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NewsReaderListFragment} and the item details (if present) is a
 * {@link NewsReaderDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NewsReaderListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class NewsReaderListActivity extends MenuUtilsSherlockFragmentActivity implements
		 NewsReaderListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	//IabHelper mHelper;
	static final String TAG = "NewsReaderListActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		ThemeChooser.chooseTheme(this);
		
		//setTheme(R.style.Theme_Sherlock);
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsreader_list);
		
		
		if (findViewById(R.id.newsreader_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			
			((NewsReaderListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.newsreader_list))
					.setActivateOnItemClick(true);
		}
		
		
		//DatabaseUtils.CopyDatabaseToSdCard(this);
		
        /*
		((NewsReaderListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.newsreader_list)).setUpdateFinishedListener(updateFinished);
        */
		
		/*
		AppUpdater au = new AppUpdater(this, false);
        au.UpdateApp();
        */
        
		/*
        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, Constants.getBase64EncodedPublicKey());
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        	   public void onIabSetupFinished(IabResult result) {
        	      if (!result.isSuccess()) {
					// Oh noes, there was a problem.
        	         Log.d(TAG, "Problem setting up In-app Billing: " + result);
        	      }  
        	   }
        	});
        */

		//Init config --> if nothing is configured start the login dialog.
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(mPrefs.getString(SettingsActivity.EDT_OWNCLOUDROOTPATH_STRING, null) == null)
        	StartLoginFragment();
        
		//if(mPrefs.getBoolean(SettingsActivity.CB_SYNCONSTARTUP_STRING, false))
		//	startSync();
		
	}

	public void updateAdapter() {
		NewsReaderListFragment nlf = ((NewsReaderListFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_list));
		if(nlf != null)
		{				
			// Block children layout for now
			PullToRefreshExpandableListView ptrel = ((PullToRefreshExpandableListView)nlf.eListView);
			BlockingExpandableListView bView = ((BlockingExpandableListView) ptrel.getRefreshableView());
			bView.setBlockLayoutChildren(true);
			nlf.lvAdapter.notifyDataSetChanged();
			bView.setBlockLayoutChildren(false);
		}
	}
	
	@Override
	protected void onResume() {
		ThemeChooser.chooseTheme(this);
		
		updateAdapter();
		
		super.onResume();
	}

	@Override
	public void onDestroy() {
		//this.unregisterReceiver()
		super.onDestroy();
	   /*
	   try
	   {
		   if (mHelper != null)
			   mHelper.dispose();
		   mHelper = null;
	   }
	   catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }*/
	}


	/**
	 * Callback method from {@link NewsReaderListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onTopItemClicked(String idSubscription, boolean isFolder, String optional_folder_id) {
		StartDetailFragment(idSubscription, isFolder, optional_folder_id);		
	}

	@Override
	public void onChildItemClicked(String idSubscription, String optional_folder_id) {
		StartDetailFragment(idSubscription, false, optional_folder_id);
	}
		
	private void StartDetailFragment(String id, Boolean folder, String optional_folder_id)
	{
		if(super.getMenuItemMarkAllAsRead() != null)
			super.getMenuItemMarkAllAsRead().setEnabled(true);
		if(super.getMenuItemDownloadMoreItems() != null)
			super.getMenuItemDownloadMoreItems().setEnabled(true);
		
		DatabaseConnection dbConn = new DatabaseConnection(getApplicationContext());
		
		Intent detailIntent = new Intent(this, NewsReaderDetailActivity.class);
		//detailIntent.putExtra(NewsReaderDetailFragment.ARG_ITEM_ID, id);
		if(!folder)
		{
			detailIntent.putExtra(NewsReaderDetailActivity.SUBSCRIPTION_ID, id);
			detailIntent.putExtra(NewsReaderDetailActivity.FOLDER_ID, optional_folder_id);
			detailIntent.putExtra(NewsReaderDetailActivity.TITEL, dbConn.getTitleOfSubscriptionByRowID(id));
		}
		else
		{
			detailIntent.putExtra(NewsReaderDetailActivity.FOLDER_ID, id);
			int idFolder = Integer.valueOf(id);
			if(idFolder >= 0)
				detailIntent.putExtra(NewsReaderDetailActivity.TITEL, dbConn.getTitleOfFolderByID(id));
			else if(idFolder == -10)
				detailIntent.putExtra(NewsReaderDetailActivity.TITEL, getString(R.string.allUnreadFeeds));
			else if(idFolder == -11)
				detailIntent.putExtra(NewsReaderDetailActivity.TITEL, getString(R.string.starredFeeds));
		}
		
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = detailIntent.getExtras();
			
			//arguments.putString(NewsReaderDetailFragment.ARG_ITEM_ID, id);
			
			//getApplicationContext().startActivity(detailIntent);
			
			NewsReaderDetailFragment fragment = new NewsReaderDetailFragment();			
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.newsreader_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.			
			startActivity(detailIntent);
		}
		
		dbConn.closeDatabase();
	}


    public void UpdateItemList()//Only in use on Tablets
    {
        if(mTwoPane)
        {
            NewsReaderDetailFragment nrD = (NewsReaderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_detail_container);
            if(nrD != null)
                nrD.UpdateCursor();
        }
    }


    void startSync()
    {
		//menuItemUpdater.setActionView(R.layout.inderterminate_progress);
		((NewsReaderListFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_list)).StartSync();		
    }

    @SuppressWarnings("static-access")
	public void UpdateButtonSyncLayout()
    {
        if(super.getMenuItemUpdater() != null)
        {
            IReader _Reader = ((NewsReaderListFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_list))._Reader;
            PullToRefreshExpandableListView pullToRefreshView = (PullToRefreshExpandableListView) findViewById(R.id.expandableListView);
            if(_Reader.isSyncRunning())   
            {
            	super.getMenuItemUpdater().setActionView(R.layout.inderterminate_progress);
                if(pullToRefreshView != null)
                	pullToRefreshView.setRefreshing(true);
                
            }
            else
            {
            	super.getMenuItemUpdater().setActionView(null);
                if(pullToRefreshView != null)
                	pullToRefreshView.onRefreshComplete();
            }
        }
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.news_reader, menu);
		//getSupportMenuInflater().inflate(R.menu.news_reader, menu);
		
		super.onCreateOptionsMenu(menu, getSupportMenuInflater(), mTwoPane, this);
		
        UpdateButtonSyncLayout();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = super.onOptionsItemSelected(item, this);
		if(!handled)
		{		
			switch (item.getItemId()) {
				case R.id.action_settings:
					Intent intent = new Intent(this, SettingsActivity.class);		    
				    //intent.putExtra(EXTRA_MESSAGE, message);
				    startActivityForResult(intent, RESULT_SETTINGS);
					return true;
					
				case R.id.menu_update:
					//menuItemUpdater = item.setActionView(R.layout.inderterminate_progress);
					startSync();
					break;
					
				case R.id.action_login:
					StartLoginFragment();
					break;				
					
				case R.id.menu_StartImageCaching:
					DatabaseConnection dbConn = new DatabaseConnection(this);
			    	try {
			    		long highestItemId = dbConn.getLowestItemIdUnread();
			    		Intent service = new Intent(this, DownloadImagesService.class);
			        	service.putExtra(DownloadImagesService.LAST_ITEM_ID, highestItemId);
			    		startService(service);
			    	} finally {
			    		dbConn.closeDatabase();
			    	}
					break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private static final int RESULT_SETTINGS = 15642;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == 1) {
        if(resultCode == RESULT_OK){
            int pos = data.getIntExtra("POS", 0);
            NewsReaderDetailActivity.UpdateListViewAndScrollToPos(this, pos);

            ((NewsReaderListFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_list)).lvAdapter.notifyDataSetChanged();
        }
        else if(requestCode == RESULT_SETTINGS)
        {
        	((NewsReaderListFragment) getSupportFragmentManager().findFragmentById(R.id.newsreader_list)).lvAdapter.ReloadAdapter();
        }
    }


    public void StartLoginFragment()
    {
    	SherlockDialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
}
