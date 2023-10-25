package com.rubens.salonadminpro.view.interfaces

import com.rubens.salonadminpro.data.models.Appointment

interface OnAppointmentClickListener {

    fun onAppointmentClick(appointment: Appointment, positionToBeChanged: Int)
}