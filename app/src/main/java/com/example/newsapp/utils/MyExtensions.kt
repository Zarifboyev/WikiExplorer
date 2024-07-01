package com.example.newsapp.utils

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import timber.log.Timber
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.domain.service.CategoryMember

fun TextView.setHtml(value: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        if (Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT).length > 36) {
            this.text = "${Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT).substring(0, 35)} ..."
        } else {
            this.text = Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
        }
    } else {
        if (Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT).length > 36) {
            this.text = "${Html.fromHtml(value).substring(0, 35)}..."
        } else {
            this.text = Html.fromHtml(value)
        }
    }
}


fun AppCompatActivity.createFragment(id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(id, fragment)
        .commit()
}

fun Fragment.createFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction()
        .replace(R.id.container, fragment)
        .commit()
}



// Extension function to check network connectivity
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

// Extension function to show a toast message for network issues
fun Context.showNetworkUnavailableToast() {
    if (!isNetworkAvailable()) {
        Toast.makeText(this, "Internet aloqasi yo'q. Tarmoq sozlamalaringizni tekshiring.", Toast.LENGTH_LONG).show()
    }
}
fun CategoryMember.toEntity() = WikiModel(
    pageid = pageid,
    ns = ns,
    title = title
)

fun WikiModel.toDomain() = CategoryMember(
    pageid = pageid,
    ns = ns,
    title = title
)


fun Fragment.startFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction()
        .addToBackStack(fragment::class.java.name)
        .replace(R.id.container, fragment)
        .commit()
}

fun Fragment.finish() {
    parentFragmentManager.popBackStack()
}

fun Fragment.playStoreUrl() =
    "Dowloand now there!: " + "https://play.google.com/store/apps/details?id=${context?.applicationContext?.packageName}"


fun myTimber(message: String) {
    Timber.tag("TTT").d(message)
}


