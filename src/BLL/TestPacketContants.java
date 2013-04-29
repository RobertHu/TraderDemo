package BLL;

import java.nio.charset.Charset;

public final class TestPacketContants {
	
	public final static int HEADER_COUNT = 5;
	public final static int SESSION_LENGTH_INDEX = 0;
	public final static int CONTENT_LENGTH_INDEX = 1;
	public final static int SESSION_HEADER_LENGTH = 1;
	public final static int CONTENT_HEADER_LENGTH = 4;
	
	public static final Charset SESSION_ENCODING=Charset.forName("US-ASCII");
	public static final Charset CONTENT_ENCODING=Charset.forName("UTF-8");
}
