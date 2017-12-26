package net.viperfish.passwordvault.crypt;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

public final class BytesPair {
	
	public static BytesPair create(byte[] first, byte[] second) {
		return new BytesPair(first, second);
	}
	
	public static BytesPair fromBase64(String base64) throws IllegalArgumentException {
		String[] parts = base64.split(":");
		if(parts.length != 2) {
			throw new IllegalArgumentException("BytesPair must be first:second");
		}
		return create(Base64.decodeBase64(parts[0]), Base64.decodeBase64(parts[1]));
	}
	
	public static BytesPair fromBase32(String base32) throws IllegalArgumentException {
		String[] parts =base32.split(":");
		if(parts.length != 2) {
			throw new IllegalArgumentException("BytesPair must be first:second");
		}
		Base32 encoder = new Base32();
		return create(encoder.decode(parts[0]), encoder.decode(parts[1]));
	}
	
	private byte[] first;
	private byte[] second;
	
	BytesPair(byte[] first, byte[] second) {
		this.first = first.clone();
		this.second = second.clone();
	}

	public final byte[] getFirst() {
		return first.clone();
	}

	public final byte[] getSecond() {
		return second.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(first);
		result = prime * result + Arrays.hashCode(second);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BytesPair other = (BytesPair) obj;
		if (!Arrays.equals(first, other.first))
			return false;
		if (!Arrays.equals(second, other.second))
			return false;
		return true;
	}
	
	public String encodeBase64() {
		StringBuilder sb = new StringBuilder();
		sb.append(Base64.encodeBase64URLSafeString(first)).append(":").append(Base64.encodeBase64URLSafeString(second));
		return sb.toString();
	}
	
	public String encodeBase32() {
		Base32 b32 = new Base32();
		StringBuilder sb = new StringBuilder();
		sb.append(b32.encodeAsString(first)).append(":").append(second);
		return sb.toString();
	}
	
}
