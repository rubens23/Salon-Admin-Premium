package com.rubens.salonadminpro.data.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rubens.salonadminpro.data.appointments.repositories.AppointmentsRepository
import com.rubens.salonadminpro.data.appointments.repositories.AppointmentsFirebaseRepositoryImpl
import com.rubens.salonadminpro.data.employees.repositories.EmployeeRepository
import com.rubens.salonadminpro.data.employees.repositories.EmployeesFirebaseRepositoryImpl
import com.rubens.salonadminpro.data.services.repositories.ServicesRepository
import com.rubens.salonadminpro.data.services.repositories.ServicesFirebaseRepositoryImpl
import com.rubens.salonadminpro.data.storage.repositories.StorageRepository
import com.rubens.salonadminpro.data.storage.repositories.StorageFirebaseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseHiltModule {

    @Singleton
    @Provides
    @EmployeesDb
    fun providesEmployeesAccess(): DatabaseReference{
        return FirebaseDatabase.getInstance().getReference("uploads")
    }

    @Singleton
    @Provides
    fun providesStorageReference(): StorageReference{
        return FirebaseStorage.getInstance().getReference("uploads")
    }

    @Singleton
    @Provides
    @ServicesDb
    fun providesServicesAccess(): DatabaseReference{
        return FirebaseDatabase.getInstance().getReference("servicos")
    }

    @Singleton
    @Provides
    @AppointmentDb
    fun providesAppointmentsAccess(): DatabaseReference{
        return FirebaseDatabase.getInstance().getReference("appointments")
    }

    @Singleton
    @Provides
    fun providesAppointmentRepository(@AppointmentDb appointmentDb: DatabaseReference): AppointmentsRepository{
        return AppointmentsFirebaseRepositoryImpl(appointmentDb)
    }

    @Singleton
    @Provides
    fun providesServicesRepository(@ServicesDb servicesDb: DatabaseReference, @EmployeesDb employeesDb: DatabaseReference): ServicesRepository{
        return ServicesFirebaseRepositoryImpl(servicesDb, employeesDb)

    }

    @Singleton
    @Provides
    fun providesEmployeesRepository(@EmployeesDb employeesDb: DatabaseReference): EmployeeRepository{
        return EmployeesFirebaseRepositoryImpl(employeesDb)

    }

    @Singleton
    @Provides
    fun providesStorageRepository(storageReference: StorageReference): StorageRepository{
        return StorageFirebaseRepositoryImpl(storageReference)

    }
}