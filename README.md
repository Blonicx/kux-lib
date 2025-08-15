# kux-lib

[![GitHub stars](https://img.shields.io/github/stars/Blonicx/kux-lib?style=social)](https://github.com/Blonicx/kux-lib/stargazers)  
[![GitHub forks](https://img.shields.io/github/forks/Blonicx/kux-lib?style=social)](https://github.com/Blonicx/kux-lib/network)  
[![GitHub issues](https://img.shields.io/github/issues/Blonicx/kux-lib)](https://github.com/Blonicx/kux-lib/issues)

## What is kux-lib?

**kux** stands for **Kotlin-Updating-X**.  
It is a library designed to simplify downloading Minecraft library dependencies for Paper plugins automatically at runtime.
You can download it from [Modrinth](https://modrinth.com/plugin/kux-lib)

Currently, it supports downloading dependencies from:
- [Modrinth](https://modrinth.com)

## How to use kux-lib

### For Users
**There is a chance it will be installed by another plugin that uses the lib!**

Download the latest version of **kux-lib** and place it in your server's `plugins` folder. It will handle downloading required dependencies for your Paper plugins automatically.

### For Developers

Add the following to your `build.gradle.kts` to include **kux-lib** as a dependency:

```groovy
repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    implementation("maven.modrinth:kux-lib:{version}")
}
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

> Made with ❤️ by Blonicx