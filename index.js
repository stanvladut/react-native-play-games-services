
import { NativeModules } from 'react-native';

const { RNPlayGamesServices } = NativeModules;

export default class PlayGamesServices {
  initializePromise;

  constructor() {
    if (!RNPlayGamesServices) {
      console.error('No module linked');
    }

    this.initializePromise = RNPlayGamesServices.init();
  }

  async signIn() {
    await this.initializePromise;
    return await RNPlayGamesServices.signIn();
  }

  async isSignedIn() {
    await this.initializePromise;
    return await RNPlayGamesServices.isSignedIn();
  }

  async signInSilently() {
    await this.initializePromise;
    return await RNPlayGamesServices.signInSilently();
  }

  async getLastSignedInAccount() {
    await this.initializePromise;
    return await RNPlayGamesServices.getLastSignedInAccount();
  }

  async showAchievements() {
    await this.initializePromise;
    return await RNPlayGamesServices.showAchievements();
  }
}