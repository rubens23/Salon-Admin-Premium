package com.rubens.salonadminpro.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonadminpro.data.employees.repositories.EmployeeRepository
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.services.repositories.ServicesRepository
import com.santalu.maskara.widget.MaskEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FragmentDetalhesFuncionarioViewModel @Inject constructor(
    private val servicesRepository: ServicesRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    var folgaDoFuncionario: String = ""
    lateinit var listaSpinnerServicos: Array<String>

    private val _etDataFolgaSharedFlow: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val etDataFolgaSharedFlow: SharedFlow<String> = _etDataFolgaSharedFlow

    private val _msgDeErro: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val msgDeErro: SharedFlow<String> = _msgDeErro

    private val _salvouNovaFolga: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val salvouNovaFolga: SharedFlow<Boolean> = _salvouNovaFolga

    private val _pegouFolgaDoFuncionario: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val pegouFolgaDoFuncionario: SharedFlow<String> = _pegouFolgaDoFuncionario

    private val _atualizouListaDeServicos: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val atualizouListaDeServicos: SharedFlow<Boolean> = _atualizouListaDeServicos

    private val _pegouListaDeServicos: MutableSharedFlow<ArrayList<String>> = MutableSharedFlow(replay = 0)
    val pegouListaDeServicos: SharedFlow<ArrayList<String>> = _pegouListaDeServicos

    var listaDeServicosDoFuncionario = arrayListOf<String>()
    var servicoSelecionado = ""




    fun checarSeDiaValidoParaFolga(etDataFolga: MaskEditText) {
        verificarSeDataEstaNoPassado(etDataFolga)
    }

    private fun verificarSeDataEstaNoPassado(etDataFolga: MaskEditText) {
        val data: String = etDataFolga.masked
        val isDatePast = isDatePast(data, "dd/MM/yyyy")
        if(isDatePast){
            viewModelScope.launch {
                _etDataFolgaSharedFlow.emit("Essa data já passou, escolha outra!")
            }
        }else{
            viewModelScope.launch {
                _etDataFolgaSharedFlow.emit("updateFolga")
            }
        }
    }


    //requer api 26+
//    private fun isDatePast(data: String, formatData: String): Boolean {
//        val localDate = LocalDate.now(ZoneId.systemDefault())
//
//
//        val dtf = DateTimeFormatter.ofPattern(formatData)
//        val inputDate = LocalDate.parse(data, dtf)
//
//        return inputDate.isBefore(localDate)
//    }

    //api 24+
    private fun isDatePast(data: String, formatData: String): Boolean {
        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat(formatData, Locale.getDefault())
        val inputDate = dateFormat.parse(data)

        return inputDate != null && inputDate.before(calendar.time)
    }

    fun updateListaDeServicos(funcionarioKey: String) {
        servicesRepository.updateListaDeServicos(funcionarioKey, listaDeServicosAtualizada = ::pegouListaDeServicosAtualizados)
    }

    private fun pegouListaDeServicosAtualizados(servicos: ArrayList<String>) {
        listaDeServicosDoFuncionario.clear()
        listaDeServicosDoFuncionario = servicos
        //obtive a lista de servicos
    }

    fun updateServicosDoFuncionario(servicoASerAdicionado: String, func: Funcionario) {
        if(!listaDeServicosDoFuncionario.contains(servicoASerAdicionado)){
            listaDeServicosDoFuncionario.add(servicoASerAdicionado)
        }

        val funcionario = Funcionario(
            cargo = func.cargo,
            childKey = func.childKey,
            dataFolga = func.dataFolga,
            linkImagem = func.linkImagem,
            nome = func.nome,
            listaServicos = listaDeServicosDoFuncionario
        )

        servicesRepository.updateServicosDoFuncionario(func.childKey, funcionario, atualizouListaDeServicos = ::atualizouServicosDoFuncionario)

    }

    private fun atualizouServicosDoFuncionario(atualizou: Boolean) {
        viewModelScope.launch {
            _atualizouListaDeServicos.emit(atualizou)
        }

    }

    fun pegarFolgas(funcionarioKey: String) {
        employeeRepository.pegarFolgas(funcionarioKey, pegouFolga = ::pegouFolgaDoFuncionario)

    }

    private fun pegouFolgaDoFuncionario(dataFolga: String) {
        //pegou data da folga
        viewModelScope.launch { 
            _pegouFolgaDoFuncionario.emit(dataFolga)
        }
    }

    fun removerServicoDoFuncionario(funcionarioKey: String, servicoFuncionario: String) {
        servicesRepository.removerServicoDoFuncionario(funcionarioKey, servicoFuncionario, deletouServicoDoFuncionario = ::removeuServicoDoFuncionario)

    }

    private fun removeuServicoDoFuncionario(removeu: Boolean, servicoRemovido: String) {
        //emitir o flow
        if(removeu){
            if(servicoRemovido != ""){
                listaDeServicosDoFuncionario.remove(servicoRemovido)
                atualizarRecyclerViewDeServicos()
            }else{
                emitirMensagemDeErro("erro ao remover serviço")
            }
        }else{
            emitirMensagemDeErro("erro ao remover serviço")

        }
    }

    private fun emitirMensagemDeErro(msg: String) {
        viewModelScope.launch { 
            _msgDeErro.emit(msg)
        }
    }

    private fun atualizarRecyclerViewDeServicos() {
        viewModelScope.launch { 
            _atualizouListaDeServicos.emit(true)
        }
    }

    fun salvarFolgaNova(funcionarioKey: String, dataFolga: String) {
        employeeRepository.salvarNovaFolga(funcionarioKey, dataFolga, salvouNovaFolga = ::salvouNovaFolgaDoFuncionario)
    }

    private fun salvouNovaFolgaDoFuncionario(salvou: Boolean) {
        viewModelScope.launch {
            _salvouNovaFolga.emit(salvou)
        }
    }

    fun carregarServicosDoFuncionario(childKey: String) {
        servicesRepository.updateListaDeServicos(childKey, listaDeServicosAtualizada = ::pegouListaDeServicos)


    }

    private fun pegouListaDeServicos(servicos: ArrayList<String>) {
        if(servicos.size > 0){
            viewModelScope.launch {
                listaDeServicosDoFuncionario.clear()
                listaDeServicosDoFuncionario = servicos
                _pegouListaDeServicos.emit(servicos)
            }
        }
    }

}