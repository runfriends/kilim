package kilim.nio;


import java.nio.ByteBuffer;

/**
 * ByteBuffer utils
 * @author boyan
 * @Date 2010-8-21
 *
 */
public class ByteBufferUtils {
    static final int DEFAULT_INCREASE_BUFF_SIZE=16*1024;
    public static final int MAX_READ_BUFFER_SIZE=64*1024;
	/**
	 * 
	 * @param byteBuffer
	 * @return *
	 */
	public static final ByteBuffer increaseBufferCapatity(ByteBuffer byteBuffer) {

		if (byteBuffer == null) {
			throw new IllegalArgumentException("buffer is null");
		}

		int capacity = byteBuffer.capacity()
				+ DEFAULT_INCREASE_BUFF_SIZE;
		if (capacity < 0) {
			throw new IllegalArgumentException("capacity can't be negative");
		}
		ByteBuffer result = byteBuffer.isDirect() ? ByteBuffer
				.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
		result.order(byteBuffer.order());
		byteBuffer.flip();
		result.put(byteBuffer);
		return result;
	}

	public static final void flip(ByteBuffer[] buffers) {
		if (buffers == null) {
			return;
		}
		for (ByteBuffer buffer : buffers) {
			if (buffer != null) {
				buffer.flip();
			}
		}
	}

	public static final ByteBuffer gather(ByteBuffer[] buffers) {
		if (buffers == null || buffers.length == 0) {
			return null;
		}
		ByteBuffer result = ByteBuffer.allocate(remaining(buffers));
		result.order(buffers[0].order());
		for (int i = 0; i < buffers.length; i++) {
			if (buffers[i] != null) {
				result.put(buffers[i]);
			}
		}
		result.flip();
		return result;
	}

	public static final int remaining(ByteBuffer[] buffers) {
		if (buffers == null) {
			return 0;
		}
		int remaining = 0;
		for (int i = 0; i < buffers.length; i++) {
			if (buffers[i] != null) {
				remaining += buffers[i].remaining();
			}
		}
		return remaining;
	}

	public static final void clear(ByteBuffer[] buffers) {
		if (buffers == null) {
			return;
		}
		for (ByteBuffer buffer : buffers) {
			if (buffer != null) {
				buffer.clear();
			}
		}
	}

	public static final String toHex(byte b) {
		return "" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF"
				.charAt(b & 0xf);
	}

	public static final int indexOf(ByteBuffer buffer, ByteBuffer pattern) {
		if (pattern == null || buffer == null) {
			return -1;
		}
		int n = buffer.remaining();
		int m = pattern.remaining();
		int patternPos = pattern.position();
		int bufferPos = buffer.position();
		if (n < m) {
			return -1;
		}
		for (int s = 0; s <= n - m; s++) {
			boolean match = true;
			for (int i = 0; i < m; i++) {
				if (buffer.get(s + i + bufferPos) != pattern
						.get(patternPos + i)) {
					match = false;
					break;
				}
			}
			if (match) {
				return bufferPos + s;
			}
		}
		return -1;
	}

	public static final int indexOf(ByteBuffer buffer, ByteBuffer pattern,
			int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("offset must be greater than 0");
		}
		if (pattern == null || buffer == null) {
			return -1;
		}
		int patternPos = pattern.position();
		int n = buffer.remaining();
		int m = pattern.remaining();
		if (n < m) {
			return -1;
		}
		if (offset < buffer.position() || offset > buffer.limit()) {
			return -1;
		}
		for (int s = 0; s <= n - m; s++) {
			boolean match = true;
			for (int i = 0; i < m; i++) {
				if (buffer.get(s + i + offset) != pattern.get(patternPos + i)) {
					match = false;
					break;
				}
			}
			if (match) {
				return offset + s;
			}
		}
		return -1;
	}

	/**
	 * 查看ByteBuffer数组是否还有剩余
	 * 
	 * @param buffers
	 *            ByteBuffers
	 * @return have remaining
	 */
	public static final boolean hasRemaining(ByteBuffer[] buffers) {
		if (buffers == null) {
			return false;
		}
		for (int i = 0; i < buffers.length; i++) {
			if (buffers[i] != null && buffers[i].hasRemaining()) {
				return true;
			}
		}
		return false;
	}

	public static final int uByte(byte b) {
		return b & 0xFF;
	}
}
