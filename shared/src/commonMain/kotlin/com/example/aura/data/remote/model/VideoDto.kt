package com.example.aura.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseDto(
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val videos: List<VideoDto>,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null
)

@Serializable
data class VideoDto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: UserDto,
    @SerialName("video_files") val videoFiles: List<VideoFileDto>,
    @SerialName("video_pictures") val videoPictures: List<VideoPictureDto>
)

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val url: String
)

@Serializable
data class VideoFileDto(
    val id: Long,
    val quality: String,
    @SerialName("file_type") val fileType: String,
    val width: Int? = null,
    val height: Int? = null,
    val fps: Double? = null,
    val link: String
)

@Serializable
data class VideoPictureDto(
    val id: Long,
    val picture: String,
    val nr: Int
)
