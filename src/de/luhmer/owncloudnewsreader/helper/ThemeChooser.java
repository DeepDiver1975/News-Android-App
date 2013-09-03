/**
* Android ownCloud News
*
* @author David Luhmer
* @copyright 2013 David Luhmer david-dev@live.de
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
* License as published by the Free Software Foundation; either
* version 3 of the License, or any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU AFFERO GENERAL PUBLIC LICENSE for more details.
*
* You should have received a copy of the GNU Affero General Public
* License along with this library.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package de.luhmer.owncloudnewsreader.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import de.luhmer.owncloudnewsreader.R;
import de.luhmer.owncloudnewsreader.SettingsActivity;

public class ThemeChooser {
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void chooseTheme(Activity act)
	{
		if(isDarkTheme(act))
		{
			//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			//	act.setTheme(android.R.style.Theme_Holo);
			//else
				//act.setTheme(R.style.Sherlock___Theme);
			act.setTheme(R.style.Theme_Sherlock);			
		} else {
			//if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			//	act.setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
			//else
				//act.setTheme(R.style.Sherlock___Theme_DarkActionBar);
			act.setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		}
	}
	
	/*
	public static void ChangeBackgroundOfSlider(Activity activity) {
		View navigation_drawer = activity.getWindow().getDecorView().findViewById(R.id.left_drawer);
		if(isDarkTheme(activity))
			navigation_drawer.setBackgroundColor(color.abs__background_holo_light);
		else
			navigation_drawer.setBackgroundColor(color.abs__background_holo_light);
	}
	*/
	
	public static boolean isDarkTheme(Context context)
	{
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String value = mPrefs.getString(SettingsActivity.SP_APP_THEME, "0");
		
		if(value.equals("0"))
			return true;
		
		return false;
	}
}
