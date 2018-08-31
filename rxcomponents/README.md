# RxComponents

Author: Alex Hart ([exallium](https://www.github.com/exallium))

Rx Components provides some RxJava facilities for working with different Android components.

## Motivation

The primary motivation for this microlib is dealing with `getActivity` within `Fragment`.  The way
this mechanism works under the hood appears to be confusing and slightly brittle.  This microlib
provides a container to provide the Activity when it is attached, and clear it when the Fragment is
destroyed.
