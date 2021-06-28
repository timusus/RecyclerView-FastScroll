# RecyclerView-FastScroll

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.simplecityapps/recyclerview-fastscroll/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.simplecityapps/recyclerview-fastscroll) [![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License](http://img.shields.io/badge/license-APACHE2-blue.svg)](NOTICE)
[![Build Status](https://travis-ci.org/timusus/RecyclerView-FastScroll.svg?branch=master)](https://travis-ci.org/timusus/RecyclerView-FastScroll)

A simple `FastScroller` for Android's `RecyclerView`.

Supports vertical `RecyclerViews` using either `LinearLayoutManager` or `GridLayoutManager` (including multiple spans).

The style is loosely based on the `ListView` `FastScroller` from whatever the last version of Lollipop was. This library borrows heavily from [Google's Launcher3 FastScroller](https://android.googlesource.com/platform/packages/apps/Launcher3/)

![Screenshot](https://github.com/timusus/RecyclerView-FastScroll/blob/master/screenshot.png)


### Gradle
`compile 'com.simplecityapps:recyclerview-fastscroll:2.0.1'`


### Usage
You must use `FastScrollRecyclerView` as your base `RecyclerView`. See the sample project if you're having trouble.

Via xml:

```
<com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
    android:id="@+id/recycler"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:fastScrollPopupBgColor="@color/colorAccent"
    app:fastScrollPopupTextColor="@android:color/primary_text_dark"
    app:fastScrollThumbColor="@color/colorAccent" />
```

To display the `FastScrollPopup`, your adapter must implement `FastScrollRecyclerView.SectionedAdapter` and override `getSectionName()`.

If you need to know when fast-scrolling starts or stops, you can attach an OnFastScrollStateChangedListener to the FastScrollRecyclerView.


##### Varying Row Heights

By default, `FastScrollRecyclerView` assumes that all items in the adapter have the same height. If your adapter has
item views with different heights, then you should make your adapter implement the `MeasurableAdapter` interface and
override `getViewTypeHeight()` – otherwise the scroll thumb may not appear in the correct position and scrolling may
be inconsistent.

`getViewTypeHeight()` returns the height of a single view of a given type in pixels. The height of each view must be
fixed and constant between all instances of a view type. Because the implementor is responsible for computing this
value before views are laid out, this is not suitable for view types where the height of a view is determined by a
variable number of lines of text that the item consumes.

Currently, `MeasurableAdapter` only works with `LinearLayoutManager`. Using `MeasurableAdapter` with a 
`GridLayoutManager` that has more than one span will cause the scrollbar thumb to reach the bottom of the list before
the halfway point on the scrollbar's background.


##### Customisation

You can enable/disable autohide using the `fastScrollAutoHide` & `fastScrollAutoHideDelay` attributes in xml:

```
 <com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
     app:fastScrollAutoHide="true"
     app:fastScrollAutoHideDelay="1500"
     ...
```
             
Or programmatically via `setAutoHideDelay(int hideDelay)` and `setAutoHideEnabled(boolean autoHideEnabled)`

The following can  be styled via xml:

- Popup background
- Popup text
- Popup background size
- Popup text size
- Track width and background
- Thumb width and color
- Popup position
- Popup text vertical alignment mode

```
<com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
    app:fastScrollPopupBgColor="@color/colorAccent"
    app:fastScrollPopupTextColor="@android:color/primary_text_dark"
    app:fastScrollPopupTextSize="56sp"
    app:fastScrollPopupBackgroundSize="88dp"
    app:fastScrollThumbColor="@color/colorAccent"
    app:fastScrollThumbWidth="6dp"
    app:fastScrollTrackColor="#1f000000"
    app:fastScrollTrackWidth="8dp"
    app:fastScrollPopupPosition="adjacent"
    app:fastScrollPopupTextVerticalAlignmentMode="fontMetrics"/>
    ...
```

Or programmatically via

- `setThumbColor(@ColorInt int color)`
- `setTrackColor(@ColorInt int color)`
- `setPopupBgColor(@ColorInt int color)`
- `setPopupTextColor(@ColorInt int color)`
- `setPopupTextSize(int size)`
- `setPopupPosition(@FastScroller.FastScrollerPopupPosition int position)`


You can enable/disable fast-scrolling via:

```
<com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
    app:fastScrollThumbEnabled="false"
    ...
```
Or programmatically via `setFastScrollThumbEnabled(boolean enabled)`
