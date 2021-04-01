// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.adapters;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.activities.AppsListActivity;
import ru.rx1310.app.a2iga.utils.AppUtils;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
	
    private List<ApplicationInfo> oListApps;
    private List<ApplicationInfo> oList;
    private AppsListActivity oActivity;
	private PackageManager oPkgMng;
    private AppsFilter oFilter;
	
	private boolean showAppIcon, showAppPkgName;
	
    public ApplicationAdapter(AppsListActivity activity, int textViewResourceId, List<ApplicationInfo> appsList) {
		
        super(activity, textViewResourceId, appsList);
		
        this.oActivity = activity;
        this.oListApps = appsList;
        this.oList = appsList;
		
        oPkgMng = oActivity.getPackageManager();
		
    }

    @Override
    public int getCount() {
		return oListApps.size(); 
	}

    @Override
    public ApplicationInfo getItem(int p) {
		return oListApps.get(p); 
	}

    @Override
    public long getItemId(int p) {
        return oListApps.indexOf(getItem(p));
    }

    @Override
    public Filter getFilter() {
		
        if (oFilter == null) {
            oFilter = new AppsFilter();
        }
		
        return oFilter;
		
    }

    @Override
    public View getView(int p, View v, ViewGroup vg) {
		
        ViewHolder vh;
        LayoutInflater inflater = (LayoutInflater) oActivity.getSystemService(oActivity.LAYOUT_INFLATER_SERVICE);
		
		// ? Получение prefs
		showAppIcon = SharedPrefUtils.getBooleanData(oActivity, "appslist.icons");
		showAppPkgName = SharedPrefUtils.getBooleanData(oActivity, "appslist.pkgname");
		
		if (v == null) {
			
            v = inflater.inflate(R.layout.list_item_appslist, vg, false);
            vh = new ViewHolder(v);
            v.setTag(vh);
			
        } else {
            vh = (ViewHolder) v.getTag();
        }

		// ? Отображение названия приложения
        vh.appName.setText(getItem(p).loadLabel(oPkgMng));
		
		// ? Отображение Package Name
        if (showAppPkgName) vh.appPackage.setText(getItem(p).packageName);
		else vh.appPackage.setVisibility(View.GONE);
		
		// ? Отображение реальной иконки приложения
		if (showAppIcon) vh.icon.setImageDrawable(getItem(p).loadIcon(oPkgMng));
		else vh.icon.setImageDrawable(Resources.getSystem().getDrawable(android.R.mipmap.sym_def_app_icon));
        
		//vh.icon.setImageDrawable(oActivity.getDrawable(R.drawable.ic_logo));
		//vh.icon.setImageDrawable(Resources.getSystem().getDrawable(android.R.mipmap.sym_def_app_icon));
		
        v.setOnClickListener(onClickListener(p));
		v.setOnLongClickListener(OnLongClickListener(p));
		
        return v;
		
    }
	
	// ? Обработка long click на пункте в ListView
	private View.OnLongClickListener OnLongClickListener(final int p) {

        return new View.OnLongClickListener() {

			/* ? При длительном удержании на пункте в
			 *   ListView будет скопирован package name
			 *   приложения, на которое нажали
			 */
			@Override
			public boolean onLongClick(View p1) {
				
				ClipboardManager mClipboardMng = (ClipboardManager) oActivity.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData mClipData = ClipData.newPlainText(null, getItem(p).packageName);
				mClipboardMng.setPrimaryClip(mClipData);
				
				// ? Отображаем Toast, которое уведомляет юзера о копировании
				AppUtils.showToast(oActivity, oActivity.getString(R.string.pkg_name_copied) + " (" + getItem(p).packageName + ")");
				
				return true;
				
			}
			
        };

    }

	// ? Обработка нажатия на пункт в ListView
    private View.OnClickListener onClickListener(final int p) {
		
        return new View.OnClickListener() {
			
            @Override
            public void onClick(View v) {
				
                final ApplicationInfo ai = oListApps.get(p);
               
				android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(oActivity, R.style.AppTheme_Dialog_Alert);
				
				b.setTitle(ai.loadLabel(oPkgMng));
				b.setIcon(ai.loadIcon(oPkgMng));
				b.setMessage(oActivity.getString(R.string.appslist_app_select_dialog_desc));
				
				/*b.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
					public void onClick(DialogInterface d, int i) {
						d.dismiss();
						ClipboardManager mClipboardMng = (ClipboardManager) oActivity.getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData mClipData = ClipData.newPlainText(null, ai.packageName);
						mClipboardMng.setPrimaryClip(mClipData);

						Toast.makeText(getContext(), oActivity.getString(R.string.msg_pkg_name_copied), Toast.LENGTH_SHORT).show();
					}
				});*/
				
				
				b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "No"
					public void onClick(DialogInterface d, int i) {
						d.dismiss();
					}
				});
				
				b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Yes"
					public void onClick(DialogInterface d, int i) {
						SharedPrefUtils.saveData(getContext(), Constants.ASSIST_APP_PKGNAME, ai.packageName);
						oActivity.finish();
						AppUtils.showToast(oActivity, getContext().getString(R.string.app_selected_as_assistant) + " (" + ai.loadLabel(oPkgMng) + ")");
					}
				});
				
				b.show();
				
            }
			
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
				
                for (int i = 0; i < oList.size(); i++) {
					
                    if ((oList.get(i).loadLabel(oPkgMng).toString().toUpperCase()).contains(c.toString().toUpperCase())) {
						
                        ApplicationInfo ai = oList.get(i);
                        filterList.add(ai);
						
                    }
					
                }

                fr.count = filterList.size();
                fr.values = filterList;
				
            } else {
                fr.count = oList.size();
                fr.values = oList;
            }
			
            return fr;

        }

        @Override
        protected void publishResults(CharSequence c, FilterResults fr) {
			
            oListApps = (ArrayList<ApplicationInfo>) fr.values;
			
            notifyDataSetChanged();

            if (oListApps.size() == oList.size()) {
                oActivity.updateUILayout(String.format(getContext().getString(R.string.appslist_apps_count),  oListApps.size()));
            } else if (oListApps.size() == 0) {
				oActivity.updateUILayout(getContext().getString(R.string.appslist_apps_count_filtered_zero));
			} else {
                oActivity.updateUILayout(String.format(getContext().getString(R.string.appslist_apps_count_filtered), oListApps.size()));
            }
			
        }
		
    }
	
}
