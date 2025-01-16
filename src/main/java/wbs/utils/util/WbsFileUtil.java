package wbs.utils.util;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("unused")
public class WbsFileUtil {
    @SuppressWarnings("UnstableApiUsage")
    public static void saveResource(BootstrapContext context, Class<? extends JavaPlugin> clazz, @NotNull String resourcePath, boolean replace) {
        ComponentLogger logger = context.getLogger();
        saveResource(clazz, context.getDataDirectory().toFile(), (level, message) -> {
            Consumer<String> log;

            if (level.equals(Level.SEVERE)) {
                log = logger::error;
            } else if (level.equals(Level.WARNING)) {
                log = logger::warn;
            } else if (level.equals(Level.CONFIG)) {
                log = logger::debug;
            } else if (level.equals(Level.INFO) || level.equals(Level.FINE) || level.equals(Level.FINER) || level.equals(Level.FINEST)) {
                log = logger::info;
            } else {
                throw new IllegalStateException("Unexpected value: " + level.getName());
            }

            log.accept(message);
        }, resourcePath, replace);
    }
    public static void saveResource(JavaPlugin plugin, @NotNull String resourcePath, boolean replace) {
        saveResource(plugin.getClass(), plugin.getDataFolder(), plugin.getLogger()::log, resourcePath, replace);
    }

    public static void saveResource(Class<? extends JavaPlugin> clazz, File dataFolder, BiConsumer<Level, String> logger, @NotNull String resourcePath, boolean replace) {
        //noinspection ConstantValue
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        ClassLoader classLoader = clazz.getClassLoader();

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(classLoader, resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + classLoader);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.accept(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.accept(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile + ".\n" + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    @Nullable
    public static InputStream getResource(ClassLoader classLoader, @NotNull String filename) {
        //noinspection ConstantValue
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = classLoader.getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public static void zipFolder(File folderToZip, String destination) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(destination);
             ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {

            File[] children = folderToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, childFile.getName(), zipOut);
                }
            } else {
                zipFile(folderToZip, folderToZip.getName(), zipOut);
            }
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
            }
            zipOut.closeEntry();

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    public static void unzip(String zipFile, String destFolder) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destFolder + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }
}
