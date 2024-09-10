package com.rubens.salonadminpro.data.appointments.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.rubens.salonadminpro.data.models.Appointment

class AppointmentsFirebaseRepositoryImpl(
    private val appointmentsDb: DatabaseReference
): AppointmentsRepository {

    private val tag = "AppointmentsFirebaseRepositoryImpl"





    override fun pegarAppointmentsPorDia(
        diaEscolhido: String,
        appointmentsPorDia: (appointments: List<Appointment>) -> Unit
    ) {
        fetchDataFromDatabase(
            appointmentsDb.orderByChild("appointmentDayFormatted").equalTo(diaEscolhido),
            appointmentsPorDia
        )
    }

    override fun getAllAppointments(todosAppoitments: (appointments: List<Appointment>) -> Unit) {
        fetchDataFromDatabase(appointmentsDb, todosAppoitments)

    }

    override fun updateAppointmentById(appointmentToUpdate: Appointment, errorMsg: (msg: String)->Unit) {
        appointmentsDb.child(appointmentToUpdate.appointmentId).setValue(appointmentToUpdate).addOnFailureListener {
            errorMsg("Ocorreu um erro ao atualizar o atendimento")
        }
    }

    override fun pegarAppointmentsPorFuncionario(
        funcionarioKey: String,
        todosAppointmentsDoFuncionario: (appointments: List<Appointment>) -> Unit
    ) {
        fetchDataFromDatabase(
            appointmentsDb.orderByChild("employeeKey").equalTo(funcionarioKey),
            todosAppointmentsDoFuncionario
        )

    }

    private fun pegarAppointmentsPorSnapshot(childValue: Map<String, Any>?): Appointment {
        return Appointment(
            day = childValue!!["day"] as String,
            month = childValue["month"] as String,
            employee = childValue["employee"] as String,
            hour = childValue["hour"] as String,
            service = childValue["service"] as String,
            clientName = childValue["clientName"] as String,
            employeeKey = childValue["employeeKey"] as String,
            appointmentDayFormatted = childValue["appointmentDayFormatted"] as String,
            confirmacaoAtendimento = childValue["confirmacaoAtendimento"] as String,
            appointmentId = childValue["appointmentId"] as String,
            clientUid = childValue["clientUid"] as String?

        )

    }

    private fun fetchDataFromDatabase(
        query: Query,
        resultCallback: (List<Appointment>)->Unit
    ){
        val appointmentsSet = mutableSetOf<Appointment>()
        query.addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val childValue = it.getValue() as? Map<String, Any>

                        childValue?.let {
                            appointmentMap->
                            val appointment = pegarAppointmentsPorSnapshot(appointmentMap)
                            appointment.let {
                                appointmentsSet.add(appointment)
                            }

                        }



                    }

                    resultCallback(appointmentsSet.toList())


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "erro on fetchDataFromDatabase ${error.message}")

                }

            }
        )

    }
}