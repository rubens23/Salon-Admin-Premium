package com.rubens.salonadminpro.data.appointments.repositories

import com.rubens.salonadminpro.data.models.Appointment

interface AppointmentsRepository {
    fun getAllAppointments(todosAppoitments: (appointments: List<Appointment>)->Unit)

    fun updateAppointmentById(appointmentToUpdate: Appointment,errorMsg: (msg: String)->Unit)


    fun pegarAppointmentsPorFuncionario(funcionarioKey: String, todosAppointmentsDoFuncionario: (appointments: List<Appointment>) -> Unit)

    fun pegarAppointmentsPorDia(diaEscolhido: String, appointmentsPorDia: (appointments: List<Appointment>) -> Unit)


}