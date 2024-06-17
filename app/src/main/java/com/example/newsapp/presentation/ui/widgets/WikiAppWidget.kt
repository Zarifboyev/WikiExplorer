package com.example.newsapp.presentation.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.newsapp.R
import com.example.newsapp.domain.repository.WikipediaRepository
import com.example.newsapp.domain.service.WikipediaApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WikiAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == REFRESH_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
        }
    }

    companion object {
        private const val REFRESH_ACTION = "com.example.newsapp.REFRESH_WIDGET"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.wiki_app_widget)

            // Set up the intent that starts the WikiAppWidget, which handles the button click
            val intent = Intent(context, WikiAppWidget::class.java).apply {
                action = REFRESH_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingIntent)

            // Fetch stats and update the widget views
            val repo = WikipediaRepository(WikipediaApiService.create())
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stats = repo.getStats()
                    withContext(Dispatchers.Main) {
                        views.setTextViewText(R.id.widget_articles, "Articles: ${stats.articles}")
                        views.setTextViewText(R.id.widget_active_users, "Active Users: ${stats.activeUsers}")
                        views.setTextViewText(R.id.widget_edits, "Edits: ${stats.edits}")

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    // Handle the exception
                }
            }
        }
    }
}
