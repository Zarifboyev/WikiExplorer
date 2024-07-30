package com.z99a.zarifboyevjavohir.vikipediya.presentation.ui.components.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.vikipediya.MainActivity
import com.z99a.zarifboyevjavohir.vikipediya.domain.impl.WikiStatsRepositoryImpl
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.WikipediaApiService
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.WikipediaStats
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DecimalFormat

class WikiAppWidget : AppWidgetProvider() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == REFRESH_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                Timber.tag("WikiAppWidget").d("Refresh action received for widget ID: %s", appWidgetId)
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            } else {
                Timber.tag("WikiAppWidget").d("Invalid widget ID received")
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        coroutineScope.cancel()
    }

    companion object {
        private const val REFRESH_ACTION = "com.z99a.zarifboyevjavohir.vikipediya.REFRESH_WIDGET"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            setUpIntents(context, appWidgetId, views)
            showLoadingState(views)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            val repo = WikiStatsRepositoryImpl(WikipediaApiService.create())
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stats = repo.getStats()
                    withContext(Dispatchers.Main) {
                        Timber.tag("WikiAppWidget").d("Stats fetched: %s", stats)
                        updateWidgetViews(views, stats)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    Timber.tag("WikiAppWidget").e(e, "Error fetching statistics")
                    withContext(Dispatchers.Main) {
                        showErrorState(views)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }

        private fun setUpIntents(context: Context, appWidgetId: Int, views: RemoteViews) {
            val refreshIntent = Intent(context, WikiAppWidget::class.java).apply {
                action = REFRESH_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)

            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_edits, appPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_active_users, appPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_articles, appPendingIntent)
        }

        private fun showLoadingState(views: RemoteViews) {
            views.setTextViewText(R.id.widget_articles, "")
            views.setTextViewText(R.id.widget_active_users, "Loading...")
            views.setTextViewText(R.id.widget_edits, "")
        }

        private fun showErrorState(views: RemoteViews) {
            views.setTextViewText(R.id.widget_articles, "${formatIntegerNumber(301827)}")
            views.setTextViewText(R.id.widget_active_users, " ${formatIntegerNumber(645)}")
            views.setTextViewText(R.id.widget_edits, " ${formatIntegerNumber(4352232)}")
        }

        private fun updateWidgetViews(views: RemoteViews, stats: WikipediaStats) {
            views.setTextViewText(R.id.widget_articles, "${formatIntegerNumber(stats.articles)}")
            views.setTextViewText(R.id.widget_active_users, " ${formatIntegerNumber(stats.activeUsers)}")
            views.setTextViewText(R.id.widget_edits, "${formatIntegerNumber(stats.edits)}")
        }

        private fun formatIntegerNumber(number: Int): String {
            val decimalFormat = DecimalFormat("#,##0")
            return decimalFormat.format(number)
        }
    }
}
