/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package ru.rx1310.app.a2iga.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.rx1310.app.a2iga.AppList;
import ru.rx1310.app.a2iga.R;

public class AppsListAdapter extends BaseAdapter {

	public LayoutInflater layoutInflater;
	public List<AppList> listStorage;

	public AppsListAdapter(Context ctx, List<AppList> mAppList) {
		
		layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listStorage = mAppList;
		
	}

	@Override
	public int getCount() {
		return listStorage.size();
	}

	@Override
	public Object getItem(int pos) {
		return pos;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {

		ViewHolder vh;
		
		if (v == null) {
			
			vh = new ViewHolder();
			
			v = layoutInflater.inflate(R.layout.ui_packageslist_item, vg, false);

			vh.listItemAppName = v.findViewById(R.id.packagesList_appName);
			vh.listItemAppPackageName = v.findViewById(R.id.packagesList_appPackageName);
			vh.listItemAppIcon = v.findViewById(R.id.packagesList_appIcon);
			
			v.setTag(vh);
			
		} else {
			
			vh = (ViewHolder) v.getTag();
			
		}
		
		vh.listItemAppName.setText(listStorage.get(pos).getAppName());
		vh.listItemAppPackageName.setText(listStorage.get(pos).getAppPackageName());
		vh.listItemAppIcon.setImageDrawable(listStorage.get(pos).getAppIcon());

		return v;
		
	}

	class ViewHolder {
		
		TextView listItemAppName;
		TextView listItemAppPackageName;
		ImageView listItemAppIcon;
		
	}
	
}
