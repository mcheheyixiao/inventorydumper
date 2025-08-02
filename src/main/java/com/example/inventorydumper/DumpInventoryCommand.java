package com.example.inventorydumper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DumpInventoryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dumpinventory")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> execute(
                                ctx.getSource(),
                                EntityArgument.getPlayer(ctx, "target")
                        ))
                )
                .executes(ctx -> execute(
                        ctx.getSource(),
                        ctx.getSource().getPlayerOrException()
                ))
        );
    }

    private static int execute(CommandSourceStack source, ServerPlayer targetPlayer) {
        try {
            // 获取玩家背包物品列表（包含名称和ID）
            List<String> items = getPlayerInventoryItems(targetPlayer);

            // 导出到日志文件
            dumpPlayerInventoryToFile(targetPlayer, items);

            // 在聊天栏显示
            displayPlayerInventoryInChat(source, targetPlayer, items);

            source.sendSuccess(() -> Component.literal(
                    "成功导出背包物品至日志文件并在聊天栏显示"
            ), true);
        } catch (Exception e) {
            source.sendFailure(Component.literal(
                    "导出失败: " + e.getMessage()
            ));
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static List<String> getPlayerInventoryItems(ServerPlayer player) {
        // 遍历背包所有槽位 (0-35) 并收集物品信息
        return player.getInventory().items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(stack -> {
                    ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                    String itemName = getItemDisplayName(stack);
                    String itemId = (id != null) ? id.toString() : "未知物品";
                    return String.format("%s---%s x%d", itemName, itemId, stack.getCount());
                })
                .collect(Collectors.toList());
    }

    private static String getItemDisplayName(ItemStack stack) {
        // 获取物品的显示名称（不带格式代码）
        Component displayNameComponent = stack.getHoverName();
        return displayNameComponent.getString();
    }

    private static void dumpPlayerInventoryToFile(ServerPlayer player, List<String> items) throws IOException {
        Path logFile = InventoryDumperMod.getLogFile();
        StringBuilder content = new StringBuilder();

        content.append("Player: ").append(player.getName().getString()).append("\n");
        content.append("UUID: ").append(player.getUUID()).append("\n\n");
        content.append("Inventory Items (").append(items.size()).append("):\n");

        // 添加所有物品信息
        for (String item : items) {
            content.append(item).append("\n");
        }

        InventoryDumperMod.writeToFile(logFile, content.toString());
    }

    private static void displayPlayerInventoryInChat(
            CommandSourceStack source,
            ServerPlayer player,
            List<String> items
    ) {
        // 发送玩家信息
        source.sendSuccess(() -> Component.literal(
                "§6玩家背包物品列表 §7(" + player.getName().getString() + "):"
        ), false);

        // 如果没有物品
        if (items.isEmpty()) {
            source.sendSuccess(() -> Component.literal("  §7(空)"), false);
            return;
        }

        // 发送所有物品列表
        for (String item : items) {
            // 分割物品名称和ID
            String[] parts = item.split("---", 2);
            String itemName = parts.length > 0 ? parts[0] : "未知物品";
            String itemInfo = parts.length > 1 ? parts[1] : "";

            // 提取物品ID（去除数量和空格）
            String itemId = extractItemId(itemInfo);

            // 创建可点击的复制组件
            MutableComponent copyComponent = createCopyComponent(itemId);

            // 构建完整的物品信息组件
            MutableComponent itemComponent = Component.literal("  §f- §b" + itemName + " §7(" + itemInfo + ") ")
                    .append(copyComponent);

            // 发送消息
            source.sendSuccess(() -> itemComponent, false);
        }

        // 发送总数
        source.sendSuccess(() -> Component.literal(
                "§7总计: §6" + items.size() + " §7种物品"
        ), false);
    }

    private static String extractItemId(String itemInfo) {
        // 提取物品ID（格式：modid:item_name x数量）
        if (itemInfo.contains(" x")) {
            return itemInfo.substring(0, itemInfo.lastIndexOf(" x")).trim();
        }
        return itemInfo;
    }

    private static MutableComponent createCopyComponent(String itemId) {
        // 创建可点击的复制文本
        return Component.literal("[点击复制]")
                .withStyle(Style.EMPTY
                        .withColor(0x55FF55) // 绿色
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                itemId
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("点击复制物品ID: " + itemId)
                        ))
                );
    }
}