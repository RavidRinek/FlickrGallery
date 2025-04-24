package com.test.flickrgallery.features.gallery.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.test.flickrgallery.R
import com.test.flickrgallery.features.gallery.domain.models.Photo

@Composable
fun PhotoGridLazyColumn(
    photos: List<Photo>,
    gridState: LazyGridState,
    onPhotoClick: (Photo) -> Unit,
    onScrolledToBottom: (lastVisible: Int) -> Unit
) {

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                if (lastVisible != null) {
                    onScrolledToBottom(lastVisible)
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(photos) { photo ->
            AsyncImage(
                contentDescription = "Photo ${photo.id}",
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.url)
                    .crossfade(true)
                    .size(300)
                    .build(),
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPhotoClick(photo) },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_loading_placeholder),
                error = painterResource(R.drawable.image_error_placeholder),
            )
        }
    }
}