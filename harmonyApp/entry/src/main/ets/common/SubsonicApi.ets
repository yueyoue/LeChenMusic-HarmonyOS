// Subsonic API Client for HarmonyOS
import { http } from '@kit.NetworkKit';

export interface SubsonicConfig {
  serverUrl: string;
  username: string;
  password: string;
  token: string;
  salt: string;
}

export interface Song {
  id: string;
  title: string;
  artist: string;
  album: string;
  coverArt: string;
  duration: number;
  starred: string;
}

export interface Album {
  id: string;
  title: string;
  artist: string;
  coverArt: string;
  songCount: number;
}

export interface Playlist {
  id: string;
  name: string;
  songCount: number;
}

export interface RadioStation {
  id: string;
  name: string;
  streamUrl: string;
}

export interface SearchResult {
  song: Song[];
  album: Album[];
  artist: string[];
}

export class SubsonicApiClient {
  private config: SubsonicConfig = {
    serverUrl: '',
    username: '',
    password: '',
    token: '',
    salt: ''
  };

  configure(server: string, username: string, password: string): void {
    this.config.serverUrl = server.replace(/\/+$/, '');
    this.config.username = username;
    this.config.password = password;
    this.config.salt = this.generateSalt();
    this.config.token = this.generateToken(password, this.config.salt);
  }

  private generateSalt(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < 12; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }

  private generateToken(password: string, salt: string): string {
    // Note: In HarmonyOS, use @ohos.security.cryptoMessageDigest for MD5
    // This is a placeholder - actual implementation needs crypto module
    return password + salt; // TODO: implement MD5 hash
  }

  private buildUrl(endpoint: string, params?: Record<string, string>): string {
    let url = `${this.config.serverUrl}/rest/${endpoint}?`;
    url += `u=${this.config.username}&`;
    url += `t=${this.config.token}&`;
    url += `s=${this.config.salt}&`;
    url += `v=1.16.1&c=LeChenMusic&f=json&`;
    if (params) {
      for (const key in params) {
        url += `${key}=${encodeURIComponent(params[key])}&`;
      }
    }
    return url.slice(0, -1);
  }

  async request(endpoint: string, params?: Record<string, string>): Promise<Object> {
    const url = this.buildUrl(endpoint, params);
    try {
      const response = await http.createHttp().request(url, {
        method: http.RequestMethod.GET,
        expectDataType: http.HttpDataType.OBJECT,
      });
      const data = response.result as Record<string, Object>;
      const subsonicResp = data['subsonic-response'] as Record<string, Object>;
      if (subsonicResp['status'] === 'ok') {
        return subsonicResp;
      }
      throw new Error((subsonicResp['error'] as Record<string, string>)?.['message'] ?? 'Unknown error');
    } catch (err) {
      throw new Error(`Request failed: ${JSON.stringify(err)}`);
    }
  }

  async ping(): Promise<boolean> {
    try {
      await this.request('ping');
      return true;
    } catch {
      return false;
    }
  }

  async getAlbumList(type: string = 'newest', size: number = 20): Promise<Album[]> {
    const resp = await this.request('getAlbumList2', {
      type,
      size: size.toString()
    }) as Record<string, Object>;
    const albumList = resp['albumList2'] as Record<string, Object>;
    if (!albumList || !albumList['album']) return [];
    const albums = albumList['album'] as Record<string, string>[];
    return albums.map(a => ({
      id: a['id'] ?? '',
      title: a['title'] ?? a['name'] ?? '',
      artist: a['artist'] ?? '',
      coverArt: a['coverArt'] ?? '',
      songCount: Number(a['songCount'] ?? 0)
    }));
  }

  async search(query: string): Promise<SearchResult> {
    const resp = await this.request('search3', {
      query,
      songCount: '20',
      albumCount: '10',
      artistCount: '10'
    }) as Record<string, Object>;
    const result = resp['searchResult3'] ?? resp['searchResult2'] as Record<string, Object>;
    return {
      song: ((result?.['song'] as Record<string, string>[]) ?? []).map(s => ({
        id: s['id'] ?? '',
        title: s['title'] ?? '',
        artist: s['artist'] ?? '',
        album: s['album'] ?? '',
        coverArt: s['coverArt'] ?? '',
        duration: Number(s['duration'] ?? 0),
        starred: s['starred'] ?? ''
      })),
      album: ((result?.['album'] as Record<string, string>[]) ?? []).map(a => ({
        id: a['id'] ?? '',
        title: a['title'] ?? a['name'] ?? '',
        artist: a['artist'] ?? '',
        coverArt: a['coverArt'] ?? '',
        songCount: Number(a['songCount'] ?? 0)
      })),
      artist: ((result?.['artist'] as Record<string, string>[]) ?? []).map(a => a['name'] ?? '')
    };
  }

  async getPlaylists(): Promise<Playlist[]> {
    const resp = await this.request('getPlaylists') as Record<string, Object>;
    const playlists = resp['playlists'] as Record<string, Object>;
    if (!playlists || !playlists['playlist']) return [];
    return (playlists['playlist'] as Record<string, string>[]).map(p => ({
      id: p['id'] ?? '',
      name: p['name'] ?? '',
      songCount: Number(p['songCount'] ?? 0)
    }));
  }

  async getRadioStations(): Promise<RadioStation[]> {
    const resp = await this.request('getInternetRadioStations') as Record<string, Object>;
    const wrapper = resp['internetRadioStations'] as Record<string, Object>;
    if (!wrapper || !wrapper['internetRadioStation']) return [];
    return (wrapper['internetRadioStation'] as Record<string, string>[]).map(r => ({
      id: r['id'] ?? '',
      name: r['name'] ?? '',
      streamUrl: r['streamUrl'] ?? ''
    }));
  }

  async getStarred(): Promise<Song[]> {
    const resp = await this.request('getStarred2') as Record<string, Object>;
    const starred = resp['starred2'] as Record<string, Object>;
    if (!starred || !starred['song']) return [];
    return (starred['song'] as Record<string, string>[]).map(s => ({
      id: s['id'] ?? '',
      title: s['title'] ?? '',
      artist: s['artist'] ?? '',
      album: s['album'] ?? '',
      coverArt: s['coverArt'] ?? '',
      duration: Number(s['duration'] ?? 0),
      starred: s['starred'] ?? ''
    }));
  }

  async starSong(id: string): Promise<void> {
    await this.request('star', { id });
  }

  async unstarSong(id: string): Promise<void> {
    await this.request('unstar', { id });
  }

  async scrobble(id: string): Promise<void> {
    await this.request('scrobble', { id, submission: 'true' });
  }

  getStreamUrl(id: string): string {
    return this.buildUrl('stream', { id });
  }

  getCoverArtUrl(id: string): string {
    return this.buildUrl('getCoverArt', { id });
  }

  getServerUrl(): string {
    return this.config.serverUrl;
  }

  getUsername(): string {
    return this.config.username;
  }
}

export const apiClient = new SubsonicApiClient();
