(function (ng, app) {
  'use strict';

  app.value(
    'RenderContext',
    function (requestContext, actionPrefix, paramNames) {


// Return the next section after the location being watched.
      function getNextSection() {

        return(
          requestContext.getNextSection(actionPrefix)
          );

      }


// Check to see if the action has changed (and is local to the current location).
      function isChangeLocal() {

        return(
          requestContext.startsWith(actionPrefix)
          );

      }


// Determine if the last change in the request context is relevant to
// the action and route params being observed in this render context.
      function isChangeRelevant() {

// If the action is not local to the action prefix, then we don't even
// want to bother checking the params.
        if (!requestContext.startsWith(actionPrefix)) {

          return( false );

        }

// If the action has changed, we don't need to bother checking the params.
        if (requestContext.hasActionChanged()) {

          return( true );

        }

// If we made it this far, we know that the action has not changed. As such, we''ll
// have to make the change determination based on the observed parameters.
        return(
          paramNames.length &&
            requestContext.haveParamsChanged(paramNames)
          );

      }


// ---------------------------------------------- //
// ---------------------------------------------- //


// Private variables...


// ---------------------------------------------- //
// ---------------------------------------------- //


// Return the public API.
      return({
        getNextSection: getNextSection,
        isChangeLocal: isChangeLocal,
        isChangeRelevant: isChangeRelevant
      });


    }
  );

})(angular, NGS);