# 彩运宝上传 GitHub 操作步骤

## 推荐方案：先建 Private 仓库

备案仍在进行时，建议先把仓库设为 **Private**，完成资料清理和版本整理后，再决定是否公开。

## 1. 创建仓库

1. 登录 GitHub。
2. 右上角点击 `+` → `New repository`。
3. Repository name 建议填写：`CaiYunBao`
4. Description 可填写：`彩运宝 Android 号码随机生成与管理工具`
5. Visibility 先选：`Private`
6. 如果准备上传已有完整项目，建议不要在网页端重复初始化 README/.gitignore，避免第一次 push 发生历史冲突。
7. 点击 `Create repository`。

## 2. 检查敏感文件

上传前确认没有：
- .jks / .keystore
- 密码、Token、API Key
- 云服务器账号密码
- SSH 私钥
- 个人备案材料
- local.properties
- keystore.properties

## 3A. 简单网页上传（只适合少量文件）

进入仓库后：
`Add file` → `Upload files` → 拖入 README、截图等文件 → `Commit changes`

完整 Android 工程不推荐长期用网页逐个拖文件。

## 3B. 推荐：GitHub Desktop

1. 安装并登录 GitHub Desktop。
2. `File` → `Add local repository`。
3. 选择彩运宝项目根目录。
4. 如果项目还不是 Git 仓库，选择创建 repository。
5. 确认 `.gitignore` 已生效。
6. 填写提交说明，例如：`Initial GitHub backup`
7. 点击 `Commit to main`。
8. 点击 `Publish repository`。
9. 确认仓库是 Private/公开状态后发布。

## 4. 上传 APK：使用 Releases

不要把每个 APK 都堆在源码目录。

仓库主页：
`Releases` → `Draft a new release`

建议：
- Tag：`vX.Y.Z`
- Release title：`彩运宝 vX.Y.Z`
- 上传真实 APK：`CaiYunBao_vX.Y.Z.apk`
- 填写版本说明
- 如果当前仍是测试版本，可选择 `Set as a pre-release`
- 最后点击 `Publish release`

## 5. 添加真实截图

把真实截图重命名为：

- `home.png`
- `select.png`
- `numbers.png`

然后替换：

`docs/screenshots/`

目录中的占位图。

README 会自动显示这些图片。

## 6. 公开前再检查一次

建议先把 GitHub 当：
- 代码备份
- 版本记录
- APK Release 页面

备案/审核完成后，再决定是否将仓库从 Private 改为 Public。
