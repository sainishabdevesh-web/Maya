package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DeviceActionType
import com.example.device.DeviceCommandExecutor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MAYA AI", appName)
    }

    @Test
    fun `test battery command recognition`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val executor = DeviceCommandExecutor(context)
        val result = executor.checkAndExecuteCommand("What is my current battery status?")
        assertNotNull(result)
        assertEquals(DeviceActionType.BATTERY_STATUS, result?.type)
    }

    @Test
    fun `test briefing command recognition`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val executor = DeviceCommandExecutor(context)
        val result = executor.checkAndExecuteCommand("Give me a daily briefing")
        assertNotNull(result)
        assertEquals(DeviceActionType.SYSTEM_BRIEFING, result?.type)
    }
}
