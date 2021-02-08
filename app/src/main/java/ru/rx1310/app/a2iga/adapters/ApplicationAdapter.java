// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.adapters;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.AppsListActivity;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
	
    private List<ApplicationInfo> mListApps;
    private List<ApplicationInfo> mList;
    private AppsListActivity mActivity;
    private PackageManager mPkgMng;
    private AppsFilter mFilter;

    public ApplicationAdapter(AppsListActivity activity, int textViewResourceId, List<ApplicationInfo> appsList) {
		
        super(activity, textViewResourceId, appsList);
		
        this.mActivity = activity;
        this.mListApps = appsList;
        this.mList = appsList;
		
        mPkgMng = mActivity.getPackageManager();
		
    }

    @Override
    public int getCount() {
		return mListApps.size(); 
	}

    @Override
    public ApplicationInfo getItem(int p) {
		return mListApps.get(p); 
	}

    @Override
    public long getItemId(int p) {
        return mListApps.indexOf(getItem(p));
    }

    @Override
    public Filter getFilter() {
		
        if (mFilter == null) {
            mFilter = new AppsFilter();
        }
		
        return mFilter;
		
    }

    @Override
    public View getView(int p, View v, ViewGroup vg) {
		
        ViewHolder vh;
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
        
		if (v == null) {
			
            v = inflater.inflate(R.layout.ui_list_item, vg, false);
            vh = new ViewHolder(v);
            v.setTag(vh);
			
        } else {
            vh = (ViewHolder) v.getTag();
        }

        vh.appName.setText(getItem(p).loadLabel(mPkgMng)); //get app name
        vh.appPackage.setText(getItem(p).packageName); //get app package
        vh.icon.setImageDrawable(getItem(p).loadIcon(mPkgMng)); //get app icon

        v.setOnClickListener(onClickListener(p));

        return v;
		
    }

    private View.OnClickListener onClickListener(final int p) {
		
        return new View.OnClickListener() {
			
            @Override
            public void onClick(View v) {
				
                ApplicationInfo ai = mListApps.get(p);
               
				android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(mActivity);
				b.setTitle(ai.name);
				b.setItems(R.array.appslist_actions, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int p) {
						
						if (p == 0) {
							
						} if (p == 1) {
							Toast.makeText(mActivity, "Copy", Toast.LENGTH_SHORT).show();
						}
						
					}
				});
				b.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
					public void onClick(DialogInterface d, int i) {
						d.dismiss();
					}
				});
				b.show();
				
            }
			
			/*try {
			 Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
			 mActivity.startmActivity(intent);
			 if (null != intent) {
			 mActivity.startmActivity(intent);
			 }
			 } catch (mActivityNotFoundException e) {
			 e.printStackTrace();
			 }*/
			
        };
		
    }

    private class ViewHolder {
		
        private ImageView icon;
        private TextView appName;
        private TextView appPackage;

        public ViewHolder(View v) {
			
            icon = v.findViewById(R.id.icon);
            appName = v.findViewById(R.id.name);
            appPackage = v.findViewById(R.id.package_name);
			
        }
		
    }

    private class AppsFilter extends Filter {
		
        @Override
        protected FilterResults performFiltering(CharSequence c) {
			
            FilterResults fr = new FilterResults();

            if (c != null && c.length() > 0) {
				
                ArrayList<ApplicationInfo> filterList = new ArrayList<ApplicationInfo>();
				
                for (int i = 0; i < mList.size(); i++) {
					
                    if ((mList.get(i).loadLabel(mPkgMng).toString().toUpperCase()).contains(c.toString().toUpperCase())) {
						
                        ApplicationInfo ai = mList.get(i);
                        filterList.add(ai);
						
                    }
					
                }

                fr.count = filterList.size();
                fr.values = filterList;

            } else {
                fr.count = mList.size();
                fr.values = mList;
            }
			
            return fr;

        }

        @Override
        protected void publishResults(CharSequence c, FilterResults fr) {
			
            mListApps = (ArrayList<ApplicationInfo>) fr.values;
			
            notifyDataSetChanged();

            if (mListApps.size() == mList.size()) {
                mActivity.updateUILayout("All apps (" + mListApps.size() + ")");
            } else {
                mActivity.updateUILayout("Filtered apps (" + mListApps.size() + ")");
            }
			
        }
		
    }
	
}
