package com.rubens.salonadminpro.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmployeesDb

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServicesDb

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppointmentDb