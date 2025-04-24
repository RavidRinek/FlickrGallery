package com.test.flickrgallery.features.utilities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingStateItemsColumn() {
    Column(Modifier.padding(horizontal = 7.dp)) {
        Spacer(
            modifier = Modifier
                .height(17.dp)
        )
        LoadingStateItem()
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        LoadingStateItem()
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        LoadingStateItem()
    }
}

@Preview
@Composable
fun LoadingStateItemsColumnPreview() {
    LoadingStateItemsColumn()
}