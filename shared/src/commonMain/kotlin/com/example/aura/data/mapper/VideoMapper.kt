package com.example.aura.data.mapper

import com.example.aura.data.remote.model.UserDto
import com.example.aura.data.remote.model.VideoDto
import com.example.aura.data.remote.model.VideoFileDto
import com.example.aura.data.remote.model.VideoPictureDto
import com.example.aura.domain.model.User
import com.example.aura.domain.model.Video
import com.example.aura.domain.model.VideoFile
import com.example.aura.domain.model.VideoPicture

fun VideoDto.toDomain(isFavorite: Boolean): Video {
    return Video(
        id = id,
        width = width,
        height = height,
        url = url,
        image = image,
        duration = duration,
        user = user.toDomain(),
        videoFiles = videoFiles.map { it.toDomain() },
        videoPictures = videoPictures.map { it.toDomain() },
        addedAt = 0,
        isFavorite = isFavorite,
    )
}

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        url = url
    )
}

fun VideoFileDto.toDomain(): VideoFile {
    return VideoFile(
        id = id,
        quality = quality,
        fileType = fileType,
        width = width,
        height = height,
        fps = fps,
        link = link
    )
}

fun VideoPictureDto.toDomain(): VideoPicture {
    return VideoPicture(
        id = id,
        picture = picture,
        nr = nr
    )
}