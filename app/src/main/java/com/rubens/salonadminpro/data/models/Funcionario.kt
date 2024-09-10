package com.rubens.salonadminpro.data.models

import java.io.Serializable
import java.util.HashMap

data class Funcionario(
    val nome: String = "",
    val cargo: String = "",
    val linkImagem: String = "",
    val funcionarioDisponivel: Boolean = true,
    val dataFolga: String = "",
    val childKey: String = "",
    val listaServicos: List<String>? = null,
    var isSelected: Boolean = false
): Serializable