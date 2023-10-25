package com.rubens.salonadminpro.viewmodels

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
class SharedViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel(){

    private val _precisaMudarParaFragmentoDetalhesFuncionario: MutableSharedFlow<Funcionario> = MutableSharedFlow(replay = 0)
    val precisaMudarParaFragmentoDetalhesFuncionario: SharedFlow<Funcionario> = _precisaMudarParaFragmentoDetalhesFuncionario

    fun chamarMetodoDeMudarFragmento(func: Funcionario){
        viewModelScope.launch {
            _precisaMudarParaFragmentoDetalhesFuncionario.emit(func)
        }
    }
}