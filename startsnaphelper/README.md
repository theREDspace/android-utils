## SnapToPercentHelper

This is a custom implementation of `SnapHelper` class to support snap to the start position of the view.

### Usage

Instantiate `SnapToPercentHelper` and attach it to `RecyclerView`

```
// snap the top of the highest visible view to a position 30% from the top of the RecyclerView
val snapHelper = SnapToPercentHelper(0.3f)
snapHelper.attachToRecyclerView(recyclerView)
```

### Customization 

Set `paddingFactor` to move the snapping point by a percentage of the size of the RecyclerView.