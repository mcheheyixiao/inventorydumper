# Inventory Dumper Mod

一个Minecraft Forge模组，允许管理员导出玩家背包物品信息到日志文件并在游戏中查看。

## 功能介绍

⭕️此模组提供以下功能：

- 导出指定玩家或自身背包中的所有物品信息到日志文件✅
- 在游戏聊天栏中显示玩家背包物品列表✅
- 记录玩家名称和UUID信息✅
- 自动生成带时间戳的日志文件，存储在游戏的`logs`目录中✅

## 使用方法

### 命令格式

`/dumpinventory [player]`

### 权限要求

需要权限等级2或以上（默认为管理员权限）

### 使用示例

1. 导出自己的背包物品：
   `/dumpinventory`
2. 导出指定玩家的背包物品：
   `/dumpinventory [player]`
### 输出示例
⭕️执行命令后，将在聊天栏显示类似以下信息：

玩家背包物品列表 (PlayerName):

minecraft:diamond_sword x1

minecraft:diamond x10

minecraft:apple x32 总计: 3 种物品


⭕️同时会在游戏目录的`logs`文件夹中生成日志文件，文件名格式为`inventory_dump_YYYYMMDD_HHMMSS.log`，内容包括：

Player: PlayerName

UUID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

Inventory Items (3):

minecraft:diamond_sword x1

minecraft:diamond x10

minecraft:apple x32

## 权限说明

- 只有拥有权限等级2或以上的玩家才能使用此命令
- 通常这意味着需要是服务器管理员或具有相应权限的玩家

## 支持版本

该模组基于Forge开发，适用于对应Minecraft版本。具体版本信息请参考[mods.toml]文件。
