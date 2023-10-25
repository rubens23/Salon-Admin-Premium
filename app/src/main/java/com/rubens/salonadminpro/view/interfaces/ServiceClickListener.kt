package com.rubens.salonadminpro.view.interfaces

import com.rubens.salonadminpro.data.models.Servico


interface ServiceClickListener {
    fun onServiceClick(servico: Servico)
}