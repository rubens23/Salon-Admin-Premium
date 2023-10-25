package com.rubens.salonadminpro.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentFuncionariosViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
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

    private val _pegouTodosOsFuncionarios: MutableSharedFlow<ArrayList<Funcionario>> = MutableSharedFlow(replay= 0)
    val pegouTodosOsFuncionarios: SharedFlow<ArrayList<Funcionario>> = _pegouTodosOsFuncionarios

    fun getListaDeFuncionarios() {
        firebaseRepository.pegarTodosFuncionarios(pegouTodosOsFuncionarios = ::pegouTodosOsFuncionarios)
    }

    private fun pegouTodosOsFuncionarios(funcionarios: ArrayList<Funcionario>) {
        viewModelScope.launch {
            _pegouTodosOsFuncionarios.emit(funcionarios)
        }
    }

    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String?) {
        firebaseRepository.sendPhotoToStorage(imgUri, fileExtension, enviouFotoParaOStorage = ::enviouFotoParaStorage)
    }

    private fun enviouFotoParaStorage(url: String?) {
        viewModelScope.launch {
            _enviouFotoParaOStorage.emit(url)

        }
    }

    fun saveFuncionarioInDatabase(urlFoto: String?, nomeFuncionario: String, cargoFuncionario: String) {
        urlFoto?.let {
            firebaseRepository.saveFuncionarioInDatabase(urlFoto, salvouFuncionarioNoDatabase= ::salvouFuncionarioNoDatabase, corteCabelo, pinturaCabelo, manicure, pedicure,
            nomeFuncionario, cargoFuncionario)
        }

    }

    private fun salvouFuncionarioNoDatabase(salvou: Boolean) {
        viewModelScope.launch {
            _salvouFuncionarioNoFirebase.emit(salvou)
        }
    }

}