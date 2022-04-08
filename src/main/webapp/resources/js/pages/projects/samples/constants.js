export const SAMPLE_EVENTS = {
  // Used between the sample tools modal window and the samples table
  // to all the table to be notified that it needs to update.
  SAMPLE_TOOLS_CLOSED: "sample-tools:closed",
  SAMPLE_FILTER_CLOSED: "sample-filter:closed"
};

/**
 Constants for the names of filters used in ajax requests.
 */
export const FILTERS = {
  FILTER_BY_FILE: "sampleNames",
  FILTER_BY_NAME: "name",
  FILTER_BY_DESCRIPTION: "description",
  FILTER_BY_COLLECTEDBY: "collectedBy",
  FILTER_BY_ORGANISM: "organism",
  FILTER_BY_STRAIN: "strain",
  FILTER_BY_EARLY_DATE: "startDate",
  FILTER_BY_LATEST_DATE: "endDate"
};
