/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   12:18 PM
 */angular.module("irida.directives",[]).directive("fileuploader",function(){"use strict";return{restrict:"A",replace:!0,link:function(e,t,n,r){t.kendoUpload({async:{saveUrl:"/sequenceFiles",autoUpload:!0}})}}});