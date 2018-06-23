package com.example.saeedmac.saeedcapstoneproject.ui.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class AppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppWidgetDataProvider(this, intent);
    }
}