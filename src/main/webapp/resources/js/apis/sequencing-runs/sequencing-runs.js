import axios from "axios";

const URL = `${window.TL.BASE_URL}sequencingRuns/list`;

export function fetchSequencingRuns(params) {
  return axios.get(URL).then(({ data }) => console.log(data));
}
