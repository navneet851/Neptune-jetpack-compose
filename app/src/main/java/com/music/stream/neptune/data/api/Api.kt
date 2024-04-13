package com.music.stream.neptune.data.api

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.music.stream.neptune.data.entity.AlbumsModel
import com.music.stream.neptune.data.entity.ArtistsModel
import com.music.stream.neptune.data.entity.SongsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class Api @Inject constructor(){
    private val firestore: FirebaseFirestore = Firebase.firestore


     suspend fun getAlbums(): Flow<List<AlbumsModel>> {
         val firestore: FirebaseFirestore = Firebase.firestore
        return flow {
            val snapshot = firestore.collection("albums").get().await()
            val albums = snapshot.documents.mapNotNull { it.toObject(AlbumsModel::class.java) }
            Log.d("getting", albums.toString())
            emit(albums)

        }
    }


    suspend fun getArtists(): Flow<List<ArtistsModel>> {
        return flow {
            val snapshot = firestore.collection("artists").get().await()
            val artists = snapshot.documents.mapNotNull { it.toObject(ArtistsModel::class.java) }
            Log.d("getting", artists.toString())
            emit(artists)

        }
    }


    suspend fun getSongs(): Flow<List<SongsModel>> {
        return flow {
            val snapshot = firestore.collection("songs").get().await()
            val songs = snapshot.documents.mapNotNull { it.toObject(SongsModel::class.java) }
            Log.d("getting", songs.toString())
            emit(songs)

        }
    }
}