package com.example;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.api.ScanApi;
import org.openapitools.client.model.VirusFound;
import org.openapitools.client.model.VirusScanAdvancedResult;

import java.io.File;
import java.time.Duration;
import java.util.List;

/**
 * Demonstrates uploading a large file (potentially multi-GB) to the Cloudmersive
 * Advanced Virus Scan API using HTTP chunked transfer encoding via the official
 * Cloudmersive Java SDK v11.
 *
 * <p>The SDK's {@link ApiClient#enableChunkedTransfer()} method switches the
 * multipart body publisher to a pipe-backed {@code InputStream} whose length is
 * unknown to {@code java.net.http.HttpClient}, causing it to send the body with
 * {@code Transfer-Encoding: chunked}. The file is never buffered entirely in
 * memory, so this works for files of any size.</p>
 */
public class ChunkedVirusScanDemo {

    // ---------------------------------------------------------------
    // Configuration - edit these values before running
    // ---------------------------------------------------------------

    /** Path to the file to scan. Change this to point at your target file. */
    private static final String FILE_PATH = "C:\\test\\en-us_windows_server_2022_x64_dvd_620d7eac.iso";

    /** Your Cloudmersive API key. Get one at https://cloudmersive.com */
    private static final String API_KEY = "YOUR-API-KEY";

    /** Base URL for the Cloudmersive API. */
    private static final String BASE_PATH = "https://api.cloudmersive.com";

    /** Timeout for HTTP read operations in milliseconds. */
    private static final int TIMEOUT_MS = 3_000_000;

    // ---------------------------------------------------------------

    public static void main(String[] args) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("File not found: " + FILE_PATH);
            System.exit(1);
        }

        System.out.printf("File  : %s%n", file.getAbsolutePath());
        System.out.printf("Size  : %,d bytes (%.2f GB)%n",
                file.length(), file.length() / (1024.0 * 1024.0 * 1024.0));
        System.out.println();

        System.out.println("API Key    : " + API_KEY);
        System.out.println("Base Path  : " + BASE_PATH);
        System.out.printf("Timeout    : %,d ms%n", TIMEOUT_MS);
        System.out.println("Chunked    : true");
        System.out.println();

        // ---- Configure the SDK ApiClient ----
        ApiClient apiClient = Configuration.getDefaultApiClient();
        apiClient.updateBaseUri(BASE_PATH);
        apiClient.setReadTimeout(Duration.ofMillis(TIMEOUT_MS));
        apiClient.setConnectTimeout(Duration.ofMillis(TIMEOUT_MS));

        // Enable chunked transfer encoding so the file is streamed via a pipe
        // rather than buffered entirely in memory.
        apiClient.enableChunkedTransfer();

        // Set the API key header on every outgoing request.
        apiClient.setRequestInterceptor(builder -> builder.header("Apikey", API_KEY));

        ScanApi scanApi = new ScanApi(apiClient);

        System.out.println("Uploading file for advanced virus scan (chunked transfer)...");

        try {
            VirusScanAdvancedResult result = scanApi.scanFileAdvanced(
                    file,
                    null,  // fileName
                    null,  // allowExecutables
                    null,  // allowInvalidFiles
                    null,  // allowScripts
                    null,  // allowPasswordProtectedFiles
                    null,  // allowMacros
                    null,  // allowXmlExternalEntities
                    null,  // allowInsecureDeserialization
                    null,  // allowHtml
                    null,  // allowUnsafeArchives
                    null,  // allowOleEmbeddedObject
                    null,  // allowUnwantedAction
                    null,  // options
                    null   // restrictFileTypes
            );

            System.out.println("Upload complete.");
            System.out.println();
            printResult(result);

        } catch (ApiException e) {
            System.err.println("Scan failed (HTTP " + e.getCode() + "):");
            System.err.println(e.getResponseBody());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Pretty-prints the scan result to the console. */
    private static void printResult(VirusScanAdvancedResult result) {
        System.out.println("=========================================");
        System.out.println("        ADVANCED VIRUS SCAN RESULT       ");
        System.out.println("=========================================");
        System.out.println();

        Boolean clean = result.getCleanResult();
        System.out.println("  Clean result              : " + clean);
        System.out.println("  Contains executable       : " + result.getContainsExecutable());
        System.out.println("  Contains invalid file     : " + result.getContainsInvalidFile());
        System.out.println("  Contains script           : " + result.getContainsScript());
        System.out.println("  Contains password-protect : " + result.getContainsPasswordProtectedFile());
        System.out.println("  Contains restricted fmt   : " + result.getContainsRestrictedFileFormat());
        System.out.println("  Contains macros           : " + result.getContainsMacros());
        System.out.println("  Contains XML ext entities : " + result.getContainsXmlExternalEntities());
        System.out.println("  Contains insecure deser   : " + result.getContainsInsecureDeserialization());
        System.out.println("  Contains HTML             : " + result.getContainsHtml());
        System.out.println("  Contains unsafe archive   : " + result.getContainsUnsafeArchive());
        System.out.println("  Contains OLE embed object : " + result.getContainsOleEmbeddedObject());
        System.out.println("  Verified file format      : " + result.getVerifiedFileFormat());

        List<VirusFound> viruses = result.getFoundViruses();
        if (viruses != null && !viruses.isEmpty()) {
            System.out.println();
            System.out.println("  ** VIRUSES FOUND **");
            for (VirusFound v : viruses) {
                System.out.println("    - " + v.getVirusName()
                        + " (file: " + v.getFileName() + ")");
            }
        }

        if (Boolean.TRUE.equals(clean)) {
            System.out.println();
            System.out.println("  No viruses detected.");
        }

        System.out.println();
        System.out.println("=========================================");
    }
}
