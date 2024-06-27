package Backend.ObjectMappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	@JsonProperty("success") private String success;
	@JsonProperty("error") private JsonNode error;
}