# RecyclerView-FastScroll
A simple FastScroller for Android's RecyclerView.

Supports vertical RecyclerViews using either `LinearLayoutManager` or `GridLayoutManager` (including multiple spans).

The style is loosely based on the ListView FastScroller from whatever the last version of Lollipop was. This library borrows heavily from [Google's Launcher3 FastScroller](https://android.googlesource.com/platform/packages/apps/Launcher3/)

###Usage###
You must to use `FastScrollRecyclerView` as your base `RecyclerView`. 

If your RecyclerView Adapter implements a SectionIndexer, the FastScrollPopup will display the text returned from that implementation.

![Screenshot](https://github.com/timusus/RecyclerView-FastScroll/blob/master/screenshot.jpg)

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
