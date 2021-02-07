package com.example.taskit.model

class Tareas {
    lateinit var descripcion : String
    lateinit var titulo : String
    var puntuacion : Long = 0


    constructor(title: String, descripcion: String,puntos: Long) {
        this.titulo = title
        this.descripcion = descripcion
        this.puntuacion = puntos
    }

    constructor(){}

}
