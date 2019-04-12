import axios from "axios";

const URL = `${window.TL.BASE_URL}samples`;

export const getSampleDetails = id =>
  axios.get(`${URL}?id=${id}`).then(response => response.data);
