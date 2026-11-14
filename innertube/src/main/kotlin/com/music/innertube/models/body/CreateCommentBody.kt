package com.music.innertube.models.body

import com.music.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentBody(
    val context: Context,
    val commentText: String,
    val createCommentParams: String,
)

@Serializable
data class CreateCommentReplyBody(
    val context: Context,
    val commentText: String,
    val createReplyParams: String,
)

@Serializable
data class PerformCommentActionBody(
    val context: Context,
    val actions: List<String>,
)
