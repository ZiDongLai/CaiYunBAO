# 公开 GitHub 前安全检查

在把仓库从 Private 改成 Public 前逐项确认：

- [ ] 没有 `.jks` / `.keystore`
- [ ] 没有签名密码
- [ ] 没有 `keystore.properties`
- [ ] 没有 `local.properties`
- [ ] 没有 API Key / Token / Secret
- [ ] 没有服务器 root 密码
- [ ] 没有 SSH 私钥
- [ ] 没有数据库账号密码
- [ ] 没有华为云 AccessKey / SecretKey
- [ ] 没有个人身份证、手机号、备案表等材料
- [ ] README 中没有“保证中奖”“提高中奖率”等承诺性描述
- [ ] APK 版本号与 Release Tag 一致
- [ ] APK 文件名带版本号
- [ ] README 截图已替换为真实应用截图
