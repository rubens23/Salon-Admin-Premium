package com.rubens.salonadminpro.data.models

import java.io.Serializable

data class Appointment(
    val day: String,
    val month: String,
    val employee: String,
    val hour: String,
    val service: String,
    val clientName: String,
    val clientUid: String?,
    val employeeKey: String,
    val appointmentDayFormatted: String,
    var confirmacaoAtendimento: String = "aguardando confirmacao",
    val appointmentId: String = ""
): Serializable
