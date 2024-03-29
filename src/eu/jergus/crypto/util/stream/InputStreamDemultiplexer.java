package eu.jergus.crypto.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Splits a stream generated by {@link OutputStreamMultiplexer} into its
 * original parts.
 */
public class InputStreamDemultiplexer {

	/**
	 * Represents one of the reconstructed partial streams.
	 */
	private class DemultiplexedInputStream extends InputStream {
		private Queue<Byte> buffer = new ArrayDeque<Byte>();

		synchronized public int read() throws IOException {
			while (buffer.isEmpty()) {
				if (closed) throw new IOException("Stream is closed.");
				if (end) return -1;
				if (exception != null) throw exception;
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			return (int) buffer.remove() & 255;
		}

		/**
		 * Called by the outer object when new data is received.
		 */
		synchronized private void add(byte[] b) {
			for (byte cur : b) {
				buffer.add(cur);
			}
			notifyAll();
		}
	}


	private InputStream source;

	private Map<Integer, DemultiplexedInputStream> streams = new HashMap<Integer, DemultiplexedInputStream>();

	/**
	 * True when end of the underlining input stream is reached.
	 */
	private boolean end = false;

	/**
	 * True when {@link #close} was caled.
	 */
	private boolean closed = false;

	/**
	 * True when the underlining input stream has thrown an exception. This
	 * exception will be re-thrown by the next
	 * {@link DemultiplexedInputStream#read} call.
	 */
	private IOException exception = null;


	/**
	 * Creates a new demultiplexer object backed by the provided stream.
	 * <p>
	 * This automatically launches a background thread that will process all
	 * received input.
	 *
	 * @param src  source input stream
	 */
	public InputStreamDemultiplexer(InputStream src) {
		source = src;

		Thread t = new Thread() {
			public void run() {
				try {
					while (true) {
						int id = source.read();

						if (id == -1) {
							end = true;
							break;
						}

						int lenHi = source.read();
						if (lenHi == -1) throw new IOException("Unexpected end of stream.");
						int lenLo = source.read();
						if (lenLo == -1) throw new IOException("Unexpected end of stream.");

						int len = (lenHi << 8) + lenLo;

						byte[] b = new byte[len];

						if (source.read(b) != len) {
							throw new IOException("Unexpected end of stream.");
						}

						getInputStream(id).add(b);
					}
				} catch (IOException e) {
					exception = e;
				}

				for (DemultiplexedInputStream s : streams.values()) {
					synchronized (s) {
						s.notifyAll();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}


	/**
	 * Returns one of the partial input streams.
	 *
	 * @param id  ID of the stream to return (0 to 255)
	 * @return    the partial input stream
	 */
	public DemultiplexedInputStream getInputStream(int id) {
		if (id < 0 || id >= 256) {
			throw new IllegalArgumentException("Demultiplexed stream ID must be between 0 and 255.");
		}

		if (!streams.containsKey(id)) {
			streams.put(id, new DemultiplexedInputStream());
		}

		return streams.get(id);
	}


	/**
	 * Closes the underlining input stream.
	 * <p>
	 * All subsequent attempts to read from any of the partial streams will
	 * throw an exception.
	 */
	public void close() {
		closed = true;
		try {
			source.close();
		} catch (IOException e) {}
	}

}