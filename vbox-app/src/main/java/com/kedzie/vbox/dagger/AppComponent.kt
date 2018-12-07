package com.kedzie.vbox.dagger

import android.app.NotificationManager
import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.event.EventNotificationService
import com.kedzie.vbox.task.ProgressService
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


/**
 * Created by marek.kedzierski on 4/3/18.
 */
@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AndroidServicesModule::class,
        ProvidedInjectorsModule::class))
interface AppComponent {

    fun inject(app: VBoxApplication)
}

/**
 * Provide top-level dependencies to all services
 */
@Module
class AndroidServicesModule(val context: Context) {

    @Provides
    @Singleton
    fun provideNotificationManager() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() = LocalBroadcastManager.getInstance(context);
}

/**
 * Provide injectors for receivers/services which don't need custom subcomponents
 */
@Module
abstract class ProvidedInjectorsModule {

    @ContributesAndroidInjector()
    internal abstract fun contributeEventIntentServiceInjector(): EventIntentService

    @ContributesAndroidInjector()
    internal abstract fun contributeEventNotificationServiceInjector(): EventNotificationService

    @ContributesAndroidInjector()
    internal abstract fun contributeProgressServiceInjector(): ProgressService
}


