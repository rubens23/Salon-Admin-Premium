package com.rubens.salonadminpro.data.repositories

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rubens.salonadminpro.data.models.Appointment
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.view.adapters.FuncionariosAdapter
import java.util.UUID
import kotlin.reflect.KFunction1

class FirebaseRepositoryImpl: FirebaseRepository {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRefServicos: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRefHorariosMarcados: DatabaseReference


    init {
        initFirebaseInstances()
    }

    private fun initFirebaseInstances() {
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        storageRef = FirebaseStorage.getInstance().getReference("uploads")
        databaseRefServicos = FirebaseDatabase.getInstance().getReference("servicos")
        databaseRefHorariosMarcados = FirebaseDatabase.getInstance().getReference("appointments")

    }

    override fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>) -> Unit) {
        val listaServicosDoFuncionario: ArrayList<String> = arrayListOf()

        databaseRef.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach {
                                if(it.key == "listaServicos"){
                                    it.children.forEach {
                                        servico->
                                        listaServicosDoFuncionario.add(servico.value as String)

                                    }
                                }
                            }
                        }
                        listaDeServicosAtualizada(listaServicosDoFuncionario)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)

                    }

                }
            )
    }

    override fun updateServicosDoFuncionario(
        childKey: String,
        funcionario: Funcionario,
        atualizouListaDeServicos: (atualizouServicos: Boolean) -> Unit
    ) {
        databaseRef.child(childKey)
            .setValue(funcionario)
            .addOnSuccessListener {
                atualizouListaDeServicos(true)
            }.addOnFailureListener {
                atualizouListaDeServicos(false)

            }
    }

    override fun removerServicoDoFuncionario(
        funcionarioKey: String,
        servicoFuncionario: String,
        deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit
    ) {
        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.key == funcionarioKey){
                        it.children.forEach {
                            if(it.hasChildren()){
                                it.children.forEach {
                                    servico->
                                    if(servico.value == servicoFuncionario){
                                        servico.ref.removeValue().addOnSuccessListener {
                                            deletouServicoDoFuncionario(true, servicoFuncionario)
                                        }.addOnFailureListener {
                                            deletouServicoDoFuncionario(false, "")
                                        }


                                    }
                                }
                            }
                        }

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("testeremove", "to no onCancelled")
            }

        })

    }

    override fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: ArrayList<Funcionario>) -> Unit) {
        val listaDeFuncionarios: ArrayList<Funcionario> = arrayListOf()
        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val funcionario = it.getValue(Funcionario::class.java)
                    if (funcionario != null){
                        if(!listaDeFuncionarios.contains(funcionario)){
                            listaDeFuncionarios.add(funcionario)

                        }

                    }
                }
                pegouTodosOsFuncionarios(listaDeFuncionarios)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("getfuncionarios", "onCancelled: ${error.message}")
            }

        })
    }

    override fun sendPhotoToStorage(imgUri: Uri,
                                    fileExtension: String?,
                                    enviouFotoParaOStorage: (url: String?)->Unit) {
        val fileReference = storageRef.child(
            "uploads/" + System.currentTimeMillis().toString()
                    + "." + fileExtension
        )

        fileReference.putFile(imgUri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                downloadUrl->
                enviouFotoParaOStorage(downloadUrl.toString())

            }.addOnFailureListener {
                enviouFotoParaOStorage("")

            }
        }.addOnFailureListener {
            enviouFotoParaOStorage("")

        }
    }

    override fun saveFuncionarioInDatabase(
        urlFoto: String,
        salvouFuncionarioNoDatabase: (salvou: Boolean) -> Unit,
        corteCabelo: String,
        pinturaCabelo: String,
        manicure: String,
        pedicure: String,
        nomeFuncionario: String,
        cargoFuncionario: String

    ) {
        val listaDeServicos = fazerListaDeServicos(corteCabelo,
        pinturaCabelo,
        manicure,
        pedicure)

        val uploadId = databaseRef.push().key

        val funcionario = Funcionario(
            nome = nomeFuncionario,
            cargo = cargoFuncionario,
            linkImagem = urlFoto,
            childKey = uploadId!!,
            listaServicos = listaDeServicos
        )

        databaseRef.child(uploadId).setValue(funcionario)
            .addOnSuccessListener {
                salvouFuncionarioNoDatabase(true)

            }.addOnFailureListener {
                salvouFuncionarioNoDatabase(false)
            }


    }

    override fun pegarFolgas(
        funcionarioKey: String,
        pegouFolga: (folgaDoFuncionario: String) -> Unit
    ) {
        databaseRef.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach {
                                funcionario->
                                if(funcionario.key == "dataFolga"){
                                    val dataFolga = funcionario.value as String
                                    pegouFolga(dataFolga)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)
                    }

                }
            )
    }

    override fun salvarNovaFolga(
        funcionarioKey: String,
        dataFolga: String,
        salvouNovaFolga: (salvou: Boolean) -> Unit
    ) {
        databaseRef.child(funcionarioKey).child("dataFolga")
            .setValue(dataFolga)
            .addOnSuccessListener {
                salvouNovaFolga(true)
            }.addOnFailureListener {
                salvouNovaFolga(false)
            }
    }

    override fun saveServicoInDatabase(
        urlFoto: String?,
        nomeServico: String,
        salvouNovoServico: (salvou: Boolean) -> Unit
    ) {
        val servico = Servico(nomeServico = nomeServico, linkImagem = urlFoto!!)
        val servicoId = databaseRefServicos.push().key
        databaseRefServicos.child(servicoId!!).setValue(servico).addOnSuccessListener {
            salvouNovoServico(true)
        }.addOnFailureListener {
            salvouNovoServico(false)
        }
    }

    override fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Servico>) -> Unit) {
        val listaDeServicos: ArrayList<Servico> = arrayListOf()

        databaseRefServicos.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val servico: Servico? = it.getValue(Servico::class.java)
                    listaDeServicos.add(servico!!)

                }
                pegouTodosOsServicos(listaDeServicos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errogetserv", error.message.toString())

            }

        })
    }

    override fun getAllAppointments(todosAppoitments: (appointments: ArrayList<Appointment>) -> Unit) {
        val listaAppointments: ArrayList<Appointment> = arrayListOf()
        databaseRefHorariosMarcados.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val childValue = it.getValue() as? Map<String, Any>
                    val appointment = pegarAppointmentsPorSnapshot(childValue)
                    if(!listaAppointments.contains(appointment)){
                        listaAppointments.add(appointment)
                    }
                }
                todosAppoitments(listaAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("erroappoint", error.message)
            }

        })
    }

    override fun updateAppointmentById(appointmentToUpdate: Appointment, errorMsg: (msg: String)->Unit) {
        databaseRefHorariosMarcados.child(appointmentToUpdate.appointmentId).setValue(appointmentToUpdate).addOnFailureListener {
            errorMsg("Ocorreu um erro ao atualizar o atendimento")
        }
    }

    override fun pegarAppointmentsPorFuncionario(
        funcionarioKey: String,
        todosAppointmentsDoFuncionario: (appointments: ArrayList<Appointment>) -> Unit
    ) {
        val listaAppointments: ArrayList<Appointment> = arrayListOf()
        databaseRefHorariosMarcados.orderByChild("employeeKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val childValue = it.getValue() as? Map<String, Any>
                            val appointment = pegarAppointmentsPorSnapshot(childValue)
                            if(!listaAppointments.contains(appointment)){
                                listaAppointments.add(appointment)
                            }
                        }

                            todosAppointmentsDoFuncionario(listaAppointments)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)

                    }

                }
            )
    }

    override fun pegarAppointmentsPorDia(
        diaEscolhido: String,
        appointmentsPorDia: (appointments: ArrayList<Appointment>) -> Unit
    ) {
        val listaAppointments: ArrayList<Appointment> = arrayListOf()
        databaseRefHorariosMarcados.orderByChild("appointmentDayFormatted").equalTo(diaEscolhido)
            .addListenerForSingleValueEvent(
                object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val childValue = it.getValue() as? Map<String, Any>
                            val appointment = pegarAppointmentsPorSnapshot(childValue)
                            if(!listaAppointments.contains(appointment)){
                                listaAppointments.add(appointment)
                            }
                        }

                        appointmentsPorDia(listaAppointments)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)

                    }

                }
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

    private fun fazerListaDeServicos(corteCabelo: String, pinturaCabelo: String, manicure: String, pedicure: String): ArrayList<String> {
        val listaServicos: ArrayList<String> = arrayListOf()

        if (corteCabelo == "Corte de Cabelo") {
            listaServicos.add(corteCabelo)
        }
        if (pinturaCabelo == "Pintura de Cabelo") {
            listaServicos.add(pinturaCabelo)

        }
        if (manicure == "Manicure") {
            listaServicos.add(manicure)
        }
        if (pedicure == "Pedicure") {
            listaServicos.add(pedicure)
        }
        return listaServicos
    }

}