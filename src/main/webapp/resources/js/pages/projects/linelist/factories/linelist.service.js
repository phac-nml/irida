const linelistService = ($http, $window) => {
  const URL = $window.location.pathname;

  const getMetadata = () => {
    return $http.get(`${URL}/metadata`);
  };

  return {
    getMetadata
  };
};

export default linelistService;
