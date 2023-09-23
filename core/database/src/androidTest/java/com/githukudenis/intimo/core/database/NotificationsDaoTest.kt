package com.githukudenis.intimo.core.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.githukudenis.intimo.core.model.NotificationPosted
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@SmallTest
class NotificationsDaoTest {

    @Inject
    @Named("test_db")
    lateinit var intimoDatabase: IntimoDatabase
    private lateinit var notificationsDao: NotificationsDao

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule by lazy { InstantTaskExecutorRule() }

    @Before
    fun setup() {
        hiltRule.inject()
        notificationsDao = intimoDatabase.notificationsDao()
    }

    @After
    fun tearDown() {
        intimoDatabase.clearAllTables()
        intimoDatabase.close()
    }

    @Test
    fun testInsertNotification() = runTest {
        val notificationPosted = NotificationPosted(
            notifPrimaryId = 0,
            notificationId = 1001,
            packageName = "com.githukudenis.intimo"
        )
        val notificationId = notificationsDao.insertNotification(notificationPosted)
        assertEquals(notificationId, 1)
    }

    @Test
    fun getAllNotifications() = runTest {

        val notification1 = NotificationPosted(
            notifPrimaryId = 1,
            notificationId = 1001,
            packageName = "com.githukudenis.intimo"
        )
        val notification2 = NotificationPosted(
            notifPrimaryId = 2,
            notificationId = 1435,
            packageName = "com.whatsapp"
        )
        val notification3 = NotificationPosted(
            notifPrimaryId = 3,
            notificationId = 1200,
            packageName = "com.google.mail"
        )

        notificationsDao.insertNotification(notification1)
        notificationsDao.insertNotification(notification2)
        notificationsDao.insertNotification(notification3)

        val allNotifications = notificationsDao.getAllNotifications().first()
        assertThat(allNotifications.size).isEqualTo(3)
    }

    @Test
    fun getNotificationsByPackage() = runTest {
        val notificationsPosted = listOf(
            NotificationPosted(
                notifPrimaryId = 0,
                notificationId = 1001,
                packageName = "com.githukudenis.intimo"
            ),
            NotificationPosted(
                notifPrimaryId = 1,
                notificationId = 1435,
                packageName = "com.whatsapp"
            ),
            NotificationPosted(
                notifPrimaryId = 2,
                notificationId = 1200,
                packageName = "com.google.mail"
            ),
            NotificationPosted(
                notifPrimaryId = 3,
                notificationId = 1200,
                packageName = "com.google.mail"
            )
        )
        notificationsPosted.forEach { notification ->
            notificationsDao.insertNotification(notification)
        }
        val mailNotifications =
            notificationsDao.getNotificationsByPackage(pkgName = "com.google.mail").first()
        assertThat(mailNotifications).isEqualTo(notificationsPosted.takeLast(2))
    }

}