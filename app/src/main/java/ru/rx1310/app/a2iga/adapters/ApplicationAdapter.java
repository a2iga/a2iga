// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.AppsListActivity;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
	
	private List<ApplicationInfo> oListApps;
    private List<ApplicationInfo> oList;
    private AppsListActivity oActivity;
	private PackageManager oPkgMng;
    private AppsFilter oFilter;
	private Intent sendPackageName;
	private boolean showAppIcon, showAppPkgName;
	private LayoutInflater oInflater;
	private String isAssistAppPkgName;
	
    public ApplicationAdapter(AppsListActivity activity, int textViewResourceId, List<ApplicationInfo> appsList) {

        super(activity, textViewResourceId, appsList);

        this.oActivity = activity;
        this.oListApps = appsList;
        this.oList = appsList;

        oPkgMng = oActivity.getPackageManager();
		oInflater = (LayoutInflater) oActivity.getSystemService(oActivity.LAYOUT_INFLATER_SERVICE);
		
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
    public View getView(final int p, View v, ViewGroup vg) {
		
        final ViewHolder vh;
        
		// ? Получение prefs
		showAppIcon = SharedPrefUtils.getBooleanData(oActivity, "appslist.icons");
		showAppPkgName = SharedPrefUtils.getBooleanData(oActivity, "appslist.pkgname");
		
		if (v == null) {
			
			vh = new ViewHolder();
			
            v = oInflater.inflate(R.layout.list_item_appslist, vg, false);
			
			vh.icon = v.findViewById(R.id.icon);
            vh.appName = v.findViewById(R.id.name);
            vh.appPackage = v.findViewById(R.id.package_name);
			
            v.setTag(vh);
			
        } else {
            vh = (ViewHolder) v.getTag();
        }
		
		// ? Отображение названия приложения
		vh.appName.setText(getItem(p).loadLabel(oPkgMng));

		// ? Отображение Package Name
		if (showAppPkgName) vh.appPackage.setText(getItem(p).packageName);
		else vh.appPackage.setVisibility(View.GONE);
		
		// ? Отображение иконки приложения
		/*oActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (showAppIcon) vh.icon.setImageDrawable(getItem(p).loadIcon(oPkgMng));
				else vh.icon.setImageDrawable(Resources.getSystem().getDrawable(android.R.mipmap.sym_def_app_icon));
			}
		});*/
		
		if (showAppIcon) vh.icon.setImageDrawable(getItem(p).loadIcon(oPkgMng));
		else vh.icon.setImageDrawable(Resources.getSystem().getDrawable(android.R.mipmap.sym_def_app_icon));
		//vh.icon.setImageDrawable(oActivity.getDrawable(R.drawable.ic_logo));
		//vh.icon.setImageDrawable(Resources.getSystem().getDrawable(android.R.mipmap.sym_def_app_icon));
		
        v.setOnClickListener(onClickListener(p));
		v.setOnLongClickListener(OnLongClickListener(p));
		
        return v;
		
    }
	
	
	
	class ViewHolder {

        private ImageView icon;
        private TextView appName;
        private TextView appPackage;

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
				
				AppUtils.copyToClipboard(getContext(), getItem(p).packageName);
				
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
				
				isAssistAppPkgName = SharedPrefUtils.getStringData(oActivity, Constants.ASSIST_APP_PKGNAME);
				
                final ApplicationInfo ai = oListApps.get(p);
				
				android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(oActivity, R.style.AppTheme_Dialog_Alert);
				
				b.setTitle(ai.loadLabel(oPkgMng));
				b.setIcon(ai.loadIcon(oPkgMng));
				b.setMessage(oActivity.getString(R.string.appslist_app_select_dialog_desc));
				
				b.setNeutralButton(R.string.appslist_app_select_dialog_addToFavApps, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int i) {
						
						if (AppUtils.isAppInstalled(oActivity, "ru.rx1310.a2iga.module.favapps")) {
							
							sendPackageName = new Intent();
							sendPackageName.setAction(Intent.ACTION_SEND);
							sendPackageName.setClassName("ru.rx1310.a2iga.module.favapps", "ru.rx1310.a2iga.module.favapps.ModuleSettings").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							sendPackageName.putExtra(Intent.EXTRA_TEXT, ai.packageName);
							sendPackageName.setType("text/plain");

							oActivity.startActivity(Intent.createChooser(sendPackageName, "Select «FavApps»!"));
							
						} else {
							favAppsInstallRequestDialog();
						}
						
					}
				});
				
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
	
	void favAppsInstallRequestDialog() {
		
		android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(oActivity, R.style.AppTheme_Dialog_Alert);

		b.setTitle(oActivity.getString(R.string.favapps_not_installed));
		b.setIcon(oActivity.getDrawable(R.drawable.ic_logo));
		b.setMessage(oActivity.getString(R.string.favapps_not_installed_desc));

		b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "No"
			public void onClick(DialogInterface d, int i) {
				d.dismiss();
			}
		});

		b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Yes"
			public void onClick(DialogInterface d, int i) {
				AppUtils.openURL(oActivity, "https://rx1310.github.io/docs/a2iga/modules.html");
			}
		});

		b.show();
		
	}
	
}
