import axios from "axios";

export const getGalaxyDetails = clientId =>
  axios.get(`${window.TL.BASE_URL}ajax/galaxy-export`).then(({ data }) => data);

export const getGalaxyClientAuthentication = clientId =>
  axios
    .get(
      `${window.TL.BASE_URL}ajax/galaxy-export/authorized?clientId=${clientId}`
    )
    .then(({ data }) => data);

export const getGalaxySamples = () =>
  axios
    .get(`${window.TL.BASE_URL}ajax/galaxy-export/samples`)
    .then(({ data }) => data);

export const exportToGalaxy = async (
  name,
  email,
  addtohistory = true,
  makepairedcollection = true
) => {
  // Get the form from the server.
};
