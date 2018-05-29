# Redspace Android-Utils

Redspace android-utils project is a collection of modules that provide useful functionality for Android developers.


## Getting Started

This project uses [JITpack](https://jitpack.io/#theREDspace/android-utils/) for packaging, which provides on-the-fly build capability to get a release, specific module, or specific tag/commit into your project.

Add the JITpack repository to your root `build.gradle` file:
```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

Add dependency entries for each module to each of your interested modules' `build.gradle` files:
```
dependencies {
	implementation 'com.github.theREDspace.android-utils:${MODULE}:(release/tag/branch/commit)
}
```

Note: do not add the jitpack.io repository under buildscript.

Each directory in the project root (except `gradle`) is a module that can be imported.  Each module contains markdown with individual usage instructions.


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.


## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/theREDspace/android-utils/tags).

## Authors

See also the list of [contributors](https://github.com/theREDspace/android-utils/contributors) who participated in this project.

## License

This project is licensed under the BSD 3-clause License - see the [LICENSE.md](LICENSE.md) file for details

