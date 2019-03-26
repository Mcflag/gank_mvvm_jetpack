package com.ccooy.gankart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.ccooy.gankart.ui.splash.SplashActivity
import org.junit.Test
import org.junit.Rule

class SplashTest {

    @Rule
    @JvmField
    var rule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun clickTest() {
        onView(withId(R.id.splash_tv_jump)).perform(click())
    }
}