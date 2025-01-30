package fun.fengwk.chatjava.core.client.util.httpclient;

import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.zip.GZIPInputStream;

/**
 * @author fengwk
 */
@Slf4j
public class ChatHttpClientUtils {

    private ChatHttpClientUtils() {}

    public static boolean success(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public static String parseBodyString(HttpResponse<InputStream> httpResponse) throws IOException {
        HttpHeaders headers = httpResponse.headers();
        InputStream input = httpResponse.body();
        try {
            if (gzip(headers)) {
                input = new GZIPInputStream(input);
            }
            return ChatMiscUtils.toString(input, charset(headers));
        } finally {
            input.close();
        }
    }

    private static boolean gzip(HttpHeaders headers) {
        for (String contentEncoding : headers.allValues("Content-Encoding")) {
            if (contentEncoding != null && contentEncoding.contains("gzip")) {
                return true;
            }
        }
        return false;
    }

    private static Charset charset(HttpHeaders headers) {
        for (String contentType : headers.allValues("Content-Type")) {
            Charset charset = null;
            try {
                charset = parseContentTypeCharset(contentType);
            } catch (UnsupportedCharsetException ignore) {
                log.warn("not support charset, contentType: {}", contentType);
            }
            if (charset != null) {
                return charset;
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static Charset parseContentTypeCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        int i = contentType.indexOf(";");
        if (i == -1) {
            return null;
        }

        String charset = contentType.substring(i + 1);
        charset = charset.replaceAll(" ", "");
        if (charset.startsWith("charset=")) {
            charset = charset.substring("charset=".length());
        }
        return Charset.forName(charset);
    }

}
