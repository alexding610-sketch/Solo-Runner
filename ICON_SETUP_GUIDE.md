# 流光引跑 - 应用图标设置指南

## 方法一：使用在线工具（推荐）

### 步骤1：准备图片
1. 将你提供的跑步图片保存到电脑
2. 使用图片编辑工具（如Paint、Photoshop等）将图片裁剪为正方形
3. 建议尺寸：至少512x512像素

### 步骤2：生成图标
访问以下任一在线工具：

**推荐工具：Android Asset Studio**
- 网址：https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
- 或者：https://icon.kitchen/

**操作步骤**：
1. 打开网站
2. 上传你的正方形图片
3. 调整图片位置和大小
4. 选择图标形状（建议选择"Circle"或"Square"）
5. 点击"Download"下载生成的图标包

### 步骤3：解压并复制图标
下载的ZIP文件包含以下目录结构：
```
res/
  mipmap-mdpi/
    ic_launcher.png (48x48)
  mipmap-hdpi/
    ic_launcher.png (72x72)
  mipmap-xhdpi/
    ic_launcher.png (96x96)
  mipmap-xxhdpi/
    ic_launcher.png (144x144)
  mipmap-xxxhdpi/
    ic_launcher.png (192x192)
```

### 步骤4：替换项目中的图标
将解压后的文件复制到项目目录：

**目标位置**：
```
C:\Users\win2\project-guideline\project_guideline\android\java\com\google\research\guideline\res\
```

**操作**：
1. 在目标位置创建以下目录（如果不存在）：
   - mipmap-mdpi
   - mipmap-hdpi
   - mipmap-xhdpi
   - mipmap-xxhdpi
   - mipmap-xxxhdpi

2. 将对应的ic_launcher.png文件复制到各个目录中

---

## 方法二：手动处理（需要图片编辑软件）

### 需要的图标尺寸
| 密度 | 目录 | 尺寸 |
|------|------|------|
| mdpi | mipmap-mdpi | 48x48 |
| hdpi | mipmap-hdpi | 72x72 |
| xhdpi | mipmap-xhdpi | 96x96 |
| xxhdpi | mipmap-xxhdpi | 144x144 |
| xxxhdpi | mipmap-xxxhdpi | 192x192 |

### 步骤1：使用图片编辑软件
使用Photoshop、GIMP或在线工具（如Pixlr）：

1. 打开你的跑步图片
2. 裁剪为正方形
3. 调整大小为512x512像素
4. 保存为PNG格式

### 步骤2：生成不同尺寸
对于每个尺寸：
1. 打开512x512的图片
2. 调整图片大小到目标尺寸
3. 保存为ic_launcher.png
4. 放入对应的mipmap目录

---

## 方法三：使用命令行工具（适合开发者）

如果你安装了ImageMagick，可以使用以下命令批量生成：

```bash
# 假设原图为icon_512.png

# 生成mdpi (48x48)
magick icon_512.png -resize 48x48 mipmap-mdpi/ic_launcher.png

# 生成hdpi (72x72)
magick icon_512.png -resize 72x72 mipmap-hdpi/ic_launcher.png

# 生成xhdpi (96x96)
magick icon_512.png -resize 96x96 mipmap-xhdpi/ic_launcher.png

# 生成xxhdpi (144x144)
magick icon_512.png -resize 144x144 mipmap-xxhdpi/ic_launcher.png

# 生成xxxhdpi (192x192)
magick icon_512.png -resize 192x192 mipmap-xxxhdpi/ic_launcher.png
```

---

## 完成后的目录结构

```
project_guideline/android/java/com/google/research/guideline/res/
├── mipmap-mdpi/
│   └── ic_launcher.png
├── mipmap-hdpi/
│   └── ic_launcher.png
├── mipmap-xhdpi/
│   └── ic_launcher.png
├── mipmap-xxhdpi/
│   └── ic_launcher.png
└── mipmap-xxxhdpi/
    └── ic_launcher.png
```

---

## 验证图标

完成后：
1. 重新编译应用
2. 安装到设备
3. 在桌面上查看图标是否正确显示

---

## 注意事项

1. **图标设计建议**：
   - 使用简洁的设计
   - 确保在小尺寸下仍然清晰可辨
   - 避免使用过多细节
   - 考虑使用纯色背景

2. **文件格式**：
   - 必须使用PNG格式
   - 支持透明背景
   - 文件名必须是ic_launcher.png

3. **如果图标不显示**：
   - 清除应用数据
   - 卸载并重新安装应用
   - 重启设备

---

## 需要帮助？

如果你在设置图标时遇到问题，可以：
1. 使用推荐的在线工具（最简单）
2. 提供图片，我可以帮你生成配置文件
3. 查看Android官方文档：https://developer.android.com/guide/practices/ui_guidelines/icon_design_launcher
