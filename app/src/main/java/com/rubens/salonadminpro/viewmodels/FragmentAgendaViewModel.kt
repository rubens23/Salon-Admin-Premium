package com.rubens.salonadminpro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonadminpro.data.appointments.repositories.AppointmentsRepository
import com.rubens.salonadminpro.data.employees.repositories.EmployeeRepository
import com.rubens.salonadminpro.data.models.Appointment
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.utils.CalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentAgendaViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository,
    private val employeeRepository: EmployeeRepository,
    private val calendarHelper: CalendarHelper
): ViewModel() {

    private val _pegouTodosAppointments: MutableSharedFlow<List<Appointment>> = MutableSharedFlow(replay = 0)
    val pegouTodosAppointments = _pegouTodosAppointments

    private val _appointmentUpdateErrorMsg: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val appointmentToUpdateErrorMsg = _appointmentUpdateErrorMsg

    private val _pegouTodosOsFuncionarios: MutableSharedFlow<List<Funcionario>> =
        MutableSharedFlow(replay = 0)
    val pegouTodosOsFuncionarios: SharedFlow<List<Funcionario>> = _pegouTodosOsFuncionarios



    fun getAllAppointments() {
        appointmentsRepository.getAllAppointments{ allAppointments->
            viewModelScope.launch {
                _pegouTodosAppointments.emit(allAppointments)
            }
        }

    }

    fun updateAppointment(appointmentToUpdate: Appointment) {
        appointmentsRepository.updateAppointmentById(appointmentToUpdate){
            errorMsg->
            viewModelScope.launch {
                _appointmentUpdateErrorMsg.emit(errorMsg)
            }
        }

    }

        fun getAllEmployees() {
            employeeRepository.pegarTodosFuncionarios{
                listaDeFuncionarios->
                viewModelScope.launch {
                    _pegouTodosOsFuncionarios.emit(listaDeFuncionarios)
                }
            }

        }

    fun pegarAppointmentsPorFuncionario(funcionario: Funcionario) {
        appointmentsRepository.pegarAppointmentsPorFuncionario(funcionario.childKey){
            appointmentsDoFuncionario->
            viewModelScope.launch {
                _pegouTodosAppointments.emit(appointmentsDoFuncionario)
            }
        }

    }

    fun pegarAppointmentsPorDia(diaEscolhido: String) {
        appointmentsRepository.pegarAppointmentsPorDia(diaEscolhido){
                appointmentsPorDia ->
            viewModelScope.launch {
                _pegouTodosAppointments.emit(appointmentsPorDia)
            }
        }

    }

    fun obterDiaFormatado(
        numberDay: String
    ): String {
       return calendarHelper.obterDiaComDoisDigitos(numberDay)

    }


}