package Backend.ObjectMappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConvertCurrencyResponse {
	@JsonProperty("success") private String success;
	@JsonProperty("timeseries") private String timeseries;
	@JsonProperty("start_date") private String startDate;
	@JsonProperty("end_date") private String endDate;
	@JsonProperty("base") private String baseCurrency;
	@JsonProperty("rates") private JsonNode rates;
}