# Durations -- Typesafe Time Units

Author: Alex Hart (exallium)

Durations splits different time divisions into explicit subtypes of a sealed class `Duration`

E.G.

```

interface MyApi {

    // Function's signature explicitly declares that it will utilize milliseconds
    fun action(milliseconds: Duration.Milliseconds)

    // Function's signature explicitly declares that it will handle conversion
    fun action2(duration: Duration)
}

fun run(myApi: MyApi) {

  val ms = milliseconds(1000) // Returns a Duration.Milliseconds
  val s = seconds(10) // Returns a Duration.Seconds

  myApi.action(ms) // Works great!

  // myApi.action(s) // Error!

  myApi.action2(s + ms) // Works! + and - will return a Duration
}


```
