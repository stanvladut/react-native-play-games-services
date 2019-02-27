
import { NativeModules } from 'react-native';

const { RNPlayGamesServices } = NativeModules;

export default class PlayGamesServices {
  async test() {
    return await RNPlayGamesServices.test();
  }
}
