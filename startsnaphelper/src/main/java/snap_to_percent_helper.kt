package com.redspace.startsnaphelper

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * This class is responsible for snapping the first visible item of the RecyclerView to
 * the start position of the RecyclerView. if paddingFactor is bigger than 0.0f when
 * it moves the snapping point by a percentage of the size of the RecyclerView and also
 * adds a padding on the first and last items of the list.
 *
 * Examples: <ul>
 * <li>0.3f means that snap starts at 30% of the size of the RecyclerView.</li>
 * <li>0.0f means the snap start at the beginning of the RecyclerView.</li>
 * </ul>
 *
 * @param paddingFactor factor of padding from the start. This parameter should be between 0.0f and 1.0f.
 */
class SnapToPercentHelper(
        private val paddingFactor: Float = 0.0f
) : LinearSnapHelper() {

    init {
        if (paddingFactor < 0.0f || paddingFactor > 1.0f)
            throw IllegalArgumentException("paddingFactor should be a float between 0.0f and 1.0f")
    }

    private lateinit var verticalHelper: OrientationHelper
    private lateinit var horizontalHelper: OrientationHelper
    private val itemDecoration = OffsetItemDecoration(paddingFactor)
    private var attachedRecyclerView: RecyclerView? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView == null)
            attachedRecyclerView?.removeItemDecoration(itemDecoration)

        if (paddingFactor > 0) {
            recyclerView?.addItemDecoration(itemDecoration)
            attachedRecyclerView = recyclerView
        }

        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val distanceY = distanceToStart(layoutManager.canScrollVertically(), targetView,
                verticalHelperFor(layoutManager), layoutManager)
        val distanceX = distanceToStart(layoutManager.canScrollHorizontally(), targetView,
                horizontalHelperFor(layoutManager), layoutManager)

        return intArrayOf(distanceX, distanceY)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is LinearLayoutManager) {
            if (layoutManager.canScrollVertically()) startView(layoutManager, verticalHelperFor(layoutManager))
            else startView(layoutManager, horizontalHelperFor(layoutManager))
        } else {
            throw IllegalArgumentException("SnapToPercentHelper works only with LinearLayoutManager")
        }
    }

    private fun startView(layoutManager: LinearLayoutManager, helper: OrientationHelper): View? {
        val firstChild = layoutManager.findFirstCompletelyVisibleItemPosition()

        val isLastItem = layoutManager
                .findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1

        if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
            return null
        }

        val child = layoutManager.findViewByPosition(firstChild)

        val isFirstItem = firstChild == 0

        val distanceToEnd = helper.getDecoratedEnd(child) - if (isFirstItem) 0 else startOffset(layoutManager)

        val userScrolledEnoughToSnap = distanceToEnd >= helper.getDecoratedMeasurement(child) * 0.8

        return if (userScrolledEnoughToSnap && distanceToEnd > 0) {
            child
        } else {
            layoutManager.findViewByPosition(firstChild + 1)
        }
    }

    private fun distanceToStart(canScrollInDirection: Boolean, targetView: View, helper: OrientationHelper, layoutManager: RecyclerView.LayoutManager): Int {
        val isFirstPosition = layoutManager.getPosition(targetView) == 0
        return if (canScrollInDirection) {
            val distanceToStart = helper.getDecoratedStart(targetView) - helper.startAfterPadding
            if (isFirstPosition) distanceToStart
            else distanceToStart - startOffset(layoutManager)
        } else 0
    }

    private fun startOffset(layoutManager: RecyclerView.LayoutManager): Int {
        return if (layoutManager is LinearLayoutManager) {
            val totalSize = if (layoutManager.orientation == LinearLayoutManager.VERTICAL) layoutManager.height
            else layoutManager.width
            (totalSize * paddingFactor).toInt()
        } else 0
    }

    private fun verticalHelperFor(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!this::verticalHelper.isInitialized)
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        return verticalHelper
    }

    private fun horizontalHelperFor(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!this::horizontalHelper.isInitialized)
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        return horizontalHelper
    }

    private class OffsetItemDecoration(
            private val upperThresholdMultiplier: Float
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val offset = (parent.height * upperThresholdMultiplier).toInt()
            val viewPosition = parent.getChildAdapterPosition(view)
            val lastPosition = parent.adapter?.itemCount?.let { it - 1 }
            if (viewPosition == 0) {
                outRect.top = offset
            }
            if (viewPosition == lastPosition) {
                outRect.bottom = offset
            }
        }
    }
}
