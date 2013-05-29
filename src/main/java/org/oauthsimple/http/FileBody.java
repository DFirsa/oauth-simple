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
    public String fileName;
    public String contentType;
    public InputStream inputStream;

    public FileBody(File file) throws FileNotFoundException {
        String fileName = file.getName();
        String contentType = MimeUtils.getMimeTypeFromPath(fileName);
        initialize(fileName, contentType, new FileInputStream(file));
    }

    public FileBody(File file, String contentType) throws FileNotFoundException {
        initialize(fileName, contentType, new FileInputStream(file));
    }

    public FileBody(String fileName, String contentType, byte[] bytes) {
        initialize(fileName, contentType, new ByteArrayInputStream(bytes));
    }

    public FileBody(String fileName, String contentType, InputStream is) {
        initialize(fileName, contentType, is);
    }

    private void initialize(String fileName, String contentType, InputStream is) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.inputStream = is;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileBody{");
        sb.append("contentType='").append(contentType).append('\'');
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", inputStream=").append(inputStream);
        sb.append('}');
        return sb.toString();
    }
}
