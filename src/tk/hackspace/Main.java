package tk.hackspace;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String WELCOME_STRING = "Вас приветствует установщик системы \"Музейный гид ТГЛИАМЗ\".\n Введите IP адресс сервера:\n";
    private static final String IP_REGEXP = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static final String HOME_DIR = System.getProperty("user.dir");
    private static final String RES_DIR = HOME_DIR + "\\res";
    private static final String QR_CODE_FILE_NAME = HOME_DIR + "\\QR код к приложению.png";
    private static final String DB_EXE_FILE_NAME = ("\"" + HOME_DIR + "/res/setup-couchdb-1.6.1_R16B02.exe").replace('/', '\\') + "\"";
    private static final String DB_DIR = "C:\\Program Files (x86)\\Apache Software Foundation\\CouchDB\\";
    public static final String[] DB_INSTALL_ARGS = new String[]{DB_EXE_FILE_NAME, "/SILENT",/*"/SUPPRESSMSGBOXES",*/"/NORESTART", "/LOG=\"./install_log.txt\"", "/DIR=\"\"" + DB_DIR + "\"\""};
    private static final String dBStructureZipFile = (HOME_DIR + "/res/var.zip").replace('\\', '/').substring(2);

    private static final Logger log = Logger.getLogger(Main.class);
    private static final String DEFAULT_ADMIN_PASSWORD = "mysecretADMINpassword";
    public static final Path newAPKFile = Paths.get(HOME_DIR + "\\TGLIAMZ Mobile guide.apk");
    private static final String REWRITE_ALERT_TEXT = "\nВНИМАНИЕ! ЭТО ДЕЙСТВИЕ НЕОБРАТИМО. ОНО СОТРЕТ ВСЮ ИМЕЮЩУЮСЯ В БАЗЕ ИНФОРМАЦИЮ.\n";
    private static final String HELP = "По-умолчанию установщик выполняет все действия.\n Для особых случаев доступен список опций:\n" +
            "-db Не устанавливать базу данных.\n" +
            "-apk Пересобрать мобильное приложение с новым IP.\n" +
            "-wipe Сбросить базу данных в начальное состояние." + REWRITE_ALERT_TEXT +
            "-m Скопировать Контент-менеджер на рабочий стол.\n" +
            "-ip Перенести систему на другой ip."+
            "-h Вывод этого сообщения.\n" +
            "-qed";
    private static final String WRONG_ARGS = "Опции были заданы неверно.\n Для вывода помощи вызовите установщик с опцией -h.";
    private static final String OPUS = "Резюме к приобретенному автором опыту написания этого\n \"инсталлятора\":" +
            "\nУбедился что java не годится для быстрого(потратил на сей опус 3 дня)\n написания подобных скриптов и ушел учить Python.";
    private static int flags = 0;
    private static int dbMask = 0b000001;
    private static int bulidApkMask = 0b000010;
    private static int wipeDBMask = 0b000100;
    private static int copyManagerMask = 0b001000;
    private static int changeServerSettingsMask = 0b010000;
    private static int unzipMask = 0b100000;


    public static void main(String[] args) {
        new Main();
        System.setProperty("file.encoding", "UTF-8");
        StringBuilder builder = new StringBuilder();
        Arrays.asList(args).stream().forEachOrdered(builder::append);
        log.debug("args = [" + builder.toString() + "]");

        if (args.length != 0) {

            if (args.length>1) {
                System.out.println(WRONG_ARGS);
                System.console().readLine();
                return;
            }

            switch (args[0]) {
                case "-h":
                    System.out.println(HELP);
                    System.console().readLine();
                    return;
                case "-qed":
                    System.out.println(OPUS);
                    System.console().readLine();
                    return;
                case "-db":
                    flags = ~dbMask;
                    break;
                case "-apk":
                    flags = bulidApkMask;
                    break;
                case "-wipe":
                    flags = wipeDBMask|unzipMask;
                    break;
                case "-mn":
                    flags = copyManagerMask;
                    break;
                case "-ip":
                    flags = bulidApkMask|copyManagerMask|changeServerSettingsMask;
                    break;
                default:
                    System.out.println(WRONG_ARGS);
                    System.console().readLine();
                    return;
            }
            log.debug("flags: "+Integer.toBinaryString(flags));
        }else flags=Integer.MAX_VALUE;

        System.out.println(WELCOME_STRING);
        //спросить IP сервера
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean flag = false;
        String ip = "";
        while (!flag)
            try {
                String usersInput = bufferedReader.readLine();
                if (usersInput.matches(IP_REGEXP)) {
                    ip = usersInput;
                    flag = true;
                } else System.err.println("IP введен неверно. Попробуйте ввести еще раз.\n");

            } catch (IOException e) {
                System.err.println("Ошибка ввода.");
            }
        //поставить бд
        if ((flags & dbMask) == dbMask)
            if (!installDB()) return;
        //запилить структуру бд
        String serviceName = getDBServiceName();

        if (serviceName != null && flags != copyManagerMask && flags!=bulidApkMask) {

            stopDB(serviceName);
            whaitDB();

            if ((flags&unzipMask)==unzipMask) {
            System.out.println(REWRITE_ALERT_TEXT);
            System.out.println("Продолжить? Да-д,нет-н");
            //копировать файлы в папку из архива
            String input = "";
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                input = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.toLowerCase().startsWith("д"))
                log.info("Распаковка структуры базы данных.");
                unzip(dBStructureZipFile, Paths.get(DB_DIR + "\\var"));
            }

            if ((flags&changeServerSettingsMask)==changeServerSettingsMask) {
                //изменить ип адрес в настройках и настроить пароль администатора по-умолчанию.
                Path local_ini = Paths.get(DB_DIR + "\\etc\\couchdb\\local.ini");
                if (Files.notExists(local_ini)) {
                    log.error("Файл настроек local.ini не существует. Перезапустите приложение. Если проблема повторяется обратитесь к администратору.");
                    return;
                }
                if (!Files.isReadable(local_ini)) {
                    log.error("Файл настроек local.ini невозможно прочитать. Попробуйте закрыть приложение, использующее этот файл и перезапустить инсталлятор.");
                    return;
                }
                if (!Files.isWritable(local_ini)) {
                    log.error("Файл настроек local.ini невозможно изменить. Попробуйте закрыть приложение, использующее этот файл и перезапустить инсталлятор.");
                    return;
                }

                try (BufferedWriter bw = Files.newBufferedWriter(local_ini);BufferedReader br = Files.newBufferedReader(local_ini)) {
                    StringBuilder sb = new StringBuilder();
                    br.lines().forEachOrdered(sb::append);
                    Pattern pattern = Pattern.compile("bind_address = .+");
                    final Matcher matcher = pattern.matcher(sb.toString());
                    if (matcher.find()){
                    String resultString = matcher.replaceAll(String.format("bind_address = %s", ip));
                        bw.write(resultString);
                    }else
                        bw.append("[httpd]\nbind_address = ").append(ip).append("\n[admins]\nadmin = ").append(DEFAULT_ADMIN_PASSWORD).append("\n");

                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

        final boolean needApkChange = (flags & bulidApkMask) == bulidApkMask;

        if (needApkChange)
            if (!buildAPK(ip)) return;

        if (serviceName != null && flags != copyManagerMask) {
            //поднять базу
            startDB(serviceName);

            // waiting for launch db.
            whaitDB();
            if (needApkChange) {
                //отправть апк на сервер
                String urlToApk = sendApkToServer(ip);
                if (urlToApk == null) return;

                //сгенерировать qr код и записать на диск.
                generateQRCodeToApk(urlToApk);
            }
        }

        //копировать приложение менеджера на рабочий стол.
        if ((flags & copyManagerMask) == copyManagerMask)
            copyManagerToDesktopFolder(ip);
    }

    private static boolean buildAPK(String ip) {
        //изменить IP в res\MobileApp\app\src\main\res\values\network_settings.xml
        Path netSettingsXML = Paths.get(HOME_DIR + "\\res\\MobileApp\\app\\src\\main\\res\\values\\network_settings.xml");
        try (BufferedWriter bw = Files.newBufferedWriter(netSettingsXML)) {
            bw.write(String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n" +
                    "    <string name=\"server_uri\" translatable=\"false\">http://%s:5984/</string>\n" +
                    "</resources>\n", ip));
        } catch (IOException e) {
            log.error(e);
        }

        //скомпилить мобильное приложение и залить его в бд
        try {
            log.info("Начинается сборка мобильного приложения.");
            ArrayList<String> compileCommands = new ArrayList<>();
            compileCommands.add("cmd");
            compileCommands.add("cd " + HOME_DIR + "\\res\\MobileApp\\");
            //compileCommands.add("gradlew clean");
            compileCommands.add("gradlew app:assembleRelease");
            log.info("Сборка мобильного приложения завершилась с кодом:" + execCmd(compileCommands));
        } catch (IOException e) {
            log.error(e);
        } catch (InterruptedException e) {
            log.info(e);
        }
        Path apk = Paths.get(HOME_DIR + "\\res\\MobileApp\\app\\build\\outputs\\apk\\app-release.apk");
        if (Files.notExists(apk)) {
            log.error("Файл мобильного приложения отсутствует. Возникли проблемы с компиляцией приложения. Обратитесь к администратору.");
        }
        try {
            Files.move(apk, newAPKFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }

    private static void startDB(String serviceName) {
        ArrayList<String> commands;
        commands = new ArrayList<>();
        commands.add("cmd");
        commands.add(String.format("sc start \"%s\"", serviceName));
        try {
            execCmd(commands);
            log.info("База данных запускается... Ждем запуска 10 секунд.");
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
    }

    private static String sendApkToServer(String ip) {
        NetworkConnection.setServerURL(String.format("http://%s:%s/", ip, "5984"));
        if (!NetworkConnection.isServerOnline()) {
            log.error("База данных не в сети. Проверте подключение и корректность введеного IP адреса");
            return null;
        }

        return NetworkConnection.putFileByItem(newAPKFile.toFile(), "mobile_app");
    }

    private static void generateQRCodeToApk(String urlToApk) {
        try {
            QRCode.from(urlToApk).to(ImageType.PNG).withSize(256, 256).stream().writeTo(Files.newOutputStream(Paths.get(QR_CODE_FILE_NAME)));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        }
    }

    private static void copyManagerToDesktopFolder(String ip) {
        try {
            final String desktopFolder = System.getProperty("user.home") + "/Desktop";
            final Path toPath = Paths.get(desktopFolder + "\\Контент-менеджер");
            final File[] listFiles = Paths.get(RES_DIR + "\\Manager\\").toFile().listFiles();

            if (listFiles!=null)
            Arrays.asList(listFiles).forEach(file -> {
                try {
                    Files.copy(file.toPath(), toPath.resolve(file.getName()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error(e);
                }
            });

            // Paths.get(desktopFolder + "\\Контент-менеджер\\"), StandardCopyOption.REPLACE_EXISTING);

            Path propFile = toPath.resolve("properties.json");

            try (BufferedWriter bw = Files.newBufferedWriter(propFile)) {
                bw.write(String.format("{\n" +
                        "  \"db_name\": \"exibit\",\n" +
                        "  \"server_address\": \"http://%s:5984/\"\n" +
                        "}", ip));
            }

        } catch (IOException e) {
            log.error(e);
        }
    }

    private static boolean installDB() {
        System.out.println("Устанавливаем базу данных:\n");
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(DB_INSTALL_ARGS));
        try {
            Process process = processBuilder.start();

            try {
                BufferedReader errorBufferedReader1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                errorBufferedReader1.lines().forEachOrdered(log::error);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int returnValue = process.waitFor();
            if (returnValue != 0) {
                log.error("Установщик базы данных завершился некорректно. Перезапустите устанвщик.\nЕсли проблема повторяется-обратитесь к администратору.");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
        return true;
    }

    private static void stopDB(String serviceName) {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("cmd");
        commands.add(String.format("sc stop \"%s\"", serviceName));
        try {
            execCmd(commands);
            log.info("База данных останавливается... Ждем остановки 10 секунд");
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
    }

    private static void whaitDB() {
        Timer timer = new Timer(true);
        long delay = 10000;

        CountDownLatch signal = new CountDownLatch(1);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.debug("timer task is running");
                signal.countDown();
            }
        }, delay);

        try {
            signal.await();
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public static String getDBServiceName() {
        try {
            String listFileName = "service.txt";
            ArrayList<String> commands = new ArrayList<>();
            commands.add("cmd");
            commands.add("cd " + HOME_DIR + "\\");
            commands.add("sc query state= all>" + listFileName);
            int returnCode = execCmd(commands);
            log.info("Return code = " + returnCode);
            String serviceName;
            Path serviceList = Paths.get(HOME_DIR + "\\" + listFileName);
            try (BufferedReader br = Files.newBufferedReader(serviceList, Charset.forName("KOI8-U"))) {
                StringBuilder sb = new StringBuilder();
                br.lines().forEachOrdered(sb::append);
                Pattern pattern = Pattern.compile("Apache CouchDB\\w{16}");
                Matcher matcher = pattern.matcher(sb.toString());
                if (matcher.find()) {
                    serviceName = matcher.group();
                    log.debug("dbService name is " + serviceName);
                } else {
                    serviceName = null;
                    log.error("dbService name not found");
                }
                serviceList.toFile().deleteOnExit();
                return serviceName;
            }
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
        return null;
    }

    public static int execCmd(ArrayList<String> commands) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(commands.get(0));
        commands.remove(0);
        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
        new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        commands.stream().forEachOrdered(stdin::println);
        stdin.close();
        return p.waitFor();
    }


    public static void unzip(String zipFileName, final Path destinationFolder) {
        Map<String, String> zip_properties = new HashMap<>();
        /* We want to read an existing ZIP File, so we set this to False */
        zip_properties.put("create", "false");
        /* Specify the encoding as UTF -8 */
        zip_properties.put("encoding", "UTF-8");
        /* Specify the path to the ZIP File that you want to read as a File System */
        URI zip_disk = URI.create(String.format("jar:file:%s", zipFileName));
        /* Create ZIP file System */
        try {
            try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
                final Path root = zipfs.getPath("/");
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs) throws IOException {
                        final Path destFile = Paths.get(destinationFolder.toString(),
                                file.toString());
                        log.info(String.format("Извлечение файла %s в %s", file, destFile));
                        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir,
                                                             BasicFileAttributes attrs) throws IOException {
                        final Path dirToCreate = Paths.get(destinationFolder.toString(),
                                dir.toString());
                        if (Files.notExists(dirToCreate)) {
                            log.info("Создание папки." + dirToCreate);
                            Files.createDirectory(dirToCreate);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                log.error(e);
            }
        } catch (FileSystemNotFoundException e) {
            log.error(e);
        }
    }

}
