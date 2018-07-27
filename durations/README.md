# Durations -- Typesafe Time Units

Author: Alex Hart ([exallium](https://www.github.com/exallium))

Durations splits different time divisions into explicit subtypes of a sealed class `Duration`

E.G.

```

interface MyApi {
    // We can declare here that we require a duration of some kind.
    fun action(duration: Duration)
}

fun run(myApi: MyApi) {

  // A dev can create a duration using some simple methods
  val ms = milliseconds(1000) // Returns a Duration.Milliseconds
  val s = seconds(10) // Returns a Duration.Seconds

  myApi.action(s + ms) // Works! Action will be handed 11000ms
}

```
