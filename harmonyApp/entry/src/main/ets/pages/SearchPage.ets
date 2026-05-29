import { router } from '@kit.ArkUI';
import { apiClient, SearchResult, Song } from '../common/SubsonicApi';

@Entry
@Component
struct SearchPage {
  @State query: string = '';
  @State isLoading: boolean = false;
  @State results: SearchResult | null = null;

  build() {
    Column() {
      // Header
      Row() {
        Text('搜索')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 8 })

      // Search input
      Row() {
        TextInput({ placeholder: '搜索歌曲、歌手、专辑', text: this.query })
          .onChange((value: string) => { this.query = value; })
          .layoutWeight(1)
          .height(44)

        Button('搜索')
          .height(44)
          .backgroundColor('#1DB954')
          .fontColor(Color.White)
          .margin({ left: 8 })
          .onClick(() => { this.doSearch(); })
      }
      .width('100%')
      .padding({ left: 16, right: 16, bottom: 12 })

      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .width(48)
            .height(48)
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      } else if (this.results !== null) {
        Scroll() {
          Column() {
            if (this.results!.song.length > 0) {
              Text('歌曲')
                .fontSize(16)
                .fontWeight(FontWeight.Bold)
                .width('100%')
                .padding({ left: 16, right: 16, top: 12, bottom: 8 })
              ForEach(this.results!.song, (song: Song) => {
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
                      .margin({ top: 4 })
                  }
                  .layoutWeight(1)
                  .alignItems(HorizontalAlign.Start)
                  .margin({ left: 12 })
                }
                .width('100%')
                .padding({ left: 16, right: 16, top: 8, bottom: 8 })
              })
            }
          }
          .width('100%')
        }
        .layoutWeight(1)
      } else {
        Column() {
          Text('输入关键词搜索')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
      }
    }
    .width('100%')
    .height('100%')
  }

  async doSearch(): Promise<void> {
    if (this.query.length === 0) return;
    this.isLoading = true;
    try {
      this.results = await apiClient.search(this.query);
    } catch (e) {
      console.error('Search failed:', JSON.stringify(e));
    }
    this.isLoading = false;
  }
}
