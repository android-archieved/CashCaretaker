package com.androidessence.cashcaretaker

import android.graphics.Rect
import android.support.test.espresso.util.HumanReadables
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.v4.widget.NestedScrollView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf
import timber.log.Timber


/**
 * Scrolls to an item in a scroll view.
 */
class ScrollToAction : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isDescendantOfA(anyOf(
                        isAssignableFrom(ScrollView::class.java),
                        isAssignableFrom(HorizontalScrollView::class.java),
                        isAssignableFrom(NestedScrollView::class.java))
                ))
    }

    override fun getDescription(): String = "scroll to"

    override fun perform(uiController: UiController, view: View) {
        if (isDisplayingAtLeast(90).matches(view)) {
            Timber.i("View is already displayed.")
            return
        }

        val rect = Rect()
        view.getDrawingRect(rect)

        if (!view.requestRectangleOnScreen(rect, true)) {
            Timber.w("Scrolling to view was requested, but none of the parents scrolled.")
        }

        uiController.loopMainThreadUntilIdle()
        if (!isDisplayingAtLeast(90).matches(view)) {
            throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(RuntimeException("Scrolling to view was attempted, but the view was not displayed."))
                    .build()

        }
    }
}