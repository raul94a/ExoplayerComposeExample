package com.raul.musicserviceexample

import android.Manifest
import android.app.Notification
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerView
import com.raul.musicserviceexample.media.MediaSessionHandler
import com.raul.musicserviceexample.media.toMediaSource
import com.raul.musicserviceexample.notifications.PlaybackNotification
import com.raul.musicserviceexample.ui.theme.MusicServiceExampleTheme


@UnstableApi
class PlayerNotificationListener :
    PlayerNotificationManager.NotificationListener {
    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {

    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

    }
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExoPlayer.Builder(this).build()
        val grantedCode =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (grantedCode != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
        setContent {
            MusicServiceExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set MediaSource to ExoPlayer

                    Column {
                        Greeting("Android")
                        ExoPlayerView()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicServiceExampleTheme {
        Greeting("Android")
    }
}


/**
 * Composable function that displays an ExoPlayer to play a video using Jetpack Compose.
 *
 * @OptIn annotation to UnstableApi is used to indicate that the API is still experimental and may
 * undergo changes in the future.
 *
 */

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView() {
    // Get the current context
    val context = LocalContext.current

    // Initialize ExoPlayer
    val exoPlayer = ExoPlayer.Builder(context).build().apply {

    }

    val rawMedia = listOf(RawMediaItem(R.raw.one, "Gingle Bell"), RawMediaItem(R.raw.two, "La Marcha Real"), RawMediaItem(R.raw.three, "Gangsta Paradise"))
    val rawItems = rawMedia.toMediaSource(context)

    // Set MediaSource to ExoPlayer
    LaunchedEffect(Unit) {

        val token = MediaSessionHandler(context, object: Player.Listener {
            // With this we can set up an event listener
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                println("On is playing changed with value: $isPlaying")
            }
        }).onStartMediaController(exoPlayer,rawItems)


        val notificationManager = PlaybackNotification(
            context, token, exoPlayer, PlayerNotificationListener()
        )
        notificationManager.showNotificationForPlayer(exoPlayer)

    }

    // Manage lifecycle events
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Use AndroidView to embed an Android View (PlayerView) into Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                hideController()
                useController = false
                controllerHideOnTouch = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp) // Set your desired height
    )

}

