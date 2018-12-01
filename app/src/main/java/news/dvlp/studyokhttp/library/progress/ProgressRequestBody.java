package news.dvlp.studyokhttp.library.progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload inProgress of large
 * multipart requests.
 *
 * @author Leo Nikkilä
 */
class ProgressRequestBody extends RequestBody {
    private final RequestBody delegate;
    private final ProgressListener listener;
    private final Request request;

    ProgressRequestBody(RequestBody delegate, ProgressListener listener, Request request) {
        this.delegate = delegate;
        this.listener = listener;
        this.request = request;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(sink(sink));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            private long bytesWritten = 0L;
            private long contentLength = -1L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                if (contentLength == -1) {
                    //避免多次调用
                    contentLength = contentLength();
                }
                listener.onUpload(request, bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }
}