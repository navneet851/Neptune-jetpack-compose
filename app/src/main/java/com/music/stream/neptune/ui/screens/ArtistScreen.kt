package com.music.stream.neptune.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.music.stream.neptune.R
import com.music.stream.neptune.data.api.Response
import com.music.stream.neptune.data.entity.ArtistsModel
import com.music.stream.neptune.data.entity.SongsModel
import com.music.stream.neptune.data.preferences.addLikedSongId
import com.music.stream.neptune.data.preferences.isSongLiked
import com.music.stream.neptune.data.preferences.removeLikedSongId
import com.music.stream.neptune.di.Palette
import com.music.stream.neptune.di.SongPlayer
import com.music.stream.neptune.ui.components.Loader
import com.music.stream.neptune.ui.theme.AppBackground
import com.music.stream.neptune.ui.theme.AppPalette
import com.music.stream.neptune.ui.viewmodel.ArtistViewModel


@Composable
fun ArtistScreen(navController: NavController, artistName: String) {


    val artistViewModel : ArtistViewModel = hiltViewModel()
    val songs by artistViewModel.songs.collectAsState()
    val artists by artistViewModel.artists.collectAsState()

    Log.d("checku", artistName.toString())
    Log.d("checkun", songs.toString())
    Log.d("checku", artists.toString())

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(AppBackground.toArgb()))
    ) {
        when(songs){
            is Response.Loading -> {
                Log.d("homeMain", "loading-artists-songs..")
                Loader()
            }

            is Response.Success -> {
                val songsResponse = (songs as Response.Success).data
                val artistsResponse = (artists as Response.Success).data
                Log.d("homeMain", "Success-artists-songs. ${artistsResponse.toString()}")
                SumUpArtistScreen(navController = navController, artistViewModel, songsResponse, artistsResponse, artistName)
            }

            is Response.Error -> {
                Log.d("homeMain", "Error!!-artists")
            }
        }
    }

}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun SumUpArtistScreen(
    navController: NavController,
    artistViewModel: ArtistViewModel,
    songs: List<SongsModel>,
    artists: List<ArtistsModel>,
    artistName: String
) {

    var artistSongs = songs.filter {
        it.singer.lowercase().contains(artistName.lowercase())
    }

    artistSongs = artistSongs.sortedBy {
        it.title
    }
    val artist = artists.filter{
        artistName == it.name
    }

    val context = LocalContext.current

    var dominentColor by remember {
        mutableStateOf(Color(AppBackground.toArgb()))
    }
    Palette().extractSecondColorFromCoverUrl(context = context, artist[0].coverUri){ color ->
        dominentColor = color
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(16.dp, 0.dp),
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigateUp()
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(text = "")
                }
            )
        }
    ){


        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(AppBackground.toArgb()))
            .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(dominentColor, Color(AppBackground.toArgb())),
                            startY = -1000f,

                            ),

                        )
                ,
                verticalArrangement = Arrangement.Center,
                // horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(25.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    GlideImage(
                        modifier = Modifier.size(240.dp),
                        model = artist[0].coverUri,
                        failure = placeholder(R.drawable.placeholder),
                        //loading = placeholder(R.drawable.album),
                        //contentScale = ContentScale.Crop,
                        contentDescription = "",
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))



                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp, 0.dp)
                ){

                    Column(
                        modifier = Modifier
                            .width(200.dp)
                    ) {
                        Text(modifier = Modifier,
                            text = artistName,
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold)
                        Text(modifier = Modifier,
                            text = "Made For You",
                            color = Color.Gray,
                            letterSpacing = 0.sp,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium)
                    }

                    if(!artistViewModel.currentSongPlayingState.value && artistSongs.isNotEmpty()){
                        androidx.compose.foundation.layout.Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    SongPlayer.playSong(artistSongs[0].url, context)
                                    artistViewModel.updateSongState(
                                        artistSongs[0].coverUri,
                                        artistSongs[0].title,
                                        artistName,
                                        true,
                                        artistSongs[0].id,
                                        0

                                    )
                                }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(25.dp),
                                tint = Color.Black,
                                painter = painterResource(id = R.drawable.play_svgrepo_com),
                                contentDescription = "")
                        }
                    }
                }

            }

//            Spacer(modifier = Modifier.padding(25.dp))


            repeat(artistSongs.size) { song ->
                var isLiked by remember {
                    mutableStateOf(isSongLiked(context, artistSongs[song].id.toString()))
                }
                val likeState = artistViewModel.likeState.value
                LaunchedEffect(likeState){
                    isLiked = isSongLiked(context, artistSongs[song].id.toString())
                }
                val songId = artistSongs[song].id
                val currentPlayingIndicatorColor = if(songId == artistViewModel.currentSongId.value) Color(AppPalette.toArgb()) else Color.White



                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            SongPlayer.playSong(artistSongs[song].url, context)
                            artistViewModel.updateSongState(
                                artistSongs[song].coverUri,
                                artistSongs[song].title,
                                artistName,
                                true,
                                artistSongs[song].id,
                                song

                            )
                        }
                ) {

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(280.dp)
                    ) {
                        GlideImage(
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 10.dp, 0.dp)
                                .size(50.dp),
                            model = artistSongs[song].coverUri,
                            contentScale = ContentScale.Crop,
                            failure = placeholder(R.drawable.placeholder),
                            loading = placeholder(R.drawable.placeholder),
                            contentDescription = ""
                        )
                        Column {
                            Text(
                                text = artistSongs[song].title,
                                color = currentPlayingIndicatorColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = artistSongs[song].singer,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (isLiked) {
                                    removeLikedSongId(context, songId.toString())
                                } else {
                                    addLikedSongId(context, songId.toString())
                                }
                                isLiked = isSongLiked(context, songId.toString())
                                artistViewModel.updateLikeState(!artistViewModel.likeState.value)

                            },
                        painter = if (isLiked){
                            painterResource(id = R.drawable.added)
                        }
                        else{
                            painterResource(id = R.drawable.ic_add)
                        }
                        ,
                        tint = if (isLiked){
                            Color.White
                        }else{
                            Color.Gray
                        },
                        contentDescription = ""
                    )
                }
            }
            Spacer(modifier = Modifier.padding(80.dp))
        }

    }
}
