package com.raul.musicserviceexample.media

import android.app.PendingIntent
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors


@OptIn(UnstableApi::class)
class MediaSessionHandler(private val context: Context, private val listener:  Player.Listener? = null) {

    fun onStartMediaController(exoPlayer: ExoPlayer, mediaSources: List<MediaSource>) : SessionToken {
        val sessionActivityPendingIntent =
            context.packageManager?.getLaunchIntentForPackage(context.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        context,
                        887,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
        val mediaSession = MediaSession.Builder(context, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent!!).build()

        val token = mediaSession.token
        val controllerFuture = MediaController.Builder(context, token).buildAsync()
        if(listener != null) exoPlayer.addListener(listener)
        controllerFuture.addListener({
            val mediaController = controllerFuture.get()


            println("CONTROLLER FUTURE HAS BEEN RESOLVED")
//            val EXAMPLE_VIDEO_URI = "https://onlinetestcase.com/wp-content/uploads/2023/06/1-MB-MP3.mp3"

//            val fileUri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(R.raw.one.toString()).build()
//
//            val mediaItem = MediaItem.Builder().setMediaId("id.one").setUri(fileUri).build()
//            exoPlayer.setMediaItem(mediaItem)

            exoPlayer.setMediaSources(mediaSources)
            exoPlayer.prepare()
            exoPlayer.play()


        }, MoreExecutors.directExecutor())

        return token
    }
}