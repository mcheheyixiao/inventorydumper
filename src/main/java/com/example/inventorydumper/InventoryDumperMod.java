package com.example.inventorydumper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mod(InventoryDumperMod.MODID)
public class InventoryDumperMod {
    public static final String MODID = "inventorydumper";

    public InventoryDumperMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DumpInventoryCommand.register(event.getDispatcher());
    }

    public static Path getLogFile() {
        Path logDir = FMLPaths.GAMEDIR.get().resolve("logs");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return logDir.resolve("inventory_dump_" + timestamp + ".log");
    }

    public static void writeToFile(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            writer.write(content);
        }
    }
}