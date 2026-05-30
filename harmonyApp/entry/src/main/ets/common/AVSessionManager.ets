import { avSession } from '@kit.AVSessionKit';
import { image } from '@kit.ImageKit';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'AVSessionManager';

export class AVSessionManager {
  private session: avSession.AVSession | null = null;
  private isCreated: boolean = false;

  // 回调函数
  private onPlayCallback: (() => void) | null = null;
  private onPauseCallback: (() => void) | null = null;
  private onNextCallback: (() => void) | null = null;
  private onPreviousCallback: (() => void) | null = null;
  private onSeekCallback: ((position: number) => void) | null = null;

  async createSession(context: object): Promise<void> {
    if (this.isCreated) {
      return;
    }
    try {
      this.session = await avSession.createAVSession(context, 'LeChenMusicSession', 'audio');
      hilog.info(0x0000, TAG, 'AVSession created successfully');

      this.registerCallbacks();
      this.isCreated = true;
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to create AVSession: ${JSON.stringify(err)}`);
    }
  }

  private registerCallbacks(): void {
    if (!this.session) return;

    // 注册播放控制回调
    this.session.on('play', () => {
      hilog.info(0x0000, TAG, 'Lock screen play pressed');
      this.onPlayCallback?.();
    });

    this.session.on('pause', () => {
      hilog.info(0x0000, TAG, 'Lock screen pause pressed');
      this.onPauseCallback?.();
    });

    this.session.on('playNext', () => {
      hilog.info(0x0000, TAG, 'Lock screen next pressed');
      this.onNextCallback?.();
    });

    this.session.on('playPrevious', () => {
      hilog.info(0x0000, TAG, 'Lock screen previous pressed');
      this.onPreviousCallback?.();
    });

    this.session.on('seek', (time: number) => {
      hilog.info(0x0000, TAG, `Lock screen seek to: ${time}`);
      this.onSeekCallback?.(time);
    });

    this.session.on('stop', () => {
      hilog.info(0x0000, TAG, 'Lock screen stop pressed');
      this.onPauseCallback?.();
    });
  }

  setCallbacks(callbacks: {
    onPlay?: () => void;
    onPause?: () => void;
    onNext?: () => void;
    onPrevious?: () => void;
    onSeek?: (position: number) => void;
  }): void {
    this.onPlayCallback = callbacks.onPlay ?? null;
    this.onPauseCallback = callbacks.onPause ?? null;
    this.onNextCallback = callbacks.onNext ?? null;
    this.onPreviousCallback = callbacks.onPrevious ?? null;
    this.onSeekCallback = callbacks.onSeek ?? null;
  }

  async setMetadata(title: string, artist: string, album: string, duration: number,
    coverPixelMap?: image.PixelMap): Promise<void> {
    if (!this.session) return;

    try {
      const metadata: avSession.AVMediaDescription = {
        title: title,
        artist: artist,
        album: album,
        duration: duration,
        mediaImage: coverPixelMap ?? undefined
      };
      await this.session.setAVMetadata(metadata);
      hilog.info(0x0000, TAG, `Metadata set: ${title} - ${artist}`);
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to set metadata: ${JSON.stringify(err)}`);
    }
  }

  async setPlaybackState(state: {
    state: avSession.PlaybackState;
    position?: number;
    speed?: number;
  }): Promise<void> {
    if (!this.session) return;

    try {
      const playbackState: avSession.AVPlaybackState = {
        state: state.state,
        position: state.position ?? 0,
        speed: state.speed ?? 1.0
      };
      await this.session.setAVPlaybackState(playbackState);
      hilog.info(0x0000, TAG, `Playback state set: ${state.state}`);
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to set playback state: ${JSON.stringify(err)}`);
    }
  }

  async activate(): Promise<void> {
    if (!this.session) return;
    try {
      await this.session.activate();
      hilog.info(0x0000, TAG, 'AVSession activated');
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to activate session: ${JSON.stringify(err)}`);
    }
  }

  async deactivate(): Promise<void> {
    if (!this.session) return;
    try {
      await this.session.deactivate();
      hilog.info(0x0000, TAG, 'AVSession deactivated');
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to deactivate session: ${JSON.stringify(err)}`);
    }
  }

  async destroy(): Promise<void> {
    if (!this.session) return;
    try {
      this.session.off('play');
      this.session.off('pause');
      this.session.off('playNext');
      this.session.off('playPrevious');
      this.session.off('seek');
      this.session.off('stop');
      await this.session.destroy();
      this.session = null;
      this.isCreated = false;
      hilog.info(0x0000, TAG, 'AVSession destroyed');
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to destroy session: ${JSON.stringify(err)}`);
    }
  }
}

export const avSessionManager = new AVSessionManager();
