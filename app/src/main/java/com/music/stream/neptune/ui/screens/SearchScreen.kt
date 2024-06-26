package com.music.stream.neptune.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.music.stream.neptune.R
import com.music.stream.neptune.data.api.Response
import com.music.stream.neptune.data.entity.SongsModel
import com.music.stream.neptune.data.preferences.addLikedSongId
import com.music.stream.neptune.data.preferences.isSongLiked
import com.music.stream.neptune.data.preferences.removeLikedSongId
import com.music.stream.neptune.di.SongPlayer
import com.music.stream.neptune.ui.components.Loader
import com.music.stream.neptune.ui.theme.AppBackground
import com.music.stream.neptune.ui.theme.AppPalette
import com.music.stream.neptune.ui.viewmodel.SearchViewModel


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SearchScreen(navController: NavController) {
    val searchViewModel : SearchViewModel = hiltViewModel()
    val songs by searchViewModel.songs.collectAsState()



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(AppBackground.toArgb()))
    ) {
        when(songs){
            is Response.Loading -> {
                Log.d("homeMain", "loading-search-songs..")
                Loader()
            }

            is Response.Success -> {
                val songsResponse = (songs as Response.Success).data
                Log.d("homeMain", "Success-search-songs. ${songsResponse.toString()}")
                SumUpSearchScreen(navController = navController, songsResponse.sortedBy { it.title }, searchViewModel)
            }

            is Response.Error -> {
                Log.d("homeMain", "Error!!-search")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun SumUpSearchScreen(
    navController: NavController,
    songs: List<SongsModel>,
    searchViewModel: SearchViewModel,
) {
    val context = LocalContext.current

    var text by remember {
        mutableStateOf("")
    }
    var searchedList = listOf<SongsModel>()
    var times = 0


    when(text){
        "" -> {
            searchedList = songs
            times = searchedList.size
        }
        else -> {
            searchedList = songs.filter {
                it.title.lowercase().contains(text.lowercase()) || it.singer.lowercase().contains(text.lowercase())
            }
            times = searchedList.size
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(AppBackground.toArgb()))
            .statusBarsPadding()

    ){
        item{
            SearchTopBar()
        }
        stickyHeader {
            SearchStickyBar(text) {
                text = it
            }
        }

        items(times){ song ->

            var isLiked by remember {
                mutableStateOf(isSongLiked(context, searchedList[song].id.toString()))
            }
            val likeState = searchViewModel.likeState.value
            LaunchedEffect(likeState){
                isLiked = isSongLiked(context, searchedList[song].id.toString())
            }
            val songId = searchedList[song].id
            val currentPlayingIndicatorColor = if(songId == searchViewModel.currentSongId.value) Color(
                AppPalette.toArgb()) else Color.White


            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        SongPlayer.playSong(searchedList[song].url, context)
                        //navController.navigate("${Routes.Player.route}")
                        searchViewModel.updateSongState(
                            searchedList[song].coverUri,
                            searchedList[song].title,
                            searchedList[song].singer,
                            true,
                            searchedList[song].id
                        )
                    }
                ){

                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(280.dp)) {
                    GlideImage(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 10.dp, 0.dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                        ,
                        model = searchedList[song].coverUri,
                        contentScale = ContentScale.Crop,
                        failure = placeholder(R.drawable.placeholder),
                        loading = placeholder(R.drawable.placeholder),
                        contentDescription = ""
                    )
                    Column {
                        Text(text = searchedList[song].title, color = currentPlayingIndicatorColor, fontSize = 14.sp, fontWeight = FontWeight.Medium )
                        Text(text = searchedList[song].singer, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                            searchViewModel.updateLikeState(!searchViewModel.likeState.value)

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

        item{
            Spacer(modifier = Modifier.height(130.dp))
        }

    }
}

@Composable
fun SearchTopBar() {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Search", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        //Icon(imageVector = Icons.Default.Person, contentDescription = "", tint = Color.White)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SearchStickyBar(text: String, onTextChange: (String) -> Unit) {

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .height(55.dp)
            .background(Color.White)
            .padding(10.dp, 0.dp)
    ){
        Icon(
            painterResource(id = R.drawable.ic_search_big),
            tint = Color.Black,
            contentDescription = "")

        TextField(
            enabled = true,
            value = text,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight(500)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black

            ),
            singleLine = true,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                     textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = "What do you want to listen to?"

                )
            }
        )
    }
}
