# Cloudmersive Chunked Transfer Virus Scan Example

Java demo application that uploads a file to the [Cloudmersive Advanced Virus Scan API](https://cloudmersive.com/virus-api) using **HTTP chunked transfer encoding**. This approach streams the file from disk without buffering the entire payload in memory, making it suitable for scanning files that are multiple GB in size.

## How it works

Uses the official [Cloudmersive Virus Scan Java SDK v11](https://central.sonatype.com/artifact/com.cloudmersive/cloudmersive-virus-api-java-client/11.0.0) with chunked transfer encoding enabled via `ApiClient.enableChunkedTransfer()`. Under the hood the SDK streams the multipart request body through a `java.nio.channels.Pipe`, producing an `InputStream`-backed `BodyPublisher` with no predetermined content length. This causes `java.net.http.HttpClient` to send the body using `Transfer-Encoding: chunked`, so the file is never buffered entirely in memory.

## Prerequisites

- **Java 11+**
- **Apache Maven 3.6+**
- A **Cloudmersive API key** — sign up at <https://cloudmersive.com>

## Quick start

1. **Clone the repository**

   ```bash
   git clone https://github.com/YourOrg/CloudmersiveJavaChunkedTransferExample.git
   cd CloudmersiveJavaChunkedTransferExample
   ```

2. **Set your API key and file path**

   Open `src/main/java/com/example/ChunkedVirusScanDemo.java` and edit the constants at the top of the class:

   ```java
   private static final String FILE_PATH = "C:\\files\\large-test-file.dat";
   private static final String API_KEY   = "YOUR_API_KEY_HERE";
   private static final String BASE_PATH = "https://api.cloudmersive.com";
   private static final int TIMEOUT_MS   = 3_000_000;
   ```

3. **Build and run**

   ```bash
   mvn compile exec:java
   ```

## Example output

```
File  : C:\test\inputfile.iso
Size  : 5,550,684,160 bytes (5.17 GB)

API Key    : ****
Base Path  : https://api.cloudmersive.com
Timeout    : 3,000,000 ms
Chunked    : true

Uploading file for advanced virus scan (chunked transfer)...
Upload complete.

=========================================
        ADVANCED VIRUS SCAN RESULT
=========================================

  Clean result              : true
  Contains executable       : false
  Contains invalid file     : false
  Contains script           : false
  Contains password-protect : false
  Contains restricted fmt   : false
  Contains macros           : false
  Contains XML ext entities : false
  Contains insecure deser   : false
  Contains HTML             : false
  Contains unsafe archive   : false
  Contains OLE embed object : false
  Verified file format      : iso

  No viruses detected.

=========================================
```

## Project structure

```
.
├── pom.xml                       Maven build file (Cloudmersive SDK v11)
├── LICENSE                       Apache 2.0 license
├── README.md                     This file
└── src/main/java/com/example/
    └── ChunkedVirusScanDemo.java Demo application
```

## Configuration reference

| Constant | Default | Description |
|----------|---------|-------------|
| `FILE_PATH` | — | Absolute path to the file to scan |
| `API_KEY` | — | Your Cloudmersive API key |
| `BASE_PATH` | `https://api.cloudmersive.com` | API base URL |
| `TIMEOUT_MS` | `3000000` (50 min) | HTTP connect and read timeout in milliseconds |

## License

This project is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for the full text.
