import { router } from '@kit.ArkUI';
import { apiClient, Song } from '../common/SubsonicApi';
import { media } from '@kit.MediaKit';
import { avSession } from '@kit.AVSessionKit';
import { image } from '@kit.ImageKit';
import { avSessionManager } from '../common/AVSessionManager';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'PlayerPage';

@Entry
@Component
struct PlayerPage {
  @State currentSong: Song | null = null;
  @State isPlaying: boolean = false;
  @State currentPosition: number = 0;
  @State duration: number = 0;
  @State lyrics: string = '';
  @State currentView: number = 0; // -1=lyrics, 0=cover, 1=similar

  private audioPlayer: media.AVPlayer | null = null;

  async aboutToAppear(): Promise<void> {
    // 创建 AVSession
    try {
      const context = getContext(this);
      await avSessionManager.createSession(context);

      // 设置回调 - 锁屏控件的按钮事件
      avSessionManager.setCallbacks({
        onPlay: () => {
          this.isPlaying = true;
          this.audioPlayer?.play();
          this.updatePlaybackState();
        },
        onPause: () => {
          this.isPlaying = false;
          this.audioPlayer?.pause();
          this.updatePlaybackState();
        },
        onNext: () => {
          // TODO: 播放下一曲
          hilog.info(0x0000, TAG, 'Next track requested');
        },
        onPrevious: () => {
          // TODO: 播放上一曲
          hilog.info(0x0000, TAG, 'Previous track requested');
        },
        onSeek: (position: number) => {
          if (this.audioPlayer) {
            this.audioPlayer.seek(position);
            this.currentPosition = position;
            this.updatePlaybackState();
          }
        }
      });

      await avSessionManager.activate();
      hilog.info(0x0000, TAG, 'AVSession initialized');
    } catch (err) {
      hilog.error(0x0000, TAG, `AVSession init failed: ${JSON.stringify(err)}`);
    }
  }

  async aboutToDisappear(): Promise<void> {
    // 销毁 AVSession
    await avSessionManager.destroy();

    // 释放播放器
    if (this.audioPlayer) {
      this.audioPlayer.release();
      this.audioPlayer = null;
    }
  }

  // 更新锁屏播放状态
  private updatePlaybackState(): void {
    avSessionManager.setPlaybackState({
      state: this.isPlaying ? avSession.PlaybackState.PLAYBACK_STATE_PLAY :
        avSession.PlaybackState.PLAYBACK_STATE_PAUSE,
      position: this.currentPosition,
      speed: 1.0
    });
  }

  // 更新锁屏歌曲信息
  private async updateMetadata(): Promise<void> {
    if (!this.currentSong) return;

    // 尝试获取封面图片的 PixelMap
    let coverPixelMap: image.PixelMap | undefined = undefined;
    try {
      const coverUrl = apiClient.getCoverArtUrl(this.currentSong.coverArt || this.currentSong.id);
      // 注意：实际项目中需要通过网络下载图片并解码为 PixelMap
      // 这里先不传封面，锁屏会显示默认图标
    } catch (err) {
      hilog.error(0x0000, TAG, `Failed to load cover: ${JSON.stringify(err)}`);
    }

    await avSessionManager.setMetadata(
      this.currentSong.title,
      this.currentSong.artist || '未知歌手',
      this.currentSong.album || '未知专辑',
      this.currentSong.duration * 1000, // 转换为毫秒
      coverPixelMap
    );
  }

  build() {
    Column() {
      // Header
      Row() {
        Button() {
          SymbolGlyph($r('sys.symbol.chevron_left'))
            .fontSize(24)
        }
        .width(36)
        .height(36)
        .backgroundColor(Color.Transparent)
        .onClick(() => { router.back(); })

        Text('正在播放')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Button() {
          SymbolGlyph($r('sys.symbol.ellipsis'))
            .fontSize(24)
        }
        .width(36)
        .height(36)
        .backgroundColor(Color.Transparent)
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 8 })

      if (this.currentSong === null) {
        Column() {
          Text('未在播放')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else {
        // Cover art
        Column() {
          if (this.currentView === -1) {
            // Lyrics
            Column() {
              Text(this.lyrics || '暂无歌词')
                .fontSize(16)
                .lineHeight(28)
            }
            .width('100%')
            .layoutWeight(1)
            .padding(24)
            .backgroundColor('#282828')
            .borderRadius(16)
          } else if (this.currentView === 0) {
            // Cover
            Image(apiClient.getCoverArtUrl(this.currentSong!.coverArt || this.currentSong!.id))
              .width(280)
              .height(280)
              .borderRadius(16)
              .objectFit(ImageFit.Cover)
              .shadow({ radius: 20, color: '#40000000', offsetY: 8 })
          } else {
            // Similar songs placeholder
            Column() {
              Text('相似歌曲')
                .fontSize(18)
                .fontWeight(FontWeight.Bold)
              Text('加载中...')
                .fontSize(14)
                .fontColor('#999999')
                .margin({ top: 16 })
            }
            .width('100%')
            .layoutWeight(1)
            .padding(24)
            .backgroundColor('#282828')
            .borderRadius(16)
          }
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
        .gesture(
          PanGesture({ direction: PanDirection.Horizontal })
            .onActionStart((event: GestureEvent) => {
              if (event.offsetX < -50) {
                this.currentView = 1;
              } else if (event.offsetX > 50) {
                this.currentView = -1;
              }
            })
        )

        // Song info
        Row() {
          Column() {
            Text(this.currentSong!.title)
              .fontSize(20)
              .fontWeight(FontWeight.Bold)
              .maxLines(1)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
            Text(`${this.currentSong!.artist || '未知歌手'} · ${this.currentSong!.album || '未知专辑'}`)
              .fontSize(14)
              .fontColor('#999999')
              .maxLines(1)
              .margin({ top: 4 })
          }
          .layoutWeight(1)
          .alignItems(HorizontalAlign.Start)

          Button() {
            SymbolGlyph(this.currentSong!.starred ?
              $r('sys.symbol.heart_fill') : $r('sys.symbol.heart'))
              .fontSize(24)
              .fontColor(this.currentSong!.starred ? '#1DB954' : '#999999')
          }
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            if (this.currentSong!.starred) {
              apiClient.unstarSong(this.currentSong!.id);
            } else {
              apiClient.starSong(this.currentSong!.id);
            }
          })
        }
        .width('100%')
        .padding({ left: 24, right: 24, top: 16 })

        // Progress
        Slider({
          value: this.currentPosition,
          min: 0,
          max: this.duration || 1,
          step: 1
        })
          .width('100%')
          .padding({ left: 24, right: 24, top: 12 })

        Row() {
          Text(this.formatTime(this.currentPosition))
            .fontSize(12)
            .fontColor('#999999')
          Blank()
          Text(this.formatTime(this.duration))
            .fontSize(12)
            .fontColor('#999999')
        }
        .width('100%')
        .padding({ left: 24, right: 24 })

        // Controls
        Row() {
          Button() {
            SymbolGlyph($r('sys.symbol.repeat'))
              .fontSize(22)
          }
          .backgroundColor(Color.Transparent)

          Button() {
            SymbolGlyph($r('sys.symbol.backward_fill'))
              .fontSize(32)
          }
          .backgroundColor(Color.Transparent)

          Button() {
            SymbolGlyph(this.isPlaying ?
              $r('sys.symbol.pause_fill') : $r('sys.symbol.play_fill'))
              .fontSize(40)
          }
          .width(64)
          .height(64)
          .shape({ borderRadius: 32 })
          .backgroundColor('#1DB954')
          .onClick(() => {
            this.isPlaying = !this.isPlaying;
            // 同步更新 AVSession 播放状态
            this.updatePlaybackState();
          })

          Button() {
            SymbolGlyph($r('sys.symbol.forward_fill'))
              .fontSize(32)
          }
          .backgroundColor(Color.Transparent)

          Button() {
            SymbolGlyph($r('sys.symbol.list_bullet'))
              .fontSize(22)
          }
          .backgroundColor(Color.Transparent)
        }
        .width('100%')
        .justifyContent(FlexAlign.SpaceAround)
        .padding({ top: 16, bottom: 24 })
      }
    }
    .width('100%')
    .height('100%')
  }

  formatTime(ms: number): string {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  }
}
