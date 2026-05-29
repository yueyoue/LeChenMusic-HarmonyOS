import { router } from '@kit.ArkUI';
import { apiClient } from '../common/SubsonicApi';

@Entry
@Component
struct SettingsPage {
  @State selectedCache: number = 2;

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

        Text('设置')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 8 })

      Scroll() {
        Column() {
          // 音乐缓存
          Text('音乐缓存')
            .fontSize(14)
            .fontColor('#1DB954')
            .width('100%')
            .padding({ left: 16, top: 16, bottom: 8 })

          Row() {
            ForEach([2, 4, 8, 16], (size: number) => {
              Button(`${size}GB`)
                .height(36)
                .borderRadius(18)
                .backgroundColor(this.selectedCache === size ? '#1DB954' : '#282828')
                .fontColor(this.selectedCache === size ? Color.White : '#999999')
                .margin({ right: 8 })
                .onClick(() => { this.selectedCache = size; })
            })
          }
          .width('100%')
          .padding({ left: 16, right: 16 })

          Divider().margin({ top: 16, bottom: 8 })

          // 存储空间管理
          Text('存储空间管理')
            .fontSize(14)
            .fontColor('#1DB954')
            .width('100%')
            .padding({ left: 16, top: 8, bottom: 8 })

          Row() {
            Text('音乐文件缓存')
              .fontSize(16)
              .layoutWeight(1)
            Text('计算中...')
              .fontSize(14)
              .fontColor('#999999')
          }
          .width('100%')
          .padding({ left: 16, right: 16, top: 12, bottom: 12 })

          Button('清除缓存')
            .width('100%')
            .height(44)
            .borderRadius(22)
            .backgroundColor(Color.Transparent)
            .border({ width: 1, color: '#666666' })
            .fontColor(Color.White)
            .margin({ left: 16, right: 16, top: 8 })

          Divider().margin({ top: 16, bottom: 8 })

          // 服务器信息
          Text('服务器信息')
            .fontSize(14)
            .fontColor('#1DB954')
            .width('100%')
            .padding({ left: 16, top: 8, bottom: 8 })

          this.InfoRow('服务器地址', apiClient.getServerUrl())
          this.InfoRow('用户名', apiClient.getUsername())

          Divider().margin({ top: 16, bottom: 8 })

          // 退出登录
          Button('退出登录')
            .width('100%')
            .height(44)
            .borderRadius(22)
            .backgroundColor(Color.Transparent)
            .border({ width: 1, color: '#FF4444' })
            .fontColor('#FF4444')
            .margin({ left: 16, right: 16, top: 16 })
            .onClick(() => {
              router.replaceUrl({ url: 'pages/LoginPage' });
            })
        }
        .width('100%')
      }
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')
  }

  @Builder InfoRow(label: string, value: string) {
    Row() {
      Text(label)
        .fontSize(16)
        .layoutWeight(1)
      Text(value)
        .fontSize(14)
        .fontColor('#999999')
    }
    .width('100%')
    .padding({ left: 16, right: 16, top: 12, bottom: 12 })
  }
}
