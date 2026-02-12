#!/bin/bash
# 应用图标生成脚本（使用ImageMagick）
# 自动将图片转换为Android所需的各种尺寸

set -e

echo "======================================"
echo "流光引跑 - 应用图标生成工具"
echo "======================================"
echo ""

# 输入图片路径
INPUT_IMAGE="${1:-icon.png}"

# 检查图片是否存在
if [ ! -f "$INPUT_IMAGE" ]; then
    echo "错误: 找不到图片文件 '$INPUT_IMAGE'"
    echo "请将裁剪好的图片保存为 icon.png 或指定图片路径"
    echo "用法: bash generate_icons.sh <图片路径>"
    exit 1
fi

echo "正在处理图片: $INPUT_IMAGE"
echo ""

# 目标目录
BASE_DIR="project_guideline/android/java/com/google/research/guideline/res"

# 创建目录并生成图标
declare -A SIZES=(
    ["mipmap-mdpi"]=48
    ["mipmap-hdpi"]=72
    ["mipmap-xhdpi"]=96
    ["mipmap-xxhdpi"]=144
    ["mipmap-xxxhdpi"]=192
)

for folder in "${!SIZES[@]}"; do
    size=${SIZES[$folder]}
    output_dir="$BASE_DIR/$folder"

    # 创建目录
    mkdir -p "$output_dir"

    # 生成图标
    output_file="$output_dir/ic_launcher.png"
    convert "$INPUT_IMAGE" -resize ${size}x${size} "$output_file"

    echo "✓ 已生成 $folder/ic_launcher.png (${size}x${size})"
done

echo ""
echo "✓ 所有图标生成完成！"
echo ""
echo "图标已保存到: $BASE_DIR"
echo ""
echo "下一步:"
echo "1. 重新编译应用"
echo "2. 安装到设备"
echo "3. 查看新图标"
