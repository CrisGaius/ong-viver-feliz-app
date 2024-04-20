package com.cristiano.ongviverfeliz

data class AtributosLista(
    val id: String,
    val nome: String,
    val listaCaminhos: MutableList<String>,
    val listaUrls: MutableList<String>
)
