package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OrderStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("PK Arts", appName)
  }

  @Test
  fun `verify order statuses exist`() {
    assertTrue(OrderStatus.allStatuses.contains(OrderStatus.PENDING))
    assertTrue(OrderStatus.allStatuses.contains(OrderStatus.DESIGNING))
    assertTrue(OrderStatus.allStatuses.contains(OrderStatus.PRINTING))
    assertTrue(OrderStatus.allStatuses.contains(OrderStatus.READY_FOR_PICKUP))
  }
}
