package org.oauthsimple.http;

import org.oauthsimple.utils.MimeUtils;

import java.io.*;

/**
 * Project: oauthsimple
 * User: mcxiaoke
 * Date: 13-5-29
 * Time: 下午1:44
 */
class FileBody {
    public String name;
    public String fileName;
    public String contentType;
    public InputStream inputStream;

    public FileBody(String name, File file) throws FileNotFoundException {
        String fileName = file.getName();
        String contentType = MimeUtils.getMimeTypeFromPath(fileName);
        initialize(name, fileName, contentType, new FileInputStream(file));
    }

    public FileBody(String name, File file, String contentType) throws FileNotFoundException {
        initialize(name, fileName, contentType, new FileInputStream(file));
    }

    public FileBody(String name, String fileName, String contentType, byte[] bytes) {
        initialize(name, fileName, contentType, new ByteArrayInputStream(bytes));
    }

    public FileBody(String name, String fileName, String contentType, InputStream is) {
        initialize(name, fileName, contentType, is);
    }

    private void initialize(String name, String fileName, String contentType, InputStream is) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.inputStream = is;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileBody{");
        sb.append("contentType='").append(contentType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", inputStream=").append(inputStream);
        sb.append('}');
        return sb.toString();
    }
}
