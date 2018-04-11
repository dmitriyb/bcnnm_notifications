package net.bcnnm.notifications.slack;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DownloadMain {
    public static void main(String[] args) throws IOException {
        Client client = Client.create();

        WebResource resource = client.resource("https://files.slack.com/files-pri/T0V7JMTU5-F8W7FC3BM/config.zip");
        ClientResponse response = resource.header("Authorization", "Bearer xoxb-164860396197-HpfYn2666a5yVSa4fvXfnxdh")
                .get(ClientResponse.class);

        InputStream is = response.getEntity(InputStream.class);
//        Files.copy(is, new File("c:\\_work\\downloaded.zip").toPath());

        byte[] fileBytes = IOUtils.toByteArray(is);

//        -- other side --

        File receivedFile = new File("c:\\\\_work\\\\received.zip");
        FileUtils.writeByteArrayToFile(receivedFile, fileBytes);

//        File config = receivedFile.

        String readFileToString = FileUtils.readFileToString(new File("c:\\\\_work\\\\received.zip\\\\config.txt"), "UTF-8");
    }
}
