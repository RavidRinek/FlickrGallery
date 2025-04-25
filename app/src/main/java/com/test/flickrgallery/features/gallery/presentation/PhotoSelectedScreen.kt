package com.test.flickrgallery.features.gallery.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.test.flickrgallery.R
import com.test.flickrgallery.features.gallery.domain.models.Photo

@Composable
fun PhotoSelectedScreen(photo: Photo) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.url)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = "Selected Photo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(photo.width.toFloat() / photo.height.toFloat()),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.image_loading_placeholder),
            error = painterResource(R.drawable.image_error_placeholder),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = photo.title,
                color = Color.White,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}