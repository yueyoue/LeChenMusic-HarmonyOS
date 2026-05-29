import { router } from '@kit.ArkUI';
import { apiClient, Album, Song, Playlist, RadioStation } from '../common/SubsonicApi';

@Entry
@Component
struct HomePage {
  @State newestAlbums: Album[] = [];
  @State randomAlbums: Album[] = [];
  @State recentPlayed: Song[] = [];
  @State playlists: Playlist[] = [];
  @State radioStations: RadioStation[] = [];
  @State isLoading: boolean = true;

  aboutToAppear(): void {
    this.loadData();
  }

  async loadData(): Promise<void> {
    this.isLoading = true;
    try {
      this.newestAlbums = await apiClient.getAlbumList('newest', 20);
      this.randomAlbums = await apiClient.getAlbumList('random', 20);
      this.recentPlayed = await apiClient.getStarred();
      this.playlists = await apiClient.getPlaylists();
      this.radioStations = await apiClient.getRadioStations();
    } catch (e) {
      console.error('Load failed:', JSON.stringify(e));
    }
    this.isLoading = false;
  }

  build() {
    Column() {
      // Header
      Row() {
        Text('乐尘音乐')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .fontColor('#1DB954')
        Blank()
        Button() {
          SymbolGlyph($r('sys.symbol.gear'))
            .fontSize(20)
        }
        .width(36)
        .height(36)
        .backgroundColor(Color.Transparent)
        .onClick(() => { router.pushUrl({ url: 'pages/SettingsPage' }); })
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 8 })

      // Search bar
      TextInput({ placeholder: '搜索歌曲、歌手、专辑' })
        .width('100%')
        .height(44)
        .borderRadius(22)
        .backgroundColor('#282828')
        .margin({ left: 16, right: 16, bottom: 12 })
        .enabled(false)
        .onClick(() => { router.pushUrl({ url: 'pages/SearchPage' }); })

      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(48)
            .height(48)
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else {
        Scroll() {
          Column() {
            // 最新专辑
            if (this.newestAlbums.length > 0) {
              this.SectionTitle('🆕 最新专辑')
              this.AlbumRow(this.newestAlbums)
            }

            // 最近播放
            if (this.recentPlayed.length > 0) {
              this.SectionTitle('❤️ 收藏歌曲')
              ForEach(this.recentPlayed.slice(0, 10), (song: Song) => {
                this.SongItem(song)
              })
            }

            // 随机专辑
            if (this.randomAlbums.length > 0) {
              this.SectionTitle('🎲 随机专辑')
              this.AlbumRow(this.randomAlbums)
            }

            // 歌单
            if (this.playlists.length > 0) {
              this.SectionTitle('📋 歌单')
              ForEach(this.playlists, (playlist: Playlist) => {
                Row() {
                  Column() {
                    Text(playlist.name)
                      .fontSize(16)
                      .fontWeight(FontWeight.Medium)
                    Text(`${playlist.songCount} 首歌曲`)
                      .fontSize(13)
                      .fontColor('#999999')
                      .margin({ top: 4 })
                  }
                  .layoutWeight(1)
                  .alignItems(HorizontalAlign.Start)
                }
                .width('100%')
                .padding({ left: 16, right: 16, top: 12, bottom: 12 })
              })
            }

            // 电台
            if (this.radioStations.length > 0) {
              this.SectionTitle('📻 电台')
              ForEach(this.radioStations, (station: RadioStation) => {
                Row() {
                  Text(station.name)
                    .fontSize(16)
                }
                .width('100%')
                .padding({ left: 16, right: 16, top: 12, bottom: 12 })
              })
            }
          }
          .width('100%')
        }
        .layoutWeight(1)
      }
    }
    .width('100%')
    .height('100%')
  }

  @Builder SectionTitle(title: string) {
    Text(title)
      .fontSize(18)
      .fontWeight(FontWeight.Bold)
      .width('100%')
      .padding({ left: 16, right: 16, top: 16, bottom: 8 })
  }

  @Builder AlbumRow(albums: Album[]) {
    Scroll() {
      Row() {
        ForEach(albums, (album: Album) => {
          Column() {
            Image(apiClient.getCoverArtUrl(album.coverArt || album.id))
              .width(130)
              .height(130)
              .borderRadius(12)
              .objectFit(ImageFit.Cover)
            Text(album.title)
              .fontSize(14)
              .maxLines(1)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
              .width(130)
              .margin({ top: 6 })
            Text(album.artist || '未知歌手')
              .fontSize(12)
              .fontColor('#999999')
              .maxLines(1)
              .width(130)
          }
          .width(130)
          .margin({ right: 12 })
        })
      }
      .padding({ left: 16, right: 16 })
    }
    .scrollable(ScrollDirection.Horizontal)
    .width('100%')
  }

  @Builder SongItem(song: Song) {
    Row() {
      Image(apiClient.getCoverArtUrl(song.coverArt || song.id))
        .width(48)
        .height(48)
        .borderRadius(8)
        .objectFit(ImageFit.Cover)
      Column() {
        Text(song.title)
          .fontSize(15)
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
        Text(`${song.artist || '未知歌手'} - ${song.album || ''}`)
          .fontSize(13)
          .fontColor('#999999')
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .margin({ top: 4 })
      }
      .layoutWeight(1)
      .alignItems(HorizontalAlign.Start)
      .margin({ left: 12 })
    }
    .width('100%')
    .padding({ left: 16, right: 16, top: 8, bottom: 8 })
  }
}
