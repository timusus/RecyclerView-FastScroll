# RecyclerView-FastScroll

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.simplecityapps/recyclerview-fastscroll/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.simplecityapps/recyclerview-fastscroll) [![API](https://img.shields.io/badge/API-11%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=11)
[![License](http://img.shields.io/badge/license-APACHE2-blue.svg)](NOTICE)
[![Build Status](https://travis-ci.org/timusus/RecyclerView-FastScroll.svg?branch=master)](https://travis-ci.org/timusus/RecyclerView-FastScroll)

A simple `FastScroller` for Android's `RecyclerView`.

Supports vertical `RecyclerViews` using either `LinearLayoutManager` or `GridLayoutManager` (including multiple spans).

The style is loosely based on the `ListView` `FastScroller` from whatever the last version of Lollipop was. This library borrows heavily from [Google's Launcher3 FastScroller](https://android.googlesource.com/platform/packages/apps/Launcher3/)

![Screenshot](https://github.com/timusus/RecyclerView-FastScroll/blob/master/screenshot.png)

###Gradle###
`compile 'com.simplecityapps:recyclerview-fastscroll:1.0.11'`

###Usage###
You must use `FastScrollRecyclerView` as your base `RecyclerView`. See the sample project if you're having trouble.

Via xml:

     <com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
             android:id="@+id/recycler"
             android:layout_width="match_parent"
             android:layout_height="match_parent"
             app:fastScrollPopupBgColor="@color/colorAccent"
             app:fastScrollPopupTextColor="@android:color/primary_text_dark"
             app:fastScrollThumbColor="@color/colorAccent" />

To display the `FastScrollPopup`, your adapter must implement `FastScrollRecyclerView.SectionedAdapter` and override `getSectionName()`.

If you need to know when fast-scrolling starts or stops, you can attach an OnFatScrollStateChangedListener to the FastScrollRecyclerView.

#####Customisation#####

You can enable/disable autohide using the `fastScrollAutoHide` & `fastScrollAutoHideDelay` attributes in xml:

     <com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
             app:fastScrollAutoHide="true"
             app:fastScrollAutoHideDelay="1500"
             ...
             
Or programmatically via `setAutoHideDelay(int hideDelay)` and `setAutoHideEnabled(boolean autoHideEnabled)`

The popup background, popup text, popupbackground size, popup text size, track background and thumb color can all be styled via xml:

      <com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
                  app:fastScrollPopupBgColor="@color/colorAccent"
                  app:fastScrollPopupTextColor="@android:color/primary_text_dark"
                  app:fastScrollPopupTextSize="56sp"
                  app:fastScrollPopupBackgroundSize="88dp"
                  app:fastScrollThumbColor="@color/colorAccent"
                  app:fastScrollTrackColor="#1f000000" />
                  ...
Or programmatically via `setThumbColor(@ColorInt int color)`, `setTrackColor(@ColorInt int color)`, `setPopupBgColor(@ColorInt int color)`, `setPopupTextColor(@ColorInt int color)`& `setPopupTextSize(int size)`

###Updates###
v1.0.6
- The `FastScrollPopup` no longer requires your adapter to implement `SectionIndexer`, but rather `FastScrollRecyclerView.SectionedAdapter`, which is much easier to use.

v1.0.9 

- Updated gradle & dependencies
- Fixed crash when no adapter was set on the `RecyclerView`
- Fixed crash when `RecyclerView` children are null (`itemCount` is non zero, but `getChildAt(0)` returns null).

1.0.10

- Added methods/attributes to set popup background & text size
- Added method to set popup typeface
- Fixed issue where item decorations were drawn over the top of the popup (#18)
- Updated dependencies

1.0.11

- Added FastScrollStateChanged listener. Notifies when scrolling starts & stops.

###Licenses###

RecyclerView-FastScroll

     Copyright (C) 2016 Tim Malseed
   
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Launcher 3:
 
     Copyright (C) 2010 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
