package com.ideality.coreflow.security.custom;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;

// CachedBodyHttpServletRequest - RequestBody를 여러번 읽기 위해 래핑하는 클래스
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream in = request.getInputStream();
        // 요청 바디를 읽어 cachedBody에 저장
        this.cachedBody = StreamUtils.copyToByteArray(in);
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.cachedBody);

        // cachedBody를 읽는 ServletInputStram 리턴
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setReadListener(ReadListener readListener) { }
            @Override
            public int read() {
                return bais.read();
            }
        };
    }


    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
