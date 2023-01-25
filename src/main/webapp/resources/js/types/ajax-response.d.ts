export = AJAXRESPONSE;
export as namespace AJAXRESPONSE;

declare namespace AJAXRESPONSE {
  export type AjaxErrorResponse = {
    error: string;
  };

  export type AjaxSuccessResponse = {
    message: string;
  };
}
