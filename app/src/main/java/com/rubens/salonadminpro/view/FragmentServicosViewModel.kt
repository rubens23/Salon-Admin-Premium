package com.rubens.salonadminpro.view

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.data.services.repositories.ServicesRepository
import com.rubens.salonadminpro.data.storage.repositories.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentServicosViewModel @Inject constructor(
    private val servicesRepository: ServicesRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _enviouFotoParaOStorage: MutableSharedFlow<String?> = MutableSharedFlow(replay= 0)
    val enviouFotoParaOStorage: SharedFlow<String?> = _enviouFotoParaOStorage

    private val _salvouNovoServico: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val salvouNovoServico: SharedFlow<Boolean> = _salvouNovoServico

    private val _pegouTodosOsServicos: MutableSharedFlow<ArrayList<Servico>> = MutableSharedFlow(replay = 0)
    val pegouTodosOsServicos: SharedFlow<ArrayList<Servico>> = _pegouTodosOsServicos


    var imgUri: Uri? = null

    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String) {
        storageRepository.sendPhotoToStorage(imgUri, fileExtension, enviouFotoParaOStorage = ::enviouFotoParaOStorage)
    }

    private fun enviouFotoParaOStorage(imgUrl: String?) {
        viewModelScope.launch {
            _enviouFotoParaOStorage.emit(imgUrl)
        }
    }

    fun saveServicoInDatabase(urlFoto: String?, nomeServico: String) {
        servicesRepository.saveServicoInDatabase(urlFoto, nomeServico, salvouNovoServico = ::salvouONovoServico)

    }

    private fun salvouONovoServico(salvou: Boolean) {
        viewModelScope.launch {
            _salvouNovoServico.emit(salvou)

        }
    }

    fun pegarTodosOsServicos() {
        servicesRepository.pegarTodosServicos(pegouTodosOsServicos = ::pegouOsServicos)
    }

    private fun pegouOsServicos(servicos: ArrayList<Servico>) {
        if (servicos.isNotEmpty()){
            viewModelScope.launch {
                _pegouTodosOsServicos.emit(servicos)
            }
        }

    }
}