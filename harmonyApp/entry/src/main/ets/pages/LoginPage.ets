import { router } from '@kit.ArkUI';
import { apiClient } from '../common/SubsonicApi';

@Entry
@Component
struct LoginPage {
  @State serverUrl: string = 'http://j.tthsdd.top:4533';
  @State username: string = 'huang';
  @State password: string = '';
  @State isLoading: boolean = false;
  @State errorMsg: string = '';
  @State passwordVisible: boolean = false;

  build() {
    Column() {
      // Logo
      Text('🎵 乐尘音乐')
        .fontSize(36)
        .fontWeight(FontWeight.Bold)
        .fontColor('#1DB954')

      Text('Navidrome 客户端')
        .fontSize(16)
        .fontColor('#999999')
        .margin({ top: 8 })

      // Form
      Column() {
        TextInput({ placeholder: '服务器地址', text: this.serverUrl })
          .onChange((value: string) => { this.serverUrl = value; })
          .width('100%')
          .height(48)
          .margin({ bottom: 16 })

        TextInput({ placeholder: '用户名', text: this.username })
          .onChange((value: string) => { this.username = value; })
          .width('100%')
          .height(48)
          .margin({ bottom: 16 })

        TextInput({ placeholder: '密码', text: this.password })
          .type(this.passwordVisible ? InputType.Normal : InputType.Password)
          .onChange((value: string) => { this.password = value; })
          .width('100%')
          .height(48)
          .margin({ bottom: 32 })

        Button(this.isLoading ? '登录中...' : '登录')
          .width('100%')
          .height(48)
          .backgroundColor('#1DB954')
          .fontColor(Color.White)
          .fontSize(16)
          .borderRadius(24)
          .enabled(!this.isLoading && this.serverUrl.length > 0 &&
            this.username.length > 0 && this.password.length > 0)
          .onClick(() => { this.doLogin(); })

        if (this.errorMsg.length > 0) {
          Text(this.errorMsg)
            .fontColor('#FF4444')
            .fontSize(14)
            .margin({ top: 16 })
        }
      }
      .width('100%')
      .padding({ left: 32, right: 32, top: 48 })
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .padding(32)
  }

  async doLogin(): Promise<void> {
    this.isLoading = true;
    this.errorMsg = '';
    try {
      apiClient.configure(this.serverUrl, this.username, this.password);
      const ok = await apiClient.ping();
      if (ok) {
        router.replaceUrl({ url: 'pages/Index' });
      } else {
        this.errorMsg = '登录失败，请检查服务器地址和账号密码';
      }
    } catch (e) {
      this.errorMsg = `连接失败: ${JSON.stringify(e)}`;
    }
    this.isLoading = false;
  }
}
