import { router } from '@kit.ArkUI';
import { apiClient } from '../common/SubsonicApi';

@Entry
@Component
struct Index {
  @State currentIndex: number = 0;

  private tabs: TabBarOption[] = [
    new TabBarOption({ icon: $r('sys.media.ohos_ic_public_home'), title: '首页' }),
    new TabBarOption({ icon: $r('sys.media.ohos_ic_public_heart'), title: '收藏' }),
    new TabBarOption({ icon: $r('sys.media.ohos_ic_public_search'), title: '搜索' }),
    new TabBarOption({ icon: $r('sys.media.ohos_ic_public_people'), title: '歌手' }),
    new TabBarOption({ icon: $r('sys.media.ohos_ic_public_album'), title: '专辑' }),
  ];

  build() {
    Tabs({ barPosition: BarPosition.End, index: this.currentIndex }) {
      TabContent() {
        HomePage()
      }.tabBar(this.tabs[0])

      TabContent() {
        Column() {
          Text('收藏')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
      }.tabBar(this.tabs[1])

      TabContent() {
        SearchPage()
      }.tabBar(this.tabs[2])

      TabContent() {
        Column() {
          Text('歌手')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
      }.tabBar(this.tabs[3])

      TabContent() {
        Column() {
          Text('专辑')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
      }.tabBar(this.tabs[4])
    }
    .barWidth('100%')
    .barHeight(56)
    .scrollable(false)
    .animationDuration(0)
  }
}
