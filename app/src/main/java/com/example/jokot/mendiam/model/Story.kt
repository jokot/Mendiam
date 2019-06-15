package com.example.jokot.mendiam.model

class Story(
    val sid: String = "",
    val uid: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val name: String = "",
    val date: String = "",
    val image: String? = "",
    val imageContent: Int = 0,
    val textContent: Int = 0

)

class Draft(
    val did: String = "",
    val uid: String = "",
    val judul: String = "",
    val deskripsi: String = "",

    val date: String = "",
    val image: String? = "",
    val imageContent: Int = 0,
    val textContent: Int = 0

)