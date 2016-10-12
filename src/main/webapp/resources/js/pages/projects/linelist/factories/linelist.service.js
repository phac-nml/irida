const linelistService = $http => {
  const getMetadata = url => {
    return $http
      .get(url)
      .then(result => {
        return result.data;
      });
  };

  return {
    getMetadata
  };
};

export default linelistService;
