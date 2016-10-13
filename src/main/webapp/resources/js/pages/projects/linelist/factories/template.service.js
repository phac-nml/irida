const templateService = ($http, $window) => {
  const URL = $window.location.pathname;

  const getTemplate = (template = 'default') => {
    return $http.get(`${URL}/mt?template=${template}`);
  };

  return {
    getTemplate
  };
};

export default templateService;
