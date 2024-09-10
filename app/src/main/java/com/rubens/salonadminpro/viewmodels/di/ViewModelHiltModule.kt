package com.rubens.salonadminpro.viewmodels.di

import com.rubens.salonadminpro.utils.CalendarHelper
import com.rubens.salonadminpro.utils.CalendarHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelHiltModule {


    @Provides
    fun providesCalendarHelper(): CalendarHelper {
        return CalendarHelperImpl()
    }
}