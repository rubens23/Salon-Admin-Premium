package com.rubens.salonadminpro.data.repositories

import android.net.Uri
import com.rubens.salonadminpro.data.models.Appointment
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico
import kotlin.reflect.KFunction1

interface FirebaseRepository {
    fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>)->Unit)
    fun updateServicosDoFuncionario(childKey: String, funcionario: Funcionario, atualizouListaDeServicos: (atualizouServicos: Boolean)->Unit)
    fun removerServicoDoFuncionario(funcionarioKey: String, servicoFuncionario: String, deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit)
    fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: ArrayList<Funcionario>)->Unit)
    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String?, enviouFotoParaOStorage: (url: String?)->Unit)
    fun saveFuncionarioInDatabase(urlFoto: String, salvouFuncionarioNoDatabase: (salvou: Boolean)->Unit, corteCabelo: String,
                                pinturaCabelo: String,
                                manicure: String,
                                pedicure: String,
                                nomeFuncionario: String,
                                cargoFuncionario: String)

    fun pegarFolgas(funcionarioKey: String, pegouFolga: (folgaDoFuncionario: String)->Unit)

    fun salvarNovaFolga(funcionarioKey: String, dataFolga: String, salvouNovaFolga: (salvou: Boolean)->Unit)
    fun saveServicoInDatabase(urlFoto: String?, nomeServico: String, salvouNovoServico: (salvou: Boolean)->Unit)

    fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Servico>)->Unit)
    fun getAllAppointments(todosAppoitments: (appointments: ArrayList<Appointment>)->Unit)
    fun updateAppointmentById(appointmentToUpdate: Appointment,errorMsg: (msg: String)->Unit)

    fun pegarAppointmentsPorFuncionario(funcionarioKey: String, todosAppointmentsDoFuncionario: (appointments: ArrayList<Appointment>) -> Unit)

    fun pegarAppointmentsPorDia(diaEscolhido: String, appointmentsPorDia: (appointments: ArrayList<Appointment>) -> Unit)


}