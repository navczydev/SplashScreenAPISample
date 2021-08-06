package com.example.splashscreenapisample

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.animation.doOnEnd
import androidx.core.view.ContentInfoCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.splashscreenapisample.contentreceivers.RichContentReceiver

/**
 * @author Nav Singh
 */
class MainActivity : AppCompatActivity() {
    lateinit var content: View
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        content = findViewById(android.R.id.content)
        // get reference to EditText
        val appCompatEditText = findViewById<AppCompatEditText>(R.id.my_input)
        // It's used to process the content, onReceive will trigger this
        val contentResolve: (uri: Uri, source: Int) -> Unit = { uri, source ->
            val contentResolver = this.contentResolver
            val mimeType = contentResolver.getType(uri)
            Log.d(TAG, "contentResolve: $mimeType Source is $source")
            // MimeTypeMap.getSingleton().getMimeTypeFromExtension()
            // check the source content
            when (source) {
                ContentInfoCompat.SOURCE_APP -> {
                    Log.d(TAG, "contentResolve: Source ${ContentInfoCompat.SOURCE_APP}")
                }
                ContentInfoCompat.SOURCE_CLIPBOARD -> {
                    Log.d(TAG, "contentResolve: Source ${ContentInfoCompat.SOURCE_CLIPBOARD}")
                }
                ContentInfoCompat.SOURCE_DRAG_AND_DROP -> {
                    Log.d(TAG, "contentResolve: Source ${ContentInfoCompat.SOURCE_DRAG_AND_DROP}")
                }
                ContentInfoCompat.SOURCE_INPUT_METHOD -> {
                    Log.d(TAG, "contentResolve: Source ${ContentInfoCompat.SOURCE_INPUT_METHOD}")
                }
            }

            when {
                mimeType?.contains("image/") == true -> {
                    // process image
                    Log.d(TAG, "onCreate: LOAD_IMAGE")
                    val imageView = findViewById<ImageView>(R.id.image_holder)
                    imageView.apply {
                        isVisible = true
                        load(uri)
                    }
                }
                mimeType?.contains("video/") == true -> {
                    // process video
                    val videoView = findViewById<VideoView>(R.id.video_holder)
                    videoView.apply {
                        isVisible = true
                        this.setVideoURI(uri)
                    }
                }
            }
        }
        // add [OnReceiveContentListener] to appCompatEditText
        ViewCompat.setOnReceiveContentListener(
            appCompatEditText,
            RichContentReceiver.MIME_TYPES,
            RichContentReceiver(contentResolve)
        )
        // splash screen related code 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MainActivity", "onCreate: I AM RUNNING ON API 12 or higher")
            content.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean =
                        when {
                            mainViewModel.mockDataLoading() -> {
                                content.viewTreeObserver.removeOnPreDrawListener(this)
                                true
                            }
                            else -> false
                        }
                })

            // custom exit on splashScreen
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // custom animation.
                ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    splashScreenView.width.toFloat()
                ).apply {
                    duration = 1000
                    // Call SplashScreenView.remove at the end of your custom animation.
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }.also {
                    // Run your animation.
                    it.start()
                }
            }
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}
