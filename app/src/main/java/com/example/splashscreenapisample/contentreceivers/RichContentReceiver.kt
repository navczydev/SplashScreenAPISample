package com.example.splashscreenapisample.contentreceivers

import android.content.ClipData
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.view.ContentInfoCompat
import androidx.core.view.OnReceiveContentListener

/**
 * @author Nav Singh
 */
class RichContentReceiver(val contentResolver: (uri: Uri, source: Int) -> Unit) :
    OnReceiveContentListener {

    companion object {
        val TAG: String = RichContentReceiver::class.java.simpleName
        val MIME_TYPES = arrayOf("image/*", "video/*")
    }

    override fun onReceiveContent(view: View, payload: ContentInfoCompat): ContentInfoCompat? {
        Log.d(TAG, "onReceiveContent: $payload")
        val pair = payload.partition {
            it.uri != null
        }
        // image or video uri
        if (pair.first != null) {
            val clip: ClipData = pair.first.clip
            for (i in 0 until clip.itemCount) {
                val uri = clip.getItemAt(i).uri
                Log.e(TAG, "onReceiveContent: ${clip.getItemAt(0)}")
                // process the uri
                contentResolver(uri, pair.first.source)
            }
        }
        // unhandled content system will process accordingly
        return pair.second
    }
}
