###Available APIS

- SMS Sending API
- LBS API
- Subscription API
- USSD API (To be implemented)


### Intergration

**Gradle**
Step 1. Add the JitPack repository to your build file; add it in your root build.gradle at the end of repositories:
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency
```java
dependencies {
	        implementation 'com.github.tsuresh:Ideamart-Android:master'
	}
```


**Maven**
Step 1. Add the JitPack repository to your build file
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
	    <groupId>com.github.tsuresh</groupId>
	    <artifactId>Ideamart-Android</artifactId>
	    <version>master</version>
	</dependency>
```
