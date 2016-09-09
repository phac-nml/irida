/**
 * @file ui.router state for setting the column header associated with the
 * sample identifier.
 */
import {STATE_URLS} from "../../constants";

const sampleIdState = {
  url: STATE_URLS.sampleId,
  params: {headers: []},
  templateUrl: "sampleId.tmpl.html"
};

export default sampleIdState;
