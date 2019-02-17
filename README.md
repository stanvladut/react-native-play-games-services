
# react-native-play-games-services

## Getting started

`$ npm install react-native-play-games-services --save`

### Mostly automatic installation

`$ react-native link react-native-play-games-services`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.fourseconds.playgamesservices.RNPlayGamesServicesPackage;` to the imports at the top of the file
  - Add `new RNPlayGamesServicesPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-play-games-services'
  	project(':react-native-play-games-services').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-play-games-services/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-play-games-services')
  	```


## Usage
```javascript
import RNPlayGamesServices from 'react-native-play-games-services';

// TODO: What to do with the module?
RNPlayGamesServices;
```
  