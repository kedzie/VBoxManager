package com.kedzie.vbox.dagger

import android.app.NotificationManager
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.event.EventNotificationService
import com.kedzie.vbox.machine.ActionsFragment
import com.kedzie.vbox.machine.InfoFragment
import com.kedzie.vbox.machine.MachineListActivity
import com.kedzie.vbox.machine.group.GroupInfoFragment
import com.kedzie.vbox.machine.group.MachineGroupListFragment
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
    fun provideLocalBroadcastManager() = LocalBroadcastManager.getInstance(context)
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

    @ContributesAndroidInjector()
    internal abstract fun contributeInfoFragmentInjector(): InfoFragment

    @ContributesAndroidInjector()
    internal abstract fun contributeActionsFragmentInjector(): ActionsFragment

    @ContributesAndroidInjector()
    internal abstract fun contributeGroupInfoFragmentInjector(): GroupInfoFragment

    @ContributesAndroidInjector()
    internal abstract fun contributeMachineGroupListFragmentInjector(): MachineGroupListFragment

    @ContributesAndroidInjector()
    internal abstract fun contributeMachineListActivityInjector(): MachineListActivity
}


