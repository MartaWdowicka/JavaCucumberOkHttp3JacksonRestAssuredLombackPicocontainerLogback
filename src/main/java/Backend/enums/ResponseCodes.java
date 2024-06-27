package Backend.enums;

import lombok.Getter;

public class ResponseCodes {
    public enum ResponseCode {
    	ACCESSGRANTED(200),
        BADREQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDENACCESS(403),
        RESOURCENOTFOUND(404),
        TOOMANYREQUESTS(429);

    	ResponseCode(int value) {
    		this.value = value;
    	}

    	@Getter private final int value;

    	public static int getResponseCode(String key) throws Exception {
    		for (ResponseCode enumEntry : ResponseCode.class.getEnumConstants()) {
    			if (enumEntry.name().equals(key)) {
    				return enumEntry.value;
    			}
    		}
    		throw new Exception("Could not find response code \"" + key + "\" in ResponseCode enum.");
    	}
    }
}