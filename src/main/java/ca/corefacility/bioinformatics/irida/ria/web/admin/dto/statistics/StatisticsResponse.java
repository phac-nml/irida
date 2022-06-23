package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.enums.StatisticTimePeriod;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * UI Response to to encapsulate statistics.
 */

public class StatisticsResponse extends AjaxResponse {
	private List<GenericStatModel> statistics;

	public StatisticsResponse(List<GenericStatModel> statistics, StatisticTimePeriod statisticTimePeriod) {
		this.statistics = formatStatistics(statistics, statisticTimePeriod);
	}

	public List<GenericStatModel> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<GenericStatModel> statistics) {
		this.statistics = statistics;
	}

	private List<GenericStatModel> formatStatistics(List<GenericStatModel> statistics,
			StatisticTimePeriod statisticTimePeriod) {
		List<GenericStatModel> formattedStatistics = new ArrayList<>();

		if (statisticTimePeriod.getGroupByFormat().equals(statisticTimePeriod.getDisplayFormat())) {
			return statistics;
		} else {
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter = new SimpleDateFormat(statisticTimePeriod.getDisplayFormat());

			for (GenericStatModel statistic : statistics) {
				Date date;
				String formattedKey;
				try {
					date = parser.parse(statistic.getKey());
					formattedKey = formatter.format(date);
				} catch (ParseException e) {
					formattedKey = statistic.getKey();
				}

				GenericStatModel formattedStatistic = new GenericStatModel(formattedKey, statistic.getValue());
				formattedStatistics.add(formattedStatistic);
			}
		}

		return formattedStatistics;
	}
}
