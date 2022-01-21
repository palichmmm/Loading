import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Main {
    public static String path = "Games/savegames/";
    public static String nameZip = "saves.zip";
    public static String loadFile = "";
    public static List<GameProgress> saves = new ArrayList<>();
    public static List<String> fileList = new ArrayList<>();
    public static void main(String[] args) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            String step = "";
            for (String folder : path.split("/")) {
                step += folder;
                dir = new File(step);
                step += "/";
                if (!dir.mkdir()) {
                    System.out.println("Ошибка создания папки " + step);
                }
            }
        }
        if (! new File(path + nameZip).exists()) {
            saves.add(new GameProgress(12, 6, 33, 55.34));
            saves.add(new GameProgress(13, 5, 34, 65.24));
            saves.add(new GameProgress(14, 4, 35, 75.14));
            for (GameProgress save : saves) {
                saveGame(save, path + "save_" + save.hashCode() + ".dat");
            }
            zipFiles(path + nameZip, fileList);
            deleteFiles(fileList);
        }
        // Решение задачи начинается здесь!
        openZip(path + nameZip, path);
        GameProgress game = gameProgress(loadFile);
        System.out.println(game); // Вывод последнего файла в архиве
    }

    public static void saveGame(GameProgress save, String savePath) {
        try (FileOutputStream fos = new FileOutputStream(savePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(save);
            fileList.add(savePath);
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    public static void zipFiles(String zipFile, List<String> files) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String file : files) {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(new File(file).getName());
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
                fis.close();
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    public static void deleteFiles(List<String> files) {
        for (String file : files) {
            File f = new File(file);
            if (!f.delete()) {
                System.out.println("Ошибка удаления файла " + f);
            }
        }
    }

    public static void openZip(String zipFile, String path) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            String name;
            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                loadFile = path + name; // Для примера выводить будем последний файл в архиве
                FileOutputStream fout = new FileOutputStream(path + name);
                for (int c = zis.read(); c != -1; c = zis.read()) {
                    fout.write(c);
                }
                fout.flush();
                zis.closeEntry();
                fout.close();
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }
    public static GameProgress gameProgress(String file) {
        try (FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis)){
            return (GameProgress) ois.readObject();
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
        return null;
    }
}