import $ from "jquery";

/**
 * Delete an analysis.
 * @param {object} analysis to delete
 * @return {*} promise that the delete will occur.
 */
export function deleteAnalysis({ id }) {
  return $.ajax({
    url: `${window.PAGE.URLS.deleteUrl}${id}`,
    type: "DELETE"
  });
}
