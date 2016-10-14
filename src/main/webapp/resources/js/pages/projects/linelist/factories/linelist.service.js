const linelistService = ($http, $window) => {
  const URL = $window.location.pathname;

  const getMetadata = template => {
    return $http.get(`${URL}/metadata?template=${template}`);
  };

  return {
    getMetadata
  };
};

export default linelistService;
