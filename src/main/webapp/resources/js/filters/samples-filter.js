"use strict";
/**
 * Filter an array of Sample
 */
var SamplesFilter = {
  filterByProperties: function(samples, filter) {
    /**
     * Check to ensure that the arguments are valid.
     */
    if(filter === undefined) {
      throw new Error("Must have a filter object to filter against");
    } else if(samples === undefined) {
      throw new Error("Must have samples to filter");
    }

    /**
     * Check to see if the property exists on the filter (cannot filter what does not exist)
     * @param property The property to filter.
     * @returns {boolean} True if the property exists and is defined.
     */
    function checkFilterProperty(property) {
      return (!filter.hasOwnProperty(property) || filter[property] === undefined);
    }

    function nameFilter(name) {
      return (checkFilterProperty("name") || (typeof name === "string" && name.indexOf(filter.name.toLowerCase()) > -1));
    }

    function minDateFilter(date) {
      return ((checkFilterProperty(filter.date) && filter.date.startDate === null) || filter.date.startDate.isBefore(new Date(date)));
    }

    function maxDateFilter(date) {
      return (filter.date.endDate === null || filter.date.endDate.isAfter(new Date(date)));
    }

    return samples.filter(function (s) {
      return (
      nameFilter(s.sample.sampleName) &&
      minDateFilter(s.sample.createdDate) &&
      maxDateFilter(s.sample.createdDate));
    });
  }
};