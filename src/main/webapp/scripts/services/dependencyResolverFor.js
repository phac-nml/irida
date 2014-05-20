/*globals define:true, require:true */
define([], function()
{
    return function(dependencies)
    {
        var definition =
        {
            resolver: ['$q','$rootScope', function($q, $rootScope)
            {
                var deferred = $q.defer();

                require(dependencies, function()
                {
                    $rootScope.$apply(function()
                    {
                        deferred.resolve();
                    });
                });

                return deferred.promise;
            }]
        };

        return definition;
    };
});