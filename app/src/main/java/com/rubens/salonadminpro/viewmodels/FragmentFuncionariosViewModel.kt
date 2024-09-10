package com.rubens.salonadminpro.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonadminpro.data.employees.repositories.EmployeeRepository
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.storage.repositories.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentFuncionariosViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    var corteCabelo: String = ""
    var pedicure: String = ""
    var manicure: String = ""
    var pinturaCabelo: String = ""
    var imageUri: Uri? = null
    var contadorServicos = 0

    private val _enviouFotoParaOStorage: MutableSharedFlow<String?> = MutableSharedFlow(replay= 0)
    val enviouFotoParaOStorage: SharedFlow<String?> = _enviouFotoParaOStorage

    private val _salvouFuncionarioNoFirebase: MutableSharedFlow<Boolean> = MutableSharedFlow(replay= 0)
    val salvouFuncionarioNoFirebase: SharedFlow<Boolean> = _salvouFuncionarioNoFirebase

    private val _pegouTodosOsFuncionarios: MutableSharedFlow<List<Funcionario>> = MutableSharedFlow(replay= 0)
    val pegouTodosOsFuncionarios: SharedFlow<List<Funcionario>> = _pegouTodosOsFuncionarios

    fun getListaDeFuncionarios() {
        employeeRepository.pegarTodosFuncionarios(pegouTodosOsFuncionarios = ::pegouTodosOsFuncionarios)
    }

    private fun pegouTodosOsFuncionarios(funcionarios: List<Funcionario>) {
        viewModelScope.launch {
            _pegouTodosOsFuncionarios.emit(funcionarios)
        }
    }

    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String?) {
        storageRepository.sendPhotoToStorage(imgUri, fileExtension, enviouFotoParaOStorage = ::enviouFotoParaStorage)
    }

    private fun enviouFotoParaStorage(url: String?) {
        viewModelScope.launch {
            _enviouFotoParaOStorage.emit(url)

        }
    }

    fun saveFuncionarioInDatabase(urlFoto: String?, nomeFuncionario: String, cargoFuncionario: String) {
        urlFoto?.let {
            employeeRepository.saveFuncionarioInDatabase(urlFoto, salvouFuncionarioNoDatabase= ::salvouFuncionarioNoDatabase, corteCabelo, pinturaCabelo, manicure, pedicure,
            nomeFuncionario, cargoFuncionario)
        }

    }

    private fun salvouFuncionarioNoDatabase(salvou: Boolean) {
        viewModelScope.launch {
            _salvouFuncionarioNoFirebase.emit(salvou)
        }
    }

}