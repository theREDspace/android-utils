# Redspace Android-Utils

Redspace android-utils project is a collection of modules that provide useful functionality.

## Getting Started

This project uses jitpack. To get a release, specific module, or specific tag/commit into your project.
To see a list check [jitpack](https://jitpack.io/#theREDspace/android-utils/)

you will need add the jitpack.io repository:
```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
and:
```
dependencies {
	implementation 'com.github.theREDspace:android-utils:(release/tag/branch/commit)
}
```
or for specific modules
```
dependencies {
	implementation 'com.github.theREDspace.android-utils:MODULE:(release/tag/branch/commit)
}
```
Note: do not add the jitpack.io repository under buildscript



## Contributing

Please read [CONTRIBUTING.md](contributing.md) for details on our code of conduct, and the process for submitting pull requests to us.


## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/theREDspace/android-utils/tags).

## Authors

See also the list of [contributors](https://github.com/theREDspace/android-utils/contributors) who participated in this project.

## License

This project is licensed under the BSD 3-clause License - see the [LICENSE.md](LICENSE.md) file for details

