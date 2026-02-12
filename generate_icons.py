#!/usr/bin/env python3
"""
应用图标生成脚本
自动将图片转换为Android所需的各种尺寸
"""

import os
import sys
from PIL import Image

# 图标尺寸配置
ICON_SIZES = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192,
}

# 目标目录
BASE_DIR = 'project_guideline/android/java/com/google/research/guideline/res'

def generate_icons(input_image_path):
    """
    从输入图片生成所有尺寸的图标

    Args:
        input_image_path: 输入图片路径（应该是正方形）
    """
    print(f"正在读取图片: {input_image_path}")

    try:
        # 打开图片
        img = Image.open(input_image_path)

        # 检查图片是否为正方形
        width, height = img.size
        if width != height:
            print(f"警告: 图片不是正方形 ({width}x{height})")
            print("将裁剪为正方形...")

            # 裁剪为正方形（取中心部分）
            size = min(width, height)
            left = (width - size) // 2
            top = (height - size) // 2
            right = left + size
            bottom = top + size
            img = img.crop((left, top, right, bottom))
            print(f"已裁剪为 {size}x{size}")

        # 转换为RGBA模式（支持透明背景）
        if img.mode != 'RGBA':
            img = img.convert('RGBA')

        # 生成各种尺寸的图标
        for folder, size in ICON_SIZES.items():
            output_dir = os.path.join(BASE_DIR, folder)

            # 创建目录（如果不存在）
            os.makedirs(output_dir, exist_ok=True)

            # 调整图片大小
            resized_img = img.resize((size, size), Image.Resampling.LANCZOS)

            # 保存图标
            output_path = os.path.join(output_dir, 'ic_launcher.png')
            resized_img.save(output_path, 'PNG')

            print(f"[OK] 已生成 {folder}/ic_launcher.png ({size}x{size})")

        print("\n[OK] 所有图标生成完成！")
        print(f"\n图标已保存到: {BASE_DIR}")

    except FileNotFoundError:
        print(f"错误: 找不到图片文件 '{input_image_path}'")
        print("请确保图片文件存在")
        sys.exit(1)
    except Exception as e:
        print(f"错误: {e}")
        sys.exit(1)

def main():
    print("=" * 60)
    print("流光引跑 - 应用图标生成工具")
    print("=" * 60)
    print()

    # 检查是否提供了图片路径
    if len(sys.argv) > 1:
        input_image = sys.argv[1]
    else:
        # 默认图片路径
        input_image = 'icon.png'
        print(f"使用默认图片: {input_image}")
        print("提示: 你也可以指定图片路径: python generate_icons.py <图片路径>")
        print()

    # 检查PIL是否安装
    try:
        from PIL import Image
    except ImportError:
        print("错误: 需要安装Pillow库")
        print("请运行: pip install Pillow")
        sys.exit(1)

    # 生成图标
    generate_icons(input_image)

    print("\n下一步:")
    print("1. 重新编译应用")
    print("2. 安装到设备")
    print("3. 查看新图标")

if __name__ == '__main__':
    main()
