### Changelog

2.0.1

- Fixed scrolling smoothness regression
- Updated thumb style to more closely match Google's recent changes

2.0.0

- Version updated due to binary incompatible change in 1.0.21 (AndroidX migration)
- Improved vertical alignment of popup text
- Renamed `FastScrollerPopupPosition` to `PopupPosition`

1.0.21 (Backwards incompatible change & incorrect version)

- Migrated to AndroidX
- LayoutManager.reverseLayout(true) is now supported
- Fixed an issue where popup text wasn't vertically centered
- Respect top and bottom padding of RecyclerView when drawing thumb/track

1.0.20

- Crash fix

1.0.19

- Update dependencies, compile SDK to Android 28
- Fixed an issue where thumb colour was not persisted after setThumbColor()
- Fixed a compilation issue on Android 28
- Fixed 'scrolling jitter' when using items with varying height

1.0.18

- Added method `showScrollbar()` to `FastScrollRecyclerView`
- Added method `setThumbInactiveColor(ColorInt)` to `FastScroller`
- Renamed `setThumbInactiveColor(boolean) to enableThumbInactiveColor(boolean)`.
- Renamed `setStateChangeListener` to `setOnFastScrollStateChangedListener`
- Renamed `setThumbEnabled` to `setFastScrollEnabled`
- Set `enableThumbInactiveColor` to true by default
- Pass `ViewHolder` into `MeasurableAdapter` to ease item height calculations
- Dependency updates

1.0.17

- `MeasurableAdapter` tweaks
- Improved sample, better demonstration of `MeasurableAdapter`
- Add option to enable/disable fastscroll (via `fastScrollThumbEnabled` property)
- Dependency updates

1.0.16

- Added support for varying row heights via `MeasurableAdapter`

1.0.15

- Fixed an issue preventing the view from rendering in the Android Studio 'design' panel.
- Updated dependencies

1.0.14

- Make it possible to set popup position programmatically

1.0.13

- Added option to position the FastScroll-Popup in the center of the RecyclerView (rather than tracking adjacent to the FastScroll thumb)

1.0.12

- Fixed a Proguard Obfuscation issue preventing animations from running
- Fixed an issue where popup background color ignored alpha channel
- Added support for item decorations

1.0.11

- Added FastScrollStateChanged listener. Notifies when scrolling starts & stops.

1.0.10

- Added methods/attributes to set popup background & text size
- Added method to set popup typeface
- Fixed issue where item decorations were drawn over the top of the popup (#18)
- Updated dependencies

v1.0.9

- Updated gradle & dependencies
- Fixed crash when no adapter was set on the `RecyclerView`
- Fixed crash when `RecyclerView` children are null (`itemCount` is non zero, but `getChildAt(0)` returns null).

v1.0.6
- The `FastScrollPopup` no longer requires your adapter to implement `SectionIndexer`, but rather `FastScrollRecyclerView.SectionedAdapter`, which is much easier to use.