 package com.raul.musicserviceexample.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.raul.musicserviceexample.RawMediaItem


 @OptIn(UnstableApi::class)
fun List<RawMediaItem>.toMediaSource(context: Context): List<MediaSource> {

   return this.map {
       val mediaMetaData = MediaMetadata.Builder()

           .setTitle(it.title)
           .setAlbumArtist(it.title)
           .build()

       val fileUri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(it.resource.toString()).build()
       val mediaItem = MediaItem.Builder()
           .setUri(fileUri)
           .setMediaId(it.resource.toString())
           .setMediaMetadata(mediaMetaData)
           .build()
       val dataSourceFactory = DefaultDataSource.Factory(context)


       ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
    }.toList()
}