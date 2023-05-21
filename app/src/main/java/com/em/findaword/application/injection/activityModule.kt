package com.em.findaword.application.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import com.em.findaword.domain.RepositoryImpl
import com.em.findaword.domain.Repository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindIRepository(repositoryImpl: RepositoryImpl): Repository
}