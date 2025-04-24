package com.test.flickrgallery.features.utilities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingStateItem() {
    Box(
        modifier = Modifier
            .padding(horizontal = 7.5.dp)
            .height(122.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDEDEDE))
            .padding(bottom = 2.5.dp)
    ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(
                    modifier = Modifier.size(60.dp)
                ) {
                    drawCircle(
                        color = Color(0xFFE6E6E6),
                        radius = size.minDimension / 2
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 12.dp, end = 14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .height(11.dp)
                            .width(152.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFFE6E6E6))
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Box(
                        modifier = Modifier
                            .height(11.dp)
                            .width(128.5.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFFE6E6E6))
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .height(18.dp)
                                .width(152.dp)
                                .clip(RoundedCornerShape(17.dp))
                                .background(Color(0xFFE6E6E6))
                        )
                        Canvas(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                        ) {
                            drawCircle(
                                color = Color(0xFFE6E6E6),
                                radius = size.minDimension / 2
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(47.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDEDEDE))
                        .padding(
                            start = 1.5.dp,
                            top = 1.5.dp,
                            end = 1.5.dp,
                            bottom = 3.5.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE6E6E6)),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        )
                    )
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(Color(0xFFE6E6E6))
            )
    }
}