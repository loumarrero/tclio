package scripting.legacy;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileUtil {
    public static final String SCRIPTING_PROPERTY_PREFIX = "extreme.scripting";
    public final static String ZIP_FILE_FORMAT = ".zip";
    public final static String POL_FILE_FORMAT = ".pol";

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());


    public static String getContents(File file) throws IOException {
        return (getContents(new FileReader(file)));
    }

    public static String getContents(Reader reader) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(reader);
            try {
                String line = null; //not declared within while loop
                String lineSeparator = System.getProperty("line.separator", "\n");
            /*
            * readLine is a bit quirky :
            * it returns the content of a line MINUS the newline.
            * it returns null only for the END of the stream.
            * it returns an empty String if two newlines appear in a row.
            */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(lineSeparator);
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }


    public static String getRelativePath(String filePath, String relativeFrom) {
        if (filePath != null) {
            return filePath.substring(relativeFrom.length());
        }
        return null;
    }

    public static boolean deleteDirectory(File path) {
        try {
            if (path.exists()) {
                File[] files = path.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            return (path.delete());
        } catch (SecurityException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return false;
    }

    public static void markFileForDeletion(String filename) {
        File file = new File(filename);
        file.deleteOnExit();
    }

    public static String getFileFromZip(String filenme, String zipfileName) {
        String filename = filenme;
        BufferedOutputStream bufferOutputStream = null;
        FileInputStream fileInStream = null;
        ZipInputStream zipInStream = null;
        try {
            if (zipfileName != null) {
                fileInStream = new FileInputStream(zipfileName);
                zipInStream = new ZipInputStream(new BufferedInputStream(fileInStream));
            }
            ZipEntry entry;
            while ((entry = zipInStream.getNextEntry()) != null) {
                int count;
                // write the files to the disk
                if (entry.getName().equals(filename)) {
                    String tmpFile = getParentFileName(zipfileName);
                    if (!new File(tmpFile).exists()) {
                        new File(tmpFile).mkdir();
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(new File(tmpFile).getAbsolutePath());
                    builder.append(File.separator);
                    builder.append(entry.getName());
                    filename = builder.toString();
                    FileOutputStream fos = new FileOutputStream(new File(filename));
                    bufferOutputStream = new BufferedOutputStream(fos);
                    while ((count = zipInStream.read()) != -1) {
                        bufferOutputStream.write(count);
                    }
                    bufferOutputStream.flush();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return null;
        } finally {
            try {
                if (bufferOutputStream != null) {
                    bufferOutputStream.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }

            try {
                if (fileInStream != null) {
                    fileInStream.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }

            try {
                if (zipInStream != null) {
                    zipInStream.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }
        return filename;
    }

    public static String getParentFileName(String fileName) {
        if (fileName != null) {
            return (fileName.indexOf(ZIP_FILE_FORMAT) > 0
                    || fileName.indexOf(POL_FILE_FORMAT) > 0) ?
                    fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        }
        return null;
    }

    public static String replaceByFileSeparator(String pathString, String delimiter, String fileSeparator) {
        StringTokenizer tokenizer = new StringTokenizer(pathString, delimiter);
        StringBuilder fileBuilder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            fileBuilder.append(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
                fileBuilder.append(fileSeparator);
            }
        }
        return fileBuilder.toString();
    }

    public static String convertInputStreamToString(InputStream is) throws IOException {
        //StringWriter writer = new StringWriter();
        //IOUtils.copy(is, writer, "UTF-8");
        //return (writer.toString());
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        return inputStringBuilder.toString();
    }


    /**
     * Deletes a file at the specified path and fileName.
     *
     * @param fileName     the full path and name of the file to remove
     * @param removeParent - Removes the parent directory if after file delete
     *                     the directory is empty.
     * @return true if and only if the file or directory is successfully
     * deleted; false otherwise
     */
    public static boolean deleteFile(String fileName, boolean removeParent) {
        File file = new File(fileName);
        boolean isDeleted = file.delete();
        // if this directory is empty, remove it
        if (removeParent) {
            File parent = new File(file.getParent());
            if (parent.isDirectory()) {
                String[] files = parent.list();
                if (files.length == 0) {
                    boolean isParentDeleted = parent.delete();
                    if (isParentDeleted)
                        LOGGER.log(Level.INFO, "parent directory was empty and has been removed.");
                }
            }
        }
        return isDeleted;
    }

    /**
     * <code>deleteSubDirs</code>
     *
     * @param dir          the directory to remove all files and sub directories for.
     * @param removeParent if true will remove the <code>dir</code>, parent
     *                     directory, when all sub directories are finished being removed.
     * @return true if all deletions were successful; false if not. This method
     * will still try to remove all other sub directories even is a sub
     * directory remove fails.
     */
    public static boolean deleteSubDirs(File dir, boolean removeParent) {
        boolean deletesuccess = true;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteSubDirs(new File(dir, children[i]), true);
                if (!success) {
                    deletesuccess = false;
                }
            }
        }

        // The directory is now empty so delete it
        if (removeParent && deletesuccess) {
            deletesuccess = dir.delete();
        }

        return deletesuccess;
    }

    /**
     * The <code>removeDuplicatePath</code> will remove the <i>rootdir</i> from
     * the <i>path</i> specified to ensure that there is not root directory
     * created within the real root directory.
     * <p>
     * Removes the rootdir from the path specified. Returns the path without the
     * rootdir
     *
     * @param rootdir a <code>String</code> the root directory to remove from
     * @param path    a <code>String</code> the path that need the root dir removed
     */
    public static String removeDuplicatePath(String rootdir, String path) {
        rootdir = normalizeStringPath(rootdir);
        path = normalizeStringPath(path);

        if (rootdir.endsWith(File.separator + File.separator)) {
            // if for some reason we get 2 file.separators, then remove one of
            // them
            rootdir = rootdir.substring(0, rootdir.length() - 1);
        }

        String returnval = path;

        if (path.startsWith(rootdir)) {
            // strip off the root dir
            returnval = path.substring(rootdir.length());
            if (returnval.startsWith(File.separator) == false) {
                // make sure we still have a FileSeparator
                returnval = File.separator + returnval;
            }// endif
        }// endif

        LOGGER.log(Level.FINE, "removeDuplicatePath - rootdir = " + rootdir);
        LOGGER.log(Level.FINE, "removeDuplicatePath - path = " + path);
        LOGGER.log(Level.FINE, "removeDuplicatePath - returnval = " + returnval);

        return returnval;
    }// removeDuplicatePath

    /**
     * Returns a array of {@link File}s.
     *
     * @param directory the full path to the directory to start from.
     * @param recurse   true to recurse all directories
     * @return the array of {@link File}s found
     */
    public static File[] getFiles(java.lang.String directory, boolean recurse) {
        List<File> fileList = getFileList(new File(directory), recurse);
        return fileList.toArray(new File[fileList.size()]);
    }// getFiles

    /**
     * Returns a list of {@link File}s.
     *
     * @param directory the directory to start from.
     * @param recurse   true to recurse all directories
     * @return the list of {@link File}s found
     */
    public static List<File> getFileList(File directory, boolean recurse) {
        ArrayList<File> returnList = new ArrayList<File>();

        if ((directory != null) && (directory.exists())) {
            if (directory.isDirectory()) {
                // get all the files in this directory
                File[] filelist = directory.listFiles();
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].isFile()) {
                        // add this file
                        returnList.add(filelist[i]);
                    } else {
                        if (recurse) {
                            // This is a directory recurse.
                            returnList.addAll(getFileList(filelist[i], recurse));
                        }
                    }
                }
            } else {
                if (directory.isFile()) {
                    returnList.add(directory);
                }
            }
        }

        return returnList;
    }// getFileList

    /**
     * Returns a list of {@link File}s.
     *
     * @param directory the directory to start from.
     * @param recurse   true to recurse all directories
     * @param fileFile  a list of directories that we will not process
     * @return the list of {@link File}s found
     */
    public static List<File> getFileList(File directory, boolean recurse, String[] fileFilter) {
        ArrayList<File> returnList = new ArrayList<File>();
        boolean passFilter = true;

        if ((directory != null) && (directory.exists())) {

            if (fileFilter != null) {
                for (String filter : fileFilter) {
                    if (directory.getParent().endsWith(filter) || directory.getPath().endsWith(filter)) {
                        LOGGER.log(Level.INFO, "getFileList - Bypassing file: " + directory.getPath() + " based on filter: "
                                + filter);
                        passFilter = false;
                        break;
                    }
                }
            }

            if (passFilter) {
                if (directory.isDirectory()) {
                    // get all the files in this directory
                    File[] filelist = directory.listFiles();
                    for (int i = 0; i < filelist.length; i++) {
                        if (filelist[i].isFile()) {
                            // add this file
                            returnList.add(filelist[i]);
                        } else {
                            if (recurse) {
                                // This is a directory recurse.
                                returnList.addAll(getFileList(filelist[i], recurse, fileFilter));
                            }
                        }
                    }
                } else {
                    if (directory.isFile()) {
                        returnList.add(directory);
                    }
                }
            }
        }

        return returnList;
    }// getFileList

    /**
     * The <code>normalizeStringPath</code> method will normialize the path
     * passed in by Replacing both "/" and "\" with the appropriate
     * <code>File.separatorChar</code> character.
     *
     * @param path a <code>String</code> path to correct
     */
    public static String normalizeStringPath(String path) {
        String returnval = path;
        // Normalize the string
        // Because we don't know what the file separator will be, replace
        // both / and \ characters with the file separator
        returnval = returnval.replace('/', File.separatorChar);
        returnval = returnval.replace('\\', File.separatorChar);
        return returnval;
    }// normalizeStringPath

    /**
     * Replaces all "\" with "/" .
     *
     * @param path a <code>String</code> path to correct
     */
    public static String normalizeStringPathToUrlFormat(String path) {
        String returnval = path;
        // If the path is in pc format then replace the separator char "\"
        // with the URL separator "/". Unix directories already use this
        // format so no changes will be made.
        returnval = returnval.replace('\\', '/');
        return returnval;
    }

    /**
     * The <code>copyFile</code> method is used to copy the <i>source</i> file
     * to the <i>destination</i> file specified.
     *
     * @param source      the source file to copy from.
     * @param destination the destination file to copy to.
     * @return true if the file could be copied, false if not.
     */
    public static boolean copyFile(File source, File destination) {
        boolean result = true;
        FileInputStream fis;
        try {
            new File(destination.getParent()).mkdirs();
            fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination);
            byte buf[] = new byte[4096];
            int n;
            while ((n = fis.read(buf)) > 0)
                fos.write(buf, 0, n);
            fis.close();
            fos.close();
        } catch (Exception e) {
            result = false;
            LOGGER.log(Level.FINE, "Unable to copy file: " + source.getAbsolutePath() + " to: " + destination.getAbsolutePath(),
                    e);
        }
        return result;
    }

    /**
     * Copyies the contents source directory to the destination directory.
     *
     * @param source  Directory
     * @param dest    Directory
     * @param recurse if the contect of the source are to be recursed.
     * @return
     */
    public static boolean copyDirectory(File source, File dest, boolean recurse) {
        return copyDirectory(source, dest, null, recurse);
    }

    /**
     * Copyies the contents source directory to the destination directory.
     *
     * @param source     Directory
     * @param dest       Directory
     * @param FileFilter a list of files to ignore
     * @param recurse    if the contect of the source are to be recursed.
     * @return
     */
    public static boolean copyDirectory(File source, File dest, String[] fileFilter, boolean recurse) {
        boolean retval = false;
        if (source != null && source.exists()) {
            List<File> files = getFileList(source, recurse, fileFilter);
            if (files != null && files.size() != 0) {
                retval = copyFiles(files, source, dest, fileFilter);
            } else {
                LOGGER.log(Level.INFO, "No Files found in directory " + source);
            }
        } else {
            LOGGER.log(Level.INFO, "Could not find source directory " + source);
        }

        return retval;
    }

    public static boolean copyFiles(List<File> files, File sourceDir, File destDir, String[] fileFilter) {
        boolean retval = false;
        boolean passFilter = true;
        if (files != null && files.size() != 0) {
            for (File sf : files) {
                passFilter = true;
                if (fileFilter != null) {
                    for (String filter : fileFilter) {
                        if (sf.getParent().endsWith(filter) || sf.getPath().endsWith(filter)) {
                            LOGGER.log(Level.INFO, "copyFiles - Bypassing file: " + sf.getPath() + " based on filter: " + filter);
                            passFilter = false;
                            break;
                        }
                    }
                }

                if (!passFilter) {
                    continue;
                }

                String partialPath = removeDuplicatePath(sourceDir.getPath(), sf.getPath());
                File df = new File(destDir.getPath() + partialPath);
                if (!df.exists()) {
                    df.getParentFile().mkdirs();
                }
                if (sf.isFile()) {
                    retval = copyFile(sf, new File(destDir.getPath() + partialPath));
                    if (!retval) {
                        LOGGER.log(Level.SEVERE, "Could not copy file " + sf + " to " + df);
                        break;
                    }
                }
            }
        } else {
            LOGGER.log(Level.SEVERE, "No Files found in directory " + sourceDir);
        }
        return retval;
    }

    /**
     * Creates a directory structure if needed.
     *
     * @param path the full path you want created expects the string to end with
     *             a file separator like "/" or "\".
     * @return true if the directory exists or was able to be created, false if
     * not.
     */
    public static boolean createPath(java.lang.String path) {
        // save the file separator
        String fs = File.separator;

        // Normalize the string
        // Because we don't know what the file separator will be, replace
        // both / and \ characters with the file separator
        path = normalizeStringPath(path);

        // default cleanPath
        String cleanPath = path;

        if (path.endsWith(fs) == false) {
            // path did not end with a file separator which means
            // there is a filename on the end of the path, lets
            // strip off the filename so we can create the path.
            int li = path.lastIndexOf(fs);
            // make sure we find at least one file separator
            if (li != -1) {
                cleanPath = path.substring(0, (li + 1));
            }// endif
        }// endif

        // make a file object to do the dirty work
        File f = new File(cleanPath);

        // see if it already exists
        if (f.isDirectory()) {
            // just return true
            return true;
        }// endif
        else {
            // we gotta make it
            return f.mkdirs();
        }// end else
    }// createPath

    /**
     * The <code>passesFilter</code> method is used to test a file name to see
     * if it passes the filter. In the case of an inclusive filter the test will
     * verify that the file has a suffix listed in the
     * <code>filterSuffixes</code>. In the case of an exclusive filter the file
     * name must not have a suffix specified in the <code>filterSuffixes</code>.
     * <p>
     * <pre>
     * Example:
     *      "image.png", true, [".png",".gif",".jpg"]:
     *      since this is an inclusive list we look to make sure the file ends with
     *      one of the suffixes listed.  In this case image.png would pass the test.
     * </pre>
     *
     * @param fileName        The file name to test.
     * @param inclusiveFilter true to tread the <code>filterPrefixes</code> as
     *                        an inclusiveFilter, false to treat the list as an exclusive
     *                        filter.
     * @param filterSuffixes  The list of suffixes to use as a filter.
     * @return true if the file passes the filter, false if not.
     */
    public static boolean passesFilter(String fileName, boolean inclusiveFilter, String[] filterSuffixes) {
        if (filterSuffixes == null || filterSuffixes.length == 0)
            return true;

        boolean passesFilter;
        if (inclusiveFilter) {
            passesFilter = false;
            for (String testSuffix : filterSuffixes) {
                if (fileName.endsWith(testSuffix)) {
                    passesFilter = true;
                    break;
                }
            }
        } else {
            passesFilter = true;
            for (String testSuffix : filterSuffixes) {
                if (fileName.endsWith(testSuffix)) {
                    passesFilter = false;
                    break;
                }
            }
        }

        return passesFilter;
    }

    /**
     * <code>calculateChecksum</code>
     * <p>
     * Calculates a CRC32 Checksum filename is the file that a Checksum will be
     * calculated for. If filename does not exists, 0 will be returned
     *
     * @param filename a <code>String</code> absolute name of a file
     */
    public static long calculateChecksum(String filename) {
        File file = new File(filename);
        CRC32 checksum = new CRC32();

        if (!file.exists() || !file.isFile() || !file.canRead()) {
            // can not calculate. Exit
            return 0;
        }

        try {
            FileInputStream fis = new FileInputStream(filename);

            byte buf[] = new byte[4096];
            int n;
            while ((n = fis.read(buf)) > 0) {
                checksum.update(buf, 0, n);
            }
            fis.close();
        } catch (IOException e) {
            return 0;
        }

        LOGGER.log(Level.FINE, "Calculated CheckSum = " + checksum.getValue());
        return checksum.getValue();
    }

    /**
     * Replaces the HTML characters + and and %# using the URLDecoder class.
     * This method wraps the method call to URLDecoder using the UTF-8 character
     * set.
     *
     * @param url String containing HTML characters
     * @return A String with the the HTML characters replaced.
     */
    public static String normalizeURL(String url) {
        String charEncoding = "UTF-8";
        String normalizedString = url;

        try {
            normalizedString = URLDecoder.decode(url, charEncoding);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }

        return (normalizedString);
    }

    public static Map<String, String> findMatchingLines(String[] regexes, String filename) {
        Map<String, String> retVal = new HashMap<String, String>();
        BufferedReader in = null;

        try {
            String inputLine = null;
            in = new BufferedReader(new FileReader(filename));

            while ((inputLine = in.readLine()) != null) {
                for (String regex : regexes) {
                    if (inputLine.contains(regex)) {
                        retVal.put(regex, inputLine);
                    }
                }
            }
        } catch (Exception e) {
            //something went wrong!
            LOGGER.log(Level.SEVERE, "Error parsing parsing file " + filename);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                //error closing file, naught to be done here
            }
        }

        return retVal;
    }

    public static void findAndReplace(Map<String, String> map, String name) {
        PrintWriter out = null;
        BufferedReader in = null;
        File oldFile = new File(name);
        File newFile = new File(oldFile.getAbsolutePath() + ".new");
        try {
            String inputLine = null;
            in = new BufferedReader(new FileReader(name));
            out = new PrintWriter(new FileWriter(newFile));
            while ((inputLine = in.readLine()) != null) {
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    if (inputLine.contains(key))
                        inputLine = inputLine.replace(key, map.get(key)); // there may be other things on the line, only replace the key with the replacement.
                }
                out.println(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
        File bakFile = new File(oldFile.getAbsolutePath() + ".bak");
        bakFile.delete();
        if (oldFile.renameTo(bakFile))
            newFile.renameTo(oldFile);
        newFile.delete();
    }

    public static void findAndReplaceEntireLine(Map<String, String> map, String name) {
        PrintWriter out = null;
        BufferedReader in = null;
        File oldFile = new File(name);
        File newFile = new File(oldFile.getAbsolutePath() + ".new");
        try {
            String inputLine = null;
            in = new BufferedReader(new FileReader(name));
            out = new PrintWriter(new FileWriter(newFile));
            while ((inputLine = in.readLine()) != null) {
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    if (inputLine.contains(key))
                        inputLine = map.get(key);
                }
                out.println(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
        File bakFile = new File(oldFile.getAbsolutePath() + ".bak");
        bakFile.delete();
        if (oldFile.renameTo(bakFile))
            newFile.renameTo(oldFile);
        newFile.delete();
    }

    public static void addLineIfMissing(String lineToAdd, String name) {
        PrintWriter out = null;
        BufferedReader in = null;
        File oldFile = new File(name);
        File newFile = new File(oldFile.getAbsolutePath() + ".new");
        boolean found = false;
        try {
            String inputLine = null;
            in = new BufferedReader(new FileReader(name));
            out = new PrintWriter(new FileWriter(newFile));
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(lineToAdd))
                    found = true;
                out.println(inputLine);
            }
            if (!found)
                out.println(lineToAdd);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
        File bakFile = new File(oldFile.getAbsolutePath() + ".bak");
        bakFile.delete();
        if (oldFile.renameTo(bakFile))
            newFile.renameTo(oldFile);
        newFile.delete();
    }

    /*
     * Convert unix linefeeds to windows style
     */
    public static void Unix2Dos(String name) {
        PrintWriter out = null;
        BufferedReader in = null;
        File oldFile = new File(name);
        File newFile = new File(oldFile.getAbsolutePath() + ".new");
        try {
            String inputLine = null;
            in = new BufferedReader(new FileReader(name));
            out = new PrintWriter(new FileWriter(newFile));
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine.replaceAll("\r", "\r\n"));
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
        if (oldFile.renameTo(new File(oldFile.getAbsolutePath() + ".bak")))
            newFile.renameTo(oldFile);
        newFile.delete();
    }

    /**
     * Returns a list of {@link File}s.
     *
     * @param filter    this is used to filter the list thats created
     * @param directory the directory to start from.
     * @param recurse   true to recurse all directories
     * @return the list of {@link File}s found
     */
    public static List<File> getFileFilteredList(FileFilter filter, File directory, boolean recurse) {
        ArrayList<File> returnList = new ArrayList<File>();

        if ((directory != null) && (directory.exists())) {
            if (directory.isDirectory()) {
                // get all the files in this directory
                File[] filelist = directory.listFiles(filter);
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].isFile()) {
                        // add this file
                        returnList.add(filelist[i]);
                    } else {
                        if (recurse) {
                            // This is a directory recurse.
                            returnList.addAll(getFileFilteredList(filter, filelist[i], recurse));
                        }
                    }
                }
            } else {
                if (directory.isFile()) {
                    returnList.add(directory);
                }
            }
        }

        return returnList;
    }// getFileList

    public static ArrayList<File> delete(String pathToDelete) {
        FileUtil tmp = new FileUtil();
        FileDeletor deletor = tmp.new FileDeletor(false);
        deletor.delete(pathToDelete);
        return deletor.getFailures();
    }

    public static void backupList(String files[], String from, String to) {
        LOGGER.log(Level.INFO, "Backup data files from " + from + " to " + to);
        for (int i = 0; i < files.length; i++) {
            File copyFile = new File(from + File.separator + files[i]);
            if (copyFile.exists() == true)
                copyDirectory(from + File.separator + files[i], to + File.separator + files[i]);
            else
                LOGGER.log(Level.WARNING, "Backup files " + files[i] + " did not exist in " + from);
        }
    }

    public static void stripFileExt(final String ext, String path) {
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                if (file.isFile())
                    return (file.getAbsolutePath().toLowerCase().endsWith(ext.toLowerCase()));
                else
                    return true; // return directories to allow the search to
                // recurse.
            }
        };
        List<File> files = FileUtil.getFileFilteredList(filter, new File(path), true);
        for (File fileName : files) {
            String name = fileName.getAbsolutePath();
            name = name.substring(0, name.lastIndexOf(ext));
            File newName = new File(name);
            if (newName.exists()) {
                LOGGER.log(Level.INFO, "Deleting old file " + newName.getAbsolutePath());
                newName.delete();
            }
            LOGGER.log(Level.INFO, "Renaming " + fileName.getAbsolutePath() + " to " + newName.getAbsolutePath());
            fileName.renameTo(newName);
        }

    }

    public static boolean copyDirectory(String srcDir, String dstDir) {
        return copyDirectory(new File(srcDir), new File(dstDir));
    }

    public static boolean copyFile(String src, String dst) {
        return copyFile(new File(src), new File(dst));
    }

    /**
     * Copies all files under srcDir to dstDir. If dstDir does not exist, it
     * will be created.
     */
    public static boolean copyDirectory(File src, File dst) {
        LOGGER.log(Level.FINE, "copyDirectory from " + src + " to " + dst);
        if (src.isDirectory()) {
            if (!dst.exists()) {
                dst.mkdirs();
            }
            String[] children = src.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(src, children[i]), new File(dst, children[i]));
            }
        } else {
            if (!src.exists()) {
                LOGGER.log(Level.INFO, "Unable to copy: " + src + " file or directory does not exist.");
                return false;
            } else {
                try {
                    copyFile(src, dst);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Unable to copy", e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Moves the "current" backupdir to a timestamped version.
     */
    public static boolean stampBackupDir(String serverDir) {
//        String backupDir = Migrate3xFiles.getBackupPath(serverDir);
//
//        File backup = new File(backupDir);
//
//        if (backup.exists())
//        {
//            Date modDate = new Date(backup.lastModified());
//            SimpleDateFormat format = new SimpleDateFormat("yyMMdd_HHmmss");
//            String modDateStr = format.format(modDate);
//            File backupStamped = new File(Migrate3xFiles.getBaseBackupPath(serverDir) + "/backup_" + modDateStr);
//            boolean renamed = backup.renameTo(backupStamped);
//            if (!renamed)
//            {
//                LOGGER.log(Level.SEVERE,"Unable to rename backup directory from " + backup.getPath() + " to "
//                        + backupStamped.getPath());
//                return false;
//            }
//        }
//        backup.mkdirs();
//        if (!backup.exists())
//        {
//            LOGGER.log(Level.SEVERE,"Unable to create backup directory: " + backup.getPath());
//            return false;
//        }
        return true;
    }

    public static void touchDirectory(File path) {
        File files[] = path.listFiles();
        for (File fileName : files) {
            fileName.setLastModified(System.currentTimeMillis());
        }
    }

    public static void cleanupInstallBackups(String installPath) {
        File path = null;// new File(Migrate3xFiles.getBaseBackupPath(installPath));
        List<File> order = new ArrayList<File>();
        if (path != null) {
            File files[] = path.listFiles();
            if (files != null && files.length > 3) {
                for (File fileName : files) {
                    if (fileName.isDirectory()) {
                        if (fileName.getAbsolutePath().contains("backup"))
                            order.add(fileName);
                    }
                }

                Collections.sort(order, new Comparator<File>() {
                    Long key;

                    public int compare(File o1, File o2) {
                        key = o2.lastModified();
                        return key.compareTo(o1.lastModified());
                    }
                });
                if (order.size() > 1) {
                    List<File> remove = order.subList(2, order.size());
                    for (File dir : remove) {
                        FileUtil.deleteSubDirs(dir, true);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the first file specified is newer than the second file specified.
     */
    public static boolean isFileNewer(String newerFileName, String olderFileName) {
        boolean isNewer = false;

        long newerTime = getLastModifiedTime(newerFileName);
        long olderTime = getLastModifiedTime(olderFileName);

        if (newerTime >= 0 && olderTime >= 0) {
            isNewer = newerTime > olderTime;
        }

        return isNewer;
    }

    /**
     * Convenience method to return the last modified time of a file, or -1 if it doesn't exist.
     */
    public static long getLastModifiedTime(String fileName) {
        long lastModified = -1;
        if (fileName != null && fileName.length() > 0) {
            File f = new File(fileName);
            if (f.exists()) {
                lastModified = f.lastModified();
            }
        }
        return lastModified;
    }

    /**
     * Deletes the oldest directories in the specified backupDir, keeps the specified
     * number of newest directories.  This is all done on a new thread as not to block the
     * current thread of there is going to be a lot of directories to remove.
     *
     * @param backupDir
     * @param numToKeep
     */
    public static void deleteOldestBackups(String backupDir, final int numToKeep) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.SEVERE, "Looking to see if we need to remove old backup dirs from: " + backupDir);
        }

        File baseDir = null;
        try {
            String tmp = new File(backupDir).getCanonicalPath();
            baseDir = new File(tmp);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Unable to convert to canonical path: " + baseDir, e);
        }

        if (baseDir == null || baseDir.isDirectory() == false) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.SEVERE, "Unable to remove old backup dirs from: " + backupDir);
            }
            return;
        }

        //Find the files/spin off the delete on a new thread
        try {

            File[] files = baseDir.listFiles();
            ArrayList<File> directories = new ArrayList<File>();

            for (File f : files) {
                if (f.isDirectory()) {
                    directories.add(f);
                }
            }
            if (directories.size() > numToKeep) {
                //Sort them
                Collections.sort(directories, new Comparator<File>() {
                    public int compare(File o1, File o2) {

                        if (o1.lastModified() > o2.lastModified()) {
                            return +1;
                        } else if (o1.lastModified() < o2.lastModified()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }

                });

                final ArrayList<File> dirsToRemove = directories;
                Thread cleanupThread = new Thread("Directory Cleanup Thread") {
                    public void run() {
                        File dir = null;
                        try {
                            //remove old directories
                            while (dirsToRemove.size() > numToKeep) {
                                dir = dirsToRemove.remove(0);
                                if (LOGGER.isLoggable(Level.FINE)) {
                                    LOGGER.log(Level.SEVERE, "Deleting dir: " + dir == null ? "null" : dir.getName());
                                }
                                FileUtil.deleteSubDirs(dir, true);
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error cleaning directory: " + dir == null ? "null" : dir.getName(), e);
                        }
                    }
                };
                cleanupThread.start();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cleaning directories from: " + backupDir, e);
        }
    }

    /**
     * Deletes the oldest files in the specified backupDir, keeps the specified
     * number of newest files.  This is all done on a new thread as not to block the
     * current thread of there is going to be a lot of files to remove.
     *
     * @param dir
     * @param numToKeep
     */
    public static void deleteOldestBackupFiles(String dir, final int numToKeep) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.SEVERE, "Looking to see if we need to remove old backup files from: " + dir);
        }

        File baseDir = null;
        try {
            String tmp = new File(dir).getCanonicalPath();
            baseDir = new File(tmp);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Unable to convert to canonical path: " + baseDir, e);
        }

        if (baseDir == null || baseDir.isDirectory() == false) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.SEVERE, "Unable to remove old backup files from: " + dir);
            }
            return;
        }

        //Find the files/spin off the delete on a new thread
        try {

            File[] files = baseDir.listFiles();
            ArrayList<File> fileList = new ArrayList<File>();

            for (File f : files) {
                if (!f.isDirectory()) {
                    fileList.add(f);
                }
            }
            if (fileList.size() > numToKeep) {
                //Sort them
                Collections.sort(fileList, new Comparator<File>() {
                    public int compare(File o1, File o2) {

                        if (o1.lastModified() > o2.lastModified()) {
                            return +1;
                        } else if (o1.lastModified() < o2.lastModified()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }

                });

                final ArrayList<File> filesToRemove = fileList;
                Thread cleanupThread = new Thread("Files Cleanup Thread") {
                    public void run() {
                        File file = null;
                        try {
                            //remove old files
                            while (filesToRemove.size() > numToKeep) {
                                file = filesToRemove.remove(0);
                                if (LOGGER.isLoggable(Level.FINE)) {
                                    LOGGER.log(Level.SEVERE, "Deleting file: " + file == null ? "null" : file.getName());
                                }
                                file.delete();

                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error removing: " + file == null ? "null" : file.getName(), e);
                        }
                    }
                };
                cleanupThread.start();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cleaning files from: " + dir, e);
        }
    }

    /**
     * Get the most recent n number of files in a given directory that match a filename pattern
     *
     * @param baseDir         directory to look in for the files
     * @param filenamePattern to match against
     * @param num             number of most recent files to get
     * @return
     */
    public static List<File> getMostRecentFiles(File baseDir, final String filenamePattern, int num) {

        List<File> result = new ArrayList<File>(num);

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.SEVERE, "Trying to get " + num + " most recent files containing " + filenamePattern + " from " + baseDir);
        }

        List<File> fileList = getFilesSortedByLastModified(baseDir, filenamePattern);

        if (fileList.size() > num) {
            int startIdx = (fileList.size() - num);
            for (int i = startIdx; i < fileList.size(); i++)
                result.add(fileList.get(i));
        } else {
            result.addAll(fileList);
        }

        return result;

    }

    /**
     * Get a list of files in a given directory that match a filename pattern and are sorted by last modified time (oldest first)
     *
     * @param baseDir         directory to look in for the files
     * @param filenamePattern to match against
     * @return
     */
    public static List<File> getFilesSortedByLastModified(File baseDir, final String filenamePattern) {

        List<File> result = new ArrayList<File>();

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.SEVERE, "Get a list of file names " + filenamePattern + " from " + (baseDir == null ? "NULL" : baseDir.toString()) + "sorted by LastModified");
        }

        String dir = baseDir.toString();

        if (baseDir == null || baseDir.isDirectory() == false) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.SEVERE, "Unable to get a list of file names " + filenamePattern + " from " + dir + "sorted by LastModified");
            }
            return result;
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String fileName) {
                return (fileName.indexOf(filenamePattern) != -1);
            }
        };

        try {

            File[] files = baseDir.listFiles(filter);

            if (files == null || files.length == 0) {
                return result;
            }

            List<File> fileList = Arrays.asList(files);

            //Sort them
            Collections.sort(fileList, new Comparator<File>() {
                public int compare(File o1, File o2) {

                    if (o1.lastModified() > o2.lastModified()) {
                        return +1;
                    } else if (o1.lastModified() < o2.lastModified()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }

            });

            result.addAll(fileList);


        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occured when trying to get a list of file names " + filenamePattern + " from " + dir + "sorted by LastModified", e);
        }

        return result;

    }

    /**
     * Used to copy the last x lines of a text file/log file.
     *
     * @param srcFile   the original file we are copying from
     * @param destFile  the destination file to will write to
     * @param lineCount (if -1 the default of 5000 will be used)
     * @return
     */
    public static boolean copyLinesFromTextFile(File srcFile, File destFile, long lineCount) {
        boolean retval = false;
        long defaultMaxLineCount = 5000;
        if (lineCount < 0) {
            lineCount = defaultMaxLineCount;//max lines
        }

        if (srcFile != null) {
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(srcFile);
                FileChannel fc = fis.getChannel();
                long fileLength = fc.size();
                int bbBufferSize = 8192;

                // If they only want to read a certain number of lines, find the offset
                // and set the position accordingly.
                if (lineCount > 0) {
                    long offset = fileLength;

                    ByteBuffer bb = ByteBuffer.allocateDirect(bbBufferSize);
                    for (int i = 0; i <= lineCount; i++) {
                        do {
                            bb.clear();
                            long position = Math.max(0, offset - bbBufferSize);
                            fc.position(position);
                            int n = fc.read(bb);
                            if (n > 0) {
                                for (int pos = n - 1; (pos >= 0) && (i <= lineCount); pos--) {
                                    bb.position(pos);
                                    if (bb.get() == '\n') {
                                        i++;
                                    }
                                    if (i <= lineCount) {
                                        offset--;
                                    }
                                }
                            }

                        } while ((offset > 0) && i <= lineCount);

                        if (offset <= 0) {
                            offset = 0;
                            break;
                        }
                    }
                    fileLength = fileLength - offset;
                    fc.position(offset);
                }

                //Read all the lines into a byte[]
                final BufferedInputStream bis = new BufferedInputStream(fis);
                final byte[] bytes = new byte[(int) fileLength];
                bis.read(bytes);
                bis.close();
                fis.close();
                fc.close();

                //Write the byte[] to the destination file
                FileOutputStream fos = new FileOutputStream(destFile);
                fos.write(bytes);
                fos.close();
                retval = true;
            } catch (FileNotFoundException fnfex) {
                String msg = "Error Retrieving File";
                LOGGER.log(Level.SEVERE, msg, fnfex);
            } catch (IOException ioe) {
                String msg = "Error Retrieving File";
                LOGGER.log(Level.SEVERE, msg, ioe);
            }
        }
        return retval;
    }

    public static void main(String[] args) {

    }

    class FileDeletor {
        ArrayList<File> failures = new ArrayList<File>();
        private boolean deleteOnExit = false;
        private boolean recurse = false;

        FileDeletor(boolean deleteOnExit) {
            this.deleteOnExit = deleteOnExit;
        }

        public void delete(String pathToDelete) {
            if (pathToDelete.endsWith("**.*")) {
                recurse = true;
                String pathWithoutWildcard = pathToDelete.substring(0, pathToDelete.length() - 4);
                deleteImpl(pathWithoutWildcard);
            } else {
                recurse = false;
                deleteImpl(pathToDelete);
            }
        }

        public ArrayList<File> getFailures() {
            return failures;
        }

        private void deleteImpl(String pathToDelete) {
            File fileOrDirToDelete = new File(pathToDelete);
            if (fileOrDirToDelete.exists()) {
                if (fileOrDirToDelete.isDirectory())
                    deleteDirectory(fileOrDirToDelete);
                else {
                    if (deleteOnExit)
                        fileOrDirToDelete.deleteOnExit();
                    else {
                        if (!fileOrDirToDelete.delete())
                            failures.add(fileOrDirToDelete);
                    }
                }
            } else {
                LOGGER.log(Level.INFO, "delete failed, path not found:" + pathToDelete);
                failures.add(fileOrDirToDelete);
            }
        }

        private void deleteDirectory(File dir) {
            if (dir.exists() && dir.isDirectory()) {
                File files[] = dir.listFiles();
                LOGGER.log(Level.FINE, "deleteDirectory: [" + files.length + "][" + deleteOnExit + "} " + dir.getAbsolutePath());
                if (files.length != 0) {
                    for (int i = 0; i < files.length; i++) {
                        // this is used to prevent problems with linux link
                        // files
                        // a broken link can be returned as a file object but
                        // fails the isFile test but the delete works correctly
                        if (!files[i].isDirectory()) {
                            deleteFile(files[i]);
                        } else {
                            if (recurse)
                                deleteDirectory(files[i]);
                        }
                    }
                }
                if (deleteOnExit)
                    dir.deleteOnExit();
                else if (!dir.delete())
                    failures.add(dir);
            }
        }

        private void deleteFile(File fileOrDirToDelete) {
            if (deleteOnExit)
                fileOrDirToDelete.deleteOnExit();
            else {
                if (!fileOrDirToDelete.delete())
                    failures.add(fileOrDirToDelete);
                else
                    LOGGER.log(Level.FINE, "deleted:" + fileOrDirToDelete.getPath());
            }
        }
    }


}
