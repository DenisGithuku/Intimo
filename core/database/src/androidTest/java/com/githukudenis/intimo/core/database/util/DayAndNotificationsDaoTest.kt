package com.githukudenis.intimo.core.database.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.githukudenis.intimo.core.database.DayAndNotificationsDao
import com.githukudenis.intimo.core.database.DayDao
import com.githukudenis.intimo.core.database.IntimoDatabase
import com.githukudenis.intimo.core.database.NotificationsDao
import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DayAndNotifications
import com.githukudenis.intimo.core.model.DayAndNotificationsPostedCrossRef
import com.githukudenis.intimo.core.model.NotificationPosted
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@SmallTest
@RunWith(AndroidJUnit4::class)
class DayAndNotificationsDaoTest {

    @Inject
    @Named("test_db")
    lateinit var intimoDatabase: IntimoDatabase
    private lateinit var dayAndNotificationsDao: DayAndNotificationsDao
    private lateinit var notificationsDao: NotificationsDao
    private lateinit var dayDao: DayDao

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule by lazy { InstantTaskExecutorRule() }

    @Before
    fun setUp(){
        hiltRule.inject()
        dayAndNotificationsDao = intimoDatabase.dayAndNotificationsDao()
        notificationsDao = intimoDatabase.notificationsDao()
        dayDao = intimoDatabase.dayDao()
    }

    @After
    fun tearDown() {
        intimoDatabase.clearAllTables()
        intimoDatabase.close()
    }

    @Test
    fun insertDayAndNotifications() = runTest {
        val day = Day(
            dayId = 1000L
        )

        dayDao.insertDay(day)

        val notification = NotificationPosted(
            notifPrimaryId = 1,
            notificationId = 100,
            packageName = "com.githukudenis.intimo"
        )

        val notificationId = notificationsDao.insertNotification(notification)

        val dayAndNotification = DayAndNotificationsPostedCrossRef(
            dayId = day.dayId,
            notifPrimaryId = notificationId
        )

        dayAndNotificationsDao.insertDayAndNotification(dayAndNotification)


        val dayAndNotifications = dayAndNotificationsDao.getDayAndNotifications().first()

        assertThat(dayAndNotifications.map { it.day.dayId }).contains(dayAndNotification.dayId)
    }
}